import React from 'react';
import { Link } from 'react-router-dom';
import { Lock } from 'lucide-react';
import './styles.scss';

const LandingPage: React.FC = () => {
    return (
        <div className="landing-page">
            <div className="landing-container">
                {/* Hero Section */}
                <header className="landing-hero">
                    <div className="landing-hero__badge">
                        АСТЕРА
                    </div>
                    <h1 className="landing-hero__title">
                        Блочные индивидуальные тепловые пункты
                    </h1>
                    <p className="landing-hero__subtitle">
                        Подбор и определение предпроектной стоимости
                    </p>
                </header>

                {/* CTA Section */}
                <div className="landing-cta">
                    <div className="landing-cta__card">
                        <h2 className="landing-cta__title">Начните работу</h2>
                        <div className="landing-cta__buttons">
                            <Link to="/register" className="btn btn--primary">
                                Регистрация
                            </Link>
                            <Link to="/client/login" className="btn btn--secondary">
                                Войти
                            </Link>
                        </div>
                        <div className="landing-cta__admin">
                            <Link to="/admin/login" className="admin-link">
                                <Lock size={16} />
                                <span>Вход для администраторов</span>
                            </Link>
                        </div>
                    </div>
                </div>

                {/* Footer */}
                <footer className="landing-footer">
                    <p>© 2024 АСТЕРА. Система подбора оборудования БИТП</p>
                </footer>
            </div>
        </div>
    );
};

export default LandingPage;