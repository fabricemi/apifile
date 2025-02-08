package com.cgtech.apifile.services.merge;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Service responsable de la fusion de fichiers PDF.
 *
 * Cette classe permet de combiner plusieurs fichiers PDF en un seul document.
 *
 *  @author Fabrice MISSIDI MBAZI BASSEHA
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
public class FusionPdfService {
    private final StorageProperty docStorageProperty;


    //private final Logger logger = LoggerFactory.getLogger(FusionPdfService.class);


    /**
     * Fusionne deux fichiers PDF en un seul document.
     *
     * Cette méthode prend deux fichiers PDF en entrée, les fusionne en un document unique,
     * puis enregistre le fichier résultant dans le répertoire cible.
     *
     * @param multipartFile Premier fichier PDF à fusionner.
     * @param multipartFile1 Deuxième fichier PDF à fusionner.
     * @param sousRep Le sous-répertoire où enregistrer le fichier fusionné.
     * @return Une réponse HTTP contenant le fichier PDF fusionné en cas de succès (200 OK),
     *         ou une erreur (500 Internal Server Error) en cas d'échec.
     * @throws IOException Levée en cas d'erreur de lecture ou d'écriture des fichiers PDF.
     */
    public ResponseEntity<?> fusionnerPdf(MultipartFile multipartFile, MultipartFile multipartFile1, String sousRep) throws IOException {
        File file = null;
        File file1 = null;
        File outPutFile=null;
        try {
            Path path = Utils.workDirectory(docStorageProperty.getUploadFile(), sousRep);

            String reference = Utils.getRandomStr(30);
            Path target = path.resolve(reference + ".pdf");

            String reference1 = Utils.getRandomStr(31);

            Path target1 = path.resolve(reference1 + ".pdf");

            multipartFile.transferTo(target);
            multipartFile1.transferTo(target1);


            file = new File(path + "/" + reference + ".pdf");
            file1 = new File(path + "/" + reference1 + ".pdf");

            String outPut = Utils.getRandomStr(20);

            PdfDocument out = new PdfDocument(new PdfWriter(path + "/" + outPut + ".pdf"));

            PdfDocument fich1 = new PdfDocument(new PdfReader(file));
            PdfDocument fich2 = new PdfDocument(new PdfReader(file1));

            fich1.copyPagesTo(1, fich1.getNumberOfPages(), out);
            fich1.close();

            fich2.copyPagesTo(1, fich2.getNumberOfPages(), out);
            fich2.close();

            out.close();

            outPutFile = new File(path + "/" + outPut + ".pdf");

            return Utils.fileResponse(outPutFile, MediaType.APPLICATION_PDF);
        } catch (IOException e) {
            //logger.error("|||||| "+e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu "));
        } catch (BadPasswordException e) {
            //logger.error("|||||| "+e.getMessage());
            return ResponseEntity.status(500).body(Map.of("is_encrypt",
                    "fichier.s cryppté.s "));

        }

    }


}
