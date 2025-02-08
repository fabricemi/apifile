package com.cgtech.apifile.contollers.encrypt;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.cgtech.apifile.services.encrypts.EncryptPdf;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
 * Contrôleur REST pour l'encryption de fichiers PDF.
 * Ce contrôleur expose un endpoint permettant d'encrypter un fichier PDF avec un mot de passe fourni.
 *
 * **Endpoint disponible :**
 * - /api/encrypt/pdf : Encrypte un fichier PDF avec un mot de passe fourni.
 * @author Fabrice MISSIDI MBAZI BASSEHA
 */
@RestController
@RequestMapping(path = "/encrypt")
@RequiredArgsConstructor
public class EncryptController {

    private final EncryptPdf service;
    private final StorageProperty storage;
    //private Logger logger= LoggerFactory.getLogger(EncryptPdf.class);

    /**
     * Endpoint pour encrypter un fichier PDF avec un mot de passe.
     *
     * @param file Le fichier PDF à encrypter (nom du paramètre : "file").
     * @param mdp Le mot de passe pour l'encryption du fichier (nom du paramètre : "motdepasse").
     * @return Une réponse contenant le fichier PDF encrypté ou une erreur en cas d'échec.
     */
    @PostMapping(path = "/pdf")
    public ResponseEntity<?> encryptFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("motdepasse") String mdp) {
        try {
            String sd= Utils.getRandomStr(14);
            ResponseEntity<?> response=service.encrypt(file, mdp, sd);
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(
                    () -> {
                        Path path = Path.of(Paths.get(storage.getUploadFile()).
                                toAbsolutePath().normalize() + File.separator + sd);
                        Utils.remove(path);

                    }, 30, TimeUnit.SECONDS
            );

            return response;


        }
        catch (Exception e){

            return new ResponseEntity<>(Map.of("error", "une erreur est survenue"), HttpStatus.BAD_REQUEST);
        }
    }
}
