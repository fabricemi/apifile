package com.cgtech.apifile.contollers.merge;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/merge")
@RequiredArgsConstructor
public class MergeController {

    private final FusionPdfService pdfService;
    private final StorageProperty storageProperty;
    private final Logger logger = LoggerFactory.getLogger(MergeController.class);

    @PostMapping(path = "/pdf")
    public ResponseEntity<?> fusionner(@RequestParam("fileTop") MultipartFile multipartFile,
                                       @RequestParam("fileBottom") MultipartFile multipartFile1) {
        try {
            String sousRep = Utils.getRandomStr(11);
            ResponseEntity<?> response = pdfService.fusionnerPdf(multipartFile, multipartFile1, sousRep);
            scheduleForMerge(sousRep);
            return response;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",
                    "une erreur est survenu(" + e.getMessage() + ")"));
        }

    }

    public void scheduleForMerge(String sousRep) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(
                () -> {
                    Path path = Path.of(Paths.get(storageProperty.getUploadFile()).toAbsolutePath().normalize() + File.separator + sousRep);
                    Utils.remove(path);

                }, 32, TimeUnit.SECONDS
        );
    }
}
