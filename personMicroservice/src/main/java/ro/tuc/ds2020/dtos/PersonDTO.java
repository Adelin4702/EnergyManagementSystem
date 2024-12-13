package ro.tuc.ds2020.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PersonDTO extends RepresentationModel<PersonDTO> {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private boolean admin;

//    public PersonDTO() {
//    }

//    public PersonDTO(UUID id, String name, int age) {
//        this.id = id;
//        this.name = name;
//        this.age = age;
//    }

//    public UUID getId() {
//        return id;
//    }
//
//    public void setId(UUID id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public int getAge() {
//        return age;
//    }
//
//    public void setAge(int age) {
//        this.age = age;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return  Objects.equals(name, personDTO.name) &&
                Objects.equals(email, personDTO.email) &&
                Objects.equals(password, personDTO.password) &&
                admin == personDTO.admin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, admin);
    }
}
