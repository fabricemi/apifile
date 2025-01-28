package com.cgtech.apifile.contollers.encrypt;


import com.cgtech.apifile.services.encrypts.EncryptPdf;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(path = "/encrypt")
@RequiredArgsConstructor
public class EncryptController {

    private final EncryptPdf service;

    @PostMapping(path = "/pdf")
    public ResponseEntity<?> encryptFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("motdepasse") String mdp) {
        try {
            return service.encrypt(file, mdp);
        }
        catch (Exception e){
            return new ResponseEntity<>(Map.of("error", "une erreur est survenue"), HttpStatus.BAD_REQUEST);
        }
    }
}
