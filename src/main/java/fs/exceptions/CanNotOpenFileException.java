package fs.exceptions;

public class CanNotOpenFileException extends RuntimeException {
  public CanNotOpenFileException(String path) {
    super("No se pudo abrir el archivo: " + path);
  }
}
