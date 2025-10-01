package ru.astera.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.astera.backend.dto.LeadRegistrationDto;
import ru.astera.backend.entity.Lead;
import ru.astera.backend.exception.LeadAlreadyExistsException;
import ru.astera.backend.repository.LeadRepository;

@Service
@RequiredArgsConstructor
public class LeadService {
    
    private final LeadRepository leadRepository;
    
    public Lead createLead(LeadRegistrationDto dto) {
        if (leadRepository.existsByEmailAndPhone(dto.getEmail(), dto.getPhone())) {
            throw new LeadAlreadyExistsException("Клиент с такими контактными данными уже зарегистрирован");
        }
        
        Lead lead = new Lead();
        lead.setFullName(dto.getFullName());
        lead.setPhone(dto.getPhone());
        lead.setEmail(dto.getEmail());
        lead.setOrganization(dto.getOrganization());
        lead.setConsentPersonalData(dto.getConsentPersonalData());
        
        return leadRepository.save(lead);
    }
}