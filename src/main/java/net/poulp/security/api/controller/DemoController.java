package net.poulp.security.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/demo")
public class DemoController {

    @RequestMapping(path = "/secured")
    public String securedPath() {
        return "This is a secured path !";
    }

}
