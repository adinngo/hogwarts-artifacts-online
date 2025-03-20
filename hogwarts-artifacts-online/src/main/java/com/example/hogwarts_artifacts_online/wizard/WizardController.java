package com.example.hogwarts_artifacts_online.wizard;


import com.example.hogwarts_artifacts_online.system.Result;
import com.example.hogwarts_artifacts_online.system.StatusCode;
import com.example.hogwarts_artifacts_online.wizard.converter.WizardDtoToWizardConverter;
import com.example.hogwarts_artifacts_online.wizard.converter.WizardToWizardDtoConverter;
import com.example.hogwarts_artifacts_online.wizard.dto.WizardDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

    public final WizardService wizardService;

    public final WizardToWizardDtoConverter wizardToWizardDtoConverter;
    
    public final WizardDtoToWizardConverter wizardDtoToWizardConverter;

    public WizardController(WizardService wizardService, 
                            WizardToWizardDtoConverter wizardToWizardDtoConverter, 
                            WizardDtoToWizardConverter wizardDtoToWizardConverter) {
        this.wizardService = wizardService;
        this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
        this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
    }

    @GetMapping("/{wizardId}")
    public Result findWizardById(@PathVariable String wizardId) {
        var returnedWizard = this.wizardService.findById(wizardId);
        var returnedWizardDto = this.wizardToWizardDtoConverter.convert(returnedWizard);
        return new Result(true, StatusCode.SUCCESS, "Find One Success", returnedWizardDto);
    }

    @GetMapping
    public Result findAllWizard() {
        List<Wizard> wizards = this.wizardService.findAll();
        List<WizardDto> wizardDtos = wizards.stream().map(this.wizardToWizardDtoConverter::convert).collect(Collectors.toList());
        return new Result(true, StatusCode.SUCCESS, "Find All Success", wizardDtos);
    }

    @PostMapping
    public Result addWizard(@Valid @RequestBody WizardDto wizardDto) {
        Wizard wizard = this.wizardDtoToWizardConverter.convert(wizardDto);
        var savedWizard = this.wizardService.save(wizard);
        var savedWizardDto = this.wizardToWizardDtoConverter.convert(savedWizard);
        return new Result(true, StatusCode.SUCCESS, "Add Success", savedWizardDto);
    }

    @PutMapping("/{wizardId}")
    public Result updateWizard(@PathVariable String wizardId,
                               @Valid @RequestBody WizardDto wizardDto) {
        Wizard wizard = this.wizardDtoToWizardConverter.convert(wizardDto);
        Wizard updatedWizard = this.wizardService.update(wizardId, wizard);
        WizardDto updatedWizardDto = this.wizardToWizardDtoConverter.convert(updatedWizard);
        return new Result(true, StatusCode.SUCCESS, "Update Success", updatedWizardDto);
    }

    @DeleteMapping("/{wizardId}")
    public Result DeleteWizard(@PathVariable String wizardId) {
        this.wizardService.delete(wizardId);
        return new Result(true, StatusCode.SUCCESS, "Delete Success", null);

    }

    @PutMapping("/{wizardId}/artifacts/{artifactId}")
    public Result assignArtifact(@PathVariable String wizardId,
                                 @PathVariable String artifactId) {
        this.wizardService.assignArtifact(wizardId, artifactId);
        return new Result(true, StatusCode.SUCCESS,"Artifact Assignment Success", null);
    }
}
