import React, { useState } from 'react'
import { DollarSign, Clock, Cable, ChevronDown, ChevronRight, Check, Package, Truck } from 'lucide-react'
import type { ConfigurationCandidateDto } from '../../api'
import Card from './Card'
import './CandidateCard.scss'

interface CandidateCardProps {
    candidate: ConfigurationCandidateDto
    isSelected?: boolean
    onSelect?: () => void
}

const CandidateCard: React.FC<CandidateCardProps> = ({
    candidate,
    isSelected = false,
    onSelect
                                                     }) => {
    const [isExpanded, setIsExpanded] = useState(false)

    const getCategoryLabel = (category: string) => {
        const labels: Record<string, string> = {
            boiler: 'Котел',
            burner: 'Горелка',
            pump: 'Насос',
            valve: 'Клапан',
            flowmeter: 'Расходомер',
            automation: 'Автоматика'
        }
        return labels[category] || category
    }

    return (
        <Card className={`candidate-card ${isSelected ? 'selected' : ''}`}>

            <div className="candidate-header">
                <div className="candidate-main-info">
                    <div className="info-row">
                        <div className="info-icon">
                            <DollarSign size={18} />
                        </div>
                        <span className="label">Цена</span>
                        <span className="value price">
                            {candidate.totalPrice.toLocaleString()} {candidate.currency || '₽'}
                        </span>
                    </div>
                    <div className="info-row">
                        <div className="info-icon">
                            <Clock size={18} />
                        </div>
                        <span className="label">Срок поставки</span>
                        <span className="value">{candidate.maxDeliveryDays} дней</span>
                    </div>
                    <div className="info-row">
                        <div className="info-icon">
                            <Cable size={18} />
                        </div>
                        <span className="label">DN</span>
                        <span className="value">{candidate.dnSize}</span>
                    </div>
                    <div className="info-row">
                        <div className="info-icon">
                            <Cable size={18} />
                        </div>
                        <span className="label">Подключение</span>
                        <span className="value">{candidate.connectionKey}</span>
                    </div>
                </div>
            </div>

            <button
                className="expand-button"
                onClick={() => setIsExpanded(!isExpanded)}
            >
                {isExpanded ? (
                    <>
                        <ChevronDown size={16} />
                        Скрыть состав
                    </>
                ) : (
                    <>
                        <ChevronRight size={16} />
                        Показать состав
                    </>
                )}
            </button>

            {isExpanded && (
                <div className="components-list">
                    <h4>Состав конфигурации:</h4>
                    {candidate.components.map((component, index) => (
                        <div key={index} className="component-item">
                            <div className="component-header">
                                <div className="category-wrapper">
                                    <Package size={16} />
                                    <span className="category">{getCategoryLabel(component.category)}</span>
                                </div>
                                <span className="qty">× {component.qty}</span>
                            </div>
                            <div className="component-details">
                                <div className="brand-model">
                                    {component.brand} {component.model}
                                </div>
                                {(component.dnSize || component.connectionKey) && (
                                    <div className="component-specs">
                                        {component.dnSize && (
                                            <span className="spec-badge">
                                                <Cable size={12} />
                                                DN{component.dnSize}
                                            </span>
                                        )}
                                        {component.connectionKey && (
                                            <span className="spec-badge">{component.connectionKey}</span>
                                        )}
                                    </div>
                                )}
                            </div>
                            <div className="component-pricing">
                                <div className="price-item">
                                    <span className="price-label">Цена/шт:</span>
                                    <span className="unit-price">
                                        {component.unitPrice.toLocaleString()} ₽
                                    </span>
                                </div>
                                <div className="price-item">
                                    <span className="price-label">Итого:</span>
                                    <span className="subtotal">
                                        {component.subtotal.toLocaleString()} ₽
                                    </span>
                                </div>
                            </div>
                            <div className="component-delivery">
                                <Truck size={14} />
                                <span>{component.deliveryDays} дней</span>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {onSelect && (
                <button
                    className={`btn ${isSelected ? 'btn-selected' : 'btn-primary'}`}
                    onClick={onSelect}
                >
                    {isSelected ? (
                        <>
                            <Check size={16} />
                            Выбрано
                        </>
                    ) : (
                        'Выбрать'
                    )}
                </button>
            )}
        </Card>
    )
}

export default CandidateCard
