package com.example.hogwarts_artifacts_online.wizard;

import com.example.hogwarts_artifacts_online.wizard.dto.WizardDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizardList;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testFindByIdSuccess() {
        Wizard w1 = new Wizard();
        w1.setId(1L);
        w1.setName("Albus Dumbledore");

        given(wizardRepository.findById(1L)).willReturn(Optional.of(w1));
        //when
        Wizard returnedWizard = wizardService.findById("1");

        //then
        assertThat(returnedWizard.getId()).isEqualTo(w1.getId());
        assertThat(returnedWizard.getName()).isEqualTo(w1.getName());
        verify(wizardRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        given(this.wizardRepository.findById(Mockito.anyLong())).willReturn(Optional.empty());

        assertThrows(WizardNotFoundException.class, () -> this.wizardService.findById("1"));

        verify(wizardRepository, times(1)).findById(1L);

    }

    @Test
    void testFindAll() {
        Wizard w1 = new Wizard();
        w1.setId(1L);
        w1.setName("Albus Dumbledore");

        Wizard w2 = new Wizard();
        w2.setId(2L);
        w2.setName("Harry Potter");

        Wizard w3 = new Wizard();
        w3.setId(3L);
        w3.setName("Neville Longbottom");

        this.wizardList = List.of(w1, w2, w3);

        given(this.wizardRepository.findAll()).willReturn(wizardList);

        //when
        List<Wizard> actualWizardList = this.wizardService.findAll();

        //then
        assertThat(actualWizardList.size()).isEqualTo(this.wizardList.size());
        assertThat(actualWizardList.get(0).getId()).isEqualTo(this.wizardList.get(0).getId());
        assertThat(actualWizardList.get(0).getName()).isEqualTo(this.wizardList.get(0).getName());
        assertThat(actualWizardList.get(1).getId()).isEqualTo(this.wizardList.get(1).getId());
        assertThat(actualWizardList.get(1).getName()).isEqualTo(this.wizardList.get(1).getName());
    }

    @Test
    void testSaveSuccess() {
        Wizard newWizard = new Wizard();
        newWizard.setId(1L);
        newWizard.setName("Hermione Granger");

        given(this.wizardRepository.save(newWizard)).willReturn(newWizard);

        //when
        Wizard savedWizard = this.wizardService.save(newWizard);

        //then
        assertThat(savedWizard.getId()).isEqualTo(newWizard.getId());
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        verify(this.wizardRepository, times(1)).save(newWizard);
    }

    @Test
    void testUpdateSuccess() {
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1L);
        oldWizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1L)).willReturn(Optional.of(oldWizard));
        given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);
        //when
        Wizard updatedWizard = this.wizardService.update("1", oldWizard);

        //then
        assertThat(updatedWizard.getId()).isEqualTo(oldWizard.getId());
        assertThat(updatedWizard.getName()).isEqualTo(oldWizard.getName());

        verify(this.wizardRepository, times(1)).findById(1L);
        verify(this.wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateErrorWithNonExistenceId() {
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1L);
        oldWizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThrows(WizardNotFoundException.class, () -> this.wizardService.update("1", oldWizard));
        verify(this.wizardRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteSuccess() {
        Wizard foundWizard = new Wizard();
        foundWizard.setId(1L);
        foundWizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1L)).willReturn(Optional.of(foundWizard));
        doNothing().when(this.wizardRepository).delete(foundWizard);

        this.wizardService.delete("1");

        verify(this.wizardRepository, times(1)).findById(1L);
        verify(this.wizardRepository, times(1)).delete(foundWizard);
    }

    @Test
    void testDeleteSuccessErrorWithNonExistenceId() {
        Wizard foundWizard = new Wizard();
        foundWizard.setId(1L);
        foundWizard.setName("Albus Dumbledore");

        given(this.wizardRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(WizardNotFoundException.class, () -> this.wizardService.delete("1"));
        verify(this.wizardRepository, times(1)).findById(1L);;
    }
}