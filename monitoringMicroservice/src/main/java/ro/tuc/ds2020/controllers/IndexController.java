package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.services.MonitorService;


@RestController
@CrossOrigin
public class IndexController {

    private final MonitorService monitorService;

    @Autowired
    public IndexController(MonitorService monitorService) {
        this.monitorService = monitorService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<String> getStatus() {
        return new ResponseEntity<>("City APP Service is running...", HttpStatus.OK);
    }
}
