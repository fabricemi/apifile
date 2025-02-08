package com.cgtech.apifile.services.encrypts;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Service responsable du chiffrement des fichiers PDF.
 *
 * Cette classe permet d'ajouter un mot de passe à un fichier PDF pour en restreindre l'accès.
 *
 * @author Fabrice MISSIDI MBAZI BASSEHA
 */
@Service
@RequiredArgsConstructor
public class EncryptPdf {

    private final StorageProperty storage;
    //private final Logger logger = LoggerFactory.getLogger(EncryptPdf.class);

    /**
     * Chiffre un fichier PDF avec un mot de passe.
     * Cette méthode prend un fichier PDF en entrée, applique un chiffrement avec le mot de passe fourni,
     * puis stocke le fichier sécurisé dans le répertoire cible.
     *
     * @param file Le fichier PDF à chiffrer.
     * @param mdp Le mot de passe à appliquer au fichier.
     * @param sousRep Le sous-répertoire où stocker le fichier chiffré.
     * @return Une réponse HTTP contenant le fichier PDF chiffré en cas de succès (200 OK),
     *         ou une erreur (500 Internal Server Error) en cas d'échec.
     */
    public ResponseEntity<?> encrypt(MultipartFile file, String mdp, String sousRep) {
        File fileTmp = null;
        File fileEncrypted = null;
        try {
            Path path = Utils.workDirectory(storage.getUploadFile(), sousRep);
            String reference = Utils.getRandomStr(30);

            Path target = path.resolve(reference + ".pdf");
            file.transferTo(target);

            fileTmp = new File(path + "/" + reference + ".pdf");
            PDDocument document = Loader.loadPDF(fileTmp);
            AccessPermission permission = new AccessPermission();
            StandardProtectionPolicy policy = new StandardProtectionPolicy(
                    mdp,
                    mdp,
                    permission
            );
            policy.setEncryptionKeyLength(128);
            policy.setPermissions(permission);
            document.protect(policy);

            String name = Utils.getRandomStr(10) + "--f2oSec.pdf";
            document.save(path + "/" + name);

            fileEncrypted = new File(path + "/" + name);
            document.close();

            return Utils.fileResponse(fileEncrypted, MediaType.APPLICATION_PDF);
        } catch (IOException e) {
            //logger.error("|ENC| " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu (" + e.getMessage() + ")"));
        } catch (BadPasswordException e) {
            //logger.error("|ENC| " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("is_encrypt", "votre fichier est cryppté "));
        }
    }

}
