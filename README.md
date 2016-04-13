[![Build Status](https://travis-ci.org/insert-coin/CloudyGameThinClient.svg?branch=master)](https://travis-ci.org/insert-coin/CloudyGameThinClient)

# CloudyGameThinClient
This is an application that the player uses to play the game. The player will read a broadcasted video stream from the game engine, and send the player's keyboard and mouse inputs to the game engine via this application.

## Launching the client
To run the thin client itself, you will need to pass in 3 arguments: the IP from which to receive the video stream from, the port number, and the player controller ID. The fourth argument (game session ID) is optional, and will default to 1 if not given.

Example: 
```
Using all 4 arguments:
python main.py 127.0.0.1 30000 0 --session 1

Using only 3 arguments:
python main.py 127.0.0.1 30000 0
```

## Usage
Press ESC to unlock the mouse cursor from the window.

## Prerequisites for using the Thin Client 
You need:

1. Python 3.5
2. pygame 1.9.2
3. numpy 1.8
4. OpenCV 3.1.0

Ensure that the above dependencies are obtained for Python 3. We use the above mentioned versions during development. Other versions may work, but not guaranteed.

## Obtaining and installing dependencies (for Windows)

### pygame
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#pygame). There are 32-bit and 64-bit files, so make sure you get the version that matches your installation of Python.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`, replacing the filename with the correct one.

### numpy
1. Open your terminal at the root of the repository directory. 
2. Type `pip install -r requirements.txt`.

### OpenCV
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#opencv). There are 32-bit and 64-bit files, so make sure you get the version that matches your installation of Python.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`, replacing the filename with the correct one.

## Running tests
To run the tests, navigate to the root folder of the repository (`..\CloudyGameThinClient\`). 

Then, type the following:
`python -m unittest discover`

# CloudyLauncher
This is an application that allows users to access and manage games available to them.

## Prerequisites for compiling
You need:

1. JavaFX 2.0 or newer
2. Java SE 8.0 or newer
3. JSON dependency
    - can be obtained from [here](http://mvnrepository.com/artifact/org.json/json)
    - version used in development: 20151123
4. SimpleCaptcha dependency
    - can be obtained from [here](http://simplecaptcha.sourceforge.net)

### To compile on the command line
Include the json jar and the simple captcha jar in the classpath
(separate the jar files using : for linux/mac and ; for windows)
```bash
Linux/Mac:
javac -cp path/to/json/jar/file:path/to/captcha/jar/file CloudyLauncher.java Game.java CloudyLauncherJsonParser.java CloudyLauncherServerInterface.java

Windows:
javac -cp path/to/json/jar/file;path/to/captcha/jar/file CloudyLauncher.java Game.java CloudyLauncherJsonParser.java CloudyLauncherServerInterface.java
```
Example usage for Windows: Assuming you have placed the .jar file together with your java files, type:
```bash
javac -cp json-20151123.jar;simplecaptcha-1.2.1.jar CloudyLauncher.java Game.java CloudyLauncherJsonParser.java CloudyLauncherServerInterface.java
```
or simply type `javac -cp \* *.java` to include all jar and java files in the current folder

### To run on the command line
Include the json jar and the simple captcha jar in the classpath:
```bash
Linux/Mac:
java -cp .:path/to/json/jar/file:path/to/captcha/jar/file CloudyLauncher

Windows:
java -cp .;path/to/json/jar/file;path/to/captcha/jar/file CloudyLauncher
```
Example usage for Linux/Mac:
```bash
java -cp .:json-20151123.jar:simplecaptcha-1.2.1.jar CloudyLauncher
```
or simply type `java -cp .:\* CloudyLauncher`
