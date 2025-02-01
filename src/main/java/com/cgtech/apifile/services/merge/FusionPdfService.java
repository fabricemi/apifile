package com.cgtech.apifile.services.merge;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FusionPdfService {
    private final StorageProperty docStorageProperty;


    private final Logger logger = LoggerFactory.getLogger(FusionPdfService.class);


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
            logger.error("|||||| "+e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu "));
        } catch (BadPasswordException e) {
            logger.error("|||||| "+e.getMessage());
            return ResponseEntity.status(500).body(Map.of("is_encrypt",
                    "fichier.s cryppté.s "));

        }

    }


}
