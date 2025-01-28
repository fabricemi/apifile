package com.cgtech.apifile.services.converts;

import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RequiredArgsConstructor
public abstract class ImageService {
    private final StorageProperty storage;

    public ResponseEntity<?> convertTo(MultipartFile multipartFile, MediaType mediaType) throws IOException {
        File file=null;
        try{
            Path path= Paths.get(storage.getUploadImage()).toAbsolutePath().normalize();
            if(!path.toFile().exists()){
                Files.createDirectories(path);
            }
            String reference = Utils.getRandomStr(30);
            Path target = path.resolve(reference + ".pdf");

            multipartFile.transferTo(target);
            //recuperer le fichier
            file = new File(path + "/" + reference + ".pdf");
            PDDocument document = Loader.loadPDF(file);
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImage(0);

            File fileConverted=byCase(image,reference, path);


            document.close();
            return Utils.fileResponse(fileConverted,mediaType);
        }
        catch (IOException e) {
            return  ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu ("+e.getMessage()+")"));
        }
        catch (BadPasswordException e){

            return  ResponseEntity.status(500).body(Map.of("is_encrypt", "votre fichier est cryppté "));
        }
        finally {
            if(file!=null){
                file.delete();
            }
        }
    }

    public abstract File byCase(BufferedImage image, String reference, Path path) throws IOException;




}
