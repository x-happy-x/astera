import React from 'react';
import cn from 'clsx';
import './TopPanel.style.scss';

type Positioning = 'static' | 'fixed' | 'sticky';

export interface TopPanelProps {
    /** содержимое зон */
    left?: React.ReactNode;
    center?: React.ReactNode;
    right?: React.ReactNode;

    /** позиционирование хедера */
    positioning?: Positioning;     // default: 'sticky'
    topOffset?: number;            // px, для sticky/fixed

    /** визуальные опции */
    blur?: boolean;                // фон с blur
    bordered?: boolean;            // низ с бордером
    shadow?: boolean;              // тень
    dense?: boolean;               // компактная высота
    height?: number;               // явная высота в px (перебивает dense/var)

    /** обёртка и класс */
    container?: 'fluid' | 'page';  // ширина: 100% или max(1280)
    className?: string;
}

const TopPanel: React.FC<TopPanelProps> = ({
                                               left,
                                               center,
                                               right,
                                               positioning = 'sticky',
                                               topOffset = 0,
                                               blur = true,
                                               bordered = true,
                                               shadow = false,
                                               dense = false,
                                               height,
                                               container = 'page',
                                               className,
                                           }) => {
    return (
        <header
            className={cn(
                'tp',
                `tp--${positioning}`,
                {
                    'tp--blur': blur,
                    'tp--bordered': bordered,
                    'tp--shadow': shadow,
                    'tp--dense': dense,
                    'tp--fluid': container === 'fluid'
                },
                className
            )}
            style={{
                top: positioning !== 'static' ? topOffset : undefined,
                height: height ? `${height}px` : undefined
            }}
        >
            <div className="tp__inner">
                <div className="tp__left">{left}</div>
                <div className="tp__center">{center}</div>
                <div className="tp__right">{right}</div>
            </div>
        </header>
    );
};

export default TopPanel;
