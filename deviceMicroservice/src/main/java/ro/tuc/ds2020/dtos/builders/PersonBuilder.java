package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.entities.Person;

public class PersonBuilder {

    private PersonBuilder() {
    }

    public static PersonDTO toPersonDTO(Person person) {
        return new PersonDTO(person.getId(), person.getPersonId());
    }

//    public static PersonDetailsDTO toPersonDetailsDTO(Person person) {
//        return new PersonDetailsDTO(person.getId(), person.getPersonId());
//    }

    public static Person toEntity(PersonDTO personDetailsDTO) {
        return new Person(personDetailsDTO.getPersonId());
    }
}