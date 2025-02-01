package com.cgtech.apifile.config;


import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@ConfigurationProperties(prefix = "document")
@Data
public class StorageProperty {
    private String uploadFile;
    private String uploadImage;
    private String uploadSplit;
    private String uploadStandard;
}
