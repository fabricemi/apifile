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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConvertImageToPdf {
    private final StorageProperty storage;

    public ResponseEntity<?> convertToPdf(MultipartFile multipartFile) throws IOException {
        Path target=null;
        try {
            Path path= Paths.get(storage.getUploadFile()).toAbsolutePath().normalize();
            if(!path.toFile().exists()){
                Files.createDirectories(path);
            }


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
        finally {
            try {
                if(target!=null){
                    Files.delete(target);
                }
            }
            catch (Exception e){
                //
            }
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
