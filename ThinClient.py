import socket
import struct
import string
import pygame, time
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
    ASCII_TO_UE_KEYCODE[chr(i)] = chr(i)
    
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
32bit UEKeyCode (A, B, , Z, 0, ... ,9, F1, ..., F12, etc.)
32bit UECharCode (A, B, , Z, 0, ... ,9, F1, ..., F12, etc.)
8bit Event (Key Down (2), Key Up (3))
"""

PACKET_FORMAT = "=BBIBIIB"
UDP_IP = "127.0.0.1"
UDP_PORT = 55555
VERSION = 0
CONTROLLER_ID = 0

def packAndSend(deviceType, sequence, UEKeyCode, UECharCode, eventType, socketName):
    data = (VERSION, deviceType, sequence, CONTROLLER_ID, UEKeyCode, UECharCode, eventType)
    message = struct.pack(PACKET_FORMAT, *data)
    print(message)
    socketName.sendto(message, (UDP_IP, UDP_PORT))
    sequence += 1 
    return sequence
    
def initializePygame():
    pygame.init()
    screen = pygame.display.set_mode((640, 480))
    pygame.display.set_caption('Remote Keyboard')
    pygame.mouse.set_visible(True)
    pygame.key.set_repeat(33, 33) # 1 input per frame (assuming 30 FPS)

def main():
    sequence = 0
    print("UDP target IP:", UDP_IP)
    print("UDP target port:", UDP_PORT)

    sock = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP

    initializePygame()
    isRunning = True

    while isRunning:
        event = pygame.event.wait() # program will sleep if there are no events in the queue

        if (event.type == KEYUP or event.type == KEYDOWN):
            deviceType = 0
            print('ASCII Key is:', event.key)
            UEKeyCode = ASCII_TO_UE_KEYCODE.get(event.key, 0)
            UECharCode = ASCII_TO_UE_CHARCODE.get(event.key, UEKeyCode)
            UEKeyCode = UECharCode or UEKeyCode
            print(UEKeyCode, UECharCode)
            sequence = packAndSend(deviceType, sequence, UEKeyCode, UECharCode, event.type, sock)
            print(event.key, '=>', UEKeyCode)

        if (event.type == MOUSEMOTION):
            deviceType = 1
            x, y = pygame.mouse.get_rel()
        if (event.type == MOUSEBUTTONDOWN or event.type == MOUSEBUTTONUP):
            deviceType = 0 # UE4 takes mouse button as key input event
            leftMouseButton, middleMouseButton, rightMouseButton = pygame.mouse.get_pressed()
            if (leftMouseButton == 1):
                UECharCode = 1
            elif (middleMouseButton == 1):
                UECharCode = 4
            elif (rightMouseButton == 1):
                UECharCode = 2
            UEKeyCode = UECharCode
            if (event.type == MOUSEBUTTONDOWN):
                sequence = packAndSend(deviceType, sequence, UEKeyCode, UECharCode, 2, sock)
            elif (event.type == MOUSEBUTTONUP):
                sequence = packAndSend(deviceType, sequence, UEKeyCode, UECharCode, 3, sock)
            print(pygame.mouse.get_pressed(), '=>', UEKeyCode)
        if (event.type == QUIT):
            isRunning = False
    
    pygame.quit()

if __name__ == '__main__':
    main()