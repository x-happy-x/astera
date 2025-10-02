import React, { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { heatingRequestsApi, type HeatingRequestDto, HeatingRequestStatus, FuelType } from '../../../api'
import Card from '../../../components/ui/Card'
import './styles.scss'

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
            const page = await heatingRequestsApi.list({
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

    const getFuelTypeLabel = (fuelType: FuelType) => {
        const fuelMap = {
            [FuelType.NATURAL_GAS]: 'Газ',
            [FuelType.DIESEL]: 'Дизель',
            [FuelType.OTHER]: 'Другое'
        }
        return fuelMap[fuelType]
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
                        + Создать заявку
                    </Link>
                    <button onClick={handleLogout} className="btn btn-secondary">
                        Выйти
                    </button>
                </div>
            </div>

            <div className="filters">
                <div className="filter-group">
                    <label>Статус:</label>
                    <select
                        value={filters.status || ''}
                        onChange={(e) => setFilters({ ...filters, status: e.target.value as HeatingRequestStatus || undefined })}
                    >
                        <option value="">Все</option>
                        <option value={HeatingRequestStatus.DRAFT}>Черновик</option>
                        <option value={HeatingRequestStatus.IN_PROGRESS}>В работе</option>
                        <option value={HeatingRequestStatus.CANDIDATES_READY}>Варианты готовы</option>
                        <option value={HeatingRequestStatus.COMPLETED}>Завершено</option>
                        <option value={HeatingRequestStatus.CANCELLED}>Отменено</option>
                    </select>
                </div>

                <div className="filter-group">
                    <label>Топливо:</label>
                    <select
                        value={filters.fuelType || ''}
                        onChange={(e) => setFilters({ ...filters, fuelType: e.target.value as FuelType || undefined })}
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
                    {requests.map((request) => (
                        <Card key={request.id} className="request-card">
                            <div className="request-header">
                                <div className="request-id">
                                    Заявка #{request.id.slice(0, 8)}
                                </div>
                            </div>

                            <div className="request-details">
                                <div className="detail-row">
                                    <span className="label">Мощность:</span>
                                    <span className="value">{request.powerKw} кВт</span>
                                </div>
                                <div className="detail-row">
                                    <span className="label">Температура:</span>
                                    <span className="value">{request.tIn}°C → {request.tOut}°C</span>
                                </div>
                                <div className="detail-row">
                                    <span className="label">Топливо:</span>
                                    <span className="value">{getFuelTypeLabel(request.fuelType)}</span>
                                </div>
                                {request.notes && (
                                    <div className="detail-row">
                                        <span className="label">Примечание:</span>
                                        <span className="value">{request.notes}</span>
                                    </div>
                                )}
                            </div>

                            <div className="request-actions">
                                <Link
                                    to={`/client/requests/${request.id}`}
                                    className="btn btn-sm btn-primary"
                                >
                                    Открыть
                                </Link>
                                <Link
                                    to={`/client/requests/${request.id}/edit`}
                                    className="btn btn-sm btn-secondary"
                                >
                                    Редактировать
                                </Link>
                            </div>
                        </Card>
                    ))}
                </div>
            )}
        </div>
    )
}

export default RequestsPage
