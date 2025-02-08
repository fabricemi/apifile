package com.cgtech.apifile.services.split;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.exceptions.StartEndException;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Service responsable de la division d'un fichier PDF en plusieurs parties.
 *
 * Cette classe permet de diviser un fichier PDF en plusieurs sous-documents,
 * en spécifiant les pages à conserver dans chaque nouveau fichier généré.
 *
 * @author Fabrice MISSIDI MBAZI BASSEHA
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SplitPdf {
    private final StorageProperty docStorageProperty;
    //private Logger logger = LoggerFactory.getLogger(SplitPdf.class);


    /**
     * Divise un fichier PDF en plusieurs parties en fonction des pages spécifiées.
     *
     * Cette méthode extrait une plage de pages du fichier PDF (définie par `start` et `end`)
     * et génère un nouveau fichier PDF contenant uniquement les pages demandées.
     *
     * @param multipartFile Le fichier PDF à diviser.
     * @param start Le numéro de la première page à inclure dans le fichier résultant.
     * @param end Le numéro de la dernière page à inclure dans le fichier résultant.
     * @param sousRep Le sous-répertoire où enregistrer le fichier PDF divisé.
     * @return Une réponse HTTP contenant le fichier PDF divisé en cas de succès (200 OK),
     *         ou une erreur (500 Internal Server Error) en cas d'échec.
     */
    public ResponseEntity<?> diviserPdf(MultipartFile multipartFile, int start, int end, String sousRep) {
        File file = null;
        try {
            Path path =Utils.workDirectory(docStorageProperty.getUploadSplit(),sousRep);
            String reference = Utils.getRandomStr(16);
            Path target = path.resolve(reference + ".pdf");
            multipartFile.transferTo(target);
            List<File> files = new ArrayList<>();
            file = new File(path + "/" + reference + ".pdf");
            try (PdfReader reader = new PdfReader(file);

                 PdfDocument pdf = new PdfDocument(reader)) {


                isValidMarge(pdf, start, end, file);

                for (int i = start; i <= end; i++) {
                    String tmp = Utils.getRandomStr(15);

                    try (
                            PdfWriter writer = new PdfWriter(path + "/" + tmp + ".pdf");
                            PdfDocument pdf1 = new PdfDocument(writer)) {
                        pdf.copyPagesTo(i, i, pdf1);
                        PdfPage pdfPage = pdf1.getPage(1);
                        pdfPage.setArtBox(PageSize.A4);
                    }


                    File fileTmp = new File(path + "/" + tmp + ".pdf");
                    files.add(fileTmp);
                }
            }

            File zip = createZip(files, path);
            deleteFiles(files);
            deleteFinally(file);
            return Utils.fileResponse(zip, MediaType.APPLICATION_OCTET_STREAM);
        } catch (IOException e) {
            deleteFinally(file);
            return ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu (" + e.getMessage() + ")"));
        } catch (BadPasswordException e) {
            deleteFinally(file);
            return ResponseEntity.status(500).body(Map.of("is_encrypt", "votre fichier est cryppté "));
        } catch (StartEndException e) {
            deleteFinally(file);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private void deleteFinally(File file) {
        if (file != null) {
            //logger.info("fichier supprimer");
            boolean delete = file.delete();
        }
    }

    private File createZip(List<File> files, Path path) throws IOException {
        File zip = new File(path + File.separator + Utils.getRandomStr(10) + "splitByCT.zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        for (File file : files) {
            ZipEntry e = new ZipEntry(file.getName());
            out.putNextEntry(e);
            out.write(Files.readAllBytes(file.toPath()));
            out.closeEntry();
        }
        out.close();
        return zip;
    }

    public void isValidMarge(PdfDocument pdf, int start, int end, File file) throws StartEndException {
        if (start <= 0 || end <= 0 || start > end) {
            deleteFinally(file);
            throw new StartEndException("Incompatibilité des marges: l'indice debut est sup ou egal à" +
                    " 1 et doit être inferieur ou egal à l'indice fin.");
        }
        if (start > pdf.getNumberOfPages() || end > pdf.getNumberOfPages()) {
            deleteFinally(file);
            throw new StartEndException("Incompatibilité des marges : les indices sont hors limites.");
        }
    }

    private void deleteFiles(List<File> files) throws IOException {
        for (File file : files) {
            file.delete();
        }

    }
}
