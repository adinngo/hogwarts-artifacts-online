package com.example.hogwarts_artifacts_online.artifact;

import com.example.hogwarts_artifacts_online.system.Result;
import com.example.hogwarts_artifacts_online.system.StatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtifactController {

    private final ArtifactService artifactService;

    public ArtifactController(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @GetMapping("/api/v1/artifacts/{artifactId}")
    public Result findArtifactById(@PathVariable String artifactId) {
        var foundArtifact = this.artifactService.findById(artifactId);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", foundArtifact);
    }

}
