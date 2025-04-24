package fs;

import fs.exceptions.CanNotOpenFileException;

public class HighLevelFileSystem implements FileSystem {
  private final LowLevelFileSystem lowLevel;

  public HighLevelFileSystem(LowLevelFileSystem lowLevel) {
    this.lowLevel = lowLevel;
  }

  @Override
  public File open(String path) {
    int fd = lowLevel.openFile(path);
    if (fd == -1) {
      throw new CanNotOpenFileException(path);
    }
    return new File(fd, lowLevel);
  }

  @Override
  public boolean exists(String path) {
    return lowLevel.exists(path);
  }

  @Override
  public boolean isRegularFile(String path) {
    return lowLevel.isRegularFile(path);
  }

  @Override
  public boolean isDirectory(String path) {
    return lowLevel.isDirectory(path);
  }
}
