import React, {useEffect, useState} from 'react'
import {Link, useNavigate} from 'react-router-dom'
import {Filter, LogOut, Plus} from 'lucide-react'
import {FuelType, type HeatingRequestDto, heatingRequestsApi, HeatingRequestStatus} from '../../../api'
import Card from '../../../components/ui/Card'
import './styles.scss'
import RequestCard from "./components";

const RequestsPage: React.FC = () => {
    const navigate = useNavigate()
    const [requests, setRequests] = useState<HeatingRequestDto[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [filters, setFilters] = useState({
        status: undefined as HeatingRequestStatus | undefined,
        fuelType: undefined as FuelType | undefined
    })

    const loadRequests = async () => {
        setIsLoading(true)
        setError(null)
        try {
            const customerId = localStorage.getItem('userId') || undefined

            const page = await heatingRequestsApi.list({
                customerId,
                status: filters.status,
                fuelType: filters.fuelType,
                page: 0,
                size: 50
            })
            setRequests(page.content)
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка загрузки заявок')
        } finally {
            setIsLoading(false)
        }
    }

    useEffect(() => {
        loadRequests()
    }, [filters])

    const handleLogout = () => {
        localStorage.removeItem('authToken')
        localStorage.removeItem('userType')
        navigate('/client/login')
    }
    return (
        <div className="requests-page">
            <div className="page-header">
                <div>
                    <h1>Мои заявки</h1>
                    <p>Управление заявками на системы отопления</p>
                </div>
                <div className="header-actions">
                    <Link to="/client/requests/new" className="btn btn-primary">
                        <Plus size={18}/>
                        Создать заявку
                    </Link>
                    <button onClick={handleLogout} className="btn btn-secondary">
                        <LogOut size={18}/>
                        Выйти
                    </button>
                </div>
            </div>

            <div className="filters glass-card">
                <Filter size={18} className="filter-icon"/>
                <div className="filter-group">
                    <label>Топливо:</label>
                    <select
                        value={filters.fuelType || ''}
                        onChange={(e) => setFilters({...filters, fuelType: e.target.value as FuelType || undefined})}
                    >
                        <option value="">Все</option>
                        <option value={FuelType.NATURAL_GAS}>Газ</option>
                        <option value={FuelType.DIESEL}>Дизель</option>
                        <option value={FuelType.OTHER}>Другое</option>
                    </select>
                </div>
            </div>

            {isLoading && (
                <div className="loading">Загрузка заявок...</div>
            )}

            {error && (
                <div className="error-message">{error}</div>
            )}

            {!isLoading && !error && requests.length === 0 && (
                <Card>
                    <div className="empty-state">
                        <p>У вас пока нет заявок</p>
                        <Link to="/client/requests/new" className="btn btn-primary">
                            Создать первую заявку
                        </Link>
                    </div>
                </Card>
            )}

            {!isLoading && !error && requests.length > 0 && (
                <div className="requests-grid">
                    {requests.map((r) => (
                        <RequestCard key={r.id} data={r}/>
                    ))}
                </div>
            )}
        </div>
    )
}

export default RequestsPage
