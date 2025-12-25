package com.multishop.exception;

/**
 * Runtime exception to represent a missing resource (maps to HTTP 404 via ControllerAdvice).
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() { super(); }
    public ResourceNotFoundException(String message) { super(message); }
    public ResourceNotFoundException(String message, Throwable cause) { super(message, cause); }
}
