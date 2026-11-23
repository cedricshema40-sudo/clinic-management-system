
package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.AppointmentService;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping({"/api/patient", "/api/patients"})
public class PatientController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    // Constructor
    public PatientController(
            PatientService patientService,
            AppointmentService appointmentService) {

        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    // Save a patient.
    // POST /api/patient/save
    @PostMapping("/save")
    public Patient savePatient(@RequestBody Patient patient) {
        return patientService.savePatient(patient);
    }

    // Get all patients.
    // GET /api/patient
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    // Get a patient by ID.
    // GET /api/patient/{id}
    @GetMapping("/{id}")
    public Patient getPatientById(@PathVariable UUID id) {
        return patientService.getPatientById(id);
    }

    // Update a patient.
    // PUT /api/patient/{id}
    @PutMapping("/{id}")
    public Patient updatePatient(
            @PathVariable UUID id,
            @RequestBody Patient patient) {

        return patientService.updatePatient(id, patient);
    }

    // Delete a patient.
    // DELETE /api/patient/{id}
    @DeleteMapping("/{id}")
    public String deletePatient(@PathVariable UUID id) {
        patientService.deletePatient(id);
        return "Patient deleted successfully";
    }

    // A1 - Find patients by last name.
    // GET /api/patient/by-last-name?lastName=uwase
    @GetMapping("/by-last-name")
    public List<Patient> getPatientsByLastName(
            @RequestParam String lastName) {

        return patientService.getPatientsByLastName(lastName);
    }

    // B4 - Find patients of a doctor.
    // GET /api/patient/of-doctor/{doctorId}
    @GetMapping("/of-doctor/{doctorId}")
    public List<Patient> getPatientsOfDoctor(
            @PathVariable UUID doctorId) {

        try {
            return patientService.getPatientsOfDoctor(doctorId);

        } catch (RuntimeException e) {
            if (e.getMessage() != null
                    && e.getMessage().equals(
                            "The doctor with that id does not exist")) {

                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The doctor with that id does not exist");
            }

            throw e;
        }
    }

    // C2 - Find frequent patients.
    // GET /api/patient/frequent?min=3
    @GetMapping("/frequent")
    public List<Patient> getFrequentPatients(
            @RequestParam long min) {

        return patientService.getFrequentPatients(min);
    }

    // D2 - Find upcoming appointments for a patient.
    // GET /api/patients/{ssNumber}/upcoming
    @GetMapping("/{ssNumber}/upcoming")
    public List<Appointment> getUpcomingAppointments(
            @PathVariable String ssNumber) {

        return appointmentService.getUpcomingAppointments(ssNumber);
    }
}
