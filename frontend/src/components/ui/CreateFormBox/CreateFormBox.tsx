import React from 'react';
import { Save, X } from 'lucide-react';
import IconButton from '../IconButton/IconButton';
import './CreateFormBox.style.scss';

export interface CreateFormBoxProps {
    onSubmit: () => void;
    onCancel: () => void;     // очищает форму
    children: React.ReactNode;
    submitDisabled?: boolean;
}

const CreateFormBox: React.FC<CreateFormBoxProps> = ({onSubmit, onCancel, children}) => {
    return (
        <div className="cfb">
            <div className="cfb__grid">
                {children}
            </div>
            <div className="cfb__actions">
                <IconButton label="Сохранить" variant="filled" onClick={onSubmit}>
                    <Save size={18} />
                </IconButton>
                <IconButton label="Отменить и очистить" variant="outline" onClick={onCancel}>
                    <X size={18} />
                </IconButton>
            </div>
        </div>
    );
};

export default CreateFormBox;
