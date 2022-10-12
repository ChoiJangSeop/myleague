package jangseop.myleague.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectTest {

    @GetMapping("/test")
    public String connectTest() {
        return "connect success!";
    }
}
