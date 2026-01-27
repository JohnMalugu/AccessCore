package com.jcmlabs.AccessCore.Exceptions.Domain;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends DomainException {

    private final String resource;
    private final String field;
    private final Object value;

    private ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s = %s", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }

    public static ResourceNotFoundException of(
            String resource,
            String field,
            Object value
    ) {
        return new ResourceNotFoundException(resource, field, value);
    }

}
