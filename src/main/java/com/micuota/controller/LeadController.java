package com.micuota.controller;

import com.micuota.entity.Lead;
import com.micuota.repository.LeadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/leads")
public class LeadController {

    private final LeadRepository leadRepository;

    public LeadController(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @PostMapping
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        if (lead.getEmail() == null || lead.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Lead saved = leadRepository.save(lead);
        return ResponseEntity.created(URI.create("/leads/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> getLead(@PathVariable Long id) {
        return leadRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
