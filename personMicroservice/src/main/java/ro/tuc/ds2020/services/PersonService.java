package ro.tuc.ds2020.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.builders.PersonBuilder;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Autowired
    RestTemplate restTemplate;

    public List<PersonDTO> findPersons() {
        List<Person> personList = personRepository.findAll();
        return personList.stream()
                .map(PersonBuilder::toPersonDTO)
                .collect(Collectors.toList());
    }

    public PersonDetailsDTO findPersonById(UUID id) {
        Optional<Person> prosumerOptional = personRepository.findById(id);
        if (!prosumerOptional.isPresent()) {
            LOGGER.error("Person with id {} was not found in db", id);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with id: " + id);
        }
        return PersonBuilder.toPersonDetailsDTO(prosumerOptional.get());
    }

    public PersonDetailsDTO findPersonByEmail(String email) {
        Person prosumerOptional = personRepository.findByEmail(email);
        if (prosumerOptional == null) {
            LOGGER.error("Person with email {} was not found in db", email);
            throw new ResourceNotFoundException(Person.class.getSimpleName() + " with email: " + email);
        }
        return PersonBuilder.toPersonDetailsDTO(prosumerOptional);
    }



    @Transactional
    public UUID insertPerson(PersonDetailsDTO personDTO) throws URISyntaxException {
        if (personRepository.findByEmail(personDTO.getEmail()) != null) {
            return null;
        }
        // Encode the password
        personDTO.setPassword(new BCryptPasswordEncoder().encode(personDTO.getPassword()));
        Person person = PersonBuilder.toEntity(personDTO);
        person = personRepository.save(person);

//        restTemplate.postForEntity("http://device-backend-container:8081/person/" + person.getId().toString(),
        restTemplate.postForEntity("http://backend-device:8081/api-device/person/" + person.getId().toString(),
                null, Void.class);

        LOGGER.debug("Person with id {} was inserted in db", person.getId());
        System.out.println("http://localhost:80/api-device/person/" + person.getId().toString());
        return person.getId();
    }

    public UUID updatePerson(UUID id, PersonDetailsDTO personDTO){
        Person person = PersonBuilder.toEntity(personDTO);
        person.setId(id);
        person.setPassword(new BCryptPasswordEncoder().encode(personDTO.getPassword()));
        person = personRepository.save(person);
        LOGGER.debug("Person with id {} was updated in db", id);
        return person.getId();
    }

    @Transactional
    public UUID deletePerson(UUID id) {
        personRepository.deleteById(id);
//        restTemplate.delete("http://device-backend-container:8081/person/" + id);
        restTemplate.delete("http://backend-device:8081/api-device/person/" + id);
        LOGGER.debug("Person with id {} was deleted from db", id);
        return id;
    }

}
