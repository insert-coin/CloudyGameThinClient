import os
import struct
import socket
import logging
from thin_client import settings

def initialize_udp_socket():
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
"""
def pack_and_send(device_type, sequence, controller_id, ue_key_code, 
                  ue_char_code, event_type, socket_name):
    data_keyboard = (settings.VERSION, device_type, sequence, controller_id,
                     ue_key_code, ue_char_code, event_type)
    data_mouse = (settings.VERSION, device_type, sequence, controller_id, 
                  ue_key_code, ue_char_code)
    if (device_type == settings.DEVICE_KEYBOARD):
        message = struct.pack(settings.PACKET_FORMAT_KEY, *data_keyboard)
    elif (device_type == settings.DEVICE_MOUSE):
        message = struct.pack(settings.PACKET_FORMAT_MOUSE, *data_mouse)
    socket_name.sendto(message, (settings.UDP_IP, settings.UDP_PORT))
    sequence += 1 
    return sequence

def send_quit_command(player_controller_id):
    quit_command = "0001000" + str(player_controller_id)
    try:
        tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        tcp_socket.connect((settings.TCP_IP, settings.TCP_PORT))
        tcp_socket.sendall(quit_command.encode("utf-8"))
    except socket.error as error:
        logging.warning(os.strerror(error.errno));
    finally:
        tcp_socket.close()