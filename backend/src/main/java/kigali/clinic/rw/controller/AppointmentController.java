
package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping({"/api/appointment", "/api/appointments"})
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    // Create a new appointment.
    @PostMapping("/save")
    public ResponseEntity<Appointment> saveAppointment(
            @RequestBody Appointment appointment) {

        Appointment savedAppointment =
                appointmentService.saveAppointment(appointment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedAppointment);
    }

    // Get pending appointments for a doctor.
    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<Appointment>> getPendingAppointmentsByDoctor(
            @PathVariable UUID doctorId) {

        return ResponseEntity.ok(
                appointmentService.getPendingAppointmentsByDoctor(doctorId));
    }

    // Get past appointments that are still scheduled.
    @GetMapping("/past-pending")
    public ResponseEntity<List<Appointment>> getPastPendingAppointments() {

        return ResponseEntity.ok(
                appointmentService.getPastPendingAppointments());
    }

    // Count appointments for each doctor.
    @GetMapping("/count-per-doctor")
    public ResponseEntity<List<Object[]>> countAppointmentsPerDoctor() {

        return ResponseEntity.ok(
                appointmentService.countAppointmentsPerDoctor());
    }

    // Find appointments related to a specialization.
    @GetMapping("/specialization/{specializationId}")
    public ResponseEntity<List<Appointment>> getAppointmentsBySpecialization(
            @PathVariable UUID specializationId) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsBySpecialization(
                        specializationId));
    }

    // Get a doctor's appointments using pagination.
    @GetMapping("/doctor/{doctorId}/page")
    public ResponseEntity<Page<Appointment>> getAppointmentsByDoctor(
            @PathVariable UUID doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByDoctor(
                        doctorId, pageable));
    }

    // Find appointments by status.
    @GetMapping("/by-status")
    public ResponseEntity<List<Appointment>> getAppointmentsByStatus(
            @RequestParam AppointmentStatus status) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsByStatus(status));
    }

    // Find appointments between two dates.
    @GetMapping("/between")
    public ResponseEntity<List<Appointment>> getAppointmentsBetweenDates(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsBetweenDates(
                        startDate, endDate));
    }

    // Count appointments grouped by status.
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getAppointmentStatsByStatus() {

        return ResponseEntity.ok(
                appointmentService.getAppointmentStatsByStatus());
    }

    // Cancel a doctor's appointments on a specified date.
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelAppointmentsForDoctorOnDate(
            @RequestParam UUID doctorId,
            @RequestParam String date) {

        return ResponseEntity.ok(
                appointmentService.cancelAppointmentsForDoctorOnDate(
                        doctorId, date));
    }

    // Get all appointments using pagination.
    @GetMapping("/page")
    public ResponseEntity<Page<Appointment>> getAppointmentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentsPage(page, size, sortBy));
    }

    // Change an appointment's status.
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable UUID id,
            @RequestParam AppointmentStatus status) {

        appointmentService.updateAppointmentStatus(id, status);

        return ResponseEntity.ok(
                "Appointment status updated successfully");
    }

    // Reschedule an appointment.
    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable UUID id,
            @RequestParam String newDate) {

        appointmentService.rescheduleAppointment(id, newDate);

        return ResponseEntity.ok(
                "Appointment rescheduled successfully");
    }

    // Delete cancelled appointments older than a specified date.
    @DeleteMapping("/cancelled-before")
    public ResponseEntity<String> deleteCancelledAppointmentsBefore(
            @RequestParam String date) {

        int deleted = appointmentService
                .deleteCancelledAppointmentsBefore(date);

        return ResponseEntity.ok(
                deleted + " cancelled appointment(s) deleted");
    }

    // Delete one appointment only if it has no prescriptions.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(
            @PathVariable UUID id) {

        appointmentService.deleteAppointment(id);

        return ResponseEntity.ok("Appointment deleted successfully");
    }

    // Return the status code and message for service exceptions.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(
            ResponseStatusException exception) {

        return ResponseEntity
                .status(exception.getStatusCode())
                .body(exception.getReason());
    }
}