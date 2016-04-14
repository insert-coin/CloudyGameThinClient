[![Build Status](https://travis-ci.org/insert-coin/CloudyGameThinClient.svg?branch=master)](https://travis-ci.org/insert-coin/CloudyGameThinClient)

# Thin Client
This is an application that the player uses to play the game. The player will read a broadcasted video stream from the game engine, and send the player's keyboard and mouse inputs to the game engine via this application.

## Prerequisites for using the Thin Client 
You need:

1. Python 3.5
2. pygame 1.9.2
3. numpy 1.8
4. OpenCV 3.1.0

Ensure that the above dependencies are obtained for Python 3. We use the above mentioned versions during development. Other versions may work, but not guaranteed.

## Obtaining and installing dependencies (for Windows)

### Python 3.5
1. We recommend downloading the Anaconda distribution for Python 3. It is a prepackaged version of Python that sets up all required system environment variables for you, and no configuration is needed (it is a simple `.exe` installer).
2. You can download Anaconda [here](https://www.continuum.io/downloads). Ensure that you download Python 3.X.

### pygame
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#pygame). There are 32-bit and 64-bit files, so make sure you get the version that matches your installation of Python.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`, replacing the filename with the correct one.

### numpy
1. Open your terminal at the root of the repository directory. 
2. Type `pip install -r requirements.txt`.

### OpenCV
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#opencv). There are 32-bit and 64-bit files, so make sure you get the version that matches your installation of Python.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`, replacing the filename with the correct one.

## Launching the client
To run the thin client itself, you will need to pass in 3 arguments: the IP from which to receive the video stream from, the port number, and the player controller ID. The fourth argument (game session ID) is optional, and will default to 1 if not given.

Example: 
```
Using all 4 arguments:
python main.py 127.0.0.1 30000 0 --session 1

Using only 3 arguments:
python main.py 127.0.0.1 30000 0
```
However, if you are an end user, you do not need to launch the thin client manually on your own. The thin client should be launched automatically by Cloudy Launcher when you want to play a game.

## Using The Thin Client
Press ESC to unlock the mouse cursor from the window.

## Running tests
To run the tests, navigate to the root folder of the repository (`..\CloudyGameThinClient\`). 

Then, type the following:
`python -m unittest discover`

# Cloudy Launcher
This is an application that allows users to access and manage games available to them.

## Prerequisites for compiling
You need:

1. JavaFX 2.0 or newer
    - Already included in the Java Development Kit (JDK)
2. Java SE 8.0 or newer
    - Download the Java Development Kit (JDK) [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).
    - Accept the License Agreement, then download the correct version for your computer.
    - You will need to add the Java `bin` directory into the system environment variables. 
        - If you downloaded the 64-bit version, the `bin` directory should be found at `C:\Program Files\Java\jdk1.8.0_73\bin`. Your JDK version may be different.
        - If you downloaded the 32-bit version, the `bin` directory should be found at `C:\Program Files (x86)\Java\jdk1.8.0_73\bin`. Your JDK version may be different.
        - If you need help on how to add a system environment variable for Windows, please read this guide [here](http://www.howtogeek.com/118594/how-to-edit-your-system-path-for-easy-command-line-access/).
3. JSON dependency
    - Can be obtained from [here](http://mvnrepository.com/artifact/org.json/json)
    - Version used in development: 20151123
    - Place it in the same directory as the `.java` files
4. SimpleCaptcha dependency
    - Can be obtained from [here](http://simplecaptcha.sourceforge.net)
    - Download SimpleCaptcha for Java 6
    - Place it in the same directory as the `.java` files

### To compile on the command line
Include the JSON `.jar` file and the SimpleCaptcha `.jar` file in the classpath
(separate the `.jar` files using `:` for Linux/Mac and `;` for Windows):
```bash
Linux/Mac:
javac -cp path/to/json/jar/file:path/to/captcha/jar/file *.java

Windows:
javac -cp path/to/json/jar/file;path/to/captcha/jar/file *.java
```
- Example command for Windows: 

    Assuming that you have placed the `.jar` files together with your `.java` files, type:
    ```bash
    javac -cp json-20151123.jar;simplecaptcha-1.2.1.jar *.java
    ```
- Example command for Mac/Linux:
    
    ```
    javac -cp \* *.java
    ```

### To run on the command line
Include the JSON `.jar` and the SimpleCaptcha `.jar` in the classpath:
```bash
Linux/Mac:
java -cp .:path/to/json/jar/file:path/to/captcha/jar/file CloudyLauncher

Windows:
java -cp .;path/to/json/jar/file;path/to/captcha/jar/file CloudyLauncher
```
- Example command for Mac/Linux:
    ```bash
    java -cp .:json-20151123.jar:simplecaptcha-1.2.1.jar CloudyLauncher
    ```
    or simply type `java -cp .:\* CloudyLauncher` (only for Mac/Linux)

- Example command for Windows:
    ```bash
    java -cp .;json-20151123.jar;simplecaptcha-1.2.1.jar CloudyLauncher
    ```
