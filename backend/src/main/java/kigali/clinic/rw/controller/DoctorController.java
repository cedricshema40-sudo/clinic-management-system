
package kigali.clinic.rw.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import java.time.format.DateTimeParseException;
import kigali.clinic.rw.domain.Appointment;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping({"/api/doctor", "/api/doctors"})
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // Save a doctor.
    @PostMapping("/save")
    public Doctor saveDoctor(@RequestBody Doctor doctor) {
        return doctorService.saveDoctor(doctor);
    }

    // Get all doctors.
    @GetMapping
    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    // Get a doctor by ID.
    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id);
    }

    // Update a doctor.
    @PutMapping("/{id}")
    public Doctor updateDoctor(
            @PathVariable UUID id,
            @RequestBody Doctor doctor) {

        return doctorService.updateDoctor(id, doctor);
    }

    // Delete a doctor.
    @DeleteMapping("/{id}")
    public String deleteDoctor(@PathVariable UUID id) {
        doctorService.deleteDoctor(id);
        return "Doctor deleted successfully";
    }

    // Find doctors by specialization.
    @GetMapping("/by-specialization")
    public List<Doctor> getDoctorsBySpecialization(
            @RequestParam String name) {

        return doctorService.getDoctorsBySpecialization(name);
    }

    // Find doctors who do not have an office.
    @GetMapping("/without-office")
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorService.getDoctorsWithoutOffice();
    }

    // Add a specialization to a doctor.
    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecializationToDoctor(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message = doctorService.addSpecializationToDoctor(
                doctorId, specializationId);

        if (message.equals("The doctor with that id does not exist")
                || message.equals("That specialization does not exist")) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message);
        }

        if (message.equals("The doctor already has that specialization")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(message);
        }

        return ResponseEntity.ok(message);
    }

    // Remove a specialization from a doctor.
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecializationFromDoctor(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String message = doctorService.removeSpecializationFromDoctor(
                doctorId, specializationId);

        if (message.equals("The doctor with that id does not exist")
                || message.equals("That specialization does not exist")) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(message);
        }

        if (message.equals("The doctor does not have that specialization")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(message);
        }

        return ResponseEntity.ok(message);
    }

    // Change a doctor's office.
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> changeDoctorOffice(
            @PathVariable UUID doctorId,
            @RequestParam int officeNumber) {

        String result = doctorService.changeDoctorOffice(
                doctorId, officeNumber);

        if (result.equals("The doctor with that id does not exist")
                || result.equals(
                        "The office with that office number does not exist")) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(result);
        }

        if (result.equals("That office is taken by other doctor")) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(result);
        }

        return ResponseEntity.ok(result);
    }

    // Get the number of completed appointments for each doctor.
    @GetMapping("/workload")
    public ResponseEntity<List<Object[]>> getDoctorWorkload(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(start);
            endDate = LocalDate.parse(end);
        } catch (java.time.format.DateTimeParseException exception) {
            return ResponseEntity.badRequest().build();
        }

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().build();
        }

        List<Object[]> workload = doctorService.getDoctorWorkload(
                Date.valueOf(startDate),
                Date.valueOf(endDate));

        return ResponseEntity.ok(workload);
    }
    // GEt a doctor agenda for a specific date.
    @GetMapping("/{doctorId}/agenda")
    public ResponseEntity<?> getDoctorAgenda(
        @PathVariable UUID doctorId,
        @RequestParam String date) {

          LocalDate localDate;
          
          try {
            localDate = LocalDate.parse(date);
          } catch (DateTimeParseException exception) {
            return ResponseEntity.badRequest()
                .body("Date must use the format yyyy-MM-dd");
          }

          try {
            List<Appointment> appointments = 
               doctorService.getDoctorAgenda(
                  doctorId,
                  Date.valueOf(localDate));

                  return ResponseEntity.ok(appointments);
          } catch (ResponseStatusException exception) {
             if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                  return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(exception.getReason());
            }

             throw exception;
          }
        }
    
    // D3 - Search doctors by first name or last name.
    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctorsByName(
            @RequestParam String name) {

        return ResponseEntity.ok(
                doctorService.searchDoctorsByName(name));
    }

}