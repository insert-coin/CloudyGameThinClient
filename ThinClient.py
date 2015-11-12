import socket
import struct
import pygame, time
from pygame.locals import * 

"""
8bit Version (Currently use 0)
8bit Type : (Keyboard (0), Mouse (1), Gamepad, etc
32bit Sequence (counter for event)
8bit ControllerID (start from 0)
16bit KeyCode (A, B, , Z, 0, ... ,9, F1, ..., F12, etc)
8bit Event (Key Down, Key Up)
"""

def main():
    PACKET_FORMAT = "=BBIBIBB"
    UDP_IP = "127.0.0.1"
    UDP_PORT = 55555
    VERSION = 0
    TYPE = 1
    sequence = 0
    CONTROLLER_ID = 0


    print("UDP target IP:", UDP_IP)
    print("UDP target port:", UDP_PORT)

    sock = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP

    pygame.init()
    screen = pygame.display.set_mode((640, 480))
    pygame.display.set_caption('Remote Keyboard')
    pygame.mouse.set_visible(True)
    
    isRunning = True
    pygame.key.set_repeat(33, 33) # 1 input per frame

    while isRunning:
        event = pygame.event.wait() # program will sleep if there are no events in the queue
        if (event.type == KEYUP or event.type == KEYDOWN):
            if (event.type == KEYDOWN and len(event.unicode) > 0):
                data = (VERSION, TYPE, sequence, CONTROLLER_ID, event.key, event.type, ord(event.unicode))
                message = struct.pack(PACKET_FORMAT, *data)
                print(message)
                sock.sendto(message, (UDP_IP, UDP_PORT))
                sequence += 1
        if (event.type == MOUSEMOTION):
            x, y = pygame.mouse.get_rel()
        if (event.type == MOUSEBUTTONDOWN or event.type == MOUSEBUTTONUP):
            leftMouseButton, middleMouseButton, rightMouseButton = pygame.mouse.get_pressed()
        if (event.type == QUIT):
            isRunning = False
    
    pygame.quit()

if __name__ == '__main__':
    main()