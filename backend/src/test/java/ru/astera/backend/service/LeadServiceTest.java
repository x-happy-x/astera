package ru.astera.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.astera.backend.dto.LeadRegistrationDto;
import ru.astera.backend.entity.Lead;
import ru.astera.backend.exception.LeadAlreadyExistsException;
import ru.astera.backend.repository.LeadRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private LeadService leadService;

    private LeadRegistrationDto leadDto;
    private Lead existingLead;

    @BeforeEach
    void setUp() {
        leadDto = new LeadRegistrationDto();
        leadDto.setFullName("Test Client");
        leadDto.setPhone("+7(999)123-45-67");
        leadDto.setEmail("client@test.com");
        leadDto.setOrganization("Test Organization");
        leadDto.setConsentPersonalData(true);

        existingLead = new Lead();
        existingLead.setFullName("Test Client");
        existingLead.setPhone("+7(999)123-45-67");
        existingLead.setEmail("client@test.com");
        existingLead.setOrganization("Test Organization");
        existingLead.setConsentPersonalData(true);
    }

    @Test
    void createLead_Success() {
        when(leadRepository.existsByEmailAndPhone(leadDto.getEmail(), leadDto.getPhone())).thenReturn(false);
        when(leadRepository.save(any(Lead.class))).thenReturn(existingLead);

        Lead result = leadService.createLead(leadDto);

        assertNotNull(result);
        assertEquals(leadDto.getFullName(), result.getFullName());
        assertEquals(leadDto.getPhone(), result.getPhone());
        assertEquals(leadDto.getEmail(), result.getEmail());
        assertEquals(leadDto.getOrganization(), result.getOrganization());
        assertTrue(result.getConsentPersonalData());

        verify(leadRepository).existsByEmailAndPhone(leadDto.getEmail(), leadDto.getPhone());
        verify(leadRepository).save(any(Lead.class));
    }

    @Test
    void createLead_LeadAlreadyExists() {
        when(leadRepository.existsByEmailAndPhone(leadDto.getEmail(), leadDto.getPhone())).thenReturn(true);

        assertThrows(LeadAlreadyExistsException.class, () -> leadService.createLead(leadDto));

        verify(leadRepository).existsByEmailAndPhone(leadDto.getEmail(), leadDto.getPhone());
        verify(leadRepository, never()).save(any(Lead.class));
    }
}