package com.cgtech.apifile.contollers.merge;


import com.cgtech.apifile.services.merge.FusionPdfService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = "/merge")
@RequiredArgsConstructor
public class MergeController {

    private final FusionPdfService pdfService;
   private final Logger logger= LoggerFactory.getLogger(MergeController.class);

    @PostMapping(path = "/pdf")
    public ResponseEntity<?> fusionner(@RequestParam("fileTop") MultipartFile multipartFile,
                                       @RequestParam("fileBottom") MultipartFile multipartFile1){
        try {
         return pdfService.fusionnerPdf(multipartFile,multipartFile1);
        } catch (IOException e) {
         logger.error(e.getMessage());
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",
                 "une erreur est survenu("+e.getMessage()+")"));
        }

    }

}
