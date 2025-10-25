import React from 'react'
import { Routes, Route } from 'react-router-dom'
import LandingPage from '../../pages/client/LandingPage'
import RegisterPage from '../../pages/client/RegisterPage'
import LoginPage from '../../pages/client/LoginPage'
import RequestsPage from '../../pages/client/RequestsPage'
import RequestFormPage from '../../pages/client/RequestFormPage'
import RequestDetailPage from '../../pages/client/RequestDetailPage'

const ClientRoutes: React.FC = () => {
    return (
        <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/client/login" element={<LoginPage />} />
            <Route path="/client/requests" element={<RequestsPage />} />
            <Route path="/client/requests/new" element={<RequestFormPage />} />
            <Route path="/client/requests/:id" element={<RequestDetailPage />} />
            <Route path="/client/requests/:id/edit" element={<RequestFormPage />} />
        </Routes>
    )
}

export default ClientRoutes