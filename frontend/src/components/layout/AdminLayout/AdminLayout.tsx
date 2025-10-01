import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '../../../hooks/useAuth';
import Sidebar from '../Sidebar';
import TopPanel from '../TopPanel';
import './styles.scss';

const MOBILE_BP = 768;

interface AdminLayoutProps {
    children: React.ReactNode;
}

const AdminLayout: React.FC<AdminLayoutProps> = ({ children }) => {
    const { state, logout } = useAuth();
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const [isMobile, setIsMobile] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const handleResize = () => {
            const mobile = window.innerWidth < MOBILE_BP;
            setIsMobile(mobile);
            setSidebarOpen(!mobile); // на мобилке по умолчанию закрыт, на десктопе открыт
        };
        window.addEventListener('resize', handleResize);
        handleResize();
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    // блокируем скролл боди, когда открыт мобильный сайдбар
    useEffect(() => {
        if (isMobile && sidebarOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = '';
        }
        return () => { document.body.style.overflow = ''; };
    }, [isMobile, sidebarOpen]);

    if (!state.user) return null;

    const navigation = [
        { name: 'Главная', path: '/admin/dashboard' },
        { name: 'Клиенты', path: '/admin/clients' },
        { name: 'Оборудование', path: '/admin/equipment' },
        { name: 'Заявки', path: '/admin/requests' },
        ...(state.user.role === 'admin' ? [{ name: 'Менеджеры', path: '/admin/managers' }] : []),
    ];

    const currentPage = navigation.find(item => item.path === location.pathname);
    const currentPageName = currentPage?.name || 'Административная панель';

    const handleToggleSidebar = () => setSidebarOpen(prev => !prev);

    return (
        <div className="admin-layout">
            <Sidebar
                isOpen={sidebarOpen}
                isMobile={isMobile}
                user={state.user}
                onLogout={logout}
            />

            {/* Оверлей ТОЛЬКО на мобилке и только когда открыт сайдбар */}
            {isMobile && sidebarOpen && (
                <div
                    className="admin-backdrop"
                    role="button"
                    aria-label="Закрыть меню"
                    tabIndex={0}
                    onClick={() => setSidebarOpen(false)}
                    onKeyDown={(e) => { if (e.key === 'Escape' || e.key === 'Enter' || e.key === ' ') setSidebarOpen(false); }}
                />
            )}

            <main className="admin-main">
                <TopPanel
                    currentPageName={currentPageName}
                    user={state.user}
                    onToggleSidebar={handleToggleSidebar}
                />
                <div className="admin-content">
                    {children}
                </div>
            </main>
        </div>
    );
};

export default AdminLayout;
