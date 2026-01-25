package com.mlbstats.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(String.format("%s not found with id: %d", resourceType, id));
    }

    public ResourceNotFoundException(String resourceType, Integer mlbId) {
        super(String.format("%s not found with MLB ID: %d", resourceType, mlbId));
    }
}
