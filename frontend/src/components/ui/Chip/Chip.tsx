import React from 'react';
import cn from 'clsx';
import './Chip.style.scss';

export type ChipVariant = 'neutral' | 'primary' | 'accent';
export type ChipSize = 'sm' | 'md' | 'lg';

export interface ChipProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    selected?: boolean;
    variant?: ChipVariant;
    size?: ChipSize;
    leading?: React.ReactNode;
    trailing?: React.ReactNode;
    rounded?: 'md' | 'lg' | 'pill';
}

const Chip = React.forwardRef<HTMLButtonElement, ChipProps>(
    (
        {
            children,
            selected = false,
            variant = 'neutral',
            size = 'md',
            leading,
            trailing,
            rounded = 'pill',
            className,
            ...rest
        },
        ref
    ) => {
        return (
            <button
                ref={ref}
                type="button"
                className={cn(
                    'chip',
                    `chip--${variant}`,
                    `chip--${size}`,
                    `chip--r-${rounded}`,
                    {'is-selected': selected},
                    className
                )}
                {...rest}
            >
                {leading && <span className="chip__leading">{leading}</span>}
                <span className="chip__label">{children}</span>
                {trailing && <span className="chip__trailing">{trailing}</span>}
            </button>
        );
    }
);

Chip.displayName = 'Chip';
export default Chip;
