import React from 'react';
import cn from 'clsx';
import HeaderBlock from '../../ui/HeaderBlock/HeaderBlock';
import './EntityIndexLayout.style.scss';

export interface EntityIndexLayoutProps {
    title: React.ReactNode;
    subtitle?: React.ReactNode;
    createForm: React.ReactNode;
    filters: React.ReactNode;
    children: React.ReactNode;
    className?: string;
}

const EntityIndexLayout: React.FC<EntityIndexLayoutProps> = ({
                                                                 title,
                                                                 subtitle,
                                                                 createForm,
                                                                 filters,
                                                                 children,
                                                                 className,
                                                             }) => {
    return (
        <div className={cn('entity-index', className)}>
            <HeaderBlock title={title} subtitle={subtitle}/>
            <div className="ei__create">
                {createForm}
            </div>
            <div className="ei__filters">
                {filters}
            </div>
            <div className="ei__content">
                {children}
            </div>
        </div>
    );
};

export default EntityIndexLayout;
