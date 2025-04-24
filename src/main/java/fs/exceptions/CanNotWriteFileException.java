package fs.exceptions;

public class CanNotWriteFileException extends RuntimeException {
  public CanNotWriteFileException(String descriptor) {
    super("No se pudo escribir en el archivo con descriptor: " + descriptor);
  }
}
