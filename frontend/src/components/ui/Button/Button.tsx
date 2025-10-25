import React from 'react';
import cn from 'clsx';
import './Button.style.scss';

type Variant = 'filled' | 'outline' | 'ghost';
type Color = 'primary' | 'accent' | 'neutral' | 'danger' | 'warning';
type Size = 'sm' | 'md' | 'lg';
type Radius = 'sm' | 'md' | 'lg' | 'pill';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    as?: React.ElementType;
    variant?: Variant;
    color?: Color;
    size?: Size;
    radius?: Radius;
    fullWidth?: boolean;
    isLoading?: boolean;
    leftIcon?: React.ReactNode;
    rightIcon?: React.ReactNode;
    to?: string;
}

const Button = React.forwardRef<HTMLButtonElement, ButtonProps>(
    (
        {
            as: Component = 'button',
            children,
            variant = 'filled',
            color = 'primary',
            size = 'md',
            radius = 'lg',
            fullWidth,
            isLoading,
            leftIcon,
            rightIcon,
            className,
            disabled,
            ...rest
        },
        ref
    ) => {
        const isDisabled = disabled || isLoading;

        return (
            <Component
                ref={ref}
                className={cn(
                    'btn',
                    `btn--${variant}`,
                    `btn--${color}`,
                    `btn--${size}`,
                    `btn--r-${radius}`,
                    {'btn--block': fullWidth, 'is-loading': !!isLoading},
                    className
                )}
                disabled={isDisabled}
                {...rest}
            >
                {isLoading && <span className="btn__spinner" aria-hidden/>}
                {!isLoading && leftIcon ? <span className="btn__icon left">{leftIcon}</span> : null}
                <span className="btn__label">{children}</span>
                {!isLoading && rightIcon ? <span className="btn__icon right">{rightIcon}</span> : null}
            </Component>
        );
    }
);

Button.displayName = 'Button';

export default Button;