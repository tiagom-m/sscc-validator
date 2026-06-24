package com.example.backend.sscc;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.sscc.dto.SsccRequestDto;
import com.example.backend.sscc.dto.SsccResponseDto;
import com.example.backend.sscc.service.SsccService;
import com.example.backend.sscc.service.SsccStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/sscc")
@Tag(name = "SSCC", description = "SSCC management endpoints")
public class SsccController {

    private final SsccService ssccService;
    private final SsccStorageService storageService;

    public SsccController(SsccService ssccService,
            SsccStorageService storageService) {
        this.ssccService = ssccService;
        this.storageService = storageService;
    }

    @GetMapping
    @Operation(summary = "List all saved SSCCs")
    public ResponseEntity<List<String>> getAll() {
        return ResponseEntity.ok(storageService.getAll());
    }

    @PostMapping
    @Operation(summary = "Saves a valid SSCC", description = "Validates format, length, and check digit. If valid, stores it")
    public ResponseEntity<SsccResponseDto> validate(@RequestBody SsccRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ssccService.save(request.sscc(), request.gs1Prefix()));
    }

    @DeleteMapping
    @Operation(summary = "Clear all saved SSCCs", description = "Removes all SSCCs from the in-memory list")
    public ResponseEntity<Void> clearAll() {
        storageService.clear();
        return ResponseEntity.noContent().build();
    }

}
