import os
import struct
import socket
import thin_client.settings as s

def initializeUDPSocket():
    sock = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP
    return sock

"""
Keyboard:
8bit Version (Currently use 0)
8bit Protocol Type : (Keyboard (1), Mouse (2), Gamepad, etc.)
32bit Sequence (counter for event)
8bit ControllerID (start from 0)
32bit UEKeyCode (A, B, , Z, 0, ... ,9, punctuation, etc.)
32bit UECharCode (F1, ..., F12, Ctrl, Alt, Numpad, etc.)
8bit Event (Key Down (2), Key Up (3))

Mouse:
8bit Version (Currently use 0)
8bit Protocol Type : (Keyboard (1), Mouse (2), Gamepad, etc.)
32bit Sequence (counter for event)
8bit ControllerID (start from 0)
32bit x-axis movement
32bit y-axis movement

Packet formats:
B = unsigned char (1 byte)
I = unsigned int (4 bytes)
i = signed int (4 bytes)
"""
def packAndSend(deviceType, sequence, controllerID, UEKeyCode, UECharCode, eventType, socketName):
    dataKeyboard = (s.VERSION, deviceType, sequence, controllerID, UEKeyCode, UECharCode, eventType)
    dataMouse = (s.VERSION, deviceType, sequence, controllerID, UEKeyCode, UECharCode)
    if (deviceType == s.DEVICE_KEYBOARD):
        message = struct.pack(s.PACKET_FORMAT_KEY, *dataKeyboard)
    elif (deviceType == s.DEVICE_MOUSE):
        message = struct.pack(s.PACKET_FORMAT_MOUSE, *dataMouse)
    print(message)
    socketName.sendto(message, (s.UDP_IP, s.UDP_PORT))
    sequence += 1 
    return sequence

def sendQuitCommand(playerControllerID):
    quitCommand = "0001000" + str(playerControllerID)
    try:
        cpsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        cpsocket.connect((s.TCP_IP, s.TCP_PORT))
        cpsocket.sendall(quitCommand.encode("utf-8"))
    except socket.error as error:
        print("Thin client:", os.strerror(error.errno));
    finally:
        cpsocket.close()