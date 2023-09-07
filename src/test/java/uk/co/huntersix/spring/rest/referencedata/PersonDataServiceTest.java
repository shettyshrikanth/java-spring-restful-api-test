package uk.co.huntersix.spring.rest.referencedata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import uk.co.huntersix.spring.rest.dto.PersonDto;
import uk.co.huntersix.spring.rest.model.Person;

public class PersonDataServiceTest {
    PersonDataService service;

    @Before
    public void setUp() throws Exception {
        service = new PersonDataService();
    }

    @Test
    public void findPersonShouldReturnPersonWhenFound() {
        String firstName = "Mary";
        String lastName = "Smith";

        Optional<Person> person = service.findPerson(lastName, firstName);

        assertTrue(person.isPresent());
        assertEquals(lastName, person.get().getLastName());
        assertEquals(firstName, person.get().getFirstName());
        assertNotNull(person.get().getId());
    }

    @Test
    public void findPersonShouldReturnEmptyWhenNotFound() throws Exception {

        Optional<Person> person = service.findPerson("Clinton", "Murray");

        assertFalse(person.isPresent());
    }

    @Test
    public void findPersonsBySurnameShouldReturnEmptyListWhenNoSurnameMatch() throws Exception {

        List<Person> people = service.findPersonsBySurname("Bond");

        assertEquals(0, people.size());
    }

    @Test
    public void findPersonsBySurnameShouldReturnPersonsListWhenSurnameMatch() throws Exception {
        service.addPerson(new PersonDto("Gordon", "Brown"));

        List<Person> peoples = service.findPersonsBySurname("Brown");

        assertEquals(2, peoples.size());
        assertNotNull(peoples.get(0).getId());
        assertEquals("Collin", peoples.get(0).getFirstName());
        assertEquals("Brown", peoples.get(0).getLastName());
        assertNotNull(peoples.get(1).getId());
        assertEquals("Gordon", peoples.get(1).getFirstName());
        assertEquals("Brown", peoples.get(1).getLastName());
    }

    @Test
    public void addPersonShouldAddPerson() throws Exception {
        String firstName = "Bukkad";
        String lastName = "Khan";
        PersonDto personDto = new PersonDto(firstName, lastName);

        Person person = service.addPerson(personDto);
        assertNotNull(person.getId());
        assertEquals(firstName, person.getFirstName());
        assertEquals(lastName, person.getLastName());
    }
}
