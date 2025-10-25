import React from 'react';
import './auth.styles.scss';

type Props = {
    left: React.ReactNode;
    right: React.ReactNode;
};

const AuthLayout: React.FC<Props> = ({left, right}) => {
    return (
        <div className="auth-page">
            <div className="auth-shell">
                <div className="auth-card">
                    <section className="auth-pane auth-pane--left">
                        {left}
                    </section>
                    <section className="auth-pane auth-pane--right">
                        {right}
                    </section>
                </div>
            </div>
        </div>
    );
};

export default AuthLayout;
