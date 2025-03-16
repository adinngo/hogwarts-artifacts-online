package com.example.hogwarts_artifacts_online.artifact;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;

    public ArtifactService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public Artifact findById(String artifactId) {
        return artifactRepository.findById(artifactId).orElseThrow(() -> {
            return new ArtifactNotFoundException(artifactId);
        });
    }

    public List<Artifact> findAll() {
        return artifactRepository.findAll();
    }
}
