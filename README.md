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
    - vlc (provided in the repository. However, VLC needs to be installed)
    
Important Note: If you use the 32-bit version of Python, then you need the 32-bit version of VLC. If you use the 64-bit version of Python, then you need the 64-bit version of VLC. 

### Windows

### pygame
1. Obtain the .whl file for your Python version [here](http://www.lfd.uci.edu/~gohlke/pythonlibs/#pygame). There are 32-bit and 64-bit files, so make sure you get the correct version.
2. Open the terminal, and navigate to where you placed your .whl file. Type `pip install filename.whl`. Replace the filename with the correct one.

### VLC
1. Install VLC: [https://www.videolan.org/vlc/index.html](https://www.videolan.org/vlc/index.html). Check whether you are using the 32-bit or 64-bit version of Python, and get the same version for VLC. 