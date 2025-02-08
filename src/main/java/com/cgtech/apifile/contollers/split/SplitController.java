package com.cgtech.apifile.contollers.split;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.cgtech.apifile.services.split.SplitPdf;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Contrôleur REST pour la gestion de la division de fichiers PDF.
 * Ce contrôleur expose un endpoint permettant de diviser un fichier PDF en plusieurs parties.

 * Les fichiers PDF fournis dans la requête seront découpés en plusieurs sous-fichiers en fonction des pages spécifiées.
 *  api/split/pdf (unique onde point disponible)
 * @author Fabrice MISSIDI MBAZI BASSEHA
 * @version 1.0
 */
@RestController
@RequestMapping(path = "/split")
@RequiredArgsConstructor
public class SplitController {
    private final SplitPdf pdfService;
    //private final Logger logger = LoggerFactory.getLogger(SplitController.class);
    private final StorageProperty storageProperty;


    /**
     * Divise un fichier PDF en plusieurs sous-fichiers, selon les pages spécifiées.
     *
     * @param file le fichier PDF à diviser, envoyé dans la requête sous le paramètre `file`.
     * @param start le numéro de la première page du sous-fichier à créer.
     * @param end le numéro de la dernière page du sous-fichier à créer.
     * @return ResponseEntity<?> une réponse HTTP contenant les sous-fichiers PDF ou un message d'erreur en cas d'échec.
     *
     * @throws Exception si une erreur se produit lors de la lecture ou de l'écriture du fichier PDF.
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
            //logger.error("|SPLIT| " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "une erreur inatendue"));
        }
    }

}
