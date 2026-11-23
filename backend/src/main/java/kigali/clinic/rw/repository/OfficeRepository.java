package kigali.clinic.rw.repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kigali.clinic.rw.domain.Office;

public interface OfficeRepository
        extends JpaRepository<Office, UUID> {

    // C3 - Find the busiest office
    //
    
    @Query("""
            SELECT o.name, o.officeNumber, COUNT(a)
            FROM Appointment a
            JOIN a.doctor d
            JOIN d.office o
            GROUP BY o
            ORDER BY COUNT(a) DESC
            """)
    List<Object[]> findBusiestOffice();
        
   //A3 TO FIND OFFICE WITHOUT A DOCTOR
   @Query("""
         SELECT o
         FROM Office o 
         WHERE NOT EXISTS (
               SELECT d
               FROM Doctor d 
               WHERE d.office = o         
   )
     ORDER BY o.officeNumber ASC
   """)
   List<Office> findFreeOffice();
   Optional<Office> findByOfficeNumber(int officeNumber);
}
