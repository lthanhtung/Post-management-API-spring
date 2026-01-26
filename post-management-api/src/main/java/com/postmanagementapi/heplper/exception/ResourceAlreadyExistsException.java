package com.postmanagementapi.heplper.exception;

public class ResourceAlreadyExistsException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException(String messege) {
        super(messege);
    }
}
