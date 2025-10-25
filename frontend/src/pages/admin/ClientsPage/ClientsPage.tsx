// src/pages/admin/clients/ClientsPage.tsx
import React, {useEffect, useMemo, useState} from 'react';
import { Download, Trash2 } from 'lucide-react';
import EntityIndexLayout from '../../../components/ui/EntityIndexLayout/EntityIndexLayout';
import FiltersBar, {FilterGroup} from '../../../components/ui/FiltersBar/FiltersBar';
import CreateFormBox from '../../../components/ui/CreateFormBox/CreateFormBox';
import IconButton from '../../../components/ui/IconButton/IconButton';
import DataTable, {type Column} from '../../../components/ui/DataTable/DataTable';
import Chip from '../../../components/ui/Chip/Chip';
import {type CustomerCreateDto, type CustomerDto, customersApi, type CustomerUpdateDto,} from '../../../api';

const ClientsPage: React.FC = () => {
    const [customers, setCustomers] = useState<CustomerDto[]>([]);
    const [totalCustomers, setTotalCustomers] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selected, setSelected] = useState<Set<string>>(new Set());

    // Фильтры + поиск (поиск справа от фильтров)
    const [filters, setFilters] = useState<{ status: 'all' | 'active' | 'inactive'; q: string }>({
        status: 'all',
        q: '',
    });

    // Форма создания — всегда видима
    const [formData, setFormData] = useState<CustomerCreateDto>({
        email: '',
        fullName: '',
        phone: '',
        organization: '',
        password: '',
    });

    const loadCustomers = async () => {
        try {
            setLoading(true);
            const resp = await customersApi.list(page, 20);
            setCustomers(resp.customers);
            setTotalCustomers(resp.totalCustomers ?? resp.customers.length);
            setTotalPages(resp.totalPages);
            setError(null);
        } catch (e) {
            setError('Ошибка загрузки клиентов');
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadCustomers();
    }, [page]);

    const filtered = useMemo(() => {
        const byStatus = customers.filter(c => {
            if (filters.status === 'all') return true;
            return filters.status === 'active' ? !!c.isActive : !c.isActive;
        });
        const q = filters.q.trim().toLowerCase();
        if (!q) return byStatus;
        return byStatus.filter(c =>
            [c.fullName, c.email, c.phone, c.organization].filter(Boolean).join(' ').toLowerCase().includes(q)
        );
    }, [customers, filters]);

    // Полный PUT
    const toUpdateDto = (c: CustomerDto): CustomerUpdateDto => ({
        email: c.email,
        fullName: c.fullName,
        phone: c.phone,
        organization: c.organization,
        isActive: c.isActive ?? false,
    });

    const applyLocal = (id: string, patch: Partial<CustomerDto>) =>
        setCustomers(prev => prev.map(x => (x.id === id ? {...x, ...patch} : x)));

    const patchCustomer = async (id: string, patch: Partial<CustomerDto>) => {
        const original = customers.find(c => c.id === id);
        if (!original) return;

        // оптимистично
        applyLocal(id, patch);

        const dto: CustomerUpdateDto = toUpdateDto({...original, ...patch});
        try {
            await customersApi.update(id, dto);
        } catch (e) {
            applyLocal(id, original);
            console.error(e);
            alert('Не удалось сохранить изменения');
        }
    };

    const handleDelete = async (id: string) => {
        if (!confirm('Удалить клиента?')) return;
        try {
            await customersApi.delete(id);
            loadCustomers();
        } catch (e) {
            setError('Ошибка удаления клиента');
            console.error(e);
        }
    };

    const toggleRow = (key: string, checked: boolean) =>
        setSelected(prev => {
            const next = new Set(prev);
            checked ? next.add(key) : next.delete(key);
            return next;
        });

    const toggleAll = (checked: boolean, keys: string[]) =>
        setSelected(checked ? new Set(keys) : new Set());

    const columns: Column<CustomerDto>[] = [
        {id: 'name', header: 'ФИО', field: 'fullName', editable: {type: 'text', placeholder: 'ФИО'}},
        {id: 'email', header: 'Email', field: 'email', editable: {type: 'text'}},
        {id: 'phone', header: 'Телефон', field: 'phone', editable: {type: 'text'}},
        {id: 'org', header: 'Организация', field: 'organization', editable: {type: 'text'}},
        {
            id: 'active',
            header: 'Статус',
            field: 'isActive',
            align: 'center',
            // чип-тоггл с одного клика (Boolean(undefined) => false)
            editable: {type: 'toggle', labels: {on: 'Активен', off: 'Неактивен'}},
        },
        {
            id: 'created',
            header: 'Дата регистрации',
            accessor: (r) => new Date(r.createdAt).toLocaleDateString(),
            align: 'right',
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

    // экспорт CSV отфильтрованных
    const exportCsv = () => {
        const headers = ['ФИО', 'Email', 'Телефон', 'Организация', 'Статус', 'Дата регистрации'];
        const rows = filtered.map(c => [
            c.fullName,
            c.email,
            c.phone,
            c.organization,
            c.isActive ? 'Активен' : 'Неактивен',
            new Date(c.createdAt).toLocaleDateString(),
        ]);
        const csv = [headers.join(','), ...rows.map(r => r.map(x => `"${x}"`).join(','))].join('\n');
        const blob = new Blob(['\ufeff' + csv], {type: 'text/csv;charset=utf-8;'});
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `clients_${new Date().toISOString().split('T')[0]}.csv`;
        a.click();
    };

    return (
        <EntityIndexLayout
            title="Управление клиентами"
            subtitle={`Всего: ${totalCustomers}`}
            createForm={
                <CreateFormBox
                    onSubmit={async () => {
                        try {
                            await customersApi.create(formData);
                            setFormData({email: '', fullName: '', phone: '', organization: '', password: ''});
                            loadCustomers();
                        } catch {
                            alert('Ошибка создания клиента');
                        }
                    }}
                    onCancel={() => setFormData({email: '', fullName: '', phone: '', organization: '', password: ''})}
                >
                    <input
                        type="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={e => setFormData({...formData, email: e.target.value})}
                        required
                    />
                    <input
                        type="text"
                        placeholder="ФИО"
                        value={formData.fullName}
                        onChange={e => setFormData({...formData, fullName: e.target.value})}
                        required
                    />
                    <input
                        type="tel"
                        placeholder="Телефон"
                        value={formData.phone}
                        onChange={e => setFormData({...formData, phone: e.target.value})}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Организация"
                        value={formData.organization}
                        onChange={e => setFormData({...formData, organization: e.target.value})}
                        required
                    />
                    <input
                        type="password"
                        placeholder="Пароль (опционально)"
                        value={formData.password}
                        onChange={e => setFormData({...formData, password: e.target.value})}
                    />
                </CreateFormBox>
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
                                onChange={(e) => setFilters(f => ({...f, q: e.target.value}))}
                            />
                            <IconButton label="Экспорт CSV" onClick={exportCsv}>
                                <Download size={18} />
                            </IconButton>
                        </>
                    }
                    groups={[
                        <FilterGroup key="status" title="Статус">
                            <Chip selected={filters.status === 'all'}
                                  onClick={() => setFilters({...filters, status: 'all'})}>Все</Chip>
                            <Chip selected={filters.status === 'active'}
                                  onClick={() => setFilters({...filters, status: 'active'})}>Активные</Chip>
                            <Chip selected={filters.status === 'inactive'}
                                  onClick={() => setFilters({...filters, status: 'inactive'})}>Неактивные</Chip>
                        </FilterGroup>,
                    ]}
                />
            }
        >
            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <p>Загрузка…</p>
            ) : (
                <DataTable<CustomerDto>
                    rows={filtered}
                    columns={columns}
                    rowKey={(r) => r.id}
                    selectable
                    selectedKeys={selected}
                    onToggleRow={toggleRow}
                    onToggleAll={toggleAll}
                    onPatch={patchCustomer}
                    hideSearch={true}              // поиск у нас справа в FiltersBar
                    page={page}
                    totalPages={totalPages}
                    onPageChange={setPage}
                />
            )}
        </EntityIndexLayout>
    );
};

export default ClientsPage;
