package fs;

import fs.exceptions.CanNotReadFileException;
import java.util.function.Consumer;

public class File {
  private final int fd;
  private final LowLevelFileSystem lowLevel;
  private boolean closed = false;

  public File(int fd, LowLevelFileSystem lowLevel) {
    this.fd = fd;
    this.lowLevel = lowLevel;
  }

  public int getDescriptor() {
    return fd;
  }

  public void close() {
    if (closed) {
      throw new IllegalStateException("Archivo ya cerrado");
    }
    lowLevel.closeFile(fd);
    closed = true;
  }

  public void read(Buffer buffer) {
    int bytesRead = lowLevel.syncReadFile(
        fd,
        buffer.getBytes(),
        buffer.getStart(),
        buffer.getEnd()
    );
    if (bytesRead == -1) {
      throw new CanNotReadFileException(String.valueOf(fd));
    }
    buffer.limit(bytesRead);
  }

  public void write(Buffer buffer) {
    if (buffer.getCurrentSize() <= 0) {
      return;
    }
    lowLevel.syncWriteFile(
        fd,
        buffer.getBytes(),
        buffer.getStart(),
        buffer.getEnd()
    );
  }

  public void asyncRead(Buffer buffer, Consumer<Buffer> callback) {
    lowLevel.asyncReadFile(
        fd,
        buffer.getBytes(),
        buffer.getStart(),
        buffer.getEnd(),
        bytesRead -> {
          buffer.limit(bytesRead);
          callback.accept(buffer);
        }
    );
  }

  public void asyncWrite(Buffer buffer, Runnable callback) {
    lowLevel.asyncWriteFile(
        fd,
        buffer.getBytes(),
        buffer.getStart(),
        buffer.getEnd(),
        callback
    );
  }
}
