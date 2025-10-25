import React, { useEffect, useMemo, useState } from 'react';
import { Download, Trash2 } from 'lucide-react';
import EntityIndexLayout from '../../../components/ui/EntityIndexLayout/EntityIndexLayout';
import FiltersBar, { FilterGroup } from '../../../components/ui/FiltersBar/FiltersBar';
import IconButton from '../../../components/ui/IconButton/IconButton';
import DataTable, { type Column } from '../../../components/ui/DataTable/DataTable';
import Chip from '../../../components/ui/Chip/Chip';
import {
    heatingRequestsApi,
    type HeatingRequestDto,
    HeatingRequestStatus,
    FuelType,
} from '../../../api';
import './styles.scss';

const statusLabels: Record<HeatingRequestStatus, string> = {
    DRAFT: 'Черновик',
    IN_PROGRESS: 'В работе',
    CANDIDATES_READY: 'Варианты готовы',
    COMPLETED: 'Завершена',
    CANCELLED: 'Отменена',
};

const fuelLabels: Record<FuelType, string> = {
    gas: 'Газ',
    diesel: 'Дизель',
    other: 'Другое',
};

const statusOrder: HeatingRequestStatus[] = [
    'DRAFT',
    'IN_PROGRESS',
    'CANDIDATES_READY',
    'COMPLETED',
    'CANCELLED',
];

const fuelOrder: FuelType[] = ['gas', 'diesel', 'other'];

const RequestsPage: React.FC = () => {
    const [requests, setRequests] = useState<HeatingRequestDto[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // фильтры + поиск
    const [filters, setFilters] = useState<{ status?: HeatingRequestStatus; fuel?: FuelType; q: string }>({
        status: undefined,
        fuel: undefined,
        q: '',
    });

    // выбор строк
    const [selected, setSelected] = useState<Set<string>>(new Set());

    const loadRequests = async () => {
        try {
            setLoading(true);
            const resp = await heatingRequestsApi.list({
                status: filters.status,
                fuelType: filters.fuel,
                page,
                size: 20,
            });
            setRequests(resp.content);
            setTotalPages(resp.totalPages);
            setError(null);
        } catch (e) {
            setError('Ошибка загрузки заявок');
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadRequests(); }, [page, filters.status, filters.fuel]);

    const filteredLocal = useMemo(() => {
        const q = filters.q.trim().toLowerCase();
        if (!q) return requests;
        return requests.filter(r => {
            const idShort = r.id?.slice(0, 8) ?? '';
            const custShort = r.customerId?.slice(0, 8) ?? '';
            const fuel = fuelLabels[r.fuelType] ?? r.fuelType;
            return [r.id, idShort, r.customerId, custShort, String(r.powerKw), fuel]
                .filter(Boolean)
                .join(' ')
                .toLowerCase()
                .includes(q);
        });
    }, [requests, filters.q]);

    const applyLocal = (id: string, patch: Partial<HeatingRequestDto>) =>
        setRequests(prev => prev.map(x => (x.id === id ? { ...x, ...patch } : x)));

    // инлайн-патч из таблицы: сейчас поддерживаем только смену статуса
    const onPatch = async (id: string, patch: Partial<HeatingRequestDto>) => {
        if ('status' in patch && patch.status) {
            const original = requests.find(r => r.id === id);
            if (!original) return;
            // оптимистично
            applyLocal(id, { status: patch.status });
            try {
                await heatingRequestsApi.setStatus(id, patch.status as HeatingRequestStatus);
            } catch (e) {
                // откат
                applyLocal(id, { status: original.status });
                console.error(e);
                alert('Не удалось изменить статус');
            }
        }
    };

    const handleDelete = async (id: string) => {
        if (!confirm('Удалить заявку?')) return;
        try {
            await heatingRequestsApi.delete(id);
            loadRequests();
        } catch (e) {
            setError('Ошибка удаления заявки');
            console.error(e);
        }
    };

    const columns: Column<HeatingRequestDto>[] = [
        {
            id: 'id',
            header: 'ID',
            accessor: (r) => `${r.id.slice(0, 8)}…`,
        },
        {
            id: 'customer',
            header: 'Клиент',
            accessor: (r) => r.customerId ? `${r.customerId.slice(0, 8)}…` : '—',
        },
        { id: 'power', header: 'Мощность (кВт)', accessor: (r) => r.powerKw },
        { id: 'tIn', header: 'Tвх', accessor: (r) => `${r.tIn}°C`, align: 'center' },
        { id: 'tOut', header: 'Tвых', accessor: (r) => `${r.tOut}°C`, align: 'center' },
        { id: 'fuel', header: 'Топливо', accessor: (r) => fuelLabels[r.fuelType] ?? r.fuelType },
        // статус — через селект (инлайн)
        {
            id: 'status',
            header: 'Статус',
            field: 'status',
            align: 'center',
            editable: {
                type: 'select',
                options: statusOrder.map(s => ({ value: s, label: statusLabels[s] })),
            },
            accessor: (r) => statusLabels[r.status],
        },
        {
            id: 'actions',
            header: 'Действия',
            align: 'right',
            accessor: (r) => (
                <IconButton label="Удалить" variant="outline" onClick={() => handleDelete(r.id)}>
                    <Trash2 size={18} />
                </IconButton>
            ),
        },
    ];

    // экспорт CSV (по текущей локальной выборке)
    const exportCsv = () => {
        const headers = ['ID', 'Клиент', 'Мощн.', 'Tвх', 'Tвых', 'Топливо', 'Статус'];
        const rows = filteredLocal.map(r => [
            r.id,
            r.customerId ?? '',
            r.powerKw,
            r.tIn,
            r.tOut,
            fuelLabels[r.fuelType] ?? r.fuelType,
            statusLabels[r.status],
        ]);
        const csv = [headers.join(','), ...rows.map(row => row.map(c => `"${c}"`).join(','))].join('\n');
        const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `requests_${new Date().toISOString().split('T')[0]}.csv`;
        a.click();
    };

    return (
        <EntityIndexLayout
            title="Управление заявками"
            subtitle={`Всего: ${requests.length}`}
            createForm={
                // На этой странице отдельной формы создания нет — оставим пусто или вставим подсказку при желании
                <div style={{ color: 'var(--color-muted)' }}>Создание заявок выполняется клиентом.</div>
            }
            filters={
                <FiltersBar
                    title="Фильтры"
                    right={
                        <>
                            <input
                                className="fb__search"
                                type="search"
                                placeholder="Поиск…"
                                value={filters.q}
                                onChange={(e) => setFilters(f => ({ ...f, q: e.target.value }))}
                            />
                            <IconButton label="Экспорт CSV" onClick={exportCsv}>
                                <Download size={18} />
                            </IconButton>
                        </>
                    }
                    groups={[
                        <FilterGroup key="status" title="Статус">
                            <Chip
                                selected={!filters.status}
                                onClick={() => { setFilters(f => ({ ...f, status: undefined })); setPage(0); }}
                            >
                                Все
                            </Chip>
                            {statusOrder.map(s => (
                                <Chip
                                    key={s}
                                    selected={filters.status === s}
                                    onClick={() => { setFilters(f => ({ ...f, status: s })); setPage(0); }}
                                >
                                    {statusLabels[s]}
                                </Chip>
                            ))}
                        </FilterGroup>,
                        <FilterGroup key="fuel" title="Топливо">
                            <Chip
                                selected={!filters.fuel}
                                onClick={() => { setFilters(f => ({ ...f, fuel: undefined })); setPage(0); }}
                            >
                                Все типы
                            </Chip>
                            {fuelOrder.map(ft => (
                                <Chip
                                    key={ft}
                                    selected={filters.fuel === ft}
                                    onClick={() => { setFilters(f => ({ ...f, fuel: ft })); setPage(0); }}
                                >
                                    {fuelLabels[ft]}
                                </Chip>
                            ))}
                        </FilterGroup>,
                    ]}
                />
            }
        >
            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <p>Загрузка…</p>
            ) : (
                <DataTable<HeatingRequestDto>
                    rows={filteredLocal}
                    columns={columns}
                    rowKey={(r) => r.id}
                    selectable
                    selectedKeys={selected}
                    onToggleRow={(k, ch) =>
                        setSelected(s => {
                            const next = new Set(s);
                            ch ? next.add(k) : next.delete(k);
                            return next;
                        })
                    }
                    onToggleAll={(ch, keys) => setSelected(ch ? new Set(keys) : new Set())}
                    onPatch={onPatch}         // селект статуса → setStatus
                    hideSearch={true}         // поиск вынесен вправо от фильтров
                    page={page}
                    totalPages={totalPages}
                    onPageChange={setPage}
                />
            )}
        </EntityIndexLayout>
    );
};

export default RequestsPage;
