package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.services.PersonService;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin
@RequestMapping(value = "/person")
public class PersonController {
    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping()
    public ResponseEntity<List<PersonDTO>> getPersons() {
        List<PersonDTO> dtos = personService.getAllPersons();
        for(PersonDTO dto : dtos) {
            Link personLink = linkTo(methodOn(PersonController.class)
                    .getPerson(dto.getId())).withRel("personId");
            dto.add(personLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonDTO> getPerson(@PathVariable UUID id) {
        PersonDTO dto = personService.findPersonById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}")
    public void insertPerson(@PathVariable String id) {
        PersonDTO dto = new PersonDTO();
        dto.setPersonId(UUID.fromString(id));
        UUID personId = personService.insertPerson(dto);
//        return new ResponseEntity<>(personId, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{personId}")
    public void deletePerson(@PathVariable String personId) {
        //PersonDTO dto = personService.findPersonByPersonId(UUID.fromString(personId));
        personService.deletePerson(UUID.fromString(personId));
//        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


}
