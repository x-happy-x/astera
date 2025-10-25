import React from 'react';
import { useNavigate } from 'react-router-dom';
import AuthLayout from '../../../routing/components/AuthLayout';
import AuthIntro from '../../../routing/components/AuthIntro';
import RegisterForm from './components/RegisterForm';

const RegisterPage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <AuthLayout
            left={
                <AuthIntro
                    brand="АСТЕРА"
                    title="Создайте аккаунт"
                    subtitle="Зарегистрируйтесь для доступа к системе подбора оборудования и управлению заявками."
                    points={[
                        'Мгновенный доступ после регистрации',
                        'Сохранение истории расчётов',
                        'Персональные коммерческие предложения',
                        'Онлайн-консультация специалистов'
                    ]}
                    ctaText="Уже есть аккаунт? Войти"
                    onCta={() => navigate('/client/login')}
                />
            }
            right={
                <RegisterForm
                    title="Регистрация"
                    subtitle="Заполните форму для создания аккаунта"
                    onSuccess={() => navigate('/client/requests')}
                />
            }
        />
    );
};

export default RegisterPage;