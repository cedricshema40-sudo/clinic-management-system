
package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class OfficeService {

    private final OfficeRepository officeRepository;

    public OfficeService(OfficeRepository officeRepository) {
        this.officeRepository = officeRepository;
    }

    // Save a new office
    public Office saveOffice(Office office) {
        return officeRepository.save(office);
    }

    // Get all offices
    public List<Office> getAllOffices() {
        return officeRepository.findAll();
    }

    // Get an office by ID
    public Office getOfficeById(UUID id) {
        return officeRepository.findById(id).orElse(null);
    }

    // Update an existing office
    public Office updateOffice(UUID id, Office officeDetails) {

        Office office = officeRepository.findById(id).orElse(null);

        if (office == null) {
            return null;
        }

        office.setName(officeDetails.getName());
        office.setOfficeNumber(officeDetails.getOfficeNumber());

        return officeRepository.save(office);
    }

    // Delete an office
    public void deleteOffice(UUID id) {
        officeRepository.deleteById(id);
    }

    // C3 - Find the busiest office
    public String getBusiestOffice() {

        List<Object[]> results = officeRepository.findBusiestOffice();

        if (results.isEmpty()) {
            return "No appointments yet";
        }

        Object[] row = results.get(0);

        String officeName = String.valueOf(row[0]);
        int officeNumber = ((Number) row[1]).intValue();
        long appointmentCount = ((Number) row[2]).longValue();

        return officeName
                + " (Office "
                + officeNumber
                + ") - "
                + appointmentCount
                + " appointments";
    }

    // A3 - Find offices without a doctor
    public List<Office> getFreeOffices() {
        return officeRepository.findFreeOffice();
    }
}
