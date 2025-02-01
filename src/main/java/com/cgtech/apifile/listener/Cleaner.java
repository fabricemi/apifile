package com.cgtech.apifile.listener;


import com.cgtech.apifile.config.StorageProperty;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class Cleaner {
    private final StorageProperty storage;
    private Logger logger= LoggerFactory.getLogger(Cleaner.class);


    @Scheduled(cron = "0 */5 * * * *")
    @Async
    public void delete() {
        try {
            logger.info("Fichiers vidés à : "+ Instant.now());
        } catch (Exception e) {
            //logger.error(e.getMessage());
        }
    }
}
