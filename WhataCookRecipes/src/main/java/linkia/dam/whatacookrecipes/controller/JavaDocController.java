package linkia.dam.whatacookrecipes.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("${app.endpoint.javadoc}")
@Log4j2
public class JavaDocController {

    @GetMapping("/")
    public ResponseEntity<byte[]> getJavadoc() {
        try {
            Resource resource = new FileSystemResource("/app/javadoc/index.html");
            if (!resource.exists()) {
                log.error("Javadoc file not found at /app/javadoc/index.html");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            byte[] content = Files.readAllBytes(resource.getFile().toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/html");
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Error reading Javadoc file: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
