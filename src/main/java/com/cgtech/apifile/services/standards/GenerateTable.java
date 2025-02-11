package com.cgtech.apifile.services.standards;

import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class GenerateTable {
    private final StorageProperty storageProperty;


  /*  public void generate(){

        try {
           Path path = Utils.workDirectory(storageProperty.getUploadStandard(), "aaaa");
            String name=Utils.getRandomStr(7)+"--cgtechpdf.pdf";
            PdfWriter writer = new PdfWriter(path.toString() + File.separator +name);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document=new Document(pdfDocument);
            System.out.println("appelé");
            float [] colums={255f,255f};
            Table table=new Table(colums);

            Cell cell=new Cell();
            cell.add(new Paragraph("BBB")).setBold();
            table.addCell(cell);

            Cell cell1=new Cell();
            cell1.add(new Paragraph("AAA"));
            table.addCell(cell1);

            document.add(table);
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }*/

    public File generate(MultipartFile file, String sousRep) throws IOException {
        Path path=Utils.workDirectory(storageProperty.getUploadStandard(), sousRep);

        String reference=Utils.getRandomStr(10);
        Path target=path.resolve(reference+"--cgtechtable.html");
        file.transferTo(target);

        File htmlSource = target.toFile();

        String dstName=Utils.getRandomStr(9)+"--cgtechtable.pdf";
        File pdfDest = new File(path + File.separator + dstName);

        ConverterProperties converterProperties = new ConverterProperties();
        FileInputStream fileInputStream=new FileInputStream(htmlSource);
        FileOutputStream fileOutputStream=new FileOutputStream(pdfDest);
        HtmlConverter.convertToPdf(fileInputStream,fileOutputStream, converterProperties);

        fileInputStream.close();
        fileOutputStream.close();

        return pdfDest;
    }


    /**
     * PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
     * Document document = new Document(pdf);
     * BufferedReader br = new BufferedReader(new FileReader(SRC));
     * String line;
     * while ((line = br.readLine()) != null) {
     *    document.add(new Paragraph(line));
     * }
     * document.close();
     */
}
