package com.example.hogwarts_artifacts_online.artifact;

import com.example.hogwarts_artifacts_online.artifact.utils.IdWorker;
import com.example.hogwarts_artifacts_online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;

    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return artifactRepository.findById(artifactId).orElseThrow(() -> {
            return new ObjectNotFoundException("Artifact",artifactId);
        });
    }

    public List<Artifact> findAll() {
        return artifactRepository.findAll();
    }

    public Page<Artifact> findAll(Pageable pageable) {
        return artifactRepository.findAll(pageable);
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update) {
        return this.artifactRepository.findById(artifactId)
                .map((oldArtifact) -> {
                        oldArtifact.setName(update.getName());
                        oldArtifact.setDescription(update.getDescription());
                        oldArtifact.setImageUrl(update.getImageUrl());
                        return this.artifactRepository.save(oldArtifact);
                        })
                .orElseThrow(() -> new ObjectNotFoundException("Artifact",artifactId));

    }

    public void delete(String artifactId) {
        Artifact artifact = this.artifactRepository.findById(artifactId).orElseThrow(() ->
                new ObjectNotFoundException("Artifact",artifactId));
        this.artifactRepository.deleteById(artifactId);
    }


    public Page<Artifact> findByCriteria(Map<String, String> searchCriteria, Pageable pageable) {
        Specification<Artifact> spec = Specification.where(null);

        if (StringUtils.hasLength(searchCriteria.get("id"))) {
            spec = spec.and(ArtifactSpecs.hasId(searchCriteria.get("id")));
        }

        if (StringUtils.hasLength(searchCriteria.get("name"))) {
            spec = spec.and(ArtifactSpecs.containsName(searchCriteria.get("name")));
        }

        if (StringUtils.hasLength(searchCriteria.get("description"))) {
            spec = spec.and(ArtifactSpecs.containsDescription(searchCriteria.get("description")));
        }

        if (StringUtils.hasLength(searchCriteria.get("ownerName"))) {
            spec = spec.and(ArtifactSpecs.hasOwnerName(searchCriteria.get("ownerName")));
        }

        return this.artifactRepository.findAll(spec, pageable);
    }
}
