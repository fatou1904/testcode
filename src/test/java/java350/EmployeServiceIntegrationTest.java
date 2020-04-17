package java350;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import com.ipiecoles.java.java350.service.EmployeService;

@SpringBootTest
public class EmployeServiceIntegrationTest {
	
	@Autowired
	EmployeService employeService;
	
	@Autowired
	EmployeRepository employeRepository;
	
	
	@BeforeEach
	void  setup() {
		employeRepository.deleteAll();
	}
	
	
	@Test
	void testEmbaucheEmploye() {
		//given
		//Employe employe = new Employe("doe", "jane", "C12345", LocalDate.now(), 2000d);
		//employeRepository.save(employeInitial);
		//When
		
		//Then
	}
	
	@Test
	void calculPerformanceCommercialIntegrationTest() throws EmployeException {
	    //Given
	    Employe employe = new Employe("Delacour", "Michel", "C00001", LocalDate.now(), 1825.46, 2, null);
	    employeRepository.save(employe);
	    //When
	    employeService.calculPerformanceCommercial("C00001", 2000L, 2500L);
	    //Then
	    Assertions.assertEquals( 1, employeRepository.findByMatricule("C00001").getPerformance());
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
	
}