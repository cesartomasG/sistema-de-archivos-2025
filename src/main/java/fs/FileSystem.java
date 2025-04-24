package fs;

public interface FileSystem {

  File open(String path);

  boolean exists(String path);

  boolean isRegularFile(String path);

  boolean isDirectory(String path);
}
