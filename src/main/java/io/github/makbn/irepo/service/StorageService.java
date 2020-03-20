package io.github.makbn.irepo.service;

import io.github.makbn.irepo.exception.StorageException;
import io.github.makbn.irepo.exception.StorageFileNotFoundException;
import io.github.makbn.irepo.model.IPA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class StorageService{

    @Value("${storage.root.path}")
    private String rootDir;

    @Value("${storage.root.path.icon}")
    private String iconDir;

    private Path rootLocation;


    public IPA store(MultipartFile file) {
        String filename = UUID.randomUUID().toString().replace("-","");
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }

            return IPA.builder()
                    .uuid(filename)
                    .build();

        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    public void saveIcon(IPA ipa, InputStream in) throws IOException {
        File icon = new File(iconDir, ipa.getUuid()+".png");
        Files.copy(in, icon.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public Path loadIcon(String filename) {
        return rootLocation.resolve("icon/"+filename);
    }

    public Resource loadAsResource(String filename, boolean icon) {
        try {
            Path file = icon ? loadIcon(filename) :load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            this.rootLocation = Paths.get(rootDir);
            Files.createDirectories(rootLocation);
            Files.createDirectories(Paths.get(iconDir));
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}