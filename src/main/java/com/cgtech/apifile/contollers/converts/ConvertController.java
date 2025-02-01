package com.cgtech.apifile.contollers.converts;



import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.cgtech.apifile.services.converts.ConvertImageToPdf;
import com.cgtech.apifile.services.converts.ConvertPdfToJpeg;
import com.cgtech.apifile.services.converts.ConvertPdfToPng;
import com.cgtech.apifile.services.converts.TextToPdf;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.MediaType.IMAGE_JPEG;
import static org.springframework.http.MediaType.IMAGE_PNG;

@RestController
@RequestMapping(path = "/convert")
@RequiredArgsConstructor
public class ConvertController {

    private final ConvertPdfToPng toPng;
    private final ConvertPdfToJpeg toJPEG;
    private final ConvertImageToPdf pdfService;
    private final TextToPdf textToPdf;
    private final StorageProperty storageProperty;
    public Logger LOGGER= LoggerFactory.getLogger(ConvertController.class);

    @PostMapping(path = "/convertPdfToPng")
    public ResponseEntity<?> convertJng(@RequestParam("file") MultipartFile multipartFile)  {
        LOGGER.info("to png");
        try {
            String sousRep= Utils.getRandomStr(20);
            ResponseEntity<?> response=toPng.convertTo(multipartFile, IMAGE_PNG, sousRep);
            scheduleForConvert(sousRep);
            return response;

        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "png: relative au fichier"), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping(path = "/convertPdfToJpeg")
    public ResponseEntity<?> convertJpeg(@RequestParam("file") MultipartFile multipartFile)  {
        LOGGER.info("to jpeg");
        try {
            String sousRep= Utils.getRandomStr(21);
            ResponseEntity<?> response=toJPEG.convertTo(multipartFile, IMAGE_JPEG, sousRep);
            scheduleForConvert(sousRep);
            return response;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "jpeg: relative au fichier"), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping(path = "/imgToPdf")
    public ResponseEntity<?> convertToPdg(@RequestParam("file") MultipartFile multipartFile){
        try {
            String sousRep=Utils.getRandomStr(10);
            ResponseEntity<?> response=pdfService.convertToPdf(multipartFile,sousRep);

            scheduleForConvert(sousRep);
            return  response;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "jpeg: relative au fichier"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/txtToPdf")
    public ResponseEntity<?> convertTxtToPdf(@RequestParam("file") MultipartFile multipartFile){
        try {
            LOGGER.info("txt to pdf");
            String sousRep=Utils.getRandomStr(10);
            File file=textToPdf.convert(multipartFile, sousRep);

            ResponseEntity<?> response=Utils.fileResponse(file, MediaType.APPLICATION_PDF);
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(
                    () -> {
                        Path path = Path.of(Paths.get(storageProperty.getUploadStandard()).toAbsolutePath().normalize() + File.separator + sousRep);
                        Utils.remove(path);

                    }, 35, TimeUnit.SECONDS
            );
            return  response;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "erreur inantendue"), HttpStatus.BAD_REQUEST);
        }
    }


    public void scheduleForConvert(String sousRep){
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(
                () -> {
                    Path path = Path.of(Paths.get(storageProperty.getUploadImage()).toAbsolutePath().normalize() + File.separator + sousRep);
                    Utils.remove(path);

                }, 30, TimeUnit.SECONDS
        );
    }

}
