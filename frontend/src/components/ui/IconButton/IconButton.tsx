import React from 'react';
import cn from 'clsx';
import './IconButton.style.scss';

export interface IconButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    label?: string;        // aria-label / tooltip text
    size?: 'sm' | 'md';
    variant?: 'ghost' | 'outline' | 'filled';
}

const IconButton = React.forwardRef<HTMLButtonElement, IconButtonProps>(
    ({label, children, className, size = 'md', variant = 'ghost', ...rest}, ref) => {
        return (
            <button
                ref={ref}
                className={cn('iconbtn', `iconbtn--${size}`, `iconbtn--${variant}`, className)}
                aria-label={label}
                title={label}
                type="button"
                {...rest}
            >
                {children}
            </button>
        );
    }
);
IconButton.displayName = 'IconButton';
export default IconButton;
