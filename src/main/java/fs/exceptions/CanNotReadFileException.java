package fs.exceptions;

public class CanNotReadFileException extends RuntimeException {
  public CanNotReadFileException(String descriptor) {
    super("No se pudo leer el archivo con descriptor: " + descriptor);
  }
}
