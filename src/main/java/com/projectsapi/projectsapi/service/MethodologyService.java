package com.projectsapi.projectsapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.projectsapi.projectsapi.model.Methodology;
import com.projectsapi.projectsapi.repository.MethodologyRepository;
import java.util.List;
import java.util.Optional;

@Service
public class MethodologyService {

    @Autowired
    private MethodologyRepository methodologyRepository;

    public List<Methodology> getAllMethodologies() {
        return methodologyRepository.findAll();
    }

    public Optional<Methodology> getMethodologyById(Integer id) {
        return methodologyRepository.findById(id);
    }

    public Methodology createMethodology(Methodology methodology) {
        return methodologyRepository.save(methodology);
    }

    public Methodology updateMethodology(Integer id, Methodology updatedMethodology) {
        Methodology existingMethodology = methodologyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Methodology not found"));

        existingMethodology.setName(updatedMethodology.getName());

        return methodologyRepository.save(existingMethodology);
    }

    public void deleteMethodology(Integer id) {
        Methodology methodology = methodologyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Methodology not found"));
        methodologyRepository.deleteById(id);
    }
}
