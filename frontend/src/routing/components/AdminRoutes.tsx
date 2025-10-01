import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import AdminLayout from '../../components/layout/AdminLayout'
import LoginPage from '../../pages/admin/LoginPage'
import DashboardPage from '../../pages/admin/DashboardPage'
import ClientsPage from '../../pages/admin/ClientsPage'
import EquipmentPage from '../../pages/admin/EquipmentPage'
import RequestsPage from '../../pages/admin/RequestsPage'
import ManagersPage from '../../pages/admin/ManagersPage'

const AdminRoutes: React.FC = () => {
    const { state } = useAuth()

    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/*" element={
                state.user ? (
                    <AdminLayout>
                        <Routes>
                            <Route path="/" element={<Navigate to="/admin/dashboard" replace />} />
                            <Route path="/dashboard" element={<DashboardPage />} />
                            <Route path="/clients" element={<ClientsPage />} />
                            <Route path="/equipment" element={<EquipmentPage />} />
                            <Route path="/requests" element={<RequestsPage />} />
                            {state.user.role === 'admin' && (
                                <Route path="/managers" element={<ManagersPage />} />
                            )}
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