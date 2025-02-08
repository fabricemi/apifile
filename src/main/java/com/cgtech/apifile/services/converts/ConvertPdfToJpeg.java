package com.cgtech.apifile.services.converts;

import com.cgtech.apifile.config.StorageProperty;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @see com.cgtech.apifile.services.converts.ImageService
 */
@Service
public class ConvertPdfToJpeg extends ImageService {
    /**
     * Constructeur du service de conversion PDF → JPEG.
     *
     * @param storage Propriété de stockage utilisée pour définir le répertoire cible.
     */
    public ConvertPdfToJpeg(StorageProperty storage) {
        super(storage);
    }

    /**
     * Convertit une image extraite d'un PDF en format JPEG et l'enregistre sur le disque.
     *
     * @param image L'image extraite du fichier PDF.
     * @param reference Une référence unique associée au fichier (ex: identifiant, nom).
     * @param path Le chemin où enregistrer l'image JPEG générée.
     * @return Le fichier image JPEG généré.
     * @throws IOException Levée en cas d'erreur d'écriture du fichier sur le disque.
     */
    @Override
    public File byCase(BufferedImage image, String reference, Path path) throws IOException {
        File out=new File(path + "/" + reference + ".jpeg");
        ImageIO.write(image, "JPEG",out);
        return out;
    }
}
