package com.blogggr.fakes;

import com.blogggr.utilities.FileStorageManager;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.core.io.Resource;

/**
 * Created by Daniel Sünnen on 07/08/2018
 */
public class ThrowStoreExceptionStorageManager extends FileStorageManager {

  public ThrowStoreExceptionStorageManager(String folderName){
    super(folderName, null, null, null);
  }

  @Override
  public void storeOnCloud(String fullFileName, String name) throws IOException{
    throw new IOException("IOException");
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
