package com.ipiecoles.java.java350.service;


import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.*;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    private EmployeRepository employeRepository;

    @BeforeEach
    @AfterEach
    public void setup(){
        employeRepository.deleteAll();
    }

    @Test
    public void integrationEmbaucheEmploye() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        //When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        //Then
        Employe employe = employeRepository.findByMatricule("T12346");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("T12346", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());

        //1521.22 * 1.2 * 1.0
        Assertions.assertEquals(1825.46, employe.getSalaire().doubleValue());
    }

    @Test
    public void integrationCalculPerformanceCommercial() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        String nom = "Doe";
        String prenom = "John";
        String matricule = "C12345";
        Long caTraite = Long.valueOf(150);
        Long objectifCa = Long.valueOf(100);

        //When
        employeService.calculPerformanceCommercial(matricule,caTraite,objectifCa);

        //Then
        Employe employe = employeRepository.findByMatricule("C12345");
        Assertions.assertNotNull(employe);
        Assertions.assertEquals(nom, employe.getNom());
        Assertions.assertEquals(prenom, employe.getPrenom());
        Assertions.assertEquals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), employe.getDateEmbauche().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        Assertions.assertEquals("C12345", employe.getMatricule());
        Assertions.assertEquals(1.0, employe.getTempsPartiel().doubleValue());
        Assertions.assertEquals(6, employe.getPerformance().intValue());
    }

    @Test
    public void integrationAvgPerformanceWhereMatriculeStartsWit() throws EmployeException {
        //Given
        employeRepository.save(new Employe("Doe", "John", "C12345", LocalDate.now(), Entreprise.SALAIRE_BASE, 1, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C23456", LocalDate.now(), Entreprise.SALAIRE_BASE, 2, 1.0));
        employeRepository.save(new Employe("Doe", "John", "C34567", LocalDate.now(), Entreprise.SALAIRE_BASE, 3, 1.0));

        //When/Then
        Assertions.assertEquals(2, employeRepository.avgPerformanceWhereMatriculeStartsWith("C").intValue());
    }

}