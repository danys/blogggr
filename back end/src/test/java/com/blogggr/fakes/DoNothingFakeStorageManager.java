package com.blogggr.fakes;

import com.blogggr.utilities.FileStorageManager;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Daniel Sünnen on 07/08/2018
 */
public class DoNothingFakeStorageManager extends FileStorageManager {

  public DoNothingFakeStorageManager(String folderName){
    super(folderName, null, null, null);
  }


  @Override
  public void store(MultipartFile file, String newFileName){
    //do nothing
  }

  @Override
  public void storeOnCloud(String fullFileName, String name) {
    //do nothing
  }

  @Override
  public void delete(Path filePath) throws IOException {
    //do nothing
  }

  @Override
  public Resource getImageResourceFromCloud(String imageTag) {
    //do nothing
    return null;
  }
}
