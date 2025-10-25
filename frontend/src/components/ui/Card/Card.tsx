import React from 'react';
import cn from 'clsx';
import './Card.style.scss';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
    padded?: boolean;        // внутренние отступы
    hoverable?: boolean;     // подчёркнутый hover (тень/подсветка)
    clickable?: boolean;     // меняет курсор и лёгкое смещение по клику
    elevation?: 'sm' | 'md'; // сила тени
}

const Card: React.FC<CardProps> = ({
                                       children,
                                       className,
                                       padded = true,
                                       hoverable = false,
                                       clickable = false,
                                       elevation = 'md',
                                       ...rest
                                   }) => {
    return (
        <div
            className={cn(
                'card',
                `card--${elevation}`,
                {'card--padded': padded, 'card--hoverable': hoverable, 'card--clickable': clickable},
                className
            )}
            {...rest}
        >
            {children}
        </div>
    );
};

export default Card;
