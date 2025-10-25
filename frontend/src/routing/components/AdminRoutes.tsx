import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import AdminLayout from '../../components/layout/AdminLayout'
import LoginPage from '../../pages/admin/LoginPage'
import ClientsPage from '../../pages/admin/ClientsPage'
import EquipmentPage from '../../pages/admin/EquipmentPage'
import RequestsPage from '../../pages/admin/RequestsPage'
import ManagersPage from '../../pages/admin/ManagersPage'

const AdminRoutes: React.FC = () => {
    const { state } = useAuth()

    if (state.isLoading) {
        return <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh'}}>Загрузка...</div>
    }

    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/*" element={
                state.user ? (
                    <AdminLayout>
                        <Routes>
                            <Route path="/requests" element={<RequestsPage />} />
                            <Route path="/clients" element={<ClientsPage />} />
                            <Route path="/equipment" element={<EquipmentPage />} />
                            {state.user.role === 'admin' && (
                                <Route path="/managers" element={<ManagersPage />} />
                            )}
                            <Route path="/" element={<Navigate to="/admin/requests" replace />} />
                        </Routes>
                    </AdminLayout>
                ) : (
                    <Navigate to="/admin/login" replace />
                )
            } />
        </Routes>
    )
}

export default AdminRoutes