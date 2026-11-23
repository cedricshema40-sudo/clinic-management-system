
package kigali.clinic.rw.service;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final OfficeRepository officeRepository;
    private final SpecializationRepository specializationRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorService(
            DoctorRepository doctorRepository,
            OfficeRepository officeRepository,
            SpecializationRepository specializationRepository,
            AppointmentRepository appointmentRepository) {

        this.doctorRepository = doctorRepository;
        this.officeRepository = officeRepository;
        this.specializationRepository = specializationRepository;
        this.appointmentRepository = appointmentRepository;
    }

    // Save a doctor.
    public Doctor saveDoctor(Doctor doctor) {

        // Check whether the office exists and is available.
        if (doctor.getOffice() != null
                && doctor.getOffice().getId() != null) {

            UUID officeId = doctor.getOffice().getId();

            if (doctorRepository.existsByOffice_Id(officeId)) {
                throw new RuntimeException(
                        "The office is already assigned to another doctor");
            }

            Office office = officeRepository.findById(officeId)
                    .orElseThrow(() -> new RuntimeException(
                            "The office with that id does not exist"));

            doctor.setOffice(office);
        }

        // Check that the specializations exist.
        if (doctor.getSpecializations() != null) {

            List<Specialization> specializations =
                    doctor.getSpecializations()
                            .stream()
                            .map(specialization -> {
                                UUID specializationId =
                                        specialization.getId();

                                return specializationRepository
                                        .findById(specializationId)
                                        .orElseThrow(() -> new RuntimeException(
                                                "The specialization with that id does not exist"));
                            })
                            .toList();

            doctor.setSpecializations(specializations);
        }

        return doctorRepository.save(doctor);
    }

    // Get all doctors.
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Get a doctor by ID.
    public Doctor getDoctorById(UUID id) {
        return doctorRepository.findById(id).orElse(null);
    }

    // Update a doctor.
    public Doctor updateDoctor(UUID id, Doctor doctorDetails) {

        Doctor doctor = doctorRepository.findById(id).orElse(null);

        if (doctor == null) {
            return null;
        }

        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setDateOfBirth(doctorDetails.getDateOfBirth());

        // Update the doctor's office.
        if (doctorDetails.getOffice() != null
                && doctorDetails.getOffice().getId() != null) {

            UUID officeId = doctorDetails.getOffice().getId();

            if (doctorRepository.existsByOffice_IdAndIdNot(officeId, id)) {
                throw new RuntimeException(
                        "The office is already assigned to another doctor");
            }

            Office office = officeRepository.findById(officeId)
                    .orElseThrow(() -> new RuntimeException(
                            "The office with that id does not exist"));

            doctor.setOffice(office);
        } else {
            doctor.setOffice(null);
        }

        // Update the doctor's specializations.
        if (doctorDetails.getSpecializations() != null) {

            List<Specialization> specializations =
                    doctorDetails.getSpecializations()
                            .stream()
                            .map(specialization -> {
                                UUID specializationId =
                                        specialization.getId();

                                return specializationRepository
                                        .findById(specializationId)
                                        .orElseThrow(() -> new RuntimeException(
                                                "The specialization with that id does not exist"));
                            })
                            .toList();

            doctor.setSpecializations(specializations);
        }

        return doctorRepository.save(doctor);
    }

    // Delete a doctor.
    public void deleteDoctor(UUID id) {
        doctorRepository.deleteById(id);
    }

    // Find doctors by specialization.
    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepository.findDoctorsBySpecialization(name);
    }

    // Find doctors who do not have an office.
    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepository.findDoctorsWithoutOffice();
    }

    // Add a specialization to a doctor.
    public String addSpecializationToDoctor(
            UUID doctorId,
            UUID specializationId) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        Specialization specialization =
                specializationRepository.findById(specializationId)
                        .orElse(null);

        if (specialization == null) {
            return "That specialization does not exist";
        }

        if (doctor.getSpecializations().contains(specialization)) {
            return "The doctor already has that specialization";
        }

        doctor.getSpecializations().add(specialization);
        doctorRepository.save(doctor);

        return "Specialization added to the doctor";
    }

    // Remove a specialization from a doctor.
    public String removeSpecializationFromDoctor(
            UUID doctorId,
            UUID specializationId) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        Specialization specialization =
                specializationRepository.findById(specializationId)
                        .orElse(null);

        if (specialization == null) {
            return "That specialization does not exist";
        }

        if (!doctor.getSpecializations().contains(specialization)) {
            return "The doctor does not have that specialization";
        }

        doctor.getSpecializations().remove(specialization);
        doctorRepository.save(doctor);

        return "Specialization removed from the doctor";
    }

    // Change a doctor's office.
    public String changeDoctorOffice(UUID doctorId, int officeNumber) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor == null) {
            return "The doctor with that id does not exist";
        }

        Office office = officeRepository.findByOfficeNumber(officeNumber)
                .orElse(null);

        if (office == null) {
            return "The office with that office number does not exist";
        }

        if (doctorRepository.existsByOffice_IdAndIdNot(
                office.getId(), doctorId)) {
            return "That office is taken by other doctor";
        }

        doctor.setOffice(office);
        doctorRepository.save(doctor);

        return "Office changed successfully";
    }

    // Get the completed appointment count for each doctor in a date range.
    public List<Object[]> getDoctorWorkload(Date start, Date end) {

        return appointmentRepository.countDoctorWorkload(
                AppointmentStatus.COMPLETED,
                start,
                end);
    }
    // GET a doctor appointment for one day ,excluding cancelled ones.

    public List<kigali.clinic.rw.domain.Appointment>
    getDoctorAgenda(
        UUID doctorId, Date appointmentDate) {

                 boolean doctorExists = doctorRepository.existsById(
                        doctorId);

                        if (!doctorExists) {
                                throw new org.springframework.web.server.ResponseStatusException(

                                org.springframework.http.HttpStatus.NOT_FOUND,
                                "The doctor with that id does not exist");
                        }

                 return appointmentRepository
                          .findByDoctor_IdAndAppointmentDateAndStatusNot(
                                doctorId,
                                appointmentDate,
                                AppointmentStatus.CANCELLED);
                          
        }
    
    // D3 - Search doctors by first name or last name.
    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.searchDoctorsByName(name);
    }

}