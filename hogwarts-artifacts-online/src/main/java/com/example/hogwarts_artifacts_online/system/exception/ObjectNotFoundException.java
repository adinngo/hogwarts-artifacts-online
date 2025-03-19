package com.example.hogwarts_artifacts_online.system.exception;

public class ObjectNotFoundException extends RuntimeException{

    public ObjectNotFoundException(String objectName, String id) {
        super(String.format("Could not find %s with Id %s", objectName, id));
    }
}
