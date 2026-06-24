package com.example.backend.sscc.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SsccStorageService {

    // Creates the in-memory store for SSCCs
    private final List<String> validSsccs = new ArrayList<>();

    // Adds an SSCC to the list if not already present
    public boolean add(String sscc) {
        if (validSsccs.contains(sscc)) {
            return false;
        }
        validSsccs.add(sscc);
        return true;
    }

    public List<String> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(validSsccs));
    }

    

    public void clear() {
        validSsccs.clear();
    }

}
