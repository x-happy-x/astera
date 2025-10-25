import React, { useState } from 'react';
import cn from 'clsx';
import './Badge.style.scss';

type Size = 'sm' | 'md' | 'lg';
type Radius = 'md' | 'lg' | 'pill';
type Tone = 'neutral' | 'primary' | 'accent' | 'success' | 'warning' | 'danger';
type Fill = 'soft' | 'solid' | 'outline';
type TooltipTrigger = 'hover' | 'click';

export interface BadgeProps extends React.HTMLAttributes<HTMLDivElement> {
    children: React.ReactNode;

    /** преднастроенный тон (можно не использовать при custom-цветах) */
    tone?: Tone;
    /** визуальный стиль */
    fill?: Fill;
    /** размеры/скругление */
    size?: Size;
    radius?: Radius;

    /** кастомные цвета поверх тона (любые CSS-значения) */
    bgColor?: string;
    textColor?: string;
    borderColor?: string;

    /** тултип */
    tooltip?: React.ReactNode;
    tooltipTrigger?: TooltipTrigger;   // 'hover' | 'click'
    tooltipPlacement?: 'top' | 'bottom' | 'left' | 'right';
}

const Badge: React.FC<BadgeProps> = ({
                                         children,
                                         className,
                                         tone = 'neutral',
                                         fill = 'soft',
                                         size = 'md',
                                         radius = 'pill',
                                         bgColor,
                                         textColor,
                                         borderColor,
                                         tooltip,
                                         tooltipTrigger = 'hover',
                                         tooltipPlacement = 'top',
                                         ...rest
                                     }) => {
    const [open, setOpen] = useState(false);
    const hasTooltip = !!tooltip;

    const style: React.CSSProperties = {
        ...(bgColor ? { ['--bdg-bg' as string]: bgColor } : {}),
        ...(textColor ? { ['--bdg-fg' as string]: textColor } : {}),
        ...(borderColor ? { ['--bdg-bd' as string]: borderColor } : {}),
    };

    const onClick = (e: React.MouseEvent<HTMLDivElement>) => {
        if (tooltip && tooltipTrigger === 'click') {
            e.stopPropagation();
            setOpen((s) => !s);
        }
        rest.onClick?.(e);
    };

    // закрывать тултип по клику вне
    React.useEffect(() => {
        if (!open) return;
        const close = () => setOpen(false);
        window.addEventListener('click', close);
        return () => window.removeEventListener('click', close);
    }, [open]);

    return (
        <div
            className={cn(
                'badge',
                `badge--${fill}`,
                `badge--${tone}`,
                `badge--${size}`,
                `badge--r-${radius}`,
                { 'has-tooltip': hasTooltip, 'is-open': open, [`tt-${tooltipPlacement}`]: hasTooltip }
                , className)}
            style={style}
            onClick={onClick}
            {...rest}
        >
            <span className="badge__content">{children}</span>

            {hasTooltip && (
                <span
                    className={cn(
                        'badge__tooltip',
                        { 'by-hover': tooltipTrigger === 'hover', 'by-click': tooltipTrigger === 'click' }
                    )}
                    role="tooltip"
                >
          {tooltip}
        </span>
            )}
        </div>
    );
};

export default Badge;
