package fs.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanNotOpenFileExceptionTest {

  @Test
  void mensajeDeErrorEsCorrecto() {
    CanNotOpenFileException ex = new CanNotOpenFileException("archivo.txt");
    assertEquals("No se pudo abrir el archivo: archivo.txt", ex.getMessage());
  }
}
