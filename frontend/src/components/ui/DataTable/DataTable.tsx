import React, {useMemo, useState} from 'react';
import cn from 'clsx';
import './DataTable.style.scss';
import Chip from '../Chip';

export type Align = 'left' | 'center' | 'right';

export type EditableKind =
    | { type: 'text'; placeholder?: string }
    | { type: 'checkbox' }
    | { type: 'select'; options: Array<{ value: string | number; label: string }> }
    | { type: 'toggle'; labels?: { on: string; off: string } };

export interface Column<T> {
    id: string;
    header: React.ReactNode;         // кастомный заголовок
    accessor?: (row: T) => React.ReactNode; // отрисовка ячейки
    field?: keyof T;                 // если простое поле
    editable?: EditableKind;         // включить инлайн-редактирование
    align?: Align;
    width?: number | string;
}

export interface DataTableProps<T> {
    rows: T[];
    columns: Column<T>[];
    rowKey: (row: T) => string;

    // поиск и фильтры
    searchPlaceholder?: string;
    onSearch?: (q: string) => void;    // если не передан — локальный поиск по строкам (toString)
    toolbar?: React.ReactNode;         // свои элементы в тулбаре (справа от поиска)

    // управление
    selectable?: boolean;              // чекбоксы строк
    hideSearch?: boolean,
    selectedKeys?: Set<string>;
    onToggleRow?: (key: string, checked: boolean) => void;
    onToggleAll?: (checked: boolean, keys: string[]) => void;

    // редактирование
    onPatch?: (key: string, patch: Partial<T>) => Promise<void> | void;

    // пагинация (опционально)
    page?: number;
    pageSize?: number;
    totalPages?: number;
    onPageChange?: (p: number) => void;

    className?: string;
}

function DataTable<T>({
                          rows,
                          columns,
                          rowKey,
                          searchPlaceholder = 'Поиск…',
                          onSearch,
                          toolbar,
                          selectable = true,
                          selectedKeys,
                          onToggleRow,
                          onToggleAll,
                          onPatch,
                          page,
                          totalPages,
                          onPageChange,
                          className,
                          hideSearch = false,
                      }: DataTableProps<T>) {
    const [localQuery, setLocalQuery] = useState('');
    const [editing, setEditing] = useState<{ key: string; colId: string } | null>(null);
    const [pendingPatch, setPendingPatch] = useState<Record<string, unknown>>({});

    // фильтрация для локального поиска (если не передан onSearch)
    const filteredRows = useMemo(() => {
        if (!localQuery || onSearch) return rows;
        const q = localQuery.toLowerCase();
        return rows.filter((r) => JSON.stringify(r).toLowerCase().includes(q));
    }, [rows, localQuery, onSearch]);

    const allKeys = filteredRows.map(rowKey);
    const allChecked = selectedKeys ? allKeys.every(k => selectedKeys.has(k)) && allKeys.length > 0 : false;

    const startEdit = (key: string, colId: string, initialValue: unknown) => {
        setEditing({key, colId});
        setPendingPatch({value: initialValue});
    };

    const commitEdit = async (key: string, col: Column<T>) => {
        if (!onPatch) {
            setEditing(null);
            return;
        }
        const value = pendingPatch.value;
        const patch: Partial<T> = {};
        if (col.field) {
            (patch as Record<string, unknown>)[col.field as string] = value;
        }
        setEditing(null);
        setPendingPatch({});
        await onPatch(key, patch);
    };

    const cancelEdit = () => {
        setEditing(null);
        setPendingPatch({});
    };

    const renderCell = (row: T, col: Column<T>) => {
        const key = rowKey(row);
        const isEditing = editing && editing.key === key && editing.colId === col.id;
        const value = col.field ? (row as Record<string, unknown>)[col.field as string] : undefined;

        // если editable + режим редактирования
        if (col.editable) {
            const kind = col.editable;

            if (isEditing) {
                if (kind.type === 'text') {
                    return (
                        <div className="dt__edit">
                            <input
                                className="dt__input"
                                type="text"
                                autoFocus
                                defaultValue={String(value ?? '')}
                                placeholder={kind.placeholder}
                                onChange={(e) => setPendingPatch({value: e.target.value})}
                                onKeyDown={async (e) => {
                                    if (e.key === 'Enter') await commitEdit(key, col);
                                    if (e.key === 'Escape') cancelEdit();
                                }}
                            />
                            <div className="dt__edit-actions">
                                <button className="dt__iconbtn ok" title="Сохранить"
                                        onClick={() => commitEdit(key, col)} aria-label="Сохранить">
                                    ✓
                                </button>
                                <button className="dt__iconbtn cancel" title="Отмена" onClick={cancelEdit}
                                        aria-label="Отмена">
                                    ✕
                                </button>
                            </div>
                        </div>
                    );
                }
                if (kind.type === 'checkbox') {
                    return (
                        <label className="dt__checkbox">
                            <input
                                type="checkbox"
                                defaultChecked={!!value}
                                onChange={async (e) => {
                                    setPendingPatch({value: e.target.checked});
                                    await commitEdit(key, col);
                                }}
                            />
                            <span/>
                        </label>
                    );
                }
                if (kind.type === 'select') {
                    return (
                        <select
                            className="dt__select"
                            autoFocus
                            defaultValue={String(value ?? '')}
                            onChange={(e) => setPendingPatch({value: e.target.value})}
                            onBlur={() => commitEdit(key, col)}
                        >
                            {kind.options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
                        </select>
                    );
                }
            }
            if (kind.type === 'toggle') {
                const current = Boolean(value);
                const labels = kind.labels ?? {on: 'Активен', off: 'Неактивен'};

                return (
                    <Chip
                        size="sm"
                        variant={current ? 'primary' : 'neutral'}
                        selected={current}
                        onClick={async () => {
                            if (onPatch && col.field) {
                                const next = !current;
                                await onPatch(key, {[col.field]: next} as Partial<T>);
                            }
                        }}
                    >
                        {current ? labels.on : labels.off}
                    </Chip>
                );
            }
        }

        // обычная ячейка
        if (col.accessor) return col.accessor(row);
        if (col.field) return (row as Record<string, unknown>)[col.field as string] as React.ReactNode;
        return null;
    };

    return (
        <div className={cn('dt', className)}>
            <div className="dt__toolbar">
                {!hideSearch && !onSearch && (
                    <input
                        className="dt__search"
                        type="search"
                        placeholder={searchPlaceholder}
                        value={localQuery}
                        onChange={(e) => setLocalQuery(e.target.value)}
                    />
                )}
                {!hideSearch && onSearch && (
                    <input
                        className="dt__search"
                        type="search"
                        placeholder={searchPlaceholder}
                        onChange={(e) => onSearch(e.target.value)}
                    />
                )}
                <div className="dt__tools">{toolbar}</div>
            </div>

            <div className="dt__scroll">
                <table className="dt__table">
                    <thead>
                    <tr>
                        {selectable && (
                            <th className="dt__th chk">
                                <label className="dt__checkbox">
                                    <input
                                        type="checkbox"
                                        checked={allChecked}
                                        onChange={(e) => onToggleAll?.(e.target.checked, allKeys)}
                                    />
                                    <span/>
                                </label>
                            </th>
                        )}
                        {columns.map((c) => (
                            <th key={c.id} className={cn('dt__th', c.align)} style={{width: c.width}}>
                                {c.header}
                            </th>
                        ))}
                    </tr>
                    </thead>
                    <tbody>
                    {filteredRows.map((row) => {
                        const key = rowKey(row);
                        const checked = selectedKeys?.has(key) ?? false;

                        return (
                            <tr key={key} className={cn({selected: checked})}>
                                {selectable && (
                                    <td className="dt__td chk">
                                        <label className="dt__checkbox">
                                            <input
                                                type="checkbox"
                                                checked={checked}
                                                onChange={(e) => onToggleRow?.(key, e.target.checked)}
                                            />
                                            <span/>
                                        </label>
                                    </td>
                                )}

                                {columns.map((c) => {
                                    const canEdit = !!c.editable && onPatch && !editing;

                                    return (
                                        <td
                                            key={c.id}
                                            className={cn('dt__td', c.align, {editable: canEdit})}
                                            onDoubleClick={() => {
                                                if (c.editable && c.editable.type !== 'toggle') {
                                                    startEdit(rowKey(row), c.id, c.field ? (row as Record<string, unknown>)[c.field as string] : undefined);
                                                }
                                            }}
                                        >
                                            {renderCell(row, c)}
                                        </td>
                                    );
                                })}
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>

            {typeof page === 'number' && typeof totalPages === 'number' && (
                <div className="dt__pagination">
                    <button
                        className="pg__btn"
                        disabled={page <= 0}
                        onClick={() => onPageChange?.(page - 1)}
                    >
                        ← Назад
                    </button>
                    <span className="pg__info">Стр. {page + 1} из {totalPages}</span>
                    <button
                        className="pg__btn"
                        disabled={page >= (totalPages - 1)}
                        onClick={() => onPageChange?.(page + 1)}
                    >
                        Вперёд →
                    </button>
                </div>
            )}
        </div>
    );
}

export default DataTable;
