import React from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../../../routing/components/AuthLayout';
import AuthIntro from '../../../routing/components/AuthIntro';
import LoginForm from '../../../routing/components/LoginForm';

const LoginPage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <AuthLayout
            left={
                <AuthIntro
                    brand="АСТЕРА"
                    title="Подбор БИТП"
                    subtitle="Войдите, чтобы подобрать БИТП и сформировать вариант типового технического решения."
                    points={[
                        'Быстрая регистрация заказчика',
                        'Ввод параметров системы отопления',
                        'Предварительный выбор типового решения',
                        'Формирование КП и печать формы'
                    ]}
                    ctaText="Нет аккаунта? Зарегистрироваться"
                    onCta={() => navigate('/register')}
                />
            }
            right={
                <LoginForm
                    title="Вход для клиентов"
                    subtitle="Введите e-mail и пароль"
                    onSuccess={() => navigate('/client/requests')}
                />
            }
        />
    );
};

export default LoginPage;
