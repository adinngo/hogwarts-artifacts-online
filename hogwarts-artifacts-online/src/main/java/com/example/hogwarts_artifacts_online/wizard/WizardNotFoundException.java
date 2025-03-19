package com.example.hogwarts_artifacts_online.wizard;

public class WizardNotFoundException extends RuntimeException{

    public WizardNotFoundException(String wizardId) {
        super("Could not find Wizard with Id " + wizardId);
    }
}
