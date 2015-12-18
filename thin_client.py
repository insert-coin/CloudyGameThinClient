import socket
import struct
import string
import pygame
import vlc
import sys
from pygame.locals import * 

ASCII_TO_UE_KEYCODE = {
   32: 32, # Space
   39: 39, # Quote
   44: 44, # Comma
   45: 45, # Hyphen
   46: 46, # Period
   47: 47, # Forward Slash
   59: 59, # Semicolon
   61: 61, # Equals
   91: 91, # Left square bracket
   92: 92, # Backslash
   93: 93, # Right square bracket
}

# Maps from ASCII 97-122 to UE KeyCode 65-90
for i in string.ascii_lowercase:
    ASCII_TO_UE_KEYCODE[ord(i)] = ord(i) - 32
    
# Maps from ASCII 48-57 to UE KeyCode 48-57
# Keyboard number keys 0-9
for i in range(48, 58): 
    ASCII_TO_UE_KEYCODE[i] = i
    
ASCII_TO_UE_CHARCODE = {
   8: 8, # Backspace
   9: 9, # Tab
   13: 13, # Enter
   19: 19, # Pause Break
   27: 27, # Escape
   32: 32, # Space
   127: 46, # Delete
   256: 96, # NumPad 0
   257: 97, # NumPad 1
   258: 98, # NumPad 2
   259: 99, # NumPad 3
   260: 100, # NumPad 4
   261: 101, # NumPad 5 
   262: 102, # NumPad 6 
   263: 103, # NumPad 7
   264: 104, # NumPad 8
   265: 105, # NumPad 9
   266: 110, # NumPad Decimal
   267: 111, # NumPad Divide
   268: 106, # NumPad Multiply
   269: 109, # NumPad Subtract
   270: 107, # NumPad Add
   271: 13, # NumPad Enter
   273: 38, # Arrow Up
   274: 40, # Arrow Down
   275: 39, # Arrow Right
   276: 37, # Arrow Left
   277: 45, # Insert
   278: 35, # Home
   279: 36, # End
   280: 33, # Page Up
   281: 34, # Page Down
   282: 112, # F1
   283: 113, # F2
   284: 114, # F3
   285: 115, # F4
   286: 116, # F5
   287: 117, # F6
   288: 118, # F7
   289: 119, # F8
   290: 120, # F9
   291: 121, # F10
   292: 122, # F11
   293: 123, # F12
   300: 144, # Num Lock
   301: 20, # Caps Lock
   302: 145, # Scroll Lock
   303: 161, # Right Shift
   304: 160, # Left Shift
   305: 163, # Right Ctrl
   306: 162, # Left Ctrl
   307: 165, # Right Alt
   308: 164, # Left Alt
   311: 91 # WinKey/Left Command
}

"""
8bit Version (Currently use 0)
8bit Device Type : (Keyboard (0), Mouse (1), Gamepad, etc.)
32bit Sequence (counter for event)
8bit ControllerID (start from 0)
32bit UEKeyCode (A, B, , Z, 0, ... ,9, punctuation, etc.)
32bit UECharCode (F1, ..., F12, Ctrl, Alt, Numpad, etc.)
8bit Event (Key Down (2), Key Up (3))
"""

PACKET_FORMAT = "=BBIBIIB"
UDP_IP = "127.0.0.1"
UDP_PORT = 55555
VERSION = 0
RESO_WIDTH = 640
RESO_HEIGHT = 480
DEVICE_KEYBOARD = 0
DEVICE_MOUSE = 1

def packAndSend(deviceType, sequence, controllerID, UEKeyCode, UECharCode, eventType, socketName):
    data = (VERSION, deviceType, sequence, controllerID, UEKeyCode, UECharCode, eventType)
    message = struct.pack(PACKET_FORMAT, *data)
    print(message)
    socketName.sendto(message, (UDP_IP, UDP_PORT))
    sequence += 1 
    return sequence
    
def initializePygame(FPS):
    pygame.init()
    screen = pygame.display.set_mode((RESO_WIDTH, RESO_HEIGHT))
    pygame.display.set_caption('Remote Keyboard')
    pygame.mouse.set_visible(True)
    # pygame.event.set_grab(True) # confines the mouse cursor to the window
    frameInterval = int((1/FPS)*1000)
    pygame.key.set_repeat(frameInterval, frameInterval) # 1 input per frame
    
# Taken from https://gist.github.com/smathot/1521059 with modifications
def initializeStream():
    # Tested formats: rtmp, rtsp, http
    # http://128.104.128.176:8080/udp/239.1.1.78:3078?live786
    # rtmp://wowza-bnr.cdp.triple-it.nl/bnr/BNRstudio1 
    # rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov

    movie = 'http://128.104.128.176:8080/udp/239.1.1.78:3078?live786'
    
    # Create instane of VLC and create reference to movie.
    vlcInstance = vlc.Instance()
    media = vlcInstance.media_new(movie)
    media.get_mrl()
    
    # Create new instance of vlc player
    player = vlcInstance.media_player_new()
        
    # Pass pygame window id to vlc player, so it can render its contents there.
    win_id = pygame.display.get_wm_info()['window']
    if sys.platform == "linux2": # for Linux using the X Server
        player.set_xwindow(win_id)
    elif sys.platform == "win32": # for Windows
        player.set_hwnd(win_id)
    elif sys.platform == "darwin": # for MacOS
        player.set_agl(win_id)
    
    # Load movie into vlc player instance
    player.set_media(media)
    
    # Disable VLC event handling so pygame can handle them
    player.video_set_mouse_input(False)
    player.video_set_key_input(False)
    
    # Quit pygame mixer to allow vlc full access to audio device (REINIT AFTER MOVIE PLAYBACK IS FINISHED!)
    pygame.mixer.quit()
    
    # Start movie playback
    player.play()
    
def startClient(playerControllerID):
    sequence = 0
    print("UDP target IP:", UDP_IP)
    print("UDP target port:", UDP_PORT)

    sock = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP

    initializePygame(30) #FPS
    initializeStream()
    isRunning = True

    while isRunning:
        event = pygame.event.wait() # program will sleep if there are no events in the queue

        if (event.type == KEYDOWN or event.type == KEYUP):
            deviceType = DEVICE_KEYBOARD
            print('ASCII Key is:', event.key)
            UEKeyCode = ASCII_TO_UE_KEYCODE.get(event.key, 0)
            UECharCode = ASCII_TO_UE_CHARCODE.get(event.key, UEKeyCode)
            UEKeyCode = UECharCode or UEKeyCode # This code is redundant. It changes nothing.
            print(UEKeyCode, UECharCode)
            sequence = packAndSend(deviceType, sequence, playerControllerID, UEKeyCode, UECharCode, event.type, sock)
            print(event.key, '=>', UEKeyCode)

        if (event.type == pygame.MOUSEMOTION):
            deviceType = DEVICE_MOUSE
            x, y = pygame.mouse.get_rel()
        if (event.type == MOUSEBUTTONDOWN or event.type == MOUSEBUTTONUP):
            deviceType = DEVICE_KEYBOARD # UE4 takes mouse button as key input event
            leftMouseButton, middleMouseButton, rightMouseButton = pygame.mouse.get_pressed()
            if (leftMouseButton == 1):
                UECharCode = 1
            elif (middleMouseButton == 1):
                UECharCode = 4
            elif (rightMouseButton == 1):
                UECharCode = 2
            else:
                UECharCode = 0
            UEKeyCode = UECharCode
            if (event.type == MOUSEBUTTONDOWN):
                sequence = packAndSend(deviceType, sequence, playerControllerID, UEKeyCode, UECharCode, 2, sock)
            elif (event.type == MOUSEBUTTONUP):
                sequence = packAndSend(deviceType, sequence, playerControllerID, UEKeyCode, UECharCode, 3, sock)
            print(pygame.mouse.get_pressed(), '=>', UEKeyCode)
        if (event.type == QUIT):
            isRunning = False

    pygame.quit()

def main():
    startClient(0)
    
if __name__ == '__main__':
    main()