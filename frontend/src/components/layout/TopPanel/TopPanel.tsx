import React from 'react';
import type {User} from '../../../types/auth';
import './styles.scss';

interface TopPanelProps {
    currentPageName: string;
    user: User;
    onToggleSidebar: () => void;
}

const TopPanel: React.FC<TopPanelProps> = ({currentPageName, user, onToggleSidebar}) => {
    const getRoleDisplayName = (role: string) => (role === 'admin' ? 'Администратор' : 'Менеджер');

    const getInitials = (name?: string) => {
        if (!name) return 'U';
        const parts = name.trim().split(/\s+/);
        const first = parts[0]?.[0] ?? '';
        const last = parts[1]?.[0] ?? '';
        return (first + last).toUpperCase();
    };

    return (
        <header className="top-panel" role="banner">
            <div className="top-panel__inner">
                <div className="top-panel__left">
                    <button
                        onClick={onToggleSidebar}
                        className="top-panel__toggle"
                        aria-label="Переключить боковую панель"
                        title="Меню"
                    >
                        <svg width="22" height="22" viewBox="0 0 24 24" aria-hidden="true">
                            <path d="M3 6h18M3 12h18M3 18h18" fill="none" stroke="currentColor" strokeWidth="2"
                                  strokeLinecap="round"/>
                        </svg>
                    </button>

                    <h1 className="top-panel__title" title={currentPageName}>
                        {currentPageName}
                    </h1>
                </div>

                <div className="top-panel__right">
          <span className="top-panel__role" aria-label={`Роль: ${getRoleDisplayName(user.role)}`}>
            <span className="dot" aria-hidden="true"/>
              {getRoleDisplayName(user.role)}
          </span>

                    <div className="top-panel__avatar" aria-label={user.fullName ?? 'Пользователь'}>
                        {getInitials(user.fullName)}
                    </div>
                </div>
            </div>
        </header>
    );
};

export default TopPanel;
