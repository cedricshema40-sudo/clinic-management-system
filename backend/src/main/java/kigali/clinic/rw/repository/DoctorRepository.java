
package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kigali.clinic.rw.domain.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    boolean existsByOffice_Id(UUID officeId);

    boolean existsByOffice_IdAndIdNot(
            UUID officeId,
            UUID doctorId
    );

    // B1 - Find doctors by specialization.
    @Query("""
            SELECT d
            FROM Doctor d
            JOIN d.specializations s
            WHERE LOWER(s.name) = LOWER(:name)
            """)
    List<Doctor> findDoctorsBySpecialization(
            @Param("name") String name
    );

    // B2 - Find doctors who do not have an office.
    @Query("""
            SELECT d
            FROM Doctor d
            WHERE d.office IS NULL
            ORDER BY d.lastName ASC
            """)
    List<Doctor> findDoctorsWithoutOffice();

    // D3 - Search doctors by first name or last name.
    @Query("""
            SELECT d
            FROM Doctor d
            WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
               OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY d.lastName ASC
            """)
    List<Doctor> searchDoctorsByName(
            @Param("name") String name
    );
}