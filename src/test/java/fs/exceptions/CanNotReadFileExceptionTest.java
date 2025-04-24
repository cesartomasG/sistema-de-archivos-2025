package fs.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanNotReadFileExceptionTest {

  @Test
  void mensajeDeErrorEsCorrecto() {
    CanNotReadFileException ex = new CanNotReadFileException("descriptor: 42");
    assertEquals("No se pudo leer el archivo con descriptor: descriptor: 42", ex.getMessage());
  }
}
