package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    EmployeService employeService;

    @Mock
    EmployeRepository employeRepository;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this.getClass());
    }

    @Test
    public void testEmbaucheEmployeTechnicienPleinTempsBts() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("T00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("00345");
        when(employeRepository.findByMatricule("M00346")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(nom, employeArgumentCaptor.getValue().getNom());
        Assertions.assertEquals(prenom, employeArgumentCaptor.getValue().getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employeArgumentCaptor.getValue().getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("M00346", employeArgumentCaptor.getValue().getMatricule());
        Assertions.assertEquals(tempsPartiel, employeArgumentCaptor.getValue().getTempsPartiel());

        //1521.22 * 1.4 * 0.5
        Assertions.assertEquals(1064.85, employeArgumentCaptor.getValue().getSalaire().doubleValue());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterNoLastMatricule() throws EmployeException {
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(null);

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals("M00001", employeArgumentCaptor.getValue().getMatricule());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMasterExistingEmploye(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn(null);
        when(employeRepository.findByMatricule("M00001")).thenReturn(new Employe());

        //When/Then
        EntityExistsException e = Assertions.assertThrows(EntityExistsException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("L'employé de matricule M00001 existe déjà en BDD", e.getMessage());
    }

    @Test
    public void testEmbaucheEmployeManagerMiTempsMaster99999(){
        //Given
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.MANAGER;
        NiveauEtude niveauEtude = NiveauEtude.MASTER;
        Double tempsPartiel = 0.5;
        when(employeRepository.findLastMatricule()).thenReturn("99999");

        //When/Then
        EmployeException e = Assertions.assertThrows(EmployeException.class, () -> employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel));
        Assertions.assertEquals("Limite des 100000 matricules atteinte !", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "3, 2.5, 3000, 1200",
            "5, 2, 1200, 600"
    })
    public void calculSalaireMoyenETPTest(Long count, Double sumTempsPartiel, Double sumSalaire, Double expected) throws Exception {
        when(employeRepository.count()).thenReturn(Long.valueOf(count));
        when(employeRepository.sumTempsPartiel()).thenReturn(Double.valueOf(sumTempsPartiel));
        when(employeRepository.sumSalaire()).thenReturn(Double.valueOf(sumSalaire));

//        if (count == 0) {
//            Exception e = Assertions.assertThrows(Exception.class, () -> employeService.calculSalaireMoyenETP());
//            Assertions.assertEquals("Aucun employé, impossible de calculer le salaire moyen !", e.getMessage());
//        }
//        else{
            Double moyenneDesSalaires = employeService.calculSalaireMoyenETP();
            Assertions.assertEquals(expected, moyenneDesSalaires);
//        }

    }

    public void calculSalaireMoyenETPWith0EmployeTest() throws Exception {
        when(employeRepository.count()).thenReturn(Long.valueOf(0));


        Exception e = Assertions.assertThrows(Exception.class, () -> employeService.calculSalaireMoyenETP());
        Assertions.assertEquals("Aucun employé, impossible de calculer le salaire moyen !", e.getMessage());
    }

    public void calculSalaireMoyenETPWithTxPartielSupperieurNbrEmployeTest() throws Exception {
        when(employeRepository.count()).thenReturn(Long.valueOf(2));
        when(employeRepository.sumTempsPartiel()).thenReturn(Double.valueOf(2.5));

        Exception e = Assertions.assertThrows(Exception.class, () -> employeService.calculSalaireMoyenETP());
        Assertions.assertEquals("Taux d'activité des employés incohérent !", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "'-15000'",
            ","
    })
    public void calculPerformanceCommercialTestBadCaTraite(Long caTraite) throws EmployeException {

        EmployeException e = Assertions.assertThrows(EmployeException.class, () ->  employeService.calculPerformanceCommercial("C00001", caTraite, 96L));
        Assertions.assertEquals("Le chiffre d'affaire traité ne peut être négatif ou null !", e.getMessage());
    }


    @ParameterizedTest
    @CsvSource({
            ",",
            "'T00001'"
    })
    public void calculPerformanceCommercialTestBadMatricule(String matricule) throws EmployeException {
        System.out.println("coucou "+matricule);
        EmployeException e = Assertions.assertThrows(EmployeException.class, () ->  employeService.calculPerformanceCommercial(matricule, 1500L, 96L));
        Assertions.assertEquals("Le matricule ne peut être null et doit commencer par un C !", e.getMessage());
    }


    @ParameterizedTest
    @CsvSource({
            ",",
            "'-1'"
    })
    public void calculPerformanceCommercialTestBadObjectifca(Long objectifCa) throws EmployeException {
        EmployeException e = Assertions.assertThrows(EmployeException.class, () ->  employeService.calculPerformanceCommercial("C00001", 1500L, objectifCa));
        Assertions.assertEquals("L'objectif de chiffre d'affaire ne peut être négatif ou null !", e.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "'C00001','9', '10', '1', '1'",
            "'C00001','9', '10', '4', '2'",
            "'C00001','10', '10', '1', '1'",
            "'C00001','1', '1', '4', '4'",
            "'C00001','106', '100', '1', '2'",
            "'C00001','12', '10', '1', '2'",
            "'C00001','13', '10', '5', '10'"
    })
    public void calculPerformanceCommercialTestPassant(String matricule, Long caTraite, Long objectifCa, Integer performance, Integer performanceFinal) throws EmployeException {

        Employe employe = new Employe("Doe", "John", matricule, LocalDate.now().minusYears(2), Entreprise.SALAIRE_BASE,performance, 1.0);

        when(employeRepository.findByMatricule(matricule)).thenReturn(employe);
//        when(employe.getPerformance()).thenReturn(employe.getPerformance());
        when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(Double.valueOf("4"));

        employeService.calculPerformanceCommercial(matricule, caTraite, objectifCa);

        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        verify(employeRepository, times(1)).save(employeArgumentCaptor.capture());
        Assertions.assertEquals(performanceFinal, employeArgumentCaptor.getValue().getPerformance());
    }
}