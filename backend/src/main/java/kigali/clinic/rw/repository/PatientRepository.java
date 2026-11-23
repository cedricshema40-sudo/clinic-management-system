
package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinic.rw.domain.Patient;

public interface PatientRepository
        extends JpaRepository<Patient, UUID> {

    // A1 - Find patients by last name.
    List<Patient> findByLastNameIgnoreCaseOrderByFirstNameAsc(
            String lastName
    );

    // B4 - Find patients who have appointments with a doctor.
    @Query("""
            SELECT DISTINCT p
            FROM Patient p
            JOIN p.appointments a
            WHERE a.doctor.id = :doctorId
            """)
    List<Patient> findPatientsOfDoctor(
            @Param("doctorId") UUID doctorId
    );

    // C2 - Find frequent patients.
    @Query("""
            SELECT p
            FROM Patient p
            JOIN p.appointments a
            GROUP BY p
            HAVING COUNT(a) >= :min
            ORDER BY COUNT(a) DESC
            """)
    List<Patient> findFrequentPatients(
            @Param("min") long min
    );
    // D2 - Find a patient using their social security number.
    Optional<Patient> findBySsNumber(String ssNumber);
}
