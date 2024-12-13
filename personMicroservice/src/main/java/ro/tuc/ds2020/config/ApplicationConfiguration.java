//package ro.tuc.ds2020.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import ro.tuc.ds2020.repositories.PersonRepository;
//
//@Configuration
//public class ApplicationConfiguration {
//    private final PersonRepository personRepository;
//
//    public ApplicationConfiguration(PersonRepository personRepository) {
//        this.personRepository = personRepository;
//    }
//
//    @Bean
//    PersonDetailsService personDetailsService() {
//        return email -> personRepository.findByEmail(email)
//                .orElseThrow(() -> new EmailNotFoundException("Person not found"));
//    }
//
//    @Bean
//    BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//
//        authProvider.setPersonDetailsService(personDetailsService());
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }
//}
