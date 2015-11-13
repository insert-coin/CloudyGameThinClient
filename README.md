# CloudyGameThinClient
This is an application that the player uses to play the game. The player will receive an input video stream from the cloud servers, and send their keyboard and mouse inputs to the servers via this application.

## Prerequisites for using the C++ Thin Client 
### Windows
The application is developed on Visual Studio 2013. Follow [this tutorial](http://lazyfoo.net/tutorials/SDL/01_hello_SDL/windows/msvsnet2010u/index.php) to set up Visual Studio 2013 to work with SDL. You can download the development library files [here](https://www.libsdl.org/download-2.0.php) (SDL2-devel-2.0.3-VC.zip).

The library files should be placed in the following directories:
- \Library\SDL\include
- \Library\SDL\lib\x86

Also, SDL.dll is needed to run the application. Put it either your project's working directory (where the .vcxproj file is), or inside of the system directory (C:\WINDOWS\SYSTEM32).

### Mac
The tutorial is [here](http://lazyfoo.net/tutorials/SDL/01_hello_SDL/mac/index.php), no idea if it works.
