![Build](https://github.com/cesartomasG/sistema-de-archivos-2025/actions/workflows/maven.yml/badge.svg)

# Sistema de Archivos - Java

Trabajo práctico realizado para la materia **Diseño de Sistemas** en **UTN FRBA**, 2025.

## Descripción

Este proyecto implementa una interfaz de alto nivel (API) para un sistema de archivos, que actúa como adaptador/fachada sobre una interfaz de bajo nivel provista. El objetivo es facilitar su uso por parte de desarrolladores, ocultando detalles técnicos complejos mediante una abstracción orientada a objetos.

Además, se incluyen ejemplos de uso representados como tests, y se aplican buenas prácticas de diseño como robustez y claridad en la API. El trabajo también incorpora pruebas con **Mockito** para simular el comportamiento del sistema subyacente.

## Tecnologías

- Java 17
- Maven
- JUnit 5
- Mockito

## Estructura

- `src/main/java`: código fuente del adaptador del sistema de archivos
- `src/test/java`: tests unitarios y de integración con Mockito
- `pom.xml`: configuración de Maven y dependencias

## Ejecución

### Ejecutar los tests

```bash
mvn test
