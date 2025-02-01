package com.cgtech.apifile.services.standards;

import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;

@Service
@RequiredArgsConstructor
public class GenerateEmptyPdf {
    private final StorageProperty storageProperty;

    public File generate( String format,String sousRep) throws IllegalArgumentException, IOException {

        Path path = Utils.workDirectory(storageProperty.getUploadStandard(), sousRep);
        String name=Utils.getRandomStr(7)+"--cgtechpdf.pdf";
        PdfWriter writer = new PdfWriter(path.toString() + File.separator +name);
        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(format(format));
        pdfDocument.close();
        return new File(path.toString() + File.separator + name);
    }

    public PageSize format(String i) throws IllegalArgumentException {

        switch (i.toLowerCase()) {
            case "a0":
                return PageSize.A0;
            case "a1":
                return PageSize.A1;
            case "a2":
                return PageSize.A2;
            case "a3":
                return PageSize.A3;
            case "a4":
                return PageSize.A4;
            case "a5":
                return PageSize.A5;
            case "a6":
                return PageSize.A6;
            case "a7":
                return PageSize.A7;
            case "a8":
                return PageSize.A8;
            case "a9":
                return PageSize.A9;
            case "a10":
                return PageSize.A10;

            case "b0":
                return PageSize.B0;
            case "b1":
                return PageSize.B1;
            case "b2":
                return PageSize.B2;
            case "b3":
                return PageSize.B3;
            case "b4":
                return PageSize.B4;
            case "b5":
                return PageSize.B5;
            case "b6":
                return PageSize.B6;
            case "b7":
                return PageSize.B7;
            case "b8":
                return PageSize.B8;
            case "b9":
                return PageSize.B9;
            case "b10":
                return PageSize.B10;

            case "letter":
                return PageSize.LETTER;
            case "legal":
                return PageSize.LEGAL;
            case "tabloid":
                return PageSize.TABLOID;
            case "ledger":
                return PageSize.LEDGER;
            case "executive":
                return PageSize.EXECUTIVE;

            default:
                throw new IllegalArgumentException("Format non reconnu : " + i);
        }
    }

}
