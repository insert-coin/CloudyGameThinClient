import socket
import struct
import string
import pygame, time
from pygame.locals import * 

ASCII_TO_UE_KEYCODE = {8: 12, #Backspace
                       9: 13, # Tab
                       13: 14, # Enter
                       27: 17, # Escape
                       32: 18, # Space
                       ord('0'): 29, 
                       ord('1'): 30,
                       ord('2'): 31,
                       ord('3'): 32,
                       ord('4'): 33,
                       ord('5'): 34,
                       ord('6'): 35, 
                       ord('7'): 36, 
                       ord('8'): 37, 
                       ord('9'): 38, 
                       ord('a'): 39, 
                       ord('b'): 40,
                       ord('c'): 41,
                       ord('d'): 42, 
                       ord('e'): 43,
                       ord('f'): 44,
                       ord('g'): 45, 
                       ord('h'): 46,
                       ord('i'): 47,
                       ord('j'): 48, 
                       ord('k'): 49,
                       ord('l'): 50,
                       ord('m'): 51, 
                       ord('n'): 52,
                       ord('o'): 53,
                       ord('p'): 54, 
                       ord('q'): 55,
                       ord('r'): 56,
                       ord('s'): 57, 
                       ord('t'): 58,
                       ord('u'): 59,
                       ord('v'): 60, 
                       ord('w'): 61,
                       ord('x'): 62,
                       ord('y'): 63, 
                       ord('z'): 64,
                       282: 80, # F1
                       283: 81, # F2
                       284: 82,
                       285: 83,
                       286: 84,
                       287: 85,
                       288: 86,
                       289: 87,
                       290: 88, #F9
                       291: 89,
                       292: 90, #F11
                       293: 91,
                       301: 16, # Caps Lock
                       303: 95, # Right Shift
                       304: 94, # Left Shift
                       305: 97, # Right Ctrl
                       306: 96, # Left Ctrl
                       307: 99, # Right Alt
                       308: 98 # Left Alt
                       }

for i in string.ascii_lowercase:
    ASCII_TO_UE_KEYCODE[ord(i)] = ord(i) - 32

"""
8bit Version (Currently use 0)
8bit Type : (Keyboard (0), Mouse (1), Gamepad, etc
32bit Sequence (counter for event)
8bit ControllerID (start from 0)
16bit KeyCode (A, B, , Z, 0, ... ,9, F1, ..., F12, etc)
8bit Event (Key Down, Key Up)
"""

def main():
    PACKET_FORMAT = "=BBIBIB"
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
        #try:
        #    print event
       # except:
       #     pass
        if (event.type == KEYUP or event.type == KEYDOWN):
            data = (VERSION, TYPE, sequence, CONTROLLER_ID, ASCII_TO_UE_KEYCODE[event.key], event.type)
            print(event.key, '=>', ASCII_TO_UE_KEYCODE[event.key])
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