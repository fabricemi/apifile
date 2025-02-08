package com.cgtech.apifile.contollers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * endpoint: /api
 */
@RestController
public class HomeController {



    @GetMapping()
    public List<Object> test(){
        return List.of("Ceci n'est pas un formt radio", "servez-vous", "@Fabrice MISSIDI");
    }

}
