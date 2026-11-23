package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public Prescription savePrescription(
            UUID appointmentId, Prescription prescription) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The appointment with that id does not exist"));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Prescriptions are only for COMPLETED appointments");
        }

        prescription.setId(null);
        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());

        return prescriptionRepository.save(prescription);
    }

    public List<Prescription> getPrescriptionsByAppointment(
            UUID appointmentId) {

        if (!appointmentRepository.existsById(appointmentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "The appointment with that id does not exist");
        }

        return prescriptionRepository.findByAppointment_Id(appointmentId);
    }

    public List<Prescription> getPrescriptionsByPatient(String ssNumber) {
        return prescriptionRepository
                .findByAppointment_Patient_SsNumberOrderByIssuedDateDesc(
                        ssNumber);
    }

    public Prescription updatePrescription(
            UUID id, Prescription updatedPrescription) {

        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The prescription with that id does not exist"));

        existing.setMedicineName(updatedPrescription.getMedicineName());
        existing.setDosage(updatedPrescription.getDosage());
        existing.setDurationDays(updatedPrescription.getDurationDays());

        return prescriptionRepository.save(existing);
    }

    public void deletePrescription(UUID id) {
        Prescription existing = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "The prescription with that id does not exist"));

        prescriptionRepository.delete(existing);
    }
    public List<Object[]> getTopMedicines() {
    return prescriptionRepository.findTopMedicines();
}
}