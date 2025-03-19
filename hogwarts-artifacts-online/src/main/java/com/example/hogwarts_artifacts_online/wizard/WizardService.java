package com.example.hogwarts_artifacts_online.wizard;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WizardService {

    private final WizardRepository wizardRepository;

    public WizardService(WizardRepository wizardRepository) {
        this.wizardRepository = wizardRepository;
    }

    public Wizard findById(String wizardId) {
        return this.wizardRepository.findById(Long.parseLong(wizardId))
                .orElseThrow(() -> new WizardNotFoundException(wizardId));
    }

    public List<Wizard> findAll() {
        return this.wizardRepository.findAll();
    }

    public Wizard save(Wizard wizard) {
        return this.wizardRepository.save(wizard);
    }

    public Wizard update(String id, Wizard update) {
        return this.wizardRepository.findById(Long.parseLong(id))
                .map(oldWizard -> {
                    oldWizard.setName(update.getName());
                    return this.wizardRepository.save(oldWizard);
                })
                .orElseThrow(() -> new WizardNotFoundException(id));
    }

    public void delete(String id) {
        Wizard foundWizard = this.wizardRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new WizardNotFoundException(id));
        this.wizardRepository.delete(foundWizard);
    }
}
