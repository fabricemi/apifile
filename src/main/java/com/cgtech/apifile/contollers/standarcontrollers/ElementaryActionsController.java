package com.cgtech.apifile.contollers.standarcontrollers;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.cgtech.apifile.services.standards.GenerateEmptyPdf;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/action")
@RequiredArgsConstructor
public class ElementaryActionsController {
    private final GenerateEmptyPdf emptyPdf;
    private final StorageProperty storageProperty;
    private final Logger logger = LoggerFactory.getLogger(ElementaryActionsController.class);

    @PostMapping(path = "/emptypdf")
    public ResponseEntity<?> generateEmptyPdf(@RequestParam("format") String format){
        try {
            String sousRep= Utils.getRandomStr(18);
            File file=emptyPdf.generate(format,sousRep);
            ResponseEntity<?> response = Utils.fileResponse(file, MediaType.APPLICATION_PDF);
            scheduleForStandard(sousRep);
            return response;

        } catch (IOException e) {
           return ResponseEntity.status(500).body(Map.of("error", "une erreur inatendue"));
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    public void scheduleForStandard(String sousRep) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(
                () -> {
                    Path path = Path.of(Paths.get(storageProperty.getUploadStandard()).toAbsolutePath().normalize() + File.separator + sousRep);
                    Utils.remove(path);

                }, 35, TimeUnit.SECONDS
        );
    }
}
