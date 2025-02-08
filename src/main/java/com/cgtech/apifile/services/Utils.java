package com.cgtech.apifile.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Classe utilitaire fournissant des méthodes pour la gestion des fichiers et des répertoires.
 *
 * Cette classe contient des méthodes pour générer des chaînes aléatoires, obtenir des extensions de fichiers,
 * gérer les réponses de fichiers (téléchargement), supprimer des fichiers et créer des répertoires de travail.
 *
 * @author Fabrice MISSIDI MBAZI BASSEHA
 * @version 1.0
 */
public class Utils {
    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * Génère une chaîne aléatoire de longueur spécifiée.
     *
     * @param n Longueur de la chaîne à générer.
     * @return Une chaîne aléatoire de `n` caractères.
     */
    public static String getRandomStr(int n) {
        // Choisissez un caractère au hasard à partir de cette chaîne
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz" + "1234567890";
        StringBuilder s = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (str.length() * Math.random());
            s.append(str.charAt(index));
        }
        return s.toString();
    }

    /**
     * Extrait l'extension d'un fichier à partir de son nom.
     *
     * @param filename Le nom du fichier.
     * @return L'extension du fichier (ex: "pdf", "jpg").
     */
    public static String getExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }
        return extension;
    }

    /**
     * Prépare une réponse HTTP contenant un fichier à télécharger.
     *
     * @param file Le fichier à envoyer.
     * @param mediaType Le type de média (ex: PDF, JPEG).
     * @return Une réponse HTTP avec le fichier comme corps, prête pour le téléchargement.
     * @throws FileNotFoundException Si le fichier n'est pas trouvé.
     */
    public static ResponseEntity<?> fileResponse(File file, MediaType mediaType) throws FileNotFoundException {
        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamResource resource = new InputStreamResource(fileInputStream);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=" + file.getName());

            return ResponseEntity.status(200)
                    .headers(headers)
                    .contentLength(file.length())
                    .contentType(mediaType)
                    .body(resource);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(Map.of("error", "une erreur est survenue"), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Supprime un répertoire ainsi que tous ses fichiers.
     *
     * @param path Le chemin du répertoire à supprimer.
     */

    public static void remove(Path path){
        if (Files.exists(path)) {
            try (Stream<Path> pathStream = Files.walk(path)) {
                pathStream.filter(Files::isRegularFile).forEach(
                        (i) -> i.toFile().delete()
                );
                Files.delete(path);
            } catch (IOException e) {
                logger.info(e.getMessage());
            }
            ;
            logger.info("fichier supprimé le "+ Instant.now().toString() +" : existe tooujouts "+Files.exists(path));
        }
    }

    /**
     * Crée un répertoire de travail, y compris les sous-répertoires nécessaires.
     *
     * @param parent Le répertoire parent où créer le répertoire de travail.
     * @param sousRep Le sous-répertoire à créer à l'intérieur du répertoire parent.
     * @return Le chemin absolu du répertoire de travail créé.
     * @throws IOException Si une erreur se produit lors de la création des répertoires.
     */
    public static Path workDirectory(String parent, String sousRep) throws IOException {
        Path paths = Paths.get(parent).toAbsolutePath().normalize();
        if (!paths.toFile().exists()) {
            Files.createDirectories(paths);
        }
        Path path = Path.of(paths.toString() + File.separator + sousRep);
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }

        return path;
    }

}
