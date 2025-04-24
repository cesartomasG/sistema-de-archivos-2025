package fs;
import fs.exceptions.CanNotOpenFileException;
import fs.exceptions.CanNotReadFileException;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class HighLevelFileSystemTest {

  private LowLevelFileSystem lowLevelFileSystem;
  private HighLevelFileSystem fileSystem;

  @BeforeEach
  void initFileSystem() {
    lowLevelFileSystem = mock(LowLevelFileSystem.class);
    fileSystem = new HighLevelFileSystem(lowLevelFileSystem);
  }

  @Test
  void sePuedeAbrirUnArchivo() {
    when(lowLevelFileSystem.openFile("unArchivo.txt")).thenReturn(42);
    File file = fileSystem.open("unArchivo.txt");
    Assertions.assertEquals(file.getDescriptor(), 42);
  }

  @Test
  void siLaAperturaFallaUnaExcepcionEsLanzada() {
    when(lowLevelFileSystem.openFile("otroArchivo.txt")).thenReturn(-1);
    Assertions.assertThrows(CanNotOpenFileException.class, () -> fileSystem.open("otroArchivo.txt"));
  }

  @Test
  void sePuedeLeerSincronicamenteUnArchivoCuandoNoHayNadaParaLeer() {
    Buffer buffer = new Buffer(100);

    when(lowLevelFileSystem.openFile("ejemplo.txt")).thenReturn(42);
    when(lowLevelFileSystem.syncReadFile(42, buffer.getBytes(), 0, 100)).thenReturn(0);

    File file = fileSystem.open("ejemplo.txt");
    file.read(buffer);

    Assertions.assertEquals(0, buffer.getStart());
    Assertions.assertEquals(-1, buffer.getEnd());
    Assertions.assertEquals(0, buffer.getCurrentSize());
  }

  @Test
  void sePuedeLeerSincronicamenteUnArchivoCuandoHayAlgoParaLeer() {
    Buffer buffer = new Buffer(10);

    when(lowLevelFileSystem.openFile("ejemplo.txt")).thenReturn(42);
    when(lowLevelFileSystem.syncReadFile(42, buffer.getBytes(), 0, 9)).thenAnswer(invocation -> {
      Arrays.fill(buffer.getBytes(), 0, 4, (byte) 3);
      return 4;
    });

    File file = fileSystem.open("ejemplo.txt");
    file.read(buffer);

    Assertions.assertEquals(0, buffer.getStart());
    Assertions.assertEquals(3, buffer.getEnd());
    Assertions.assertEquals(4, buffer.getCurrentSize());
    Assertions.assertArrayEquals(buffer.getBytes(), new byte[] {3, 3, 3, 3, 0, 0, 0, 0, 0, 0});
  }

  @Test
  void siLaLecturaSincronicaFallaUnaExcepcionEsLanzada() {
    Buffer buffer = new Buffer(10);

    when(lowLevelFileSystem.openFile("archivoMalito.txt")).thenReturn(13);
    when(lowLevelFileSystem.syncReadFile(anyInt(), any(), anyInt(), anyInt())).thenReturn(-1);

    File file = fileSystem.open("archivoMalito.txt");

    Assertions.assertThrows(CanNotReadFileException.class, () -> file.read(buffer));
  }

  @Test
  void sePuedeEscribirSincronicamenteUnArchivoCuandoHayNoHayNadaParaEscribir() {
    Buffer buffer = new Buffer(10);
    buffer.limit(0); // indica que no hay nada para escribir

    when(lowLevelFileSystem.openFile("archivoVacio.txt")).thenReturn(42);

    File file = fileSystem.open("archivoVacio.txt");
    file.write(buffer);

    // No debe invocarse la escritura si el buffer está vacío
    verify(lowLevelFileSystem, never()).syncWriteFile(anyInt(), any(), anyInt(), anyInt());
  }

  @Test
  void sePuedeEscribirSincronicamenteUnArchivoCuandoHayAlgoParaEscribir() {
    Buffer buffer = new Buffer(5);
    buffer.limit(5); // indica que hay 5 bytes válidos en el buffer

    when(lowLevelFileSystem.openFile("archivoDatos.txt")).thenReturn(21);

    File file = fileSystem.open("archivoDatos.txt");
    file.write(buffer);

    verify(lowLevelFileSystem).syncWriteFile(
        eq(21),
        eq(buffer.getBytes()),
        eq(0),
        eq(4) // buffer.end == 4 cuando hay 5 bytes
    );
  }

  @Test
  void sePuedeLeerAsincronicamenteUnArchivo() {
    Buffer buffer = new Buffer(20);

    when(lowLevelFileSystem.openFile("archivoAsync.txt")).thenReturn(42);

    doAnswer(invocation -> {
      byte[] bytes = invocation.getArgument(1);
      int start = invocation.getArgument(2);
      Consumer<Integer> callback = invocation.getArgument(4);

      Arrays.fill(bytes, start, start + 3, (byte) 7); // simula lectura de 3 bytes
      callback.accept(3);
      return null;
    }).when(lowLevelFileSystem).asyncReadFile(anyInt(), any(), anyInt(), anyInt(), any());

    File file = fileSystem.open("archivoAsync.txt");

    file.asyncRead(buffer, resultBuffer -> {
      assertEquals(3, resultBuffer.getCurrentSize());
      assertEquals(2, resultBuffer.getEnd());
      assertEquals(7, resultBuffer.getBytes()[0]);
      assertEquals(7, resultBuffer.getBytes()[1]);
      assertEquals(7, resultBuffer.getBytes()[2]);
    });
  }

  @Test
  void sePuedeEscribirAsincronicamenteUnArchivo() {
    Buffer buffer = new Buffer(10);
    buffer.limit(5); // quiere escribir 5 bytes

    when(lowLevelFileSystem.openFile("archivoAsyncWrite.txt")).thenReturn(42);

    Runnable callback = mock(Runnable.class);

    doAnswer(invocation -> {
      Runnable cb = invocation.getArgument(4);
      cb.run(); // ejecutamos callback como si la escritura hubiera finalizado
      return null;
    }).when(lowLevelFileSystem).asyncWriteFile(anyInt(), any(), anyInt(), anyInt(), any());

    File file = fileSystem.open("archivoAsyncWrite.txt");
    file.asyncWrite(buffer, callback);

    verify(lowLevelFileSystem).asyncWriteFile(eq(42), eq(buffer.getBytes()), eq(0), eq(4), eq(callback));
    verify(callback).run(); // confirma que se llamó al callback
  }

  @Test
  void sePuedeCerrarUnArchivo() {
    when(lowLevelFileSystem.openFile("cerrar.txt")).thenReturn(42);

    File file = fileSystem.open("cerrar.txt");
    file.close();

    verify(lowLevelFileSystem).closeFile(42);

    // Segunda llamada debe lanzar excepción
    assertThrows(IllegalStateException.class, file::close);
  }

  @Test
  void sePuedeSaberSiUnPathEsUnArchivoRegular() {
    when(lowLevelFileSystem.isRegularFile("archivo.txt")).thenReturn(true);

    assertTrue(fileSystem.isRegularFile("archivo.txt"));
    verify(lowLevelFileSystem).isRegularFile("archivo.txt");
  }


  @Test
  void sePuedeSaberSiUnPathEsUnDirectorio() {
    when(lowLevelFileSystem.isDirectory("carpeta")).thenReturn(true);

    assertTrue(fileSystem.isDirectory("carpeta"));
    verify(lowLevelFileSystem).isDirectory("carpeta");
  }


  @Test
  void sePuedeSaberSiUnPathExiste() {
    when(lowLevelFileSystem.exists("archivoOCarpeta")).thenReturn(true);

    assertTrue(fileSystem.exists("archivoOCarpeta"));
    verify(lowLevelFileSystem).exists("archivoOCarpeta");
  }

}
