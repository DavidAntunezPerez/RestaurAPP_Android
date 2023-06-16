# RestaurAPP Android

##  Selecciona Idioma / Select Language
* [Español](#español)
* [English](#english)

# Español

Versión para dispositivos móviles Android de la aplicación RestaurAPP

<p align="center">
   <img src="restaurapp_android.png">
</p>

Para acceder al repositorio de RestaurAPP conjunto haga click [aqui](https://github.com/DavidAntunezPerez/RestaurAPP)

Para acceder al repositorio principal de IESCampanillas de proyectos haga click [aqui](https://github.com/IESCampanillas/proyectos-dam-2023)

## Índice: 
1. [Descripción y funcionalidades](#descripción-y-funcionalidades)
2. [Diagrama de la base de datos](#diagrama-de-la-base-de-datos)
3. [Gestión del proyecto](#gestión-del-proyecto)
4. [Histórico de cambios](#histórico-de-cambios)
5. [Descarga de la APP](#descarga-de-la-app)
6. [Manual de instalación y dependencias](#manual-de-instalación-y-dependencias)
7. [Manual de uso de la aplicación](#manual-de-uso-de-la-aplicación)
8. [Bibliografía](#bibliografía)

### Descripción y funcionalidades

Aplicación nativa del proyecto RestaurAPP. 
Esta aplicación funciona de forma conjunta con la [aplicación híbrida de RestaurAPP](https://github.com/AleCueto/restauraap) para el [Proyecto fin de ciclo 2º DAM 22/23](https://github.com/IESCampanillas/proyectos-dam-2023). 

La aplicación está desarrollada en su totalidad por [David Antúnez Pérez](https://github.com/DavidAntunezPerez) utilizando principalmente el entorno de desarrollo de [Android Studio](https://developer.android.com/studio) en conjunto al lenguaje [Kotlin](https://kotlinlang.org/) con el uso de [Python](https://www.python.org/) para alguna funcionalidad.

Esta aplicación está desarrollada para llevar el sistema de mesas y comandas por cada camarero en cualquier restaurante. Te permitirá tanto crear, ver, editar o eliminar una serie de comandas (o pedidos) en función a la necesidad del camarero en el momento.

La aplicación permite agregar además una descripción y un título a cada comanda en su creación (o añadirlo posteriormente en la edición de esta). También, permite filtrar resultados para encontrar fácilmente tanto un plato como una comanda mediante una barra de búsqueda situada encima de las listas en la página.

Además, también permite la función de gestionar los ajustes de la aplicación, así como crear un perfil donde agregar Nombre, Descripción, Ubicación o Imagen para tu restaurante. En cuanto a la imagen, se podrá tanto tomar una imagen de la propia cámara en ese momento o subir una de la galería del dispositivo, así como eliminar la imagen de perfil para dejar la que esta predeterminada en la aplicación.

También cuenta con la opción de cambio de idioma entre Inglés y Español, que está presente en el apartado de Ajustes o en las pestañas de Inicio de Sesión y Registro.

Además de las propias opciones de Inicio de Sesión y Registro, que podrán realizarse tanto con correo electrónico y contraseña como con una cuenta de Google.

Otra funcionalidad a mencionar es la de Exportar a CSV, que, mediante el uso de la librería [Chaquopy](https://chaquo.com/chaquopy/) permite ejecutar un script de Python que descargará un archivo .CSV en nuestro dispositivo móvil con la cuenta de una comanda, incluyendo título, descripción, listado de platos pedidos con su precio, precio total a pagar... Los camareros pueden usar esta funcionalidad para mostrar al cliente la cuenta final del pedido y así ahorrar papel y material de impresión. Además de tener guardado la información de cada comanda en el dispositivo móvil de forma sencilla.

### Diagrama de la base de datos

Este es el esquema de la base de datos planteado con [ERDPLus](https://erdplus.com/). Además, de esta imagen, podeis encontrar un archivo para importar el esquema [aqui](https://github.com/DavidAntunezPerez/RestaurAPP_Android/blob/master/erd_diagram/restaurapp_android.erdplus).

<p align="center">
   <img src="./erd_diagram/ERD_Diagram.png">
</p>

### Gestión del proyecto

Para la gestión de este proyecto se ha utilizado la herramienta [Jira Software](https://www.atlassian.com/es/software/jira).

En ella se han dividido las tareas del proyecto en cinco columnas:
 - Main Tasks: Tareas principales a realizar, tareas de realización fundamental.
 - Secondary Tasks: Tareas un poco más simples, que no requieren de tanto tiempo, y de interés secundario.
 - In Progress: Tareas que actualmente se encuentran en proceso de desarrollo.
 - Bugs: Errores conocidos y aun por solucionar.
 - Deprecated/Future Tasks: Tareas que están pensadas para ser agregadas en un futuro o que han sido descartadas por el momento.
 
 En total se han tratado de treinta y dos tareas divididas en estas cuatro columnas.
 
### Histórico de cambios

Para acceder al histórico de cambios de la aplicación haga
click [aqui](https://github.com/DavidAntunezPerez/RestaurAPP_Android/commits)

### Descarga de la APP

Para descargar la APP, puedes hacer click en el apartado RELEASES de GitHub o hacer click [aqui](https://github.com/DavidAntunezPerez/RestaurAPP_Android/releases)

Procura descargar la última versión de la aplicación. Al descargar el release, tendrás acceso a un archivo .apk que podrás utilizar en tu dispositivo Android para instalar la aplicación.

### Manual de instalación y dependencias

Los requisitos para que tengamos instalados la aplicación son los siguientes:

- Disponer de espacio suficiente para poder descargar e instalar la aplicación.
- Un dispositivo Android con una versión de API mínima de 30 y una máxima de 32 (Es decir, desde Android 11: Red Velvet Cake hasta Android 12: Snow Cone) . Se recomienda el uso de la API 30.
- Tener los Servicios de Google actualizados y con un funcionamiento correcto.
- Para la consulta de archivos .CSV, utilizar una aplicación de lectura de estos archivos, como recomendación, podeis usar [Excel de Microsoft](https://play.google.com/store/apps/details?id=com.microsoft.office.excel).

Una vez cumplamos con todos los requisitos, podemos pasar a instalar la aplicación:
- Descargar el [archivo APK de la aplicación](https://github.com/DavidAntunezPerez/RestaurAPP_Android/releases). 
- Haga click en el archivo APK en su dispositivo Android y haga click en Instalar.
- Se instalará RestaurAPP en su dispositivo y ya estará lista para su uso.

### Manual de uso de la aplicación

Para acceder al manual en Español, puedes visitar la Wiki de este repositorio [aqui](https://github.com/DavidAntunezPerez/RestaurAPP_Android/wiki/%5BES%5D-Manual-de-uso-de-RestaurAPP-Android)


### Bibliografía

Para el desarrollo de esta aplicación, se han utilizado diferentes herramientas y estudiado distintas fuentes:

- [Canva](https://www.canva.com/): Como herramienta de diseño de imágenes y el propio logo.
- [Iconos8](https://iconos8.es/): Como herramienta para la descarga y utilización de los iconos del proyecto en diferentes formatos.
- [Figma](https://www.figma.com/): Utilizado para desarrollar el [Anteproyecto](https://github.com/DavidAntunezPerez/restaurapp#anteproyecto).
- [Jira Software](): Utilizado para gestionar el proyecto durante su desarrollo, más información en el [apartado de gestión del proyecto](https://github.com/DavidAntunezPerez/RestaurAPP_Android#gesti%C3%B3n-del-proyecto).
- [Google Firebase](https://firebase.google.com/): Utilizado como servicio externo para consumir datos y como base de datos con Firestore, para almacenar imágenes con Storage, y como herramienta de autenticación de usuarios con Authentication.
- [PowerBI](https://powerbi.microsoft.com/es-es/): Para cumplir con los [requisitos del módulo desarrollo de interfaces](https://github.com/IESCampanillas/proyectos-dam-2023/wiki/Desarrollo-de-Interfaces) y con los [requisitos del módulo de sistemas de gestión empresarial](https://github.com/IESCampanillas/proyectos-dam-2023/wiki/Sistemas-de-Gesti%C3%B3n-Empresarial).
- [Material Design](https://m2.material.io/): Como guía completa para el diseño visual, y como herramienta para el diseño de los layouts.
- [Kotlin](https://kotlinlang.org/): Como lenguaje principal de programación para realizar la aplicación.
- [Python](https://www.python.org/) y [Chaquopy](https://chaquo.com/chaquopy/): Utilizados para la creación y ejecución del script encargado de la generación de archivos CSV. Se han utilizado las librerías de [Numpy](https://numpy.org/) y [Pandas](https://pandas.pydata.org/) para el desarrollo del script.
- [Android Studio](https://developer.android.com/studio):Como entorno de desarrollo integrado para el desarrollo de la aplicación.
- [DaFont](https://www.dafont.com/): Para la descarga de fuentes de texto usadas en la aplicación.
- [Dokka](https://github.com/Kotlin/dokka): Para la generar la [documentación del proyecto](https://github.com/DavidAntunezPerez/RestaurAPP_Android/tree/master/documentation/dokka/htmlMultiModule).
- [GitHub](https://github.com/): Para gestionar el proyecto, controlar versiones de código y almacenar el código del proyecto en la nube.
- [Git](https://git-scm.com/): Usado como  sistema de control de versiones en el proyecto.
- [Glide](https://github.com/bumptech/glide): Para la gestión de los archivos multimedia de la aplicación.
- [ERDPlus](https://erdplus.com/): Para la creación del diagrama ERD de la base de datos.

Para la búsqueda de información que me ha ayudado a realizar el proyecto se han usado estas fuentes:

- [Documentación de Kotlin](https://kotlinlang.org/docs/home.html)
- [Documentación de Android](https://developer.android.com/)
- [Documentación de Python](https://docs.python.org/es/3/)
- [Documentación de Chaquopy](https://chaquo.com/chaquopy/)
- Apuntes tomados en clases
- [Repositorio de Glide](https://github.com/bumptech/glide)
- [Repositorio de Dokka](https://github.com/Kotlin/dokka)
- [Tutoriales de Android y Kotlin de Youtube](https://www.youtube.com/watch?v=vJapzH_46a8&list=PL8ie04dqq7_OcBYDpvHrcSFVoggLi3cm_&ab_channel=Programaci%C3%B3nAndroidbyAristiDevs)
- [ChatGPT](https://chat.openai.com)
- [Documentación de Git](https://git-scm.com/doc)
- [StackOverflow](https://stackoverflow.com/)

# English

Android mobile devices version of RestaurAPP application

<p align="center">
    <img src="restaurapp_android.png">
</p>

To access the joint RestaurAPP repository click [here](https://github.com/DavidAntunezPerez/RestaurAPP)

To access the main IESCampanillas project repository click [here](https://github.com/IESCampanillas/proyectos-dam-2023)

## Index:
1. [Description and functionalities](#description-and-functionalities)
2. [Database Diagram](#database-diagram)
3. [Project Management](#project-management)
4. [History of changes](#history-of-changes)
5. [Download the APP](#download-the-app)
6. [Installation manual and dependencies](#installation-manual-and-dependencies)
7. [Application user manual](#application-user-manual)
8. [Bibliography](#bibliography)

### Description and functionalities

Native application of the RestaurAPP project.
This application works together with the [RestaurAPP hybrid application](https://github.com/AleCueto/restauraap) for the [2nd cycle end project DAM 22/23](https://github.com/IESCampanillas/proyectos-dam-2023).

The application is fully developed by [David Antúnez Pérez](https://github.com/DavidAntunezPerez) using mainly the [Android Studio](https://developer.android.com/studio) development environment together to the [Kotlin](https://kotlinlang.org/) language with the use of [Python](https://www.python.org/) for some functionality.

This application is developed to carry the system of tables and orders for each waiter in any restaurant. It will allow you to create, view, edit or delete a series of commands (or orders) depending on the waiter's need at the time.

The application also allows you to add a description and a title to each command when it is created (or add it later when editing it). Also, it allows you to filter results to easily find both a dish and an order through a search bar located above the lists on the page.

In addition, it also allows the function of managing the application settings, as well as creating a profile where you can add a Name, Description, Location or Image for your restaurant. As for the image, you can both take an image from your own camera at that moment or upload one from the device's gallery, as well as delete the profile image to leave the default one in the application.

It also has the option to change the language between English and Spanish, which is present in the Settings section or in the Login and Registration tabs.

In addition to the Login and Registration options themselves, which can be done both with email and password and with a Google account.

Another functionality to mention is that of Export to CSV, which, by using the [Chaquopy](https://chaquo.com/chaquopy/) library, allows you to execute a Python script that will download a .CSV file to your mobile device with the account of an order, including title, description, list of dishes ordered with their price, total price to pay... Waiters can use this functionality to show the customer the final account of the order and thus save paper and printing material . In addition to having the information of each command saved on the mobile device in a simple way.

### Database Diagram

This is the database schema raised with [ERDPlus](https://erdplus.com/). In addition, from this image, you can find a file to import the schema [here](https://github.com/DavidAntunezPerez/RestaurAPP_Android/blob/master/erd_diagram/restaurapp_android.erdplus).

<p align="center">
    <img src="./erd_diagram/ERD_Diagram.png">
</p>

### Project management

The tool [Jira Software](https://www.atlassian.com/es/software/jira) has been used to manage this project.

In it, the project tasks have been divided into five columns:
  - Main Tasks: Main tasks to be carried out, fundamental tasks.
  - Secondary Tasks: Tasks that are a little simpler, that do not require so much time, and of secondary interest.
  - In Progress: Tasks that are currently in the development process.
  - Bugs: Known errors and still to be solved.
  - Deprecated/Future Tasks: Tasks that are intended to be added in the future or that have been discarded for now.
 
  In total there have been thirty-two tasks divided into these four columns.
 
### History of changes

To access the change history of the application, click [here](https://github.com/DavidAntunezPerez/RestaurAPP_Android/commits)

### Download the APP

To download the APP, you can click on the RELEASES section of GitHub or click [here](https://github.com/DavidAntunezPerez/RestaurAPP_Android/releases)

Try to download the latest version of the application. When you download the release, you'll have access to an .apk file that you can use on your Android device to install the app.

### Installation manual and dependencies

The requirements for us to have the application installed are the following:

- Have enough space to download and install the application.
- An Android device with a minimum API version of 30 and a maximum of 32 (That is, from Android 11: Red Velvet Cake to Android 12: Snow Cone). The use of API 30 is recommended.
- Have Google Services updated and functioning correctly.
- To consult .CSV files, use an application that reads these files, as a recommendation, you can use [Microsoft Excel](https://play.google.com/store/apps/details?id=com.microsoft.office.excel).

Once we meet all the requirements, we can proceed to install the application:
- Download the [application APK file](https://github.com/DavidAntunezPerez/RestaurAPP_Android/releases).
- Click on the APK file on your Android device and click Install.
- RestaurAPP will be installed on your device and it will be ready to use.

### Application user manual

To access the manual in English, you can visit the Wiki of this repository [here](https://github.com/DavidAntunezPerez/RestaurAPP_Android/wiki/%5BEN%5D-RestaurAPP-Android-user-manual)


### Bibliography

For the development of this application, different tools have been used and different sources have been studied:

- [Canva](https://www.canva.com/): As a tool to design images and the logo itself.
- [Iconos8](https://iconos8.es/): As a tool for downloading and using the project's icons in different formats.
- [Figma](https://www.figma.com/): Used to develop the [Blueprint](https://github.com/DavidAntunezPerez/restaurapp#anteproyecto).
- [Jira Software](): Used to manage the project during its development, more information in the [project management section](https://github.com/DavidAntunezPerez/RestaurAPP_Android#project-management).
- [Google Firebase](https://firebase.google.com/): Used as an external service to consume data and as a database with Firestore, to store images with Storage, and as a user authentication tool with Authentication.
- [PowerBI](https://powerbi.microsoft.com/es-es/): To comply with the [interface development module requirements](https://github.com/IESCampanillas/proyectos-dam-2023/wiki/Desarrollo-de-Interfaces) and with the [requirements for the business management systems module](https://github.com/IESCampanillas/proyectos-dam-2023/wiki/Sistemas-de-Gesti%C3%B3n-Empresarial).
- [Material Design](https://m2.material.io/): As a complete guide for visual design, and as a tool for layout design.
- [Kotlin](https://kotlinlang.org/): As the main programming language to make the application.
- [Python](https://www.python.org/) and [Chaquopy](https://chaquo.com/chaquopy/): Used for the creation and execution of the script in charge of generating CSV files. The libraries of [Numpy](https://numpy.org/) and [Pandas](https://pandas.pydata.org/) have been used for the development of the script.
- [Android Studio](https://developer.android.com/studio): As an integrated development environment for application development.
- [DaFont](https://www.dafont.com/): For downloading text fonts used in the application.
- [Dokka](https://github.com/Kotlin/dokka): To generate the [project documentation](https://github.com/DavidAntunezPerez/RestaurAPP_Android/tree/master/documentation/dokka/htmlMultiModule) .
- [GitHub](https://github.com/): To manage the project, control code versions and store the project code in the cloud.
- [Git](https://git-scm.com/): Used as version control system in the project.
- [Glide](https://github.com/bumptech/glide): For managing the application's multimedia files.
- [ERDPlus](https://erdplus.com/): For the creation of the ERD diagram of the database.

For the information search that helped me in completing the project, the following sources were used:

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Android Documentation](https://developer.android.com/)
- [Python Documentation](https://docs.python.org/es/3/)
- [Chaquopy Documentation](https://chaquo.com/chaquopy/)
- Class notes
- [Glide Repository](https://github.com/bumptech/glide)
- [Dokka Repository](https://github.com/Kotlin/dokka)
- [Android and Kotlin Tutorials on Youtube](https://www.youtube.com/watch?v=vJapzH_46a8&list=PL8ie04dqq7_OcBYDpvHrcSFVoggLi3cm_&ab_channel=Programaci%C3%B3nAndroidbyAristiDevs)
- [ChatGPT](https://chat.openai.com)
- [Git Documentation](https://git-scm.com/doc)
- [StackOverflow](https://stackoverflow.com/)
