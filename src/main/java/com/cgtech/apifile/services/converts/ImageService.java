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
import java.nio.file.Path;
import java.util.Map;

/**
 * abstract class pour la gestion de la conversion pdf en image
 * ce service prend un pdf et un format d'image (jpeg/png) et genere une image correspondant a format
 * @version 1.0
 * @author Fabrice MISSIDI MBAZI BASSEHA
 */
@RequiredArgsConstructor
public abstract class ImageService {
    private final StorageProperty storage;
    //private final Logger logger= LoggerFactory.getLogger(ImageService.class);

    /**
     * Convertit un fichier PDF en une image selon le format spécifié.
     *
     * Cette méthode prend un fichier PDF en entrée, extrait sa première page,
     * la convertit en image et la sauvegarde dans un format défini par `mediaType`.
     *
     * @param multipartFile Le fichier PDF à convertir.
     * @param mediaType Le format de l'image de sortie (ex: JPEG, PNG).
     * @param sousRep Le sous-répertoire où stocker l'image générée.
     * @return Une réponse HTTP contenant le fichier image converti en cas de succès (200 OK),
     *         ou une erreur (500 Internal Server Error) en cas de problème.
     * @throws IOException Levée en cas d'erreur de lecture/écriture du fichier PDF ou de conversion en image.
     * @throws BadPasswordException Levée si le fichier PDF est protégé par un mot de passe.
     */
    public ResponseEntity<?> convertTo(MultipartFile multipartFile, MediaType mediaType, String sousRep) throws IOException {
        File file=null;
        try{
            Path path =Utils.workDirectory(storage.getUploadImage(), sousRep);
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
    }

    /**
     * Convertit et enregistre une image dans un format spécifique.
     * Cette méthode est une implémentation spécifique permettant de gérer
     * le format de l'image en sortie selon le cas d'utilisation.

     * @param bufferedImage L'image à convertir.
     * @param reference Une référence unique associée à l'image (ex: identifiant, nom).
     * @param path Le chemin où enregistrer l'image générée.
     * @return Le fichier converti et sauvegardé.
     * @throws IOException Levée en cas d'erreur d'écriture du fichier ou de format non pris en charge.
     */
    public abstract File byCase(BufferedImage bufferedImage, String reference, Path path) throws IOException;




}
