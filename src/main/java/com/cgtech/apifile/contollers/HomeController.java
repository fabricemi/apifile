package com.cgtech.apifile.contollers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class HomeController {
    @GetMapping(path= "/welcome")
    public List<Object> test(){
        return List.of("Ceci n'est pas un formt radio", "servez-vous", "@Fabrice MISSIDI");
    }
}
