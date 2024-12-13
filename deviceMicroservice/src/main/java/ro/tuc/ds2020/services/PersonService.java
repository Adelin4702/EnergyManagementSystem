package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.builders.PersonBuilder;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {this.personRepository = personRepository;}

    public List<PersonDTO> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    public UUID insertPerson(PersonDTO personDTO) {
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);
        LOGGER.debug("Person with the id {} was inserted in db", person.getPersonId());
        return person.getPersonId();
    }

    public UUID deletePerson(UUID personId) {
        Person person = personRepository.findByPersonId(personId);
        if(person == null) {
            LOGGER.error("Person with id " + personId + " not found");
            throw new ResourceNotFoundException("Person with id " + personId + " not found");
        }
        personRepository.delete(person);
        LOGGER.debug("Person with the id {} was deleted from db", person.getPersonId());
        return person.getPersonId();
    }

    public PersonDTO findPersonById(UUID personId) {
        Optional<Person> person = personRepository.findById(personId);
        if(!person.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", personId);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id " + personId + " was not found in db");
        }
        return PersonBuilder.toPersonDTO(person.get());
    }

    public PersonDTO findPersonByPersonId(UUID personId) {
        Person person = personRepository.findByPersonId(personId);
        if(person == null) {
            LOGGER.error("Device with id {} was not found in db", personId);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id " + personId + " was not found in db");
        }
        return PersonBuilder.toPersonDTO(person);
    }
}
