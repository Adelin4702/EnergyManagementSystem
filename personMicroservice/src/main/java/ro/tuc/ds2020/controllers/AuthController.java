package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.config.JwtUtil;
import ro.tuc.ds2020.dtos.PersonDTO;
import ro.tuc.ds2020.dtos.PersonDetailsDTO;
import ro.tuc.ds2020.dtos.builders.PersonBuilder;
import ro.tuc.ds2020.entities.AuthenticationRequest;
import ro.tuc.ds2020.entities.AuthenticationResponse;
import ro.tuc.ds2020.entities.Person;
import ro.tuc.ds2020.repositories.PersonRepository;
import ro.tuc.ds2020.services.MyUserDetailsService;
import ro.tuc.ds2020.services.PersonService;

import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private PersonService personService;

    @Autowired
    private PersonRepository userRepository;

    @Autowired
    RestTemplate restTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect email or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        PersonDetailsDTO personDTO = personService.findPersonByEmail(authenticationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails, personDTO.isAdmin(), personDTO.getId().toString());
        System.out.println("JWT: " + jwt);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody PersonDetailsDTO newUser) {
        System.out.println("Ajunge in register: " + newUser.toString());
        if (userRepository.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email is already in use");
        }

        // Encode the password
        newUser.setPassword(new BCryptPasswordEncoder().encode(newUser.getPassword()));
//        Person person = userRepository.save(PersonBuilder.toEntity(newUser));


        Person person = PersonBuilder.toEntity(newUser);
        person = userRepository.save(person);

        restTemplate.postForEntity("http://backend-device:8081/api-device/person/" + person.getId().toString(),
                null, Void.class);


        return ResponseEntity.ok("User registered successfully");
    }
}
