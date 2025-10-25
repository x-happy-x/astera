import React, {useEffect, useState} from 'react'
import {useNavigate, useParams} from 'react-router-dom'
import {FuelType, type HeatingRequestCreateDto, type HeatingRequestDto, heatingRequestsApi} from '../../../api'
import './styles.scss'

type FormStep = 1 | 2 | 3 | 4

const RequestFormPage: React.FC = () => {
    const navigate = useNavigate()
    const {id} = useParams<{ id: string }>()
    const isEditMode = !!id

    const [currentStep, setCurrentStep] = useState<FormStep>(1)
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
            setCurrentStep(4)
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка загрузки заявки')
        }
    }

    const nextStep = () => {
        if (currentStep < 4) {
            setCurrentStep((prev) => (prev + 1) as FormStep)
        }
    }

    const prevStep = () => {
        if (currentStep > 1) {
            setCurrentStep((prev) => (prev - 1) as FormStep)
        }
    }

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
        const {name, value} = e.target
        setFormData(prev => ({
            ...prev,
            [name]: name === 'powerKw' || name === 'tIn' || name === 'tOut'
                ? parseFloat(value) || 0
                : value
        }))
        setValidationErrors(prev => ({...prev, [name]: ''}))
        setError(null)
    }

    const validateStep = (step: FormStep): boolean => {
        const errors: Record<string, string> = {}

        switch (step) {
            case 1:
                if (formData.powerKw <= 0) {
                    errors.powerKw = 'Мощность должна быть больше 0'
                }
                break
            case 2:
                break
            case 3:
                if (formData.tIn <= formData.tOut) {
                    errors.tIn = 'Температура подачи должна быть больше температуры обратки'
                }
                break
        }

        setValidationErrors(errors)
        return Object.keys(errors).length === 0
    }

    const handleNext = () => {
        if (validateStep(currentStep)) {
            nextStep()
        }
    }

    const handleSubmit = async () => {
        if (!validateStep(currentStep)) {
            return
        }

        setIsLoading(true)
        setError(null)

        try {
            let heatingRequestDto: HeatingRequestDto;

            if (isEditMode && id) {
                heatingRequestDto = await heatingRequestsApi.update(id, formData);
            } else {
                heatingRequestDto = await heatingRequestsApi.create(formData)
            }
            navigate(`/client/requests/${heatingRequestDto.id}`)
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка сохранения заявки')
        } finally {
            setIsLoading(false)
        }
    }

    const renderStepContent = () => {
        switch (currentStep) {
            case 1:
                return (
                    <div className="step-content">
                        <div className="step-icon">⚡</div>
                        <h3 className="step-title">Мощность системы</h3>
                        <p className="step-description">Укажите требуемую мощность системы отопления</p>
                        <div className="form-group">
                            <label htmlFor="powerKw">Мощность (кВт) *</label>
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
                                autoFocus
                            />
                            {validationErrors.powerKw && (
                                <span className="field-error">{validationErrors.powerKw}</span>
                            )}
                        </div>
                    </div>
                )
            case 2:
                return (
                    <div className="step-content">
                        <div className="step-icon">🔥</div>
                        <h3 className="step-title">Тип топлива</h3>
                        <p className="step-description">Выберите тип топлива для системы</p>
                        <div className="form-group">
                            <div className="fuel-options">
                                <label
                                    className={`fuel-option ${formData.fuelType === FuelType.NATURAL_GAS ? 'active' : ''}`}>
                                    <input
                                        type="radio"
                                        name="fuelType"
                                        value={FuelType.NATURAL_GAS}
                                        checked={formData.fuelType === FuelType.NATURAL_GAS}
                                        onChange={handleChange}
                                        disabled={isLoading}
                                    />
                                    <div className="fuel-card">
                                        <div className="fuel-icon">🌿</div>
                                        <span>Природный газ</span>
                                    </div>
                                </label>
                                <label
                                    className={`fuel-option ${formData.fuelType === FuelType.DIESEL ? 'active' : ''}`}>
                                    <input
                                        type="radio"
                                        name="fuelType"
                                        value={FuelType.DIESEL}
                                        checked={formData.fuelType === FuelType.DIESEL}
                                        onChange={handleChange}
                                        disabled={isLoading}
                                    />
                                    <div className="fuel-card">
                                        <div className="fuel-icon">🛢️</div>
                                        <span>Дизель</span>
                                    </div>
                                </label>
                                <label
                                    className={`fuel-option ${formData.fuelType === FuelType.OTHER ? 'active' : ''}`}>
                                    <input
                                        type="radio"
                                        name="fuelType"
                                        value={FuelType.OTHER}
                                        checked={formData.fuelType === FuelType.OTHER}
                                        onChange={handleChange}
                                        disabled={isLoading}
                                    />
                                    <div className="fuel-card">
                                        <div className="fuel-icon">⚙️</div>
                                        <span>Другое</span>
                                    </div>
                                </label>
                            </div>
                        </div>
                    </div>
                )
            case 3:
                return (
                    <div className="step-content">
                        <div className="step-icon">🌡️</div>
                        <h3 className="step-title">Температурные параметры</h3>
                        <p className="step-description">Укажите температуру подачи и обратки</p>
                        <div className="form-row">
                            <div className="form-group">
                                <label htmlFor="tIn">Температура подачи (°C) *</label>
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
                                <label htmlFor="tOut">Температура обратки (°C) *</label>
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
                    </div>
                )
            case 4:
                return (
                    <div className="step-content">
                        <div className="step-icon">📝</div>
                        <h3 className="step-title">Дополнительная информация</h3>
                        <p className="step-description">Добавьте примечания при необходимости</p>
                        <div className="form-group">
                            <label htmlFor="notes">Примечание</label>
                            <textarea
                                id="notes"
                                name="notes"
                                value={formData.notes}
                                onChange={handleChange}
                                disabled={isLoading}
                                rows={6}
                                placeholder="Дополнительная информация о системе отопления..."
                            />
                        </div>
                    </div>
                )
        }
    }

    return (
        <div className="request-form-page">
            <div className="dialog-backdrop" onClick={() => navigate('/client/requests')}/>

            <div className="dialog-container">
                <div className="dialog-glass">
                    <button
                        className="dialog-close"
                        onClick={() => navigate('/client/requests')}
                        aria-label="Закрыть"
                    >
                        ✕
                    </button>

                    <div className="dialog-header">
                        <h2>{isEditMode ? 'Редактирование заявки' : 'Новая заявка'}</h2>
                        <div className="step-indicator">
                            {[1, 2, 3, 4].map((step) => (
                                <div
                                    key={step}
                                    className={`step-dot ${currentStep >= step ? 'active' : ''} ${currentStep === step ? 'current' : ''}`}
                                />
                            ))}
                        </div>
                    </div>

                    {error && (
                        <div className="error-message">{error}</div>
                    )}

                    <div className="dialog-body">
                        {renderStepContent()}
                    </div>

                    <div className="dialog-footer">
                        {currentStep > 1 && (
                            <button
                                type="button"
                                className="btn btn-secondary"
                                onClick={prevStep}
                                disabled={isLoading}
                            >
                                Назад
                            </button>
                        )}

                        <div className="spacer"/>

                        {currentStep < 4 ? (
                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={handleNext}
                                disabled={isLoading}
                            >
                                Далее
                            </button>
                        ) : (
                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={handleSubmit}
                                disabled={isLoading}
                            >
                                {isLoading ? 'Сохранение...' : isEditMode ? 'Сохранить' : 'Создать заявку'}
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    )
}

export default RequestFormPage
