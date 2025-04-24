package fs.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CanNotWriteFileExceptionTest {

  @Test
  void mensajeDeErrorEsCorrecto() {
    CanNotWriteFileException ex = new CanNotWriteFileException("descriptor: 99");
    assertEquals("No se pudo escribir en el archivo con descriptor: descriptor: 99", ex.getMessage());
  }
}
