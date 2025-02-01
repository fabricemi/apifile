package com.cgtech.apifile.listener;

import com.cgtech.apifile.config.StorageProperty;
import com.cgtech.apifile.services.standards.GenerateEmptyPdf;
import com.cgtech.apifile.services.standards.GenerateTable;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;

@Configuration
@RequiredArgsConstructor
public class AppDirectoryInit {
    private final StorageProperty storageProperty;
    private final GenerateTable generateTable;
    @Bean
    CommandLineRunner runner(){
        return args -> {
            Path images=Path.of(storageProperty.getUploadImage()).toAbsolutePath().normalize();
            Path files=Path.of(storageProperty.getUploadFile()).toAbsolutePath().normalize();
            Path splits=Path.of(storageProperty.getUploadSplit()).toAbsolutePath().normalize();
            Path standards=Path.of(storageProperty.getUploadStandard()).toAbsolutePath().normalize();


            Files.createDirectories(images);
            Files.createDirectories(files);
            Files.createDirectories(splits);
            Files.createDirectories(standards);

        };
    }
}
