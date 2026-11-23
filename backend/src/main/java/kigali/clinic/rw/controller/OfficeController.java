package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping({"/api/office", "/api/offices"})
public class OfficeController {
    private final OfficeService officeService;
    public OfficeController(
            OfficeService officeService) {
        this.officeService = officeService;
    }
    @PostMapping("/save")
    public Office saveOffice(
            @RequestBody Office office) {
        return officeService.saveOffice(office);
    }
    // Get All Offices
    @GetMapping("/all")
    public List<Office> getAllOffices() {
        return officeService.getAllOffices();
    }
    // Get Office By ID
    @GetMapping("/{id}")
    public Office getOfficeById(
            @PathVariable UUID id) {
        return officeService.getOfficeById(id);
    }
    // Update Office
    @PutMapping("/{id}")
    public Office updateOffice(
            @PathVariable UUID id,
            @RequestBody Office office) {

        return officeService.updateOffice(
                id,
                office
        );
    }
    // Delete Office
    @DeleteMapping("/{id}")
    public String deleteOffice(
            @PathVariable UUID id) {
        officeService.deleteOffice(id);
        return "Office deleted successfully";
    }
    // C3 - Busiest Office    
    @GetMapping("/busiest")
    public String getBusiestOffice() {
        return officeService.getBusiestOffice();
    }
    //A3 FREE OFFICES
    @GetMapping("/free")
    public List<Office> getFreeOffices(){
        return officeService.getFreeOffices();
    }
}