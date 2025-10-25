import React, {useEffect, useMemo, useState} from 'react';
import { Download, Trash2 } from 'lucide-react';
import EntityIndexLayout from '../../../components/ui/EntityIndexLayout/EntityIndexLayout';
import FiltersBar, {FilterGroup} from '../../../components/ui/FiltersBar/FiltersBar';
import IconButton from '../../../components/ui/IconButton/IconButton';
import CreateFormBox from '../../../components/ui/CreateFormBox/CreateFormBox';
import DataTable, {type Column} from '../../../components/ui/DataTable/DataTable';
import Chip from '../../../components/ui/Chip/Chip';
import {
    equipmentApi,
    EquipmentCategory,
    type EquipmentCreateDto,
    type EquipmentDto,
    type EquipmentUpdateDto,
} from '../../../api';

const categoryLabels: Record<EquipmentCategory, string> = {
    boiler: 'Котёл',
    burner: 'Горелка',
    pump: 'Насос',
    valve: 'Клапан',
    flowmeter: 'Расходомер',
    automation: 'Автоматика',
};
const categories: EquipmentCategory[] = ['boiler', 'burner', 'pump', 'valve', 'flowmeter', 'automation'];

const EquipmentPage: React.FC = () => {
    const [equipment, setEquipment] = useState<EquipmentDto[]>([]);
    const [totalItems, setTotalItems] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selected, setSelected] = useState<Set<string>>(new Set());

    const [filters, setFilters] = useState<{
        category: EquipmentCategory | 'all';
        active: 'all' | 'active' | 'inactive';
        q: string
    }>({
        category: 'all', active: 'all', q: ''
    });

    const [formData, setFormData] = useState<EquipmentCreateDto>({
        category: 'boiler', brand: '', model: '', active: true, price: 0, deliveryDays: 0,
        powerMinKw: undefined, powerMaxKw: undefined, dnSize: undefined,
    });

    const loadEquipment = async () => {
        try {
            setLoading(true);
            const resp = await equipmentApi.list(page, 20);
            setEquipment(resp.equipment);
            setTotalItems(resp.totalEquipment ?? resp.equipment.length);
            setTotalPages(resp.totalPages);
            setError(null);
        } catch (e) {
            setError('Ошибка загрузки оборудования');
        } finally {
            setLoading(false);
        }
    };
    useEffect(() => {
        loadEquipment();
    }, [page]);

    const filtered = useMemo(() => {
        return equipment
            .filter(x => filters.category === 'all' || x.category === filters.category)
            .filter(x => filters.active === 'all' || (filters.active === 'active' ? Boolean(x.active) : !x.active))
            .filter(x => {
                const q = filters.q.trim().toLowerCase();
                if (!q) return true;
                return [x.brand, x.model, categoryLabels[x.category]].join(' ').toLowerCase().includes(q);
            });
    }, [equipment, filters]);

    const toUpdateDto = (e: EquipmentDto): EquipmentUpdateDto => ({
        category: e.category, brand: e.brand, model: e.model, active: Boolean(e.active),
        price: Number(e.price ?? 0), deliveryDays: e.deliveryDays ?? 0,
        powerMinKw: e.powerMinKw, powerMaxKw: e.powerMaxKw, dnSize: e.dnSize,
    });

    const applyLocal = (id: string, patch: Partial<EquipmentDto>) =>
        setEquipment(prev => prev.map(x => x.id === id ? {...x, ...patch} : x));

    const patchEquipment = async (id: string, patch: Partial<EquipmentDto>) => {
        const original = equipment.find(e => e.id === id);
        if (!original) return;
        applyLocal(id, patch);
        const dto = toUpdateDto({...original, ...patch});
        try {
            await equipmentApi.update(id, dto);
        } catch (e) {
            applyLocal(id, original);
            alert('Не удалось сохранить изменения');
        }
    };

    const patchAdapter = async (id: string, patch: Partial<EquipmentDto>) => {
        const num = ['price', 'deliveryDays', 'powerMinKw', 'powerMaxKw', 'dnSize'] as const;
        const fixed: Partial<EquipmentDto> = {...patch};
        for (const k of num) {
            if (k in fixed) {
                const v: any = (fixed as any)[k];
                (fixed as any)[k] = v === '' || v === null ? undefined : Number(v);
            }
        }
        await patchEquipment(id, fixed);
    };

    const handleCreate = async () => {
        try {
            await equipmentApi.create(formData);
            setFormData({category: 'boiler', brand: '', model: '', active: true, price: 0, deliveryDays: 0});
            loadEquipment();
        } catch (e) {
            alert('Ошибка создания');
        }
    };
    const handleCancelCreate = () => {
        setFormData({
            category: 'boiler', brand: '', model: '', active: true, price: 0, deliveryDays: 0,
            powerMinKw: undefined, powerMaxKw: undefined, dnSize: undefined
        });
    };

    const handleDelete = async (id: string) => {
        if (!confirm('Удалить оборудование?')) return;
        try {
            await equipmentApi.delete(id);
            loadEquipment();
        } catch {
            alert('Ошибка удаления');
        }
    };

    const columns: Column<EquipmentDto>[] = [
        {
            id: 'category', header: 'Категория', field: 'category',
            accessor: r => categoryLabels[r.category],
            editable: {type: 'select', options: categories.map(c => ({value: c, label: categoryLabels[c]}))}
        },
        {id: 'brand', header: 'Бренд', field: 'brand', editable: {type: 'text'}},
        {id: 'model', header: 'Модель', field: 'model', editable: {type: 'text'}},
        {
            id: 'active', header: 'Статус', field: 'active', align: 'center',
            editable: {type: 'toggle', labels: {on: 'Активно', off: 'Неактивно'}}
        },
        {
            id: 'power', header: 'Мощность (кВт)', accessor: r => r.powerMinKw && r.powerMaxKw
                ? `${r.powerMinKw}–${r.powerMaxKw}` : r.powerMinKw ?? r.powerMaxKw ?? '—'
        },
        {
            id: 'powerMin',
            header: 'Мин кВт',
            field: 'powerMinKw',
            align: 'right',
            editable: {type: 'text', placeholder: 'мин'}
        },
        {
            id: 'powerMax',
            header: 'Макс кВт',
            field: 'powerMaxKw',
            align: 'right',
            editable: {type: 'text', placeholder: 'макс'}
        },
        {id: 'dn', header: 'DN', field: 'dnSize', align: 'right', editable: {type: 'text', placeholder: 'DN'}},
        {id: 'price', header: 'Цена', field: 'price', align: 'right', editable: {type: 'text', placeholder: '0.00'}},
        {
            id: 'delivery',
            header: 'Доставка (дней)',
            field: 'deliveryDays',
            align: 'center',
            editable: {type: 'text', placeholder: '0'}
        },
        {
            id: 'actions', header: 'Действия', align: 'right', accessor: r =>
                <IconButton label="Удалить" variant="outline" onClick={() => handleDelete(r.id)}>
                    <Trash2 size={18} />
                </IconButton>
        },
    ];

    // Правый блок: поиск + экспорт (иконка)
    const rightControls = (
        <>
            <input
                className="fb__search"
                type="search"
                placeholder="Поиск…"
                value={filters.q}
                onChange={(e) => setFilters(f => ({...f, q: e.target.value}))}
            />
            <IconButton label="Экспорт CSV" onClick={() => {
                const headers = ['Категория', 'Бренд', 'Модель', 'Статус', 'Мощность', 'DN', 'Цена', 'Доставка'];
                const rows = filtered.map((r) => [
                    categoryLabels[r.category],
                    r.brand,
                    r.model,
                    r.active ? 'Активно' : 'Неактивно',
                    r.powerMinKw && r.powerMaxKw ? `${r.powerMinKw}-${r.powerMaxKw}` : r.powerMinKw ?? r.powerMaxKw ?? '—',
                    r.dnSize ?? '—', r.price, r.deliveryDays ?? '—',
                ]);
                const csv = [headers.join(','), ...rows.map(row => row.map(c => `"${c}"`).join(','))].join('\n');
                const blob = new Blob(['\ufeff' + csv], {type: 'text/csv;charset=utf-8;'});
                const a = document.createElement('a');
                a.href = URL.createObjectURL(blob);
                a.download = `equipment_${new Date().toISOString().split('T')[0]}.csv`;
                a.click();
            }}>
                <Download size={18} />
            </IconButton>
        </>
    );

    return (
        <EntityIndexLayout
            title="Управление оборудованием"
            subtitle={`Всего: ${totalItems}`}
            createForm={
                <CreateFormBox onSubmit={handleCreate} onCancel={handleCancelCreate}>
                    <select value={formData.category}
                            onChange={e => setFormData({...formData, category: e.target.value as EquipmentCategory})}>
                        {categories.map(c => <option key={c} value={c}>{categoryLabels[c]}</option>)}
                    </select>
                    <input type="text" placeholder="Бренд" value={formData.brand}
                           onChange={e => setFormData({...formData, brand: e.target.value})}/>
                    <input type="text" placeholder="Модель" value={formData.model}
                           onChange={e => setFormData({...formData, model: e.target.value})}/>
                    <input type="number" step="0.01" placeholder="Мин кВт" value={formData.powerMinKw ?? ''}
                           onChange={e => setFormData({
                               ...formData,
                               powerMinKw: e.target.value ? Number(e.target.value) : undefined
                           })}/>
                    <input type="number" step="0.01" placeholder="Макс кВт" value={formData.powerMaxKw ?? ''}
                           onChange={e => setFormData({
                               ...formData,
                               powerMaxKw: e.target.value ? Number(e.target.value) : undefined
                           })}/>
                    <input type="number" placeholder="DN" value={formData.dnSize ?? ''} onChange={e => setFormData({
                        ...formData,
                        dnSize: e.target.value ? Number(e.target.value) : undefined
                    })}/>
                    <input type="number" step="0.01" placeholder="Цена" value={formData.price}
                           onChange={e => setFormData({...formData, price: Number(e.target.value)})}/>
                    <input type="number" placeholder="Доставка (дней)" value={formData.deliveryDays ?? ''}
                           onChange={e => setFormData({
                               ...formData,
                               deliveryDays: e.target.value ? Number(e.target.value) : undefined
                           })}/>
                </CreateFormBox>
            }
            filters={
                <FiltersBar
                    title="Фильтры"
                    right={rightControls}
                    groups={[
                        <FilterGroup key="cat" title="Категория">
                            <Chip selected={filters.category === 'all'}
                                  onClick={() => setFilters(f => ({...f, category: 'all'}))}>Все</Chip>
                            {categories.map(c => (
                                <Chip key={c} selected={filters.category === c}
                                      onClick={() => setFilters(f => ({...f, category: c}))}>
                                    {categoryLabels[c]}
                                </Chip>
                            ))}
                        </FilterGroup>,
                        <FilterGroup key="active" title="Статус">
                            <Chip selected={filters.active === 'all'}
                                  onClick={() => setFilters(f => ({...f, active: 'all'}))}>Все</Chip>
                            <Chip selected={filters.active === 'active'}
                                  onClick={() => setFilters(f => ({...f, active: 'active'}))}>Активные</Chip>
                            <Chip selected={filters.active === 'inactive'}
                                  onClick={() => setFilters(f => ({...f, active: 'inactive'}))}>Неактивные</Chip>
                        </FilterGroup>,
                    ]}
                />
            }
        >
            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <p>Загрузка…</p>
            ) : (
                <DataTable<EquipmentDto>
                    rows={filtered}
                    columns={columns}
                    rowKey={(r) => r.id}
                    selectable
                    selectedKeys={selected}
                    hideSearch={true}
                    onToggleRow={(k, ch) => setSelected(s => (ch ? new Set(s).add(k) : (s.delete(k), new Set(s))))}
                    onToggleAll={(ch, keys) => setSelected(ch ? new Set(keys) : new Set())}
                    onPatch={patchAdapter}
                    page={page}
                    totalPages={totalPages}
                    onPageChange={setPage}
                />
            )}
        </EntityIndexLayout>
    );
};

export default EquipmentPage;
