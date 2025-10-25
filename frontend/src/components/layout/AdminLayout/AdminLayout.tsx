import React from 'react';
import {useLocation, useNavigate} from 'react-router-dom';
import {useAuth} from '../../../hooks/useAuth';

import Sidebar from '../../ui/Sidebar/Sidebar';
import TopPanel from '../../ui/TopPanel/TopPanel';

import {Boxes, FileText, LogOut, Menu, Shield, Users} from 'lucide-react';
import './styles.scss';

const MOBILE_BP = 768;

interface AdminLayoutProps {
    children: React.ReactNode;
}

const AdminLayout: React.FC<AdminLayoutProps> = ({children}) => {
    const {state, logout} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const [open, setOpen] = React.useState<boolean>(window.innerWidth >= MOBILE_BP);
    const [isMobile, setIsMobile] = React.useState<boolean>(window.innerWidth < MOBILE_BP);

    React.useEffect(() => {
        const onResize = () => {
            const mobile = window.innerWidth < MOBILE_BP;
            setIsMobile(mobile);
            setOpen(!mobile);
        };
        window.addEventListener('resize', onResize);
        return () => window.removeEventListener('resize', onResize);
    }, []);

    if (!state.user) return null;

    const nav = [
        {id: 'requests', name: 'Заявки', path: '/admin/requests', icon: <FileText size={18}/>},
        {id: 'clients', name: 'Клиенты', path: '/admin/clients', icon: <Users size={18}/>},
        {id: 'equipment', name: 'Оборудование', path: '/admin/equipment', icon: <Boxes size={18}/>},
        ...(state.user.role === 'admin'
            ? [{id: 'managers', name: 'Менеджеры', path: '/admin/managers', icon: <Shield size={18}/>}]
            : []),
    ];

    const current = nav.find(n => n.path === location.pathname);
    const title = current?.name ?? 'Административная панель';

    const goTo = (path: string) => {
        navigate(path);
        if (isMobile) setOpen(false);
    };

    return (
        <div className="admin-shell">
            {/* SIDEBAR */}
            <Sidebar open={open} onClose={() => setOpen(false)} width={280}>
                <Sidebar.Header title="Админ-панель" subtitle={state.user.email}
                                onClose={isMobile ? () => setOpen(false) : undefined}/>

                <Sidebar.Section>
                    {nav.map(item => (
                        <Sidebar.Item
                            key={item.id}
                            icon={item.icon}
                            active={location.pathname === item.path}
                            onClick={() => goTo(item.path)}
                        >
                            {item.name}
                        </Sidebar.Item>
                    ))}
                </Sidebar.Section>

                <Sidebar.Section position="bottom">
                    <Sidebar.Item
                        icon={<LogOut size={18}/>}
                        onClick={() => {
                            logout();
                        }}
                    >
                        Выйти
                    </Sidebar.Item>
                    <Sidebar.Footer>
                        <small style={{color: 'var(--color-muted)'}}>
                            {state.user.role === 'admin' ? 'Администратор' : 'Менеджер'} • v1.0.0
                        </small>
                    </Sidebar.Footer>
                </Sidebar.Section>
            </Sidebar>

            {/* MAIN */}
            <main className="admin-main">
                <TopPanel
                    positioning="sticky"
                    shadow
                    left={
                        <>
                            <button aria-label="Меню" className="icon-btn" onClick={() => setOpen(o => !o)}>
                                <Menu size={20}/>
                            </button>
                            <span className="tp__muted">АСТЕРА · Личный кабинет менеджера</span>
                        </>
                    }
                    center={<h1 className="tp__title">{title}</h1>}
                />

                <div className="admin-content">
                    {children}
                </div>
            </main>
        </div>
    );
};

export default AdminLayout;
