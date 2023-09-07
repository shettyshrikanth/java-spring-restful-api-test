package uk.co.huntersix.spring.rest.controller;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import uk.co.huntersix.spring.rest.dto.PersonDto;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RestController
public class PersonController {
    private PersonDataService personDataService;

    public PersonController(@Autowired PersonDataService personDataService) {
        this.personDataService = personDataService;
    }

    @GetMapping("/persons/{lastName}/{firstName}")
    public ResponseEntity<Person> person(@PathVariable(value = "lastName") String lastName,
            @PathVariable(value = "firstName") String firstName) {

        return personDataService.findPerson(lastName, firstName)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping("/persons/{lastName}")
    public List<Person> personsBySurname(@PathVariable(value = "lastName") String lastName) {
        List<Person> persons = personDataService.findPersonsBySurname(lastName);

        return persons;

    }

    @PostMapping("/persons")
    public ResponseEntity<Person> createPerson(@RequestBody PersonDto person) {

        if (isEmpty(person.getFirstName()) || isEmpty(person.getLastName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Person> personFromRepo = personDataService.findPerson(person.getLastName(), person.getFirstName());

        if (personFromRepo.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        Person newPerson = personDataService.addPerson(person);

        return new ResponseEntity<>(newPerson, HttpStatus.CREATED);
    }

}