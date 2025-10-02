package ru.astera.backend.dto.selection;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationSelectionResponse {
    private List<ConfigurationCandidateDto> candidates = new ArrayList<>();

    public List<ConfigurationCandidateDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<ConfigurationCandidateDto> candidates) {
        this.candidates = candidates;
    }
}
