
package kigali.clinic.rw.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientService(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // Save a new patient.
    public Patient savePatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Return all patients.
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Find one patient using their ID.
    public Patient getPatientById(UUID id) {
        return patientRepository.findById(id).orElse(null);
    }

    // Update an existing patient's details.
    public Patient updatePatient(UUID id, Patient patientDetails) {

        Patient patient = patientRepository.findById(id).orElse(null);

        if (patient == null) {
            return null;
        }

        patient.setFirstName(patientDetails.getFirstName());
        patient.setLastName(patientDetails.getLastName());
        patient.setDateOfBirth(patientDetails.getDateOfBirth());
        patient.setSsNumber(patientDetails.getSsNumber());

        return patientRepository.save(patient);
    }

    // Delete a patient.
    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }

    // Find patients by last name, ignoring letter case.
    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepository
                .findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // Find patients who have appointments with a particular doctor.
    public List<Patient> getPatientsOfDoctor(UUID doctorId) {

        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException(
                    "The doctor with that id does not exist");
        }

        return patientRepository.findPatientsOfDoctor(doctorId);
    }

    // Find patients who meet the minimum appointment count.
    public List<Patient> getFrequentPatients(long min) {
        return patientRepository.findFrequentPatients(min);
    }
}
