package com.blogggr.utilities;

import com.blogggr.exceptions.StorageException;
import com.cloudinary.Cloudinary;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sunnen on 25.08.17.
 */
public class FileStorageManager {

  private final Path storageDirectory;
  private Cloudinary cloudinary;

  private static final String CLOUDINARY_IMG_ROOT = "http://res.cloudinary.com/blogggr/image/upload/";

  public FileStorageManager(String folderName, String imageApiKey, String imageApiSecret){
    this.storageDirectory = Paths.get(folderName);
    Map<String, String> imgCloudConfig = new HashMap();
    imgCloudConfig.put("cloud_name", "blogggr");
    imgCloudConfig.put("api_key", imageApiKey);
    imgCloudConfig.put("api_secret", imageApiSecret);
    this.cloudinary = new Cloudinary(imgCloudConfig);
  }

  public void store(MultipartFile file, String newFileName) throws StorageException{
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      if (file.isEmpty()) {
        throw new StorageException("Empty files are not accepted: " + filename);
      }
      this.storageDirectory.toFile().mkdirs();
      Files.copy(file.getInputStream(), this.storageDirectory.resolve(newFileName),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + newFileName, e);
    }
  }

  public void storeOnCloud(String fullFileName, String name) throws IOException{
    Map<String, String> params = new HashMap<>();
    params.put("public_id",name);
    cloudinary.uploader().upload(storageDirectory.resolve(fullFileName).toFile(), params);
  }

  public void delete(Path filePath) throws IOException{
    Files.delete(filePath);
  }

  public Resource getImageResourceFromCloud(String imageTag) throws StorageException{
    String url = CLOUDINARY_IMG_ROOT+imageTag;
    Resource resource;
    try {
      resource = new UrlResource(new URL(url));
    } catch(MalformedURLException e){
      throw new StorageException("Malformed image cloud URL!" ,e);
    }
    if (resource.exists() || resource.isReadable()) {
      return resource;
    } else {
      throw new StorageException(
          "Unable to load file: " + url);
    }
  }

  public Path getStorageDirectory(){
    return storageDirectory;
  }

}
