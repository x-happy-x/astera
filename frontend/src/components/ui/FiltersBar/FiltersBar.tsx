import React from 'react';
import cn from 'clsx';
import './FiltersBar.style.scss';

export interface FilterGroupProps {
    title: React.ReactNode;
    children: React.ReactNode; // чипсы/контролы
}

export const FilterGroup: React.FC<FilterGroupProps> = ({ title, children }) => (
    <div className="fb__group">
        <div className="fb__group-title">{title}</div>
        <div className="fb__group-body">
            {children}
        </div>
    </div>
);

export interface FiltersBarProps {
    title?: React.ReactNode;          // общий заголовок «Фильтры»
    groups: React.ReactNode[];        // массив <FilterGroup/>
    right?: React.ReactNode;          // справа: поиск + иконки (экспорт)
    className?: string;
}

const FiltersBar: React.FC<FiltersBarProps> = ({ title = 'Фильтры', groups, right, className }) => {
    return (
        <div className={cn('fb', className)}>
            <div className="fb__header">
                <div className="fb__title">{title}</div>
                <div className="fb__right">{right}</div>
            </div>

            <div className="fb__groups">
                {groups.map((g, i) => <div key={i} className="fb__group-wrap">{g}</div>)}
            </div>
        </div>
    );
};

export default FiltersBar;
