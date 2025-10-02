package ru.astera.backend.exception;

public class EquipmentAlreadyExistsException extends RuntimeException {
    public EquipmentAlreadyExistsException(String message) {
        super(message);
    }
}