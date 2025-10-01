import React from 'react';
import {NavLink} from 'react-router-dom';
import type {User} from '../../../types/auth';
import './styles.scss';

interface SidebarProps {
    isOpen: boolean;         // true — полная ширина; false — мини (иконки)
    user: User;
    onLogout: () => void;
    onCloseMobile?: () => void; // опционально: закрыть на мобильном при клике
}

type IconName = 'home' | 'clients' | 'equipment' | 'requests' | 'managers' | 'logout';

const Icon: React.FC<{
    name: IconName;
    className?: string;
    'aria-hidden'?: boolean
}> = ({
          name,
          className,
          ...rest
      }) => {
    const common = {
        width: 22,
        height: 22,
        viewBox: '0 0 24 24',
        fill: 'none',
        stroke: 'currentColor',
        strokeWidth: 2,
        strokeLinecap: 'round',
        strokeLinejoin: 'round'
    } as const;

    const paths: Record<IconName, React.ReactNode> = {
        home: (<path d="M3 11l9-8 9 8v8a2 2 0 0 1-2 2h-3a2 2 0 0 1-2-2v-3H10v3a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>),
        clients: (<>
            <path d="M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
        </>),
        equipment: (<>
            <circle cx="12" cy="12" r="3"/>
            <path
                d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 1 1-4 0v-.09A1.65 1.65 0 0 0 8 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 3.6 15 1.65 1.65 0 0 0 2 14H2a2 2 0 1 1 0-4h.09A1.65 1.65 0 0 0 3.6 8 1.65 1.65 0 0 0 3.27 6.18l-.06-.06A2 2 0 1 1 6.04 3.3l.06.06A1.65 1.65 0 0 0 8 3.6 1.65 1.65 0 0 0 9 2.09V2a2 2 0 1 1 4 0v.09A1.65 1.65 0 0 0 15 3.6c.38 0 .74-.14 1.02-.38l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 20.4 9c0 .38.14.74.38 1.02l.06.06A2 2 0 1 1 21 14h-.09a1.65 1.65 0 0 0-1.51 1z"/>
        </>),
        requests: (<>
            <rect x="3" y="4" width="18" height="16" rx="2"/>
            <path d="M7 8h10M7 12h10M7 16h7"/>
        </>),
        managers: (<>
            <path d="M12 12a5 5 0 1 0-5-5 5 5 0 0 0 5 5z"/>
            <path d="M20 21a8 8 0 1 0-16 0"/>
        </>),
        logout: (<>
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <path d="M16 17l5-5-5-5"/>
            <path d="M21 12H9"/>
        </>),
    };

    return (
        <svg className={className} {...common} {...rest}>
            {paths[name]}
        </svg>
    );
};

const Sidebar: React.FC<SidebarProps> = ({isOpen, user, onLogout, onCloseMobile}) => {

    const navigation = [
        {name: 'Главная', path: '/admin/dashboard', icon: 'home' as IconName},
        {name: 'Клиенты', path: '/admin/clients', icon: 'clients' as IconName},
        {name: 'Оборудование', path: '/admin/equipment', icon: 'equipment' as IconName},
        {name: 'Заявки', path: '/admin/requests', icon: 'requests' as IconName},
        ...(user.role === 'admin' ? [{name: 'Менеджеры', path: '/admin/managers', icon: 'managers' as IconName}] : []),
    ];

    return (
        <>
            {/* Мобильный полупрозрачный оверлей */}
            <div
                className={`sidebar-overlay ${isOpen ? 'show' : ''}`}
                onClick={onCloseMobile}
                aria-hidden={!isOpen}
            />
            <aside className={`sidebar ${isOpen ? '' : 'collapsed'}`} aria-label="Основная навигация">
                <div className="sidebar__header">
                    <div className="sidebar__brand">
                        <div className="logo">A</div>
                        <div className="brand-meta">
                            <h3 className="brand-title" title="Astera MVP">Astera MVP</h3>
                            <p className="user-name" title={user.fullName}>{user.fullName}</p>
                        </div>
                    </div>
                </div>

                <nav className="sidebar__nav">
                    <ul>
                        {navigation.map(item => {
                            return (
                                <li key={item.path}>
                                    <NavLink
                                        to={item.path}
                                        className={({isActive}) => `nav-link ${isActive ? 'active' : ''}`}
                                        title={!isOpen ? item.name : undefined}
                                        onClick={onCloseMobile}
                                    >
                                        <Icon name={item.icon} className="nav-icon" aria-hidden/>
                                        <span className="label">{item.name}</span>
                                    </NavLink>
                                </li>
                            );
                        })}

                        <li className="logout">
                            <button
                                onClick={onLogout}
                                className="nav-link danger"
                                title={!isOpen ? 'Выйти' : undefined}
                                aria-label="Выйти"
                            >
                                <Icon name="logout" className="nav-icon" aria-hidden/>
                                <span className="label">Выйти</span>
                            </button>
                        </li>
                    </ul>
                </nav>
            </aside>
        </>
    );
};

export default Sidebar;
