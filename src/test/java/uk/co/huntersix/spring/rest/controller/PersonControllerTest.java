package uk.co.huntersix.spring.rest.controller;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.huntersix.spring.rest.dto.PersonDto;
import uk.co.huntersix.spring.rest.model.Person;
import uk.co.huntersix.spring.rest.referencedata.PersonDataService;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonDataService personDataService;

    @Test
    public void shouldReturnPersonFromService() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.of(new Person("Mary", "Smith")));

        mockMvc.perform(get("/persons/smith/mary"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("firstName").value("Mary"))
            .andExpect(jsonPath("lastName").value("Smith"));
    }

    @Test
    public void shouldReturn404FromServiceWhenPersonNotExists() throws Exception {
        when(personDataService.findPerson(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/persons/David/Beckham"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void shouldReturnEmptyListFromServiceWhenNoSurnameMatchFound() throws Exception {
        when(personDataService.findPersonsBySurname(any())).thenReturn(emptyList());

        mockMvc.perform(get("/persons/Anthony"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldReturnPeoplesListFromServiceWhenOneFound() throws Exception {
        String firstName = "Brian";
        String surname = "Archer";

        when(personDataService.findPersonsBySurname(surname))
                .thenReturn(Arrays.asList(new Person(firstName, surname)));

        mockMvc.perform(get("/persons/Archer"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").value(firstName))
                .andExpect(jsonPath("$[0].lastName").value(surname));
    }

    @Test
    public void shouldReturnPeoplesListFromService() throws Exception {
        String firstName = "Brian";
        String firstName2 = "Ryan";
        String surname = "Archer";
        String surname2 = "archer";

        when(personDataService.findPersonsBySurname(surname))
                .thenReturn(Arrays.asList(new Person(firstName, surname), new Person(firstName2, surname2)));

        mockMvc.perform(get("/persons/Archer"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].firstName").value(firstName))
                .andExpect(jsonPath("$[0].lastName").value(surname))
                .andExpect(jsonPath("$[1].id").exists())
                .andExpect(jsonPath("$[1].firstName").value(firstName2))
                .andExpect(jsonPath("$[1].lastName").value(surname2));
    }

    @Test
    public void shouldReturnCreatedWhenPersonNameAndSurnameValid() throws Exception {
        PersonDto personDto = new PersonDto("Micky", "Mouse");

        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andDo(print())
                .andExpect(status().isCreated());

    }

    @Test
    public void shouldReturnBadRequesWhenFirstnameIsEmpty() throws Exception {
        PersonDto personDto = new PersonDto("", "Ronaldo");

        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequesWhenSurnameIsEmpty() throws Exception {
        PersonDto personDto = new PersonDto("Christiano", "");

        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequesWhenBothFirstnameAndSurnameEmpty() throws Exception {
        PersonDto personDto = new PersonDto("", "");

        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnConflictWhenPersonExistsWithSameName() throws Exception {
        PersonDto personDto = new PersonDto("Brian", "Archer");

        when(personDataService.findPerson(personDto.getLastName(), personDto.getFirstName()))
                .thenReturn(Optional.of(new Person(personDto.getFirstName(), personDto.getLastName())));

        mockMvc.perform(post("/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(personDto)))
                .andExpect(jsonPath("$").doesNotExist())
                .andExpect(status().isConflict());
    }
}