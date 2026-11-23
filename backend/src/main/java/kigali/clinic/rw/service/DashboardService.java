
package kigali.clinic.rw.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class DashboardService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final OfficeService officeService;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public DashboardService(
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            OfficeService officeService,
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository) {

        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.officeService = officeService;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // D4 - Get the dashboard statistics.
    public Map<String, Long> getSummary() {

        LocalDate today = LocalDate.now();

        Date startOfToday = Date.valueOf(today);
        Date startOfTomorrow = Date.valueOf(today.plusDays(1));

        long patients = patientRepository.count();
        long doctors = doctorRepository.count();

        long freeOffices =
                officeService.getFreeOffices().size();

        long appointmentsToday =
                appointmentRepository
                        .countByAppointmentDateGreaterThanEqualAndAppointmentDateLessThan(
                                startOfToday,
                                startOfTomorrow);

        long scheduled =
                appointmentRepository.countByStatus(
                        AppointmentStatus.SCHEDULED);

        long prescriptions = prescriptionRepository.count();

        Map<String, Long> summary = new LinkedHashMap<>();

        summary.put("patients", patients);
        summary.put("doctors", doctors);
        summary.put("freeOffices", freeOffices);
        summary.put("appointmentsToday", appointmentsToday);
        summary.put("scheduled", scheduled);
        summary.put("prescriptions", prescriptions);

        return summary;
    }
}
