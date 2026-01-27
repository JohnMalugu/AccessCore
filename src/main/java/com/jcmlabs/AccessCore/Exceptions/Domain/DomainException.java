package com.jcmlabs.AccessCore.Exceptions.Domain;

public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}
