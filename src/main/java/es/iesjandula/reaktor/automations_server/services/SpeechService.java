package es.iesjandula.reaktor.automations_server.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SpeechService {

    private Model model;

    @PostConstruct
    public void init() throws IOException, URISyntaxException {

        Path tempDir = Files.createTempDirectory("vosk-model");
        tempDir.toFile().deleteOnExit();

        extractModelFromResources("speech/vosk-model-small-es-0.42", tempDir);

        this.model = new Model(tempDir.toAbsolutePath().toString());

        log.info("Modelo Vosk cargado correctamente desde resources");
    }

    public String transcribe(InputStream inputStream) throws IOException {

        Recognizer recognizer = new Recognizer(model, 16000);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) >= 0) {
            recognizer.acceptWaveForm(buffer, bytesRead);
        }

        String resultJson = recognizer.getFinalResult();

        recognizer.close();

        // 🔥 EXTRAEMOS SOLO EL CAMPO "text"
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(resultJson);

        String textoPlano = node.get("text").asText();

        log.info("Texto limpio reconocido: {}", textoPlano);

        return textoPlano;
    }

    private void extractModelFromResources(String resourcePath, Path targetDir) throws IOException, URISyntaxException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        URL resource = classLoader.getResource(resourcePath);

        if (resource == null) {
            throw new IOException("Modelo no encontrado en resources");
        }

        URI uri = resource.toURI();

        if (uri.getScheme().equals("jar")) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                copyDirectory(fileSystem.getPath(resourcePath), targetDir);
            }
        } else {
            copyDirectory(Paths.get(uri), targetDir);
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {

        Files.walk(source).forEach(path -> {
            try {
                Path destination = target.resolve(source.relativize(path).toString());

                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}