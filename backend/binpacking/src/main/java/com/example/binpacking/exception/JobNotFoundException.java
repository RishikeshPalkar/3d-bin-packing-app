package com.example.binpacking.exception;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {
    public JobNotFoundException(UUID id) {
        super("Packing job not found: " + id);
    }
}