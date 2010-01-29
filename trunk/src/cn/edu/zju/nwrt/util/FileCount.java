package cn.edu.zju.nwrt.util;

import java.io.File;
import java.io.FileFilter;

public class FileCount {

  private File file;

  private int count = 0;
  
  private FileFilter filter;

  public void setFilter(FileFilter filter) {
    this.filter = filter;
  }

  public FileCount() {
    super();
  }

  public FileCount(String fileName) {
    this.setFile(new File(fileName));
  }

  public FileCount(File file) {
    this.setFile(file);
  }

  public void setFile(File file) {
    this.file = file;
  }

  public int getFileCount() {
    if (this.file == null || file.exists() == false)
      return 0;
    this.count(this.file);
    return count;
  }

  private void count(File file) {
    if (file.isDirectory() == false){
      count ++;
      return;
    }
    File[] files = file.listFiles(filter);
    for(int i=0;i<files.length;++i){
      count(files[i]);
    }
  }
}
