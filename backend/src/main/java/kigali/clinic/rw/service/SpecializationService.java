package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;

    public SpecializationService(
            SpecializationRepository specializationRepository) {

        this.specializationRepository = specializationRepository;
    }

    // Save specialization
    public Specialization saveSpecialization(
            Specialization specialization) {

        return specializationRepository.save(specialization);
    }

    // Get all specializations
    public List<Specialization> getAllSpecializations() {

        return specializationRepository.findAll();
    }

    // Get specialization by ID
    public Specialization getSpecializationById(UUID id) {

        return specializationRepository
                .findById(id)
                .orElse(null);
    }

    // Update specialization
    public Specialization updateSpecialization(
            UUID id,
            Specialization specializationDetails) {

        Specialization specialization =
                specializationRepository
                        .findById(id)
                        .orElse(null);

        if (specialization == null) {
            return null;
        }

        specialization.setName(
                specializationDetails.getName());

        return specializationRepository.save(
                specialization);
    }

    // Delete specialization
    public void deleteSpecialization(UUID id) {

        specializationRepository.deleteById(id);
    }

    // B3 - Get unused specializations
    public List<Specialization> getUnusedSpecializations() {

        return specializationRepository
                .findUnusedSpecializations();
    }
}