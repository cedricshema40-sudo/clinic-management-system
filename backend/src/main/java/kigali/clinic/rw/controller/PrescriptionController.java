
package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // Save a prescription.
    @PostMapping("/save")
    public ResponseEntity<String> savePrescription(
            @RequestParam UUID appointmentId,
            @RequestBody Prescription prescription) {

        prescriptionService.savePrescription(appointmentId, prescription);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Prescription saved successfully");
    }

    // Get prescriptions for an appointment.
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<Prescription>> getByAppointment(
            @PathVariable UUID appointmentId) {

        return ResponseEntity.ok(
                prescriptionService.getPrescriptionsByAppointment(appointmentId));
    }

    // Get prescriptions for a patient.
    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<List<Prescription>> getByPatient(
            @PathVariable String ssNumber) {

        return ResponseEntity.ok(
                prescriptionService.getPrescriptionsByPatient(ssNumber));
    }

    // Update a prescription.
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescription) {

        prescriptionService.updatePrescription(id, prescription);

        return ResponseEntity.ok("Prescription updated successfully");
    }

    // Delete a prescription.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {

        prescriptionService.deletePrescription(id);

        return ResponseEntity.ok("Prescription deleted successfully");
    }

    // Show medicines from most prescribed to least prescribed.
    @GetMapping("/stats/top-medicines")
    public ResponseEntity<List<Object[]>> getTopMedicines() {

        List<Object[]> medicines = prescriptionService.getTopMedicines();

        return ResponseEntity.ok(medicines);
    }
}