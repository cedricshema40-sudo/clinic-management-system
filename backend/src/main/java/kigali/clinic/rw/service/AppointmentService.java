
package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository) {

        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
    }

    // Save a new appointment.
    public Appointment saveAppointment(Appointment appointment) {

        if (appointment.getAppointmentDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date is required");
        }

        Date today = Date.valueOf(LocalDate.now());

        if (appointment.getAppointmentDate().before(today)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }

        if (appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A doctor must be selected");
        }

        boolean doctorIsBooked =
                appointmentRepository
                        .existsByDoctor_IdAndAppointmentDateAndStatusNot(
                                appointment.getDoctor().getId(),
                                appointment.getAppointmentDate(),
                                AppointmentStatus.CANCELLED);

        if (doctorIsBooked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The doctor already has an appointment on that date");
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    // Find pending appointments for a doctor.
    public List<Appointment> getPendingAppointmentsByDoctor(UUID doctorId) {

        return appointmentRepository
                .findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
                        doctorId,
                        AppointmentStatus.SCHEDULED);
    }

    // Find past appointments that are still scheduled.
    public List<Appointment> getPastPendingAppointments() {

        return appointmentRepository.findPastPendingAppointments(
                AppointmentStatus.SCHEDULED);
    }

    // Count appointments for each doctor.
    public List<Object[]> countAppointmentsPerDoctor() {

        return appointmentRepository.countAppointmentsPerDoctor();
    }

    // Find appointments related to a specialization.
    public List<Appointment> getAppointmentsBySpecialization(
            UUID specializationId) {

        return appointmentRepository
                .findAppointmentsBySpecialization(specializationId);
    }

    // Find appointments for one doctor using pagination.
    public Page<Appointment> getAppointmentsByDoctor(
            UUID doctorId,
            Pageable pageable) {

        return appointmentRepository.findByDoctor_Id(doctorId, pageable);
    }

    // Find appointments by status.
    public List<Appointment> getAppointmentsByStatus(
            AppointmentStatus status) {

        return appointmentRepository
                .findByStatusOrderByAppointmentDateAsc(status);
    }

    // Find appointments between two dates.
    public List<Appointment> getAppointmentsBetweenDates(
            String startDate,
            String endDate) {

        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        if (start.after(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date cannot be after end date");
        }

        return appointmentRepository
                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                        start, end);
    }

    // Count appointments grouped by status.
    public List<Object[]> getAppointmentStatsByStatus() {

        return appointmentRepository.countAppointmentsByStatus();
    }

    // Cancel appointments for a doctor on a specified date.
    public String cancelAppointmentsForDoctorOnDate(
            UUID doctorId,
            String appointmentDate) {

        Date date = parseDate(appointmentDate);

        int updated = appointmentRepository
                .cancelAppointmentsForDoctorOnDate(
                        doctorId,
                        date,
                        AppointmentStatus.CANCELLED,
                        AppointmentStatus.COMPLETED);

        return updated + " appointment(s) cancelled";
    }

    // Get appointments using pagination and sorting.
    public Page<Appointment> getAppointmentsPage(
            int page,
            int size,
            String sortBy) {

        if (page < 0 || size <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be zero or greater and size must be positive");
        }

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "appointmentDate";
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortBy).ascending());

        return appointmentRepository.findAll(pageable);
    }

    // Delete cancelled appointments older than a specified date.
    public int deleteCancelledAppointmentsBefore(String date) {

        Date cutoffDate = parseDate(date);

        return appointmentRepository.deleteCancelledAppointmentsBefore(
                AppointmentStatus.CANCELLED,
                cutoffDate);
    }

    // Delete an appointment only if it has no prescriptions.
    public void deleteAppointment(UUID appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        boolean hasPrescriptions =
                prescriptionRepository.existsByAppointment_Id(appointmentId);

        if (hasPrescriptions) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "That appointment has prescriptions, delete them first");
        }

        appointmentRepository.delete(appointment);
    }

    // Change the status of an appointment.
    public Appointment updateAppointmentStatus(
            UUID appointmentId,
            AppointmentStatus newStatus) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        AppointmentStatus currentStatus = appointment.getStatus();

        boolean validChange =
                (currentStatus == AppointmentStatus.SCHEDULED
                        && (newStatus == AppointmentStatus.CONFIRMED
                        || newStatus == AppointmentStatus.CANCELLED))
                || (currentStatus == AppointmentStatus.CONFIRMED
                        && (newStatus == AppointmentStatus.COMPLETED
                        || newStatus == AppointmentStatus.CANCELLED));

        if (!validChange) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This appointment status change is not allowed");
        }

        appointment.setStatus(newStatus);

        return appointmentRepository.save(appointment);
    }

    // Reschedule an appointment.
    public Appointment rescheduleAppointment(
            UUID appointmentId,
            String newDate) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed or cancelled appointments cannot be rescheduled");
        }

        Date date = parseDate(newDate);
        Date today = Date.valueOf(LocalDate.now());

        if (date.before(today)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Appointment date cannot be in the past");
        }

        if (appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The appointment does not have a doctor");
        }

        List<Appointment> sameDayAppointments =
                appointmentRepository
                        .findByDoctor_IdAndAppointmentDateAndStatusNot(
                                appointment.getDoctor().getId(),
                                date,
                                AppointmentStatus.CANCELLED);

        for (Appointment other : sameDayAppointments) {
            if (!other.getId().equals(appointmentId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "The doctor already has an appointment on that date");
            }
        }

        appointment.setAppointmentDate(date);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    // D2 - Find upcoming appointments for a patient.
    public List<Appointment> getUpcomingAppointments(String ssNumber) {

        patientRepository.findBySsNumber(ssNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "That patient with that ssnumber does not exist"));

        Date today = Date.valueOf(LocalDate.now());

        List<AppointmentStatus> statuses = List.of(
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.CONFIRMED);

        return appointmentRepository
                .findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber,
                        today,
                        statuses);
    }

    // Convert a date string into SQL Date.
    private Date parseDate(String date) {

        try {
            return Date.valueOf(LocalDate.parse(date));
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date must use the format yyyy-MM-dd");
        }
    }
}
