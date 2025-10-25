import React, { useEffect, useMemo, useState } from 'react';
import { Download, Trash2 } from 'lucide-react';
import EntityIndexLayout from '../../../components/ui/EntityIndexLayout/EntityIndexLayout';
import FiltersBar, { FilterGroup } from '../../../components/ui/FiltersBar/FiltersBar';
import CreateFormBox from '../../../components/ui/CreateFormBox/CreateFormBox';
import IconButton from '../../../components/ui/IconButton/IconButton';
import DataTable, { type Column } from '../../../components/ui/DataTable/DataTable';
import Chip from '../../../components/ui/Chip/Chip';
import {
    type UserCreateDto,
    type UserDto,
    type UserUpdateDto,
    usersApi,
    UserRole,
} from '../../../api';
import './styles.scss';

const roleLabels: Record<string, string> = {
    admin: 'Администратор',
    manager: 'Менеджер',
    customer: 'Клиент',
};

const ManagersPage: React.FC = () => {
    const [users, setUsers] = useState<UserDto[]>([]);
    const [totalUsers, setTotalUsers] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selected, setSelected] = useState<Set<string>>(new Set());

    // фильтры + поиск (поиск справа над таблицей)
    const [filters, setFilters] = useState<{ role: 'all' | 'admin' | 'manager'; q: string }>({
        role: 'all',
        q: '',
    });

    // форма создания — всегда видима, кнопки ✓/✕
    const [formData, setFormData] = useState<UserCreateDto>({
        email: '',
        fullName: '',
        role: 'manager',
        password: '',
        isActive: true,
    });

    const loadUsers = async () => {
        try {
            setLoading(true);
            const roleFilter =
                filters.role === 'all' ? (['admin', 'manager'] as typeof UserRole[keyof typeof UserRole][]) : [filters.role];
            const resp = await usersApi.list(page, 20, roleFilter);
            setUsers(resp.users);
            setTotalUsers(resp.totalUsers);
            setTotalPages(resp.totalPages);
            setError(null);
        } catch (e) {
            setError('Ошибка загрузки пользователей');
            console.error(e);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { loadUsers(); }, [page, filters.role]);

    const filtered = useMemo(() => {
        const q = filters.q.trim().toLowerCase();
        if (!q) return users;
        return users.filter(u =>
            [u.fullName, u.email, u.role, u.createdAt].filter(Boolean).join(' ').toLowerCase().includes(q)
        );
    }, [users, filters.q]);

    // ПОЛНЫЙ PUT
    const toUpdateDto = (u: UserDto): UserUpdateDto => ({
        email: u.email,
        fullName: u.fullName,
        role: u.role,
        isActive: u.isActive ?? false,
    });

    const applyLocal = (id: string, patch: Partial<UserDto>) =>
        setUsers(prev => prev.map(x => (x.id === id ? { ...x, ...patch } : x)));

    const patchUser = async (id: string, patch: Partial<UserDto>) => {
        const original = users.find(u => u.id === id);
        if (!original) return;

        // оптимистично
        applyLocal(id, patch);

        const dto: UserUpdateDto = toUpdateDto({ ...original, ...patch });
        try {
            await usersApi.update(id, dto);
        } catch (e) {
            applyLocal(id, original); // откат
            console.error(e);
            alert('Не удалось сохранить изменения');
        }
    };

    const handleDelete = async (id: string) => {
        if (!confirm('Удалить пользователя?')) return;
        try {
            await usersApi.delete(id);
            loadUsers();
        } catch (e) {
            setError('Ошибка удаления пользователя');
            console.error(e);
        }
    };

    // const toggleRow = (key: string, checked: boolean) =>
    //     setSelected(prev => {
    //         const next = new Set(prev);
    //         checked ? next.add(key) : next.delete(key);
    //         return next;
    //     });
    // const toggleAll = (checked: boolean, keys: string[]) => setSelected(checked ? new Set(keys) : new Set());

    const columns: Column<UserDto>[] = [
        { id: 'name', header: 'ФИО', field: 'fullName', editable: { type: 'text', placeholder: 'ФИО' } },
        { id: 'email', header: 'Email', field: 'email', editable: { type: 'text' } },
        {
            id: 'role',
            header: 'Роль',
            field: 'role',
            accessor: r => roleLabels[r.role] || r.role,
            editable: {
                type: 'select',
                options: [
                    { value: 'admin', label: 'Администратор' },
                    { value: 'manager', label: 'Менеджер' },
                ],
            },
        },
        {
            id: 'active',
            header: 'Статус',
            field: 'isActive',
            align: 'center',
            editable: { type: 'toggle', labels: { on: 'Активен', off: 'Неактивен' } }, // один клик по чипсу
        },
        { id: 'created', header: 'Дата создания', accessor: r => new Date(r.createdAt).toLocaleDateString(), align: 'right' },
        {
            id: 'actions',
            header: 'Действия',
            align: 'right',
            accessor: r => (
                <IconButton label="Удалить" variant="outline" onClick={() => handleDelete(r.id)}>
                    <Trash2 size={18} />
                </IconButton>
            ),
        },
    ];

    // экспорт CSV (по текущей локальной выборке)
    const exportCsv = () => {
        const headers = ['ФИО', 'Email', 'Роль', 'Статус', 'Дата создания'];
        const rows = filtered.map(u => [
            u.fullName,
            u.email,
            roleLabels[u.role] || u.role,
            u.isActive ? 'Активен' : 'Неактивен',
            new Date(u.createdAt).toLocaleDateString(),
        ]);
        const csv = [headers.join(','), ...rows.map(r => r.map(c => `"${c}"`).join(','))].join('\n');
        const blob = new Blob(['\ufeff' + csv], { type: 'text/csv;charset=utf-8;' });
        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = `users_${new Date().toISOString().split('T')[0]}.csv`;
        a.click();
    };

    return (
        <EntityIndexLayout
            title="Управление менеджерами и администраторами"
            subtitle={`Всего: ${totalUsers}`}
            createForm={
                <CreateFormBox
                    onSubmit={async () => {
                        try {
                            await usersApi.create(formData);
                            setFormData({ email: '', fullName: '', role: 'manager', password: '', isActive: true });
                            // перезагрузим текущую страницу
                            loadUsers();
                        } catch (e) {
                            alert('Ошибка создания пользователя');
                            console.error(e);
                        }
                    }}
                    onCancel={() => setFormData({ email: '', fullName: '', role: 'manager', password: '', isActive: true })}
                >
                    <input
                        type="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={e => setFormData({ ...formData, email: e.target.value })}
                        required
                    />
                    <input
                        type="text"
                        placeholder="ФИО"
                        value={formData.fullName}
                        onChange={e => setFormData({ ...formData, fullName: e.target.value })}
                        required
                    />
                    <select
                        value={formData.role}
                        onChange={e => setFormData({ ...formData, role: e.target.value as typeof UserRole[keyof typeof UserRole] })}
                        required
                    >
                        <option value="manager">Менеджер</option>
                        <option value="admin">Администратор</option>
                    </select>
                    <input
                        type="password"
                        placeholder="Пароль"
                        value={formData.password}
                        onChange={e => setFormData({ ...formData, password: e.target.value })}
                        required
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
                                onChange={(e) => setFilters(f => ({ ...f, q: e.target.value }))}
                            />
                            <IconButton label="Экспорт CSV" onClick={exportCsv}>
                                <Download size={18} />
                            </IconButton>
                        </>
                    }
                    groups={[
                        <FilterGroup key="role" title="Роль">
                            <Chip selected={filters.role === 'all'} onClick={() => { setFilters(f => ({ ...f, role: 'all' })); setPage(0); }}>
                                Все
                            </Chip>
                            <Chip selected={filters.role === 'admin'} onClick={() => { setFilters(f => ({ ...f, role: 'admin' })); setPage(0); }}>
                                Администраторы
                            </Chip>
                            <Chip selected={filters.role === 'manager'} onClick={() => { setFilters(f => ({ ...f, role: 'manager' })); setPage(0); }}>
                                Менеджеры
                            </Chip>
                        </FilterGroup>,
                    ]}
                />
            }
        >
            {error && <div className="error-message">{error}</div>}

            {loading ? (
                <p>Загрузка…</p>
            ) : (
                <DataTable<UserDto>
                    rows={filtered}
                    columns={columns}
                    rowKey={r => r.id}
                    selectable
                    selectedKeys={selected}
                    onToggleRow={(k, ch) => setSelected(s => {
                        const next = new Set(s);
                        ch ? next.add(k) : next.delete(k);
                        return next;
                    })}
                    onToggleAll={(ch, keys) => setSelected(ch ? new Set(keys) : new Set())}
                    onPatch={patchUser}           // полный PUT собран выше
                    hideSearch={true}             // поиск вынесен в FiltersBar
                    page={page}
                    totalPages={totalPages}
                    onPageChange={setPage}
                />
            )}
        </EntityIndexLayout>
    );
};

export default ManagersPage;
