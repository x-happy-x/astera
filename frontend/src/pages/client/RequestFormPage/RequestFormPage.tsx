import React, { useState, useEffect } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'
import { heatingRequestsApi, FuelType, type HeatingRequestCreateDto } from '../../../api'
import Card from '../../../components/ui/Card'
import './styles.scss'

const RequestFormPage: React.FC = () => {
    const navigate = useNavigate()
    const { id } = useParams<{ id: string }>()
    const isEditMode = !!id

    const [formData, setFormData] = useState<HeatingRequestCreateDto>({
        powerKw: 0,
        tIn: 0,
        tOut: 0,
        fuelType: FuelType.NATURAL_GAS,
        notes: ''
    })
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>({})

    useEffect(() => {
        if (isEditMode && id) {
            loadRequest(id)
        }
    }, [id])

    const loadRequest = async (requestId: string) => {
        try {
            const request = await heatingRequestsApi.get(requestId)
            setFormData({
                powerKw: request.powerKw,
                tIn: request.tIn,
                tOut: request.tOut,
                fuelType: request.fuelType,
                notes: request.notes || ''
            })
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка загрузки заявки')
        }
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target
        setFormData(prev => ({
            ...prev,
            [name]: name === 'powerKw' || name === 'tIn' || name === 'tOut'
                ? parseFloat(value) || 0
                : value
        }))
        setValidationErrors(prev => ({ ...prev, [name]: '' }))
        setError(null)
    }

    const validate = (): boolean => {
        const errors: Record<string, string> = {}

        if (formData.powerKw <= 0) {
            errors.powerKw = 'Мощность должна быть больше 0'
        }

        if (formData.tIn <= formData.tOut) {
            errors.tIn = 'Температура подачи должна быть больше температуры обратки'
        }

        setValidationErrors(errors)
        return Object.keys(errors).length === 0
    }

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()

        if (!validate()) {
            return
        }

        setIsLoading(true)
        setError(null)

        try {
            if (isEditMode && id) {
                await heatingRequestsApi.update(id, formData)
            } else {
                await heatingRequestsApi.create(formData)
            }
            navigate('/client/requests')
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка сохранения заявки')
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <div className="request-form-page">
            <div className="form-container">
                <Card>
                    <div className="form-header">
                        <h2>{isEditMode ? 'Редактирование заявки' : 'Новая заявка'}</h2>
                        <p>Заполните параметры системы отопления</p>
                    </div>

                    {error && (
                        <div className="error-message">{error}</div>
                    )}

                    <form onSubmit={handleSubmit} className="request-form">
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="powerKw">
                                    Мощность (кВт) *
                                </label>
                                <input
                                    type="number"
                                    id="powerKw"
                                    name="powerKw"
                                    value={formData.powerKw || ''}
                                    onChange={handleChange}
                                    required
                                    disabled={isLoading}
                                    step="0.1"
                                    min="0"
                                    placeholder="500"
                                    className={validationErrors.powerKw ? 'error' : ''}
                                />
                                {validationErrors.powerKw && (
                                    <span className="field-error">{validationErrors.powerKw}</span>
                                )}
                            </div>

                            <div className="form-group">
                                <label htmlFor="fuelType">
                                    Тип топлива *
                                </label>
                                <select
                                    id="fuelType"
                                    name="fuelType"
                                    value={formData.fuelType}
                                    onChange={handleChange}
                                    required
                                    disabled={isLoading}
                                >
                                    <option value={FuelType.NATURAL_GAS}>Природный газ</option>
                                    <option value={FuelType.DIESEL}>Дизель</option>
                                    <option value={FuelType.OTHER}>Другое</option>
                                </select>
                            </div>
                        </div>

                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="tIn">
                                    Температура подачи (°C) *
                                </label>
                                <input
                                    type="number"
                                    id="tIn"
                                    name="tIn"
                                    value={formData.tIn || ''}
                                    onChange={handleChange}
                                    required
                                    disabled={isLoading}
                                    step="1"
                                    placeholder="90"
                                    className={validationErrors.tIn ? 'error' : ''}
                                />
                                {validationErrors.tIn && (
                                    <span className="field-error">{validationErrors.tIn}</span>
                                )}
                            </div>

                            <div className="form-group">
                                <label htmlFor="tOut">
                                    Температура обратки (°C) *
                                </label>
                                <input
                                    type="number"
                                    id="tOut"
                                    name="tOut"
                                    value={formData.tOut || ''}
                                    onChange={handleChange}
                                    required
                                    disabled={isLoading}
                                    step="1"
                                    placeholder="70"
                                />
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="notes">
                                Примечание
                            </label>
                            <textarea
                                id="notes"
                                name="notes"
                                value={formData.notes}
                                onChange={handleChange}
                                disabled={isLoading}
                                rows={4}
                                placeholder="Дополнительная информация о системе отопления..."
                            />
                        </div>

                        <div className="form-actions">
                            <button
                                type="submit"
                                className="btn btn-primary"
                                disabled={isLoading}
                            >
                                {isLoading ? 'Сохранение...' : isEditMode ? 'Сохранить изменения' : 'Создать заявку'}
                            </button>
                            <Link
                                to="/client/requests"
                                className="btn btn-secondary"
                            >
                                Отмена
                            </Link>
                        </div>
                    </form>
                </Card>
            </div>
        </div>
    )
}

export default RequestFormPage
