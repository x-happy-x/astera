import React from 'react';
import cn from 'clsx';
import './HeaderBlock.style.scss';

export interface HeaderBlockProps {
    title?: React.ReactNode;
    subtitle?: React.ReactNode;
    actions?: React.ReactNode;
    leading?: React.ReactNode;
    align?: 'start' | 'center' | 'end';
    dense?: boolean;
    className?: string;
}

const HeaderBlock: React.FC<HeaderBlockProps> = ({
                                                     title,
                                                     subtitle,
                                                     actions,
                                                     leading,
                                                     align = 'start',
                                                     dense = false,
                                                     className,
                                                 }) => {
    return (
        <div className={cn('hb', className, {'hb--dense': dense, [`hb--a-${align}`]: align})}>
            <div className="hb__left">
                {leading && <div className="hb__leading">{leading}</div>}
                <div className="hb__text">
                    {title && <h2 className="hb__title">{title}</h2>}
                    {subtitle && <div className="hb__subtitle">{subtitle}</div>}
                </div>
            </div>

            {actions && <div className="hb__actions">{actions}</div>}
        </div>
    );
};

export default HeaderBlock;
