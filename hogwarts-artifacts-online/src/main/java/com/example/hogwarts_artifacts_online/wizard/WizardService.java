package com.example.hogwarts_artifacts_online.wizard;


import com.example.hogwarts_artifacts_online.artifact.ArtifactRepository;
import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WizardService {

    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository,
                         ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public Wizard findById(String wizardId) {
        return this.wizardRepository.findById(Long.parseLong(wizardId))
                .orElseThrow(() -> new ObjectNotFoundException("Wizard",wizardId));
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
                .orElseThrow(() -> new ObjectNotFoundException("Wizard",id));
    }

    public void delete(String id) {
        Wizard foundWizard = this.wizardRepository.findById(Long.parseLong(id))
                .orElseThrow(() -> new ObjectNotFoundException("Wizard",id));
        foundWizard.removeAllArtifacts();
        this.wizardRepository.delete(foundWizard);
    }

    public void assignArtifact(String wizardId, String artifactId) {
        var wizard = this.wizardRepository.findById(Long.valueOf(wizardId))
                .orElseThrow(() -> new ObjectNotFoundException("Wizard", wizardId));

        var artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ObjectNotFoundException("Artifact", artifactId));

        if(artifactToBeAssigned.getOwner() != null) {
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }

        wizard.addArtifact(artifactToBeAssigned);
        this.wizardRepository.save(wizard);
    }
}
