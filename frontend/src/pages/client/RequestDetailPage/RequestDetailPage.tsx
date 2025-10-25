import React, {useEffect, useRef, useState} from 'react'
import {Link, useParams} from 'react-router-dom'
import {ArrowLeft, Droplets, Edit3, FileText, Flame, Printer, Target, Thermometer, Zap} from 'lucide-react'
import {
    type ConfigurationCandidateDto,
    FuelType,
    type HeatingRequestDto,
    heatingRequestsApi,
    selectionApi,
    type SelectionDto
} from '../../../api'
import CandidateCard from '../../../components/ui/CandidateCard'
import PrintView from '../../../components/ui/PrintView'
import './styles.scss'

const RequestDetailPage: React.FC = () => {
    const {id} = useParams<{ id: string }>()
    const [request, setRequest] = useState<HeatingRequestDto | null>(null)
    const [candidates, setCandidates] = useState<ConfigurationCandidateDto[]>([])
    const [selectedCandidate, setSelectedCandidate] = useState<ConfigurationCandidateDto | null>(null)
    const [selection, setSelection] = useState<SelectionDto | null>(null)
    const [showPrint, setShowPrint] = useState(false)
    const [isLoading, setIsLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const isMountedRef = useRef(true)

    useEffect(() => {
        isMountedRef.current = true

        if (id) {
            loadRequestAndCandidates(id)
        }

        return () => {
            isMountedRef.current = false
        }
    }, [id])

    const loadRequestAndCandidates = async (requestId: string) => {
        setIsLoading(true)
        setError(null)
        // Сбрасываем состояние при новой загрузке
        setSelectedCandidate(null)
        setSelection(null)
        setCandidates([])

        try {
            // Загружаем заявку
            const requestData = await heatingRequestsApi.get(requestId)
            if (!isMountedRef.current) return
            setRequest(requestData)

            // Загружаем или генерируем кандидатов
            const candidatesData = await selectionApi.generateCandidates(requestId, {topN: 3, includeAutomation: true})
            if (!isMountedRef.current) return
            setCandidates(candidatesData)

            // Проверяем, есть ли уже зафиксированный выбор
            try {
                const selectionData = await selectionApi.getByRequest(requestId)
                if (!isMountedRef.current) return
                setSelection(selectionData)
                // Находим и устанавливаем выбранного кандидата
                const selected = candidatesData.find(c => c.id === selectionData.candidateId)
                if (selected) {
                    setSelectedCandidate(selected)
                }
            } catch {
                // Выбор еще не сделан, это нормально
                console.log('No selection yet')
            }
        } catch (error) {
            if (!isMountedRef.current) return
            setError(error instanceof Error ? error.message : 'Ошибка загрузки данных')
        } finally {
            if (isMountedRef.current) {
                setIsLoading(false)
            }
        }
    }

    const handleSelectCandidate = async (candidate: ConfigurationCandidateDto) => {
        if (!id || !candidate.id) return

        setSelectedCandidate(candidate)

        // Фиксируем выбор на бэке
        try {
            const selectionData = await selectionApi.select(id, candidate.id)
            setSelection(selectionData)
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Ошибка фиксации выбора')
        }
    }

    const handleShowPrint = () => {
        if (selectedCandidate) {
            setShowPrint(true)
        }
    }

    const getFuelTypeLabel = (fuelType: FuelType) => {
        const fuelMap = {
            [FuelType.NATURAL_GAS]: 'Природный газ',
            [FuelType.DIESEL]: 'Дизель',
            [FuelType.OTHER]: 'Другое'
        }
        return fuelMap[fuelType]
    }

    if (showPrint && selectedCandidate) {
        return (
            <PrintView
                candidate={selectedCandidate}
                request={request || undefined}
                onPrint={() => console.log('Printing...')}
                onClose={() => setShowPrint(false)}
            />
        )
    }

    return (
        <div className="request-detail-page">
            <div className="page-header">
                <div className="header-content">
                    <Link to="/client/requests" className="back-link">
                        <ArrowLeft size={18}/>
                        <span>Назад к списку</span>
                    </Link>
                    <div className="title-section">
                        <h1>Заявка #{id?.slice(0, 8)}</h1>
                        {selection && (
                            <span className="status-badge status-selected">Выбор сделан</span>
                        )}
                    </div>
                </div>
                <div className="header-actions">
                    <Link to={`/client/requests/${id}/edit`} className="btn btn-secondary">
                        <Edit3 size={16}/>
                        Редактировать
                    </Link>
                </div>
            </div>

            {error && (
                <div className="error-message">
                    <span className="error-icon">⚠️</span>
                    {error}
                </div>
            )}

            {request && (
                <div className="glass-card request-details-card">
                    <div className="card-header">
                        <h2>
                            <FileText size={20}/>
                            Параметры заявки
                        </h2>
                    </div>
                    <div className="card-body">
                        <div className="details-grid">
                            <div className="detail-item">
                                <div className="detail-icon">
                                    <Zap size={20}/>
                                </div>
                                <div className="detail-content">
                                    <span className="label">Мощность</span>
                                    <span className="value">{request.powerKw} кВт</span>
                                </div>
                            </div>
                            <div className="detail-item">
                                <div className="detail-icon">
                                    <Thermometer size={20}/>
                                </div>
                                <div className="detail-content">
                                    <span className="label">Температура подачи</span>
                                    <span className="value">{request.tIn}°C</span>
                                </div>
                            </div>
                            <div className="detail-item">
                                <div className="detail-icon">
                                    <Droplets size={20}/>
                                </div>
                                <div className="detail-content">
                                    <span className="label">Температура обратки</span>
                                    <span className="value">{request.tOut}°C</span>
                                </div>
                            </div>
                            <div className="detail-item">
                                <div className="detail-icon">
                                    <Flame size={20}/>
                                </div>
                                <div className="detail-content">
                                    <span className="label">Тип топлива</span>
                                    <span className="value">{getFuelTypeLabel(request.fuelType)}</span>
                                </div>
                            </div>
                        </div>
                        {request.notes && (
                            <div className="notes-section">
                                <div className="notes-header">
                                    <FileText size={18}/>
                                    <span className="label">Примечание</span>
                                </div>
                                <p className="notes-text">{request.notes}</p>
                            </div>
                        )}
                    </div>
                </div>
            )}

            <div className="candidates-section">
                <div className="section-header">
                    <h2>
                        <Target size={22}/>
                        Варианты конфигураций
                    </h2>
                    {candidates.length > 0 && (
                        <span className="candidates-count">{candidates.length} вариантов</span>
                    )}
                </div>

                {isLoading && (
                    <div className="loading-state">
                        <div className="spinner"></div>
                        <p>Подбираем оптимальные варианты...</p>
                    </div>
                )}

                {!isLoading && candidates.length === 0 && (
                    <div className="empty-state">
                        <Target size={48} />
                        <h3>Кандидаты не найдены</h3>
                        <p>К сожалению, не удалось подобрать подходящие конфигурации для ваших требований.</p>
                    </div>
                )}

                {!isLoading && candidates.length > 0 && (
                    <>
                        <div className="candidates-grid">
                            {candidates.map((candidate, index) => (
                                <CandidateCard
                                    key={candidate.id || index}
                                    candidate={candidate}
                                    isSelected={selectedCandidate?.id === candidate.id}
                                    onSelect={() => handleSelectCandidate(candidate)}
                                />
                            ))}
                        </div>

                        {selectedCandidate && (
                            <div className="print-action">
                                <button
                                    className="btn btn-primary btn-lg"
                                    onClick={handleShowPrint}
                                >
                                    <Printer size={18}/>
                                    Печать выбранного варианта
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    )
}

export default RequestDetailPage
