#include <SDL.h> 
#include <iostream>
#include <string>

#include "Net.h"
#include "Serializer.h"
#include "Deserializer.h"
#include "Datagrams.h"

using namespace std;
using namespace net;

//Starts up SDL and creates window
bool initSDL();

//Loads media
bool loadMedia();

//Frees media and shuts down SDL
void closeSDL();

//Screen dimension constants 
const int SCREEN_WIDTH = 640; 
const int SCREEN_HEIGHT = 480;

// The window we'll be rendering to 
SDL_Window* gWindow = NULL; 
// The surface contained by the window 
SDL_Surface* gScreenSurface = NULL; 
// The image we will load and show on the screen 
SDL_Surface* gHelloWorld = NULL;
// Declare display mode structure to be filled in.
SDL_DisplayMode current;

bool initSDL()
{
    // Initialization flag
    bool success = true;

    // Initialize SDL
    if (SDL_Init(SDL_INIT_VIDEO) < 0)
    {
        printf("SDL could not initialize! SDL_Error: %s\n", SDL_GetError());
        success = false;
    }
    else
    {
		int hasError = SDL_GetCurrentDisplayMode(0, &current); // 0 = first monitor

		if (hasError)
		{
			// In case of error...
			SDL_Log("Could not get display mode for video display #%d: %s", 0, SDL_GetError());
		}
		else
		{
			// On success, print the current display mode.
			SDL_Log("Display #%d: current display mode is %dx%dpx @ %dhz. \n", 0, current.w, current.h, current.refresh_rate);
		}

		// Create window
        gWindow = SDL_CreateWindow("SDL Tutorial", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, SCREEN_WIDTH, SCREEN_HEIGHT, SDL_WINDOW_SHOWN);
		//gWindow = SDL_CreateWindow("SDL Tutorial", SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, current.w, current.h, SDL_WINDOW_SHOWN);
        if (gWindow == NULL)
        {
            printf("Window could not be created! SDL_Error: %s\n", SDL_GetError());
            success = false;
        }
        else
        {
			// Set window fullscreen mode
			// Flags: SDL_WINDOW_FULLSCREEN, SDL_WINDOW_FULLSCREEN_DESKTOP, 0 (windowed mode)
			SDL_SetWindowFullscreen(gWindow, 0);
			// Get window surface (becomes invalid if window is resized, so do this after fullscreen)
			gScreenSurface = SDL_GetWindowSurface(gWindow);
        }
    }

    return success;
}

bool loadMedia()
{
    //Loading success flag
    bool success = true;

    //Load splash image
    gHelloWorld = SDL_LoadBMP("images/hello_world.bmp");
    if (gHelloWorld == NULL)
    {
        printf("Unable to load image %s! SDL Error: %s\n", "images/hello_world.bmp", SDL_GetError());
        SDL_Delay(2000);
        success = false;
    }

    return success;
}

void closeSDL()
{
    //Deallocate surface
    SDL_FreeSurface(gHelloWorld);
    gHelloWorld = NULL;

    //Destroy window
    SDL_DestroyWindow(gWindow);
    gWindow = NULL;

    //Quit SDL subsystems
    SDL_Quit();
}

int main(int argc, char* args[]) {
	const int LocalPort = 30000;
	const int PacketSize = 32;
	string playerControllerID;

	Address TargetEngineAddress = Address(127, 0, 0, 1, 30000); // 127.0.0.1:55555
	Address TargetCloudyServerAddress = Address(127, 0, 0, 1, 30000); // 127.0.0.1:55555

	// =======================
	// Initialize sockets
	// =======================
	if (!InitializeSockets())
	{
		printf("failed to initialize sockets\n");
		wait(2.0f);
		return 1;
	}

	Socket socket;
	printf("creating socket on port %d\n", LocalPort);
	if (!socket.Open(LocalPort))
	{
		printf("failed to create socket!\n");
		wait(2.0f);
		return 1;
	}

	// =====================
    // Start up SDL 
	// =====================
    if (!initSDL())
    {
        printf("Failed to initialize!\n");
        SDL_Delay(2000);
		return 1;
    }
    // Load media
    /*if (!loadMedia())
    {
        printf("Failed to load media!\n");
        SDL_Delay(2000);
		return 1;
    }*/

	// ===============================
	// Request player controller ID
	// ===============================
	// Data for socket
	//string data = "Gief ID plox";
	//data.append("");

	// Make packet to send
	//char packetData[PacketSize];
	//strncpy_s(packetData, data.c_str(), sizeof(packetData));
	//packetData[sizeof(packetData) - 1] = '\0';

	struct PacketKeyboard *s = new PacketKeyboard();
	s->keycode = 112;
	s->keyEvent = 6;
	unsigned char sendBuffer[3];

	serialize_PacketKeyboard(sendBuffer, s);

	socket.Send(TargetCloudyServerAddress, sendBuffer, sizeof(sendBuffer));

	// Receive data from socket
	while (true)
	{
		Address sender;
		unsigned char receiveBuffer[3];

		int bytes_read = socket.Receive(sender, receiveBuffer, sizeof(receiveBuffer));
		if (!bytes_read)
		{
			// Should not do this once the packet is really sent.
			// Should wait until the ID has been received, or throw an "ID not received" error.
			break; 
		}

		printf("received packet from %d.%d.%d.%d:%d (%d bytes)\n",
			sender.GetA(), sender.GetB(), sender.GetC(), sender.GetD(),
			sender.GetPort(), bytes_read);
		printf("Player Controller is: ");
		PacketKeyboard outputPacketKeyboard = deserialize_PacketKeyboard(receiveBuffer);
		printf("%d, %d\n", outputPacketKeyboard.keycode, outputPacketKeyboard.keyEvent);
		/*
		for (int i = 0; i < sizeof(receiveBuffer); i++)
		{
			// terminating character
			if (receiveBuffer[i] == '\0')
			{
				break;
			}
			printf("%c", receiveBuffer[i]);
			if (receiveBuffer[i] == '0')
			{
				playerControllerID = "0";
			}
			else if (receiveBuffer[i] == '1')
			{
				playerControllerID = "1";
			}
			else if (receiveBuffer[i] == '2')
			{
				playerControllerID = "2";
			}
			else if (receiveBuffer[i] == '3')
			{
				playerControllerID = "3";
			}
		}*/
		printf("\n");
	}

	// ========================
	// Main SDL + Event loop 
	// ========================
    // Main loop flag
    bool quit = false;

    // Event handler
    SDL_Event e;

    // While application is running
    while (!quit)
    {
        // Handle events on queue
        while (SDL_PollEvent(&e) != 0)
        {
	        // User requests quit (press 'x', alt-f4, etc.)
            if (e.type == SDL_QUIT || e.key.keysym.sym == 27)
            {
                quit = true;
            }
            if (e.type == SDL_KEYDOWN)
            {
				// 0 if key is not pressed and hold, 1 if it is press and hold
				int IsRepeat = e.key.repeat;
				// Keypress Down or Keypress Up. 768 = down, 769 = up
				int KeypressType = e.key.type;
				// ACSII Decimal Key Values
				int KeycodeValue = e.key.keysym.sym;
						
				// Data for socket
				string data = "";
				data.append(to_string(KeycodeValue));

				// Make packet to send
				char packetData[PacketSize];
				strncpy_s(packetData, data.c_str(), sizeof(packetData));
				packetData[sizeof(packetData) - 1] = '\0';

				socket.Send(TargetEngineAddress, packetData, sizeof(packetData));
            }
			if (e.type == SDL_KEYUP)
			{
				// 0 if key is not pressed and hold, 1 if it is press and hold
				int IsRepeat = e.key.repeat;
				// Keypress Down or Keypress Up. 768 = down, 769 = up
				int KeypressType = e.key.type;
				// ACSII Decimal Key Values
				int KeycodeValue = e.key.keysym.sym;

				// Data for socket
				string data = "";
				data.append(to_string(KeycodeValue));

				// Make packet to send
				char packetData[PacketSize];
				strncpy_s(packetData, data.c_str(), sizeof(packetData));
				packetData[sizeof(packetData) - 1] = '\0';

				//socket.Send(TargetEngineAddress, packetData, sizeof(packetData));
			}
            if (e.type == SDL_MOUSEMOTION)
            {
                int DeltaX = e.motion.xrel;
                int DeltaY = e.motion.yrel;

				// Data for socket
				string data = "";
				data.append(to_string(DeltaX));
				data.append(" ");
				data.append(to_string(DeltaY));

				// Make packet to send
				char packetData[PacketSize];
				strncpy_s(packetData, data.c_str(), sizeof(packetData));
				packetData[sizeof(packetData) - 1] = '\0';

				//socket.Send(TargetEngineAddress, packetData, sizeof(packetData));
            }
            if (e.type == SDL_MOUSEBUTTONDOWN)
            {
                if (e.button.state == SDL_PRESSED)
                {
					// 1 = left, 2 = middle, 3 = right
					int MouseButtonValue = e.button.button;
					// Single or double click
					int NumOfClicks = e.button.clicks; 
							
					// Data for socket
					string data = "";
					data.append(to_string(MouseButtonValue));

					// Make packet to send
					char packetData[PacketSize];
					strncpy_s(packetData, data.c_str(), sizeof(packetData));
					packetData[sizeof(packetData) - 1] = '\0';

					//socket.Send(TargetEngineAddress, packetData, sizeof(packetData));
                }
            }
        }
	
		// Receive data from socket
		while (true)
		{
			Address sender;
			unsigned char buffer[PacketSize];

			int bytes_read = socket.Receive(sender, buffer, sizeof(buffer));
			if (!bytes_read)
			{
				// Should not do this once the packet is really sent.
				// Should wait until the ID has been received, or throw an "ID not received" error.
				break;
			}

			printf("received packet from %d.%d.%d.%d:%d (%d bytes)\n",
				sender.GetA(), sender.GetB(), sender.GetC(), sender.GetD(),
				sender.GetPort(), bytes_read);
			printf("Player Controller is: ");

			for (int i = 0; i < sizeof(buffer); i++){
				// terminating character
				if (buffer[i] == '\0')
				{
					break;
				}
				printf("%c", buffer[i]);
			}
			printf("\n");
		}

        // Apply the image
        SDL_BlitSurface(gHelloWorld, NULL, gScreenSurface, NULL);

        // Update the surface
        SDL_UpdateWindowSurface(gWindow);
    }

    // Free resources
    closeSDL(); // SDL
	socket.Close();
	ShutdownSockets();

    return 0;
}