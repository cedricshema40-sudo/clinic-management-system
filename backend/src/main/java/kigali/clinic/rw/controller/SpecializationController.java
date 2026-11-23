package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.service.SpecializationService;

@RestController
@RequestMapping("/api/specialization")
public class SpecializationController {

    private final SpecializationService specializationService;

    public SpecializationController(
            SpecializationService specializationService) {

        this.specializationService = specializationService;
    }

    // Save specialization
    @PostMapping("/save")
    public Specialization saveSpecialization(
            @RequestBody Specialization specialization) {

        return specializationService
                .saveSpecialization(specialization);
    }

    // Get all specializations
    @GetMapping
    public List<Specialization> getAllSpecializations() {

        return specializationService
                .getAllSpecializations();
    }

    // Get specialization by ID
    @GetMapping("/{id}")
    public Specialization getSpecializationById(
            @PathVariable UUID id) {

        return specializationService
                .getSpecializationById(id);
    }

    // Update specialization
    @PutMapping("/{id}")
    public Specialization updateSpecialization(
            @PathVariable UUID id,
            @RequestBody Specialization specialization) {

        return specializationService
                .updateSpecialization(
                        id,
                        specialization);
    }

    // Delete specialization
    @DeleteMapping("/{id}")
    public String deleteSpecialization(
            @PathVariable UUID id) {

        specializationService
                .deleteSpecialization(id);

        return "Specialization deleted successfully";
    }

    // B3 - Unused specializations
    @GetMapping("/unused")
    public List<Specialization> getUnusedSpecializations() {

        return specializationService
                .getUnusedSpecializations();
    }
}