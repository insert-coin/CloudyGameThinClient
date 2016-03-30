[![Build Status](https://travis-ci.org/insert-coin/CloudyGameThinClient.svg?branch=master)](https://travis-ci.org/insert-coin/CloudyGameThinClient)

# CloudyGameThinClient
This is an application that the player uses to play the game. The player will receive an input video stream from the cloud servers, and send their keyboard and mouse inputs to the servers via this application.

## Usage
To run the thin client itself, you will need to pass in 4 arguments: the IP from which to receive the video stream from, the port number, the player controller ID, and the game session ID.

Example: 
`python main.py 127.0.0.1 30000 0 0`

Press ESC to unlock the mouse cursor from the window.

## Prerequisites for using the Python Thin Client 
You need:

1. Python 3
2. The following dependencies:
    - pygame
    - numpy
    - OpenCV

### Windows

### pygame
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#pygame). There are 32-bit and 64-bit files, so make sure you get the correct version.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`. Replace the filename with the correct one.

### numpy
1. Open your terminal at the root of the repository directory. 
2. Type `pip install -r requirements.txt`.

### OpenCV
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#opencv). There are 32-bit and 64-bit files, so make sure you get the correct version.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`. Replace the filename with the correct one.


# CloudyLauncher
This is an application that allows users to access and manage games available to them.

## Prerequisites for compiling
You need:

1. JavaFX 2.0 or newer
2. Java SE 6.0 or newer 
3. JSON dependency
    - can be obtained from [here](http://mvnrepository.com/artifact/org.json/json)
    - version used in development: 20151123

### To compile on the command line
Include the json jar in the classpath:
```bash
javac -cp path/to/json/jar/file CloudyLauncher.java Game.java
```
Example: Assuming you have placed the .jar file together with your java files, type:
```bash
javac -cp json-20151123.jar CloudyLauncher.java Game.java
```

### To run on the command line
Include the json jar in the classpath:
```bash
Linux/Mac:
java -cp .:path/to/json/jar/file CloudyLauncher

Windows:
java -cp .;path/to/json/jar/file CloudyLauncher
```
Example usage for Windows:
```bash
java -cp .;json-20151123.jar CloudyLauncher
```
