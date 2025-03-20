package com.example.hogwarts_artifacts_online.wizard;

import com.example.hogwarts_artifacts_online.artifact.Artifact;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Wizard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Artifact> artifactList = new ArrayList<>();

    public Wizard(){}

    public Long getId() {
        return id;
    }

    public void setId(Long  id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artifact> getArtifactList() {
        return artifactList;
    }

    public void setArtifactList(List<Artifact> artifactList) {
        this.artifactList = artifactList;
    }

    public void addArtifact(Artifact artifact) {
        artifact.setOwner(this);
        this.artifactList.add(artifact);
    }

    public Integer getNumberOfArtifacts() {
        return this.artifactList.size();
    }

    public void removeAllArtifacts() {
        this.artifactList.stream().forEach(artifact -> artifact.setOwner(null));
        this.artifactList = new ArrayList<>();
    }

    public void removeArtifact(Artifact artifactToBeAssigned) {
        artifactToBeAssigned.setOwner(null);
        this.artifactList.remove(artifactToBeAssigned);
    }
}
