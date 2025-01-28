package com.cgtech.apifile.contollers.converts;



import com.cgtech.apifile.services.converts.ConvertImageToPdf;
import com.cgtech.apifile.services.converts.ConvertPdfToJpeg;
import com.cgtech.apifile.services.converts.ConvertPdfToPng;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequiredArgsConstructor
public class ConvertController {

    private final ConvertPdfToPng toPng;
    private final ConvertPdfToJpeg toJPEG;
    private final ConvertImageToPdf pdfService;
    public Logger LOGGER= LoggerFactory.getLogger(ConvertController.class);

    @PostMapping(path = "/convertPdfToPng")
    public ResponseEntity<?> convertJng(@RequestParam("file") MultipartFile multipartFile)  {

        try {
            return toPng.convertTo(multipartFile, IMAGE_PNG);

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "png: relative au fichier"), HttpStatus.BAD_REQUEST);
        }

    }
    @PostMapping(path = "/convertPdfToJpeg")
    public ResponseEntity<?> convertJpeg(@RequestParam("file") MultipartFile multipartFile)  {
        try {
            return toJPEG.convertTo(multipartFile, IMAGE_JPEG);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "jpeg: relative au fichier"), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(path = "/imgToPdf")
    public ResponseEntity<?> convertToPdg(@RequestParam("file") MultipartFile multipartFile){
        try {
            return  pdfService.convertToPdf(multipartFile);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "jpeg: relative au fichier"), HttpStatus.BAD_REQUEST);
        }
    }



}
