package ru.astera.backend.service;

import ru.astera.backend.entity.HeatingRequestStatus;
import ru.astera.backend.service.impl.SelectionServiceImpl;

import java.util.UUID;

public interface SelectionService {
    SelectionServiceImpl.SelectionDto getByRequest(UUID requestId);

    SelectionServiceImpl.SelectionDto getById(UUID selectionId);

    SelectionServiceImpl.SelectionDto select(UUID requestId, UUID candidateId, String pdfPath);

    void deleteByRequest(UUID requestId, HeatingRequestStatus statusAfterDelete);
}
