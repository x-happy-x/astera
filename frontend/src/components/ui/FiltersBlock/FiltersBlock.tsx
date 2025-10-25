import React, { useMemo, useState } from 'react';
import cn from 'clsx';
import Chip from '../Chip/Chip';
import './FiltersBlock.style.scss';

export type FilterItem = { id: string; label: string; count?: number };
export type Section = {
    id: string;
    title?: string;
    items: FilterItem[];
    maxVisible?: number;        // сколько чипов показывать до "Ещё"
    collapsible?: boolean;      // показывать переключатель Ещё/Скрыть
};

export interface FiltersBlockProps {
    sections: Section[];
    value?: Record<string, string[]>;           // { sectionId: ['id1','id2'] }
    onChange?: (next: Record<string, string[]>) => void;
    className?: string;
    chipVariant?: 'neutral' | 'primary' | 'accent';
    chipSize?: 'sm' | 'md' | 'lg';
}

const FiltersBlock: React.FC<FiltersBlockProps> = ({
                                                       sections,
                                                       value = {},
                                                       onChange,
                                                       className,
                                                       chipVariant = 'neutral',
                                                       chipSize = 'md',
                                                   }) => {
    const [expanded, setExpanded] = useState<Record<string, boolean>>({});

    const toggle = (secId: string, itemId: string) => {
        const curr = new Set(value[secId] ?? []);
        curr.has(itemId) ? curr.delete(itemId) : curr.add(itemId);
        const next = { ...value, [secId]: Array.from(curr) };
        onChange?.(next);
    };

    const isSelected = (secId: string, itemId: string) =>
        (value[secId] ?? []).includes(itemId);

    return (
        <div className={cn('filters', className)}>
            {sections.map((sec) => {
                const open = expanded[sec.id] ?? false;
                const maxVisible = sec.maxVisible ?? 12;
                const showToggle = sec.collapsible ?? (sec.items.length > maxVisible);
                const items = useMemo(
                    () => (open ? sec.items : sec.items.slice(0, maxVisible)),
                    [open, sec.items, maxVisible]
                );

                return (
                    <div className="filters__section" key={sec.id}>
                        {sec.title && <div className="filters__label">{sec.title}</div>}

                        <div className="filters__chips">
                            {items.map((it) => (
                                <Chip
                                    key={it.id}
                                    size={chipSize}
                                    variant={chipVariant}
                                    selected={isSelected(sec.id, it.id)}
                                    onClick={() => toggle(sec.id, it.id)}
                                    trailing={it.count ? <span className="filters__count">{it.count}</span> : undefined}
                                >
                                    {it.label}
                                </Chip>
                            ))}

                            {showToggle && (
                                <button
                                    type="button"
                                    className="filters__more"
                                    onClick={() => setExpanded((s) => ({ ...s, [sec.id]: !open }))}
                                >
                                    {open ? 'Скрыть' : `Ещё (${sec.items.length - Math.min(maxVisible, sec.items.length)})`}
                                    <span className={cn('caret', { up: open })}>▾</span>
                                </button>
                            )}
                        </div>
                    </div>
                );
            })}
        </div>
    );
};

export default FiltersBlock;
