package com.cgtech.apifile.services.converts;

import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class TextToPdf {
    private final StorageProperty storageProperty;

    public File convert(MultipartFile file, String sousRep) throws IOException {

        Path path= Utils.workDirectory(storageProperty.getUploadStandard(), sousRep);
        String reference=Utils.getRandomStr(10);

        Path target=path.resolve(reference+"--cgtechconvert.txt");
        file.transferTo(target);
        String name=Utils.getRandomStr(11)+"--cgt.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(path+ File.separator+name));
        Document document = new Document(pdf);
        BufferedReader br = new BufferedReader(new FileReader(String.valueOf(target)));
        String line;
        while ((line = br.readLine()) != null) {
            document.add(new Paragraph(line));
        }
        document.close();


        return new File(path+ File.separator+name);
    }
}
