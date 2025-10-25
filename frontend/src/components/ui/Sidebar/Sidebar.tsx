import React, {useEffect, useRef} from 'react';
import cn from 'clsx';
import './Sidebar.style.scss';

type Side = 'left';
type SectionPos = 'top' | 'bottom';

export interface SidebarProps {
    open: boolean;
    onClose: () => void;
    side?: Side;
    width?: number; // px
    children: React.ReactNode;
    className?: string;
    closeOnBackdrop?: boolean;
    closeOnEsc?: boolean;
}

const SidebarContext = React.createContext<object>
({});

const Sidebar: React.FC<SidebarProps> & {
    Header: typeof Header;
    Section: typeof Section;
    Item: typeof Item;
    Separator: typeof Separator;
    Footer: typeof Footer;
} = ({
         open,
         onClose,
         side = 'left',
         width = 320,
         children,
         className,
         closeOnBackdrop = true,
         closeOnEsc = true,
     }) => {
    const backdropRef = useRef<HTMLDivElement>(null);

    // Esc для закрытия
    useEffect(() => {
        if (!open || !closeOnEsc) return;
        const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose();
        window.addEventListener('keydown', onKey);
        return () => window.removeEventListener('keydown', onKey);
    }, [open, closeOnEsc, onClose]);

    // Блокировка прокрутки body
    useEffect(() => {
        if (!open) return;
        const prev = document.body.style.overflow;
        document.body.style.overflow = 'hidden';
        return () => {
            document.body.style.overflow = prev;
        };
    }, [open]);

    return (
        <SidebarContext.Provider value={{}}>
            <div className={cn('sb', {'is-open': open}, className)}>
                <div
                    className="sb__backdrop"
                    ref={backdropRef}
                    onClick={() => (closeOnBackdrop ? onClose() : null)}
                    aria-hidden
                />
                <aside
                    className={cn('sb__panel', `side-${side}`)}
                    style={{width}}
                    role="dialog"
                    aria-modal="true"
                >
                    <div className="sb__scroll">
                        {children}
                    </div>
                </aside>
            </div>
        </SidebarContext.Provider>
    );
};

/* ---------- составные блоки ---------- */

type HeaderProps = { title?: string; subtitle?: string; children?: React.ReactNode; onClose?: () => void };
const Header: React.FC<HeaderProps> = ({title, subtitle, children, onClose}) => (
    <div className="sb__header">
        <div className="sb__header-text">
            {title && <h3 className="sb__title">{title}</h3>}
            {subtitle && <p className="sb__subtitle">{subtitle}</p>}
        </div>
        {children}
        {onClose && (
            <button className="sb__close" onClick={onClose} aria-label="Закрыть">
                ×
            </button>
        )}
    </div>
);

type SectionProps = { position?: SectionPos; children: React.ReactNode; padded?: boolean };
const Section: React.FC<SectionProps> = ({position = 'top', children, padded = true}) => (
    <div className={cn('sb__section', `pos-${position}`, {'is-padded': padded})}>{children}</div>
);

type ItemProps = {
    icon?: React.ReactNode;
    children: React.ReactNode;
    onClick?: () => void;
    active?: boolean;
    suffix?: React.ReactNode; // например, ярлык/счётчик
};
const Item: React.FC<ItemProps> = ({icon, children, onClick, active, suffix}) => (
    <button className={cn('sb__item', {active})} onClick={onClick} type="button">
        {icon && <span className="sb__item-ic">{icon}</span>}
        <span className="sb__item-label">{children}</span>
        {suffix && <span className="sb__item-suffix">{suffix}</span>}
    </button>
);

const Separator: React.FC = () => <div className="sb__sep"/>;

const Footer: React.FC<{ children: React.ReactNode }> = ({children}) => (
    <div className="sb__footer">{children}</div>
);

Sidebar.Header = Header;
Sidebar.Section = Section;
Sidebar.Item = Item;
Sidebar.Separator = Separator;
Sidebar.Footer = Footer;

export default Sidebar;
