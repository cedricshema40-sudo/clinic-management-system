
package kigali.clinic.rw.repository;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;

public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    // Find appointments for a doctor with a specific status.
    List<Appointment> findByDoctor_IdAndStatusOrderByAppointmentDateAsc(
            UUID doctorId,
            AppointmentStatus status);

    // Find appointments with a specific status.
    List<Appointment> findByStatusOrderByAppointmentDateAsc(
            AppointmentStatus status);

    // Find appointments between two dates.
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            Date start,
            Date end);

    // Find past appointments with the requested status.
    @Query("""
            SELECT a
            FROM Appointment a
            WHERE a.status = :status
              AND a.appointmentDate < CURRENT_TIMESTAMP
            ORDER BY a.appointmentDate ASC
            """)
    List<Appointment> findPastPendingAppointments(
            @Param("status") AppointmentStatus status);

    // Count all appointments for each doctor.
    @Query("""
            SELECT a.doctor, COUNT(a)
            FROM Appointment a
            GROUP BY a.doctor
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> countAppointmentsPerDoctor();

    // Count completed appointments for each doctor within a date range.
    @Query("""
            SELECT a.doctor, COUNT(a)
            FROM Appointment a
            WHERE a.status = :status
              AND a.appointmentDate BETWEEN :start AND :end
            GROUP BY a.doctor
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> countDoctorWorkload(
            @Param("status") AppointmentStatus status,
            @Param("start") Date start,
            @Param("end") Date end);

    // Find appointments associated with a specialization.
    @Query("""
            SELECT a
            FROM Appointment a
            JOIN a.doctor d
            JOIN d.specializations s
            WHERE s.id = :specializationId
            ORDER BY a.appointmentDate ASC
            """)
    List<Appointment> findAppointmentsBySpecialization(
            @Param("specializationId") UUID specializationId);

    // Find a doctor's appointments using pagination.
    Page<Appointment> findByDoctor_Id(
            UUID doctorId,
            Pageable pageable);

    // Check whether a doctor has another appointment on the same date.
    boolean existsByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            Date appointmentDate,
            AppointmentStatus status);

    // Find a doctor's appointments on a date, excluding a status.
    List<Appointment> findByDoctor_IdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            Date appointmentDate,
            AppointmentStatus status);

    // Count appointments grouped by status.
    @Query("""
            SELECT a.status, COUNT(a)
            FROM Appointment a
            GROUP BY a.status
            """)
    List<Object[]> countAppointmentsByStatus();

    // Cancel a doctor's appointments on a specific date.
    @Modifying
    @Transactional
    @Query("""
            UPDATE Appointment a
            SET a.status = :cancelledStatus
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :appointmentDate
              AND a.status <> :completedStatus
            """)
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("appointmentDate") Date appointmentDate,
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("completedStatus") AppointmentStatus completedStatus);

    // Return all appointments with pagination.
    Page<Appointment> findAll(Pageable pageable);

    // Delete cancelled appointments older than a given date.
    @Modifying
    @Transactional
    @Query("""
            DELETE FROM Appointment a
            WHERE a.status = :cancelledStatus
              AND a.appointmentDate < :appointmentDate
            """)
    int deleteCancelledAppointmentsBefore(
            @Param("cancelledStatus") AppointmentStatus cancelledStatus,
            @Param("appointmentDate") Date appointmentDate);

    // D2 - Find upcoming appointments for a patient.
    List<Appointment>
    findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber,
            Date today,
            List<AppointmentStatus> statuses);

    // D4 - Count appointments starting on or after a date
    // and before the next date.
    long countByAppointmentDateGreaterThanEqualAndAppointmentDateLessThan(
            Date start,
            Date end);

    // D4 - Count appointments with a particular status.
    long countByStatus(AppointmentStatus status);
}
