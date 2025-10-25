package ru.astera.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astera.backend.entity.ConfigCandidate;
import ru.astera.backend.entity.HeatingRequest;
import ru.astera.backend.entity.HeatingRequestStatus;
import ru.astera.backend.entity.Selection;
import ru.astera.backend.repository.ConfigCandidateRepository;
import ru.astera.backend.repository.HeatingRequestRepository;
import ru.astera.backend.repository.SelectionRepository;
import ru.astera.backend.service.SelectionService;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SelectionServiceImpl implements SelectionService {

    private final SelectionRepository selectionRepository;
    private final HeatingRequestRepository heatingRequestRepository;
    private final ConfigCandidateRepository configCandidateRepository;

    @Transactional(readOnly = true)
    @Override
    public SelectionDto getByRequest(UUID requestId) {
        Selection s = selectionRepository.findByRequest_Id(requestId)
                .orElseThrow(() -> new NoSuchElementException("Selection not found for request: " + requestId));
        return toDto(s);
    }

    @Transactional(readOnly = true)
    @Override
    public SelectionDto getById(UUID selectionId) {
        Selection s = selectionRepository.findById(selectionId)
                .orElseThrow(() -> new NoSuchElementException("Selection not found: " + selectionId));
        return toDto(s);
    }

    /**
     * Зафиксировать выбор. Если для запроса уже есть выбор — перезаписать (идемпотентно).
     * Бизнес-правила:
     * - candidate.request.id должен совпасть с requestId
     * - статус HeatingRequest → SELECTED
     */
    @Transactional
    @Override
    public SelectionDto select(UUID requestId, UUID candidateId, String pdfPath) {
        HeatingRequest req = heatingRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("HeatingRequest not found: " + requestId));

        ConfigCandidate cand = configCandidateRepository.findById(candidateId)
                .orElseThrow(() -> new NoSuchElementException("ConfigCandidate not found: " + candidateId));

        // проверим, что кандидат принадлежит запросу
        if (!cand.getRequest().getId().equals(requestId)) {
            throw new IllegalArgumentException("Candidate does not belong to the given request");
        }

        // upsert выбора
        Selection selection = selectionRepository.findByRequest_Id(requestId).orElse(null);
        if (selection == null) {
            selection = Selection.builder()
                    .request(req)
                    .candidate(cand)
                    .pdfPath(pdfPath)
                    .build();
        } else {
            selection.setCandidate(cand);
            if (pdfPath != null) selection.setPdfPath(pdfPath);
            // хотим обновить метку времени выбора — перезатрём вручную
            selection.setSelectedAt(OffsetDateTime.now());
        }

        // статус запроса: SELECTED
        req.setStatus(HeatingRequestStatus.selected);
        heatingRequestRepository.save(req);

        Selection saved = selectionRepository.save(selection);
        return toDto(saved);
    }

    /**
     * Удалить выбор для запроса.
     * По желанию возвращаем запрос к статусу PROPOSED (если есть сгенерированные варианты),
     * иначе оставляем CREATED — скорректируйте под свою логику.
     */
    @Transactional
    @Override
    public void deleteByRequest(UUID requestId, HeatingRequestStatus statusAfterDelete) {
        if (!selectionRepository.existsByRequest_Id(requestId)) return;
        selectionRepository.deleteByRequest_Id(requestId);

        HeatingRequest req = heatingRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("HeatingRequest not found: " + requestId));

        if (statusAfterDelete != null) {
            req.setStatus(statusAfterDelete);
            heatingRequestRepository.save(req);
        }
    }
    public record SelectionDto(UUID id, UUID requestId, UUID candidateId,
                               OffsetDateTime selectedAt, String pdfPath) {
    }

    private SelectionDto toDto(Selection s) {
        return new SelectionDto(
                s.getId(),
                s.getRequest().getId(),
                s.getCandidate().getId(),
                s.getSelectedAt(),
                s.getPdfPath()
        );
    }
}
