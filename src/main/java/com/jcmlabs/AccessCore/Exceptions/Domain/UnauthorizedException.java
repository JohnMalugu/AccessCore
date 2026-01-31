package com.jcmlabs.AccessCore.Exceptions.Domain;


import lombok.Getter;

@Getter
public class UnauthorizedException extends DomainException {

    private final Integer code;

    public UnauthorizedException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}