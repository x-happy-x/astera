import React from 'react';
import { Link } from 'react-router-dom';
import Card from '../../../../components/ui/Card/Card';
import Button from '../../../../components/ui/Button/Button';
import './RequestCard.style.scss';

export type Fuel = 'gas' | 'diesel' | 'solid' | 'other';

export interface RequestVM {
    id: string;
    powerKw: number;
    tIn: number;
    tOut: number;
    fuelType: Fuel | string;
    notes?: string | null;
    createdAt?: string;
}

const fuelLabel = (t: RequestVM['fuelType']) => {
    const map: Record<string, string> = {
        gas: 'Газ',
        diesel: 'Дизель',
        solid: 'Твёрдое топливо',
        other: 'Другое',
    };
    return map[t as string] ?? String(t);
};

interface Props {
    data: RequestVM;
    className?: string;
}

const RequestCard: React.FC<Props> = ({ data, className }) => {
    return (
        <Card className={className} hoverable elevation="md">
            <header className="rc__header">
                <div className="rc__title">Заявка <span className="rc__mono">#{data.id.slice(0, 8)}</span></div>
                {data.createdAt && <div className="rc__meta">от {new Date(data.createdAt).toLocaleDateString()}</div>}
            </header>

            <div className="rc__divider" />

            <dl className="rc__grid">
                <div className="rc__row">
                    <dt>Мощность</dt>
                    <dd><strong>{data.powerKw}</strong> кВт</dd>
                </div>

                <div className="rc__row">
                    <dt>Температура</dt>
                    <dd>{data.tIn}°C → {data.tOut}°C</dd>
                </div>

                <div className="rc__row">
                    <dt>Топливо</dt>
                    <dd>{fuelLabel(data.fuelType)}</dd>
                </div>

                {data.notes && (
                    <div className="rc__row">
                        <dt>Примечание</dt>
                        <dd className="rc__note">{data.notes}</dd>
                    </div>
                )}
            </dl>

            <div className="rc__actions">
                <Button as={Link} to={`/client/requests/${data.id}`} size="md" radius="pill">
                    Открыть
                </Button>
                <Button
                    as={Link}
                    to={`/client/requests/${data.id}/edit`}
                    variant="ghost"
                    color="neutral"
                    size="md"
                    radius="pill"
                    className="rc__btn-edit"
                >
                    Редактировать
                </Button>
            </div>
        </Card>
    );
};

export default RequestCard;
