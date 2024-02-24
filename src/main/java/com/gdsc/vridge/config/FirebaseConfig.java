package com.gdsc.vridge.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    private final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Initializing Firebase.");
//        FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");
        ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
            .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);
        log.info("FirebaseApp initialized: {}", app.getName());
        return app;
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        log.info("Initializing FirebaseAuth.");
        return FirebaseAuth.getInstance(firebaseApp());
    }

}
