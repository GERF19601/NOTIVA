# NOTIVA 

**Tus pensamientos merecen un lugar especial.**

NOTIVA es una aplicación nativa para Android desarrollada con Java y XML. Permite registrar usuarios e iniciar sesión localmente, crear y gestionar notas personales, asociarlas con emociones y buscarlas por título o contenido.

## 1\. Funcionalidades

* Registro e inicio de sesión con validación de datos y comprobación de correos duplicados.
* Protección de contraseñas mediante PBKDF2 con sal aleatoria.
* Sesión local persistente y cierre de sesión.
* CRUD completo de notas: crear, consultar, editar y eliminar.
* Asociación de seis emociones a las notas.
* Búsqueda dinámica, sin distinguir mayúsculas y minúsculas, por título y contenido.
* Contador de notas, fechas de creación y confirmación antes de eliminar.
* Separación de notas según el usuario autenticado localmente.
* Interfaz diseñada en XML con animaciones Lottie.

## 2\. Requisitos y versiones

|Elemento|Versión o requisito|
|-|-|
|Sistema operativo del dispositivo|**Android 8.0 (Oreo) o superior**|
|minSdk|**26**|
|compileSdk|**37**|
|targetSdk|**37**|
|Entorno de desarrollo|Android Studio con el SDK indicado por 'compileSdk'|
|Lenguaje|Java|
|Interfaces|XML|
|Gradle Wrapper|**9.6.0** (comprobar en 'gradle/wrapper/gradle-wrapper.properties')|
|Base de datos|Room 2.8.5 sobre SQLite|
|Animaciones|Lottie 6.6.10|

El proyecto declara Android 8.0/API 26 como versión mínima; no se ha verificado su comportamiento en todas las versiones y modelos posteriores. Se requiere conexión a Internet para obtener herramientas y dependencias no almacenadas en la caché del equipo durante la configuración inicial.

## 3\. Abrir el proyecto en Android Studio

1. Instalar **Android Studio** y el **Android SDK** correspondiente a la versión 'compileSdk' del proyecto.
2. Descomprimir el archivo ZIP del código fuente.
3. Abrir Android Studio y seleccionar **Open**.
4. Seleccionar la **carpeta raíz de NOTIVA**, la que contiene 'settings.gradle.kts', 'build.gradle.kts', 'gradlew.bat' y 'app/'.
5. Permitir que **Gradle Sync** termine. El proyecto utiliza **Gradle Wrapper**, por lo que no requiere instalar manualmente Gradle en una ruta local.
6. Si Android Studio solicita instalar componentes del SDK o descargar dependencias, aceptar y esperar a que termine la sincronización.

> El proyecto no debe depender de rutas locales como 'C:\\\\\\\\Gradle' ni de un 'local.properties' perteneciente a otro computador. Android Studio puede generar su propio 'local.properties' con la ruta del SDK del equipo.

## 4\. Compilar

Desde la terminal situada en la raíz de NOTIVA:

**Windows (PowerShell):**

.\\\\gradlew.bat :app:assembleDebug



**macOS / Linux:**

./gradlew :app:assembleDebug



Si la compilación es exitosa, se genera el APK de depuración en:
app/build/outputs/apk/debug/app-debug.apk



Como alternativa, en Android Studio puede utilizarse la opción **Build APK(s)** del menú **Build**.

## 5\. Ejecutar en un emulador

1. Abrir **Device Manager** en Android Studio.
2. Crear o seleccionar un dispositivo virtual con **Android 8.0/API 26 o superior**.
3. Iniciar el emulador.
4. Seleccionar la configuración de ejecución del módulo **app**.
5. Presionar **Run** (▶).
6. Cuando NOTIVA se abra, crear un usuario y acceder con sus credenciales.

## 6\. Ejecutar en un teléfono Android

### Opción A: instalar un APK

1. Generar 'app-debug.apk' siguiendo el apartado **Compilar**, o utilizar un APK de demostración proporcionado junto con la entrega.
2. Transferir el archivo al teléfono (por ejemplo, por USB).
3. Abrir el archivo desde el gestor de archivos del teléfono.
4. Autorizar la instalación desde esa fuente si Android lo solicita.
5. Instalar y abrir **NOTIVA**.

### Opción B: ejecutar desde Android Studio

1. Activar las **Opciones de desarrollador** y la **Depuración USB** en el teléfono.
2. Conectar el dispositivo por USB y autorizar la depuración cuando se solicite.
3. Seleccionar el teléfono como dispositivo de ejecución en Android Studio.
4. Presionar **Run** (▶).

El archivo 'app-debug.apk' es una compilación de desarrollo para pruebas y demostración; no es una versión de lanzamiento destinada a una tienda de aplicaciones.

## 7\. Información adicional

* Los usuarios y las notas se almacenan localmente en el dispositivo. No se sincronizan entre el emulador y el teléfono ni entre dispositivos diferentes.
* La aplicación utiliza autenticación local; no requiere un servidor para registrar usuarios, iniciar sesión o gestionar notas.
* Las contraseñas se protegen mediante PBKDF2 con sal aleatoria; el APK de depuración es solo para pruebas.
* El diseño de la arquitectura, el diagrama de datos, las decisiones de persistencia, las validaciones y los resultados de las pruebas se describen en el **documento técnico** entregado por separado.

