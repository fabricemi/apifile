package com.cgtech.apifile.contollers.split;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.exceptions.StartEndException;
import com.cgtech.apifile.services.Utils;
import com.cgtech.apifile.services.split.SplitPdf;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 *
 */
@RestController
@RequestMapping(path = "/split")
@RequiredArgsConstructor
public class SplitController {
    private final SplitPdf pdfService;
    private final Logger logger = LoggerFactory.getLogger(SplitController.class);
    private final StorageProperty storageProperty;


    /**
     *
     * @param file
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(path = "/pdf")
    public ResponseEntity<?> diviserFicher(@RequestParam("file") MultipartFile file,
                                           @RequestParam("start") int start,
                                           @RequestParam("end") int end) {

        try {
            String sousRep = Utils.getRandomStr(19);
            ResponseEntity<?> response = pdfService.diviserPdf(file, start, end, sousRep);
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(
                    () -> {
                        Path path = Path.of(Paths.get(storageProperty.getUploadSplit()).toAbsolutePath().normalize() + File.separator + sousRep);
                        Utils.remove(path);

                    }, 30, TimeUnit.SECONDS
            );
            return response;
        } catch (Exception e) {
            logger.error("|SPLIT| " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "une erreur inatendue"));
        }
    }

}
