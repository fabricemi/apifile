package com.cgtech.apifile.services.split;


import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.exceptions.StartEndException;
import com.cgtech.apifile.services.Utils;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class SplitPdf {
    private final StorageProperty docStorageProperty;

    public File diviserPdf(MultipartFile multipartFile, int start, int end) throws IOException, StartEndException,BadPasswordException {
        Path path = Paths.get(docStorageProperty.getUploadSplit()).toAbsolutePath().normalize();
        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }

        String reference = Utils.getRandomStr(16);
        Path target = path.resolve(reference + ".pdf");
        multipartFile.transferTo(target);
        List<File> files = new ArrayList<>();
        File file = new File(path + "/" + reference + ".pdf");

        PdfReader reader = new PdfReader(file);
        PdfDocument pdf = new PdfDocument(reader);
        isValidMarge(pdf,start,end);

        for (int i = start; i <= end; i++) {
            String tmp = Utils.getRandomStr(15);
            PdfDocument pdf1 = new PdfDocument(new PdfWriter(path + "/" + tmp + ".pdf"));
            pdf.copyPagesTo(i, i, pdf1);
            PdfPage pdfPage = pdf1.getPage(1);
            pdfPage.setArtBox(PageSize.A4);
            pdf1.close();

            File fileTmp = new File(path + "/" + tmp + ".pdf");
            files.add(fileTmp);
        }
        pdf.close();

        File zip = createZip(files, path);
        deleteFiles(files);

        return zip;
    }

    private File createZip(List<File> files, Path path) throws IOException {
        File zip = new File(path + File.separator + Utils.getRandomStr(10) + "splitByCT.zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        for (File file : files) {
            ZipEntry e = new ZipEntry(file.getName());
            out.putNextEntry(e);
            out.write(Files.readAllBytes(file.toPath()));
            out.closeEntry();
        }
        out.close();
        return zip;
    }

    public void isValidMarge(PdfDocument pdf, int start, int end) throws StartEndException {
        if (start <= 0 || end <= 0 || start > end) {
            throw new StartEndException("Incompatibilité des marges: l'indice est sup ou egal à" +
                    " 1 et doit être inferieur ou egal à l'indice fin.");
        }
        if (start > pdf.getNumberOfPages() || end > pdf.getNumberOfPages()) {
            throw new StartEndException("Incompatibilité des marges : les indices sont hors limites.");
        }
    }

    private void deleteFiles(List<File> files) throws IOException{
        for (File file:files) {
            file.delete();
        }

    }
}
