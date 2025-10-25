import React from 'react';

type Props = {
    brand?: string;
    title: string;
    subtitle?: string;
    points?: string[];
    ctaText: string;
    onCta: () => void;
};

const AuthIntro: React.FC<Props> = ({brand, title, subtitle, points = [], ctaText, onCta}) => {
    return (
        <div className="intro">
            {brand && <div className="intro__brand">{brand}</div>}

            <h1 className="intro__title">{title}</h1>
            {subtitle && <p className="intro__subtitle">{subtitle}</p>}

            {points.length > 0 && (
                <ul className="intro__list">
                    {points.map((p, i) => (
                        <li key={i} className="intro__list-item">{p}</li>
                    ))}
                </ul>
            )}

            <button className="intro__cta" onClick={onCta}>
                {ctaText}
            </button>
        </div>
    );
};

export default AuthIntro;
