package com.cgtech.apifile.services.converts;

import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Service pour la gestion de la conversion d'images en PDF.
 *Ce service prend une image en entrée et génère un fichier PDF.
 *  @author Fabrice MISSIDI MBAZI BASSEHA
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ConvertImageToPdf {
    private final StorageProperty storage;

    /**
     *Convertit une image en fichier PDF.
     * @param multipartFile Le fichier image à convertir en PDF
     * @param sousRep sous repertoire de travaille
     * @return Une réponse HTTP contenant le fichier PDF généré en cas de succès (200 OK),
     *               ou une erreur (500 Internal Server Error) en cas de problème.
     * @throws IOException IOException Levée si une erreur de lecture/écriture se produit ou si le fichier n'est pas compatible.
     */
    public ResponseEntity<?> convertToPdf(MultipartFile multipartFile, String sousRep) throws IOException {
        Path target=null;
        try {
            Path path=Utils.workDirectory(storage.getUploadImage(),sousRep);
            String reference = Utils.getRandomStr(30);
            String ext = Utils.getExtension(multipartFile.getOriginalFilename());
            target = path.resolve(reference + "." + ext);
            multipartFile.transferTo(target);

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDImageXObject pdImageXObject = PDImageXObject.createFromFile(
                    path + "/" + reference + "." + ext, document);

            PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0));

            redBeforeDraw(page,pdImageXObject,contentStream);

            contentStream.close();

            String finalName = Utils.getRandomStr(35);
            document.save(path + "/" + finalName + ".pdf");
            document.close();

            File file = new File(path + "/" + finalName + ".pdf");

            document.close();
            return Utils.fileResponse(file, MediaType.APPLICATION_PDF);


        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu (" + e.getMessage() + ")"));
        } catch (BadPasswordException e) {
            return ResponseEntity.status(500).body(Map.of("is_encrypt", "votre fichier est cryppté "));
        }
    }

    private void redBeforeDraw(PDPage page, PDImageXObject pdImageXObject, PDPageContentStream contentStream) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();


        float imageWidth = pdImageXObject.getWidth();
        float imageHeight = pdImageXObject.getHeight();


        float scaleX = pageWidth / imageWidth;
        float scaleY = pageHeight / imageHeight;


        float scale = Math.min(scaleX, scaleY);


        float newImageWidth = imageWidth * scale;
        float newImageHeight = imageHeight * scale;


        float x = (pageWidth - newImageWidth) / 2;
        float y = (pageHeight - newImageHeight) / 2;


        contentStream.drawImage(pdImageXObject, x, y, newImageWidth, newImageHeight);
    }

}
