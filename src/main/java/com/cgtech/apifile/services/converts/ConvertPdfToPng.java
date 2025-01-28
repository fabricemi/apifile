package com.cgtech.apifile.services.converts;

import com.cgtech.apifile.config.StorageProperty;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class ConvertPdfToPng extends ImageService{
    public ConvertPdfToPng(StorageProperty storage) {
        super(storage);
    }

    @Override
    public File byCase(BufferedImage image, String reference, Path path) throws IOException {
        File out=new File(path + "/" + reference + ".png");
        ImageIO.write(image, "PNG", out);
        return out;
    }
}
