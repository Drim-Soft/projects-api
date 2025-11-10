package com.projectsapi.projectsapi.service;

import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MethodologyServiceTest {

    @Mock
    private MethodologyRepository methodologyRepository;

    @InjectMocks
    private MethodologyService methodologyService;

    private Methodology testMethodology;

    @BeforeEach
    void setUp() {
        testMethodology = new Methodology();
        testMethodology.setIDMethodology(1);
        testMethodology.setName("Metodología Ágil");
    }

    @Test
    void testGetAllMethodologies() {
        // Given
        List<Methodology> expectedMethodologies = Arrays.asList(testMethodology);
        when(methodologyRepository.findAll()).thenReturn(expectedMethodologies);

        // When
        List<Methodology> result = methodologyService.getAllMethodologies();

        // Then
        assertEquals(1, result.size());
        assertEquals(testMethodology.getName(), result.get(0).getName());
        verify(methodologyRepository).findAll();
    }

    @Test
    void testGetMethodologyById() {
        // Given
        when(methodologyRepository.findById(1)).thenReturn(Optional.of(testMethodology));

        // When
        Optional<Methodology> result = methodologyService.getMethodologyById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMethodology.getName(), result.get().getName());
        verify(methodologyRepository).findById(1);
    }

    @Test
    void testGetMethodologyById_NotFound() {
        // Given
        when(methodologyRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Methodology> result = methodologyService.getMethodologyById(999);

        // Then
        assertFalse(result.isPresent());
        verify(methodologyRepository).findById(999);
    }

    @Test
    void testCreateMethodology() {
        // Given
        when(methodologyRepository.save(any(Methodology.class))).thenReturn(testMethodology);

        // When
        Methodology result = methodologyService.createMethodology(testMethodology);

        // Then
        assertNotNull(result);
        assertEquals(testMethodology.getName(), result.getName());
        verify(methodologyRepository).save(testMethodology);
    }

    @Test
    void testUpdateMethodology() {
        // Given
        Methodology updatedMethodology = new Methodology();
        updatedMethodology.setName("Metodología Actualizada");

        when(methodologyRepository.findById(1)).thenReturn(Optional.of(testMethodology));
        when(methodologyRepository.save(any(Methodology.class))).thenReturn(testMethodology);

        // When
        Methodology result = methodologyService.updateMethodology(1, updatedMethodology);

        // Then
        assertNotNull(result);
        assertEquals("Metodología Actualizada", result.getName());
        verify(methodologyRepository).save(testMethodology);
    }

    @Test
    void testUpdateMethodology_NotFound() {
        // Given
        Methodology updatedMethodology = new Methodology();
        when(methodologyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> methodologyService.updateMethodology(999, updatedMethodology));
        assertEquals("Methodology not found", exception.getMessage());
    }

    @Test
    void testDeleteMethodology() {
        // Given
        when(methodologyRepository.findById(1)).thenReturn(Optional.of(testMethodology));

        // When
        methodologyService.deleteMethodology(1);

        // Then
        verify(methodologyRepository).deleteById(1);
    }

    @Test
    void testDeleteMethodology_NotFound() {
        // Given
        when(methodologyRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> methodologyService.deleteMethodology(999));
        assertEquals("Methodology not found", exception.getMessage());
    }
}
