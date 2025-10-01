import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import AdminRoutes from './AdminRoutes'
import ClientRoutes from './ClientRoutes'

const AppRouter: React.FC = () => {
    return (
        <Router>
            <Routes>
                <Route path="/admin/*" element={<AdminRoutes />} />
                <Route path="/*" element={<ClientRoutes />} />
            </Routes>
        </Router>
    )
}

export default AppRouter