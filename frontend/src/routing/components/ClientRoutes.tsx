import React from 'react'
import { Routes, Route } from 'react-router-dom'
import LandingPage from '../../pages/client/LandingPage'
import RegisterPage from '../../pages/client/RegisterPage'

const ClientRoutes: React.FC = () => {
    return (
        <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/register" element={<RegisterPage />} />
        </Routes>
    )
}

export default ClientRoutes