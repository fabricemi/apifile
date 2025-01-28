package com.cgtech.apifile.contollers.split;


import com.cgtech.apifile.exceptions.StartEndException;
import com.cgtech.apifile.services.Utils;
import com.cgtech.apifile.services.split.SplitPdf;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(path = "/split")
@RequiredArgsConstructor
public class SplitController {
    private final SplitPdf pdfService;
    private final Logger logger= LoggerFactory.getLogger(SplitController.class);


    @RequestMapping(path = "/pdf")
    public ResponseEntity<?> diviserFicher(@RequestParam("file") MultipartFile file,
                                           @RequestParam("start") int start,
                                           @RequestParam("end") int end ){
        File zip=null;
        try {
            zip=pdfService.diviserPdf(file, start, end);
            return Utils.fileResponse(zip, MediaType.APPLICATION_OCTET_STREAM);
        }
        catch (IOException e) {
            logger.error("|SPLIT| "+e.getMessage());
            return  ResponseEntity.status(500).body(Map.of("error", "une erreur est survenu ("+e.getMessage()+")"));
        }
        catch (BadPasswordException e){
            logger.error("|SPLIT| "+e.getMessage());
            return  ResponseEntity.status(500).body(Map.of("is_encrypt", "votre fichier est cryppté "));
        } catch (StartEndException e) {
            return  ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
        finally {
            try {
                if(zip!=null && zip.exists()){
                    zip.delete();
                }
            }
            catch (Exception exception){
                //
            }
        }
    }
}
