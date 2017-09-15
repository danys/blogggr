package com.blogggr.utilities;

import com.blogggr.exceptions.StorageException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
public class FileStorageManager {

  private final Path storageDirectory;

  public FileStorageManager(String folderName){
    this.storageDirectory = Paths.get(folderName);
  }

  public void store(MultipartFile file, String newFileName) throws StorageException{
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      if (file.isEmpty()) {
        throw new StorageException("Empty files are not accepted: " + filename);
      }
      Files.copy(file.getInputStream(), this.storageDirectory.resolve(newFileName),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + newFileName, e);
    }
  }

  public Resource loadAsResource(String filename) throws StorageException{
    try {
      Path file = storageDirectory.resolve(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageException(
            "Unable to load file: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new StorageException("Unable to read file: " + filename, e);
    }
  }

  public Path getStorageDirectory(){
    return storageDirectory;
  }

}
