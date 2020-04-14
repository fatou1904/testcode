package com.ipiecoles.java.java350.service;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class) // Junit 5
@SpringBootTest
public class EmployeServiceTestIntegration {

    @BeforeEach
    @AfterEach //Supprimer les données qui se trouve dans le repository, pour être sur que notre repository prendra en compte notre given
    public void setup(){
        employeRepository.deleteAll();
    }

    @Autowired
    EmployeRepository employeRepository;

    @Autowired
    EmployeService employeService;

    @Test
    void calculPerformanceCommercialIntegrationTest() throws EmployeException {
        //Given
        Employe employe = new Employe("Delacour", "Michel", "C00001", LocalDate.now(), 1825.46, 2, null);
        employeRepository.save(employe);

        //When
        employeService.calculPerformanceCommercial("C00001", 2000L, 2500L);

        //Then
        Assertions.assertEquals(employeRepository.findByMatricule("C00001").getPerformance(), 1);
    }
}