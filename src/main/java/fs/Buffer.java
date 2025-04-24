package fs;

public class Buffer {
  private final byte[] bytes;
  private final int start;
  private int end;

  public Buffer(int size) {
    this.bytes = new byte[size];
    this.start = 0;
    this.end = size - 1;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public void limit(int readBytes) {
    this.end = start + readBytes - 1;
  }

  public int getMaxSize() {
    return bytes.length;
  }

  public int getCurrentSize() {
    return end - start + 1;
  }
}
