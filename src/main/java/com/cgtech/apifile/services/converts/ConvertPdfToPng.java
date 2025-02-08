package com.cgtech.apifile.services.converts;

import com.cgtech.apifile.config.StorageProperty;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 @see com.cgtech.apifile.services.converts.ImageService
 */
@Service
public class ConvertPdfToPng extends ImageService{
    public ConvertPdfToPng(StorageProperty storage) {
        super(storage);
    }

    /**
     * Convertit une image extraite d'un PDF en format PNG et l'enregistre sur le disque.
     *
     * @param image L'image extraite du fichier PDF.
     * @param reference Une référence unique associée au fichier (ex: identifiant, nom).
     * @param path Le chemin où enregistrer l'image PNG générée.
     * @return Le fichier image JPEG généré.
     * @throws IOException Levée en cas d'erreur d'écriture du fichier sur le disque.
     */

    @Override
    public File byCase(BufferedImage image, String reference, Path path) throws IOException {
        File out=new File(path + "/" + reference + ".png");
        ImageIO.write(image, "PNG", out);
        return out;
    }
}
