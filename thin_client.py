import socket
import struct
import string
import pygame
import cv2
import numpy
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
    frameInterval = int((1/FPS)*1000)
    pygame.key.set_repeat(frameInterval, frameInterval) # 1 input per frame (assuming 30 FPS)
    return screen 
    
# Reads the capture object and transforms it into a pygame readable image
def getStreamFrame(captureObject, scale):
    retval, frame = captureObject.read()
    frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    frame = numpy.flipud(numpy.rot90(frame))
    frame = cv2.resize(frame, (0, 0), fx=scale, fy=scale)  # Makes the image smaller so you can see everything in imshow
    frame = pygame.surfarray.make_surface(frame)
    return frame    

# Scale up or down the received stream to fit the window
def getScaleFactor(captureObject):
    frameWidth = int(captureObject.get(3))
    frameHeight = int(captureObject.get(4))
    widthScale = RESO_WIDTH / frameWidth
    heightScale = RESO_HEIGHT / frameHeight
    if (widthScale < heightScale):
        scale = widthScale
        widthIsSmaller = True
    else:
        scale = heightScale
        widthIsSmaller = False 
        
    return scale, widthIsSmaller

# Offset to center the image in the window
def getOffset(scale, widthIsSmaller, frameWidth, frameHeight):
    if (widthIsSmaller):
        frameHeight = frameHeight * scale
        offset = (RESO_HEIGHT - frameHeight) / 2
    else:
        frameWidth = frameWidth * scale
        offset = (RESO_WIDTH - frameWidth) / 2
    
    return offset

def initializeStream():
    # HTTP doesn't work
    captureObject = cv2.VideoCapture('rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov')
    frameWidth = int(captureObject.get(3))
    frameHeight = int(captureObject.get(4))
    FPS = int(captureObject.get(5))
    frameCount = int(captureObject.get(7))
    
    print("Frame width =", frameWidth)
    print("Frame height =", frameHeight)
    print("FPS =", FPS)
    print("Frame count =", frameCount) 
    return (captureObject, frameWidth, frameHeight, FPS)
    
def startClient(playerControllerID):
    sequence = 0
    print("UDP target IP:", UDP_IP)
    print("UDP target port:", UDP_PORT)

    sock = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP

    stream, frameWidth, frameHeight, FPS = initializeStream()
    screen = initializePygame(FPS)
    scale, widthIsSmaller = getScaleFactor(stream)
    offset = getOffset(scale, widthIsSmaller, frameWidth, frameHeight)
    isRunning = True

    while isRunning:
        imgFrame = getStreamFrame(stream, scale)

        #event = pygame.event.wait() # program will sleep if there are no events in the queue
        event = pygame.event.poll() 

        if (event.type == KEYDOWN or event.type == KEYUP):
            deviceType = 0
            print('ASCII Key is:', event.key)
            UEKeyCode = ASCII_TO_UE_KEYCODE.get(event.key, 0)
            UECharCode = ASCII_TO_UE_CHARCODE.get(event.key, UEKeyCode)
            UEKeyCode = UECharCode or UEKeyCode # This code is redundant. It changes nothing.
            print(UEKeyCode, UECharCode)
            sequence = packAndSend(deviceType, sequence, playerControllerID, UEKeyCode, UECharCode, event.type, sock)
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
                sequence = packAndSend(deviceType, sequence, playerControllerID, UEKeyCode, UECharCode, 2, sock)
            elif (event.type == MOUSEBUTTONUP):
                sequence = packAndSend(deviceType, sequence, playerControllerID, UEKeyCode, UECharCode, 3, sock)
            print(pygame.mouse.get_pressed(), '=>', UEKeyCode)
        if (event.type == QUIT):
            isRunning = False
        
        if (widthIsSmaller):
            screen.blit(imgFrame, (0, offset))
        else:
            screen.blit(imgFrame, (offset, 0))
        pygame.display.flip()
    
    stream.release()
    pygame.quit()

def main():
    startClient(0)
    
if __name__ == '__main__':
    main()