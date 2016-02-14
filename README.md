# CloudyGameThinClient
This is an application that the player uses to play the game. The player will receive an input video stream from the cloud servers, and send their keyboard and mouse inputs to the servers via this application.

## Usage
Press ESC to unlock the mouse cursor from the window.

## Prerequisites for using the Python Thin Client 
You need:

1. Python 3
2. VLC Media Player Installed
3. The following dependencies:
    - pygame
    - vlc
    
Important Note: If you use the 32-bit version of Python, then you need the 32-bit version of VLC. If you use the 64-bit version of Python, then you need the 64-bit version of VLC. 

### Windows

### pygame
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#pygame). There are 32-bit and 64-bit files, so make sure you get the correct version.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`. Replace the filename with the correct one.

### VLC
1. Install VLC: [https://www.videolan.org/vlc/index.html](https://www.videolan.org/vlc/index.html). Check whether you are using the 32-bit or 64-bit version of Python, and get the same version for VLC. 
2. Open your terminal at the repository directory. 
3. Type `pip install -r requirements.txt`.


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

### To run on the command line
Include the json jar in the classpath:
```bash
java -cp .:path/to/json/jar/file CloudyLauncher
```

Note: The above is for linux, use ; instead of : for windows.
