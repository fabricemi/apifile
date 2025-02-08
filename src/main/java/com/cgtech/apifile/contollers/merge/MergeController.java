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

/**
 * Contrôleur REST pour la gestion de la fusion de fichiers PDF.
 * Ce contrôleur expose un endpoint permettant de fusionner deux fichiers PDF.
 *
 * Les fichiers PDF fournis dans la requête seront combinés en un seul fichier.
 ** **Endpoint disponible :**
 *      /api/merge/pdf
 * @author Fabrice MISSIDI MBAZI BASSEHA
 */
@RestController
@RequestMapping(path = "/merge")
@RequiredArgsConstructor
public class MergeController {

    private final FusionPdfService pdfService;
    private final StorageProperty storageProperty;
    //private final Logger logger = LoggerFactory.getLogger(MergeController.class);

    /**
     * Fusionne deux fichiers PDF envoyés dans la requête en un seul fichier PDF.
     *
     * @param multipartFile le premier fichier PDF à fusionner, envoyé dans la requête sous le paramètre `fileTop`.
     * @param multipartFile1 le deuxième fichier PDF à fusionner, envoyé dans la requête sous le paramètre `fileBottom`.
     * @return ResponseEntity<?> une réponse HTTP contenant le fichier PDF fusionné ou un message d'erreur en cas d'échec.
     *
     * @throws IOException si une erreur se produit lors de la lecture ou de l'écriture des fichiers PDF.
     */
    @PostMapping(path = "/pdf")
    public ResponseEntity<?> fusionner(@RequestParam("fileTop") MultipartFile multipartFile,
                                       @RequestParam("fileBottom") MultipartFile multipartFile1) {
        try {
            String sousRep = Utils.getRandomStr(11);
            ResponseEntity<?> response = pdfService.fusionnerPdf(multipartFile, multipartFile1, sousRep);
            scheduleForMerge(sousRep);
            return response;
        } catch (IOException e) {
            //logger.error(e.getMessage());
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
