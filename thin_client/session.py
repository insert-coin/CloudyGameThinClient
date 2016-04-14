import os
import struct
import socket
import logging
import json
from thin_client import settings

class GameSession(object):
    """
    Keyboard:
    8bit Version (Currently use 0)
    8bit Protocol Type : (Keyboard (1), Mouse (2), Gamepad, etc.)
    32bit Sequence (counter for event)
    8bit ControllerID (start from 0)
    16bit UEKeyCode (A, B, , Z, 0, ... ,9, punctuation, etc.)
    16bit UECharCode (F1, ..., F12, Ctrl, Alt, Numpad, etc.)
    8bit Event (Key Down (2), Key Up (3))

    Mouse:
    8bit Version (Currently use 0)
    8bit Protocol Type : (Keyboard (1), Mouse (2), Gamepad, etc.)
    32bit Sequence (counter for event)
    8bit ControllerID (start from 0)
    16bit x-axis movement
    16bit y-axis movement
    """

    def __init__(self, ip_address, player_controller_id):
        self.sock = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP
        self.ip_address = ip_address
        self.player_controller_id = player_controller_id
        self.sequence = 0

    
    def pack_and_send(self, device_type, ue_key_code, 
                      ue_char_code, event_type):
        """Packs the keyboard or mouse information into a UDP packet, 
           and sends it to the game (Remote Controller module)
        """
        data_keyboard = (settings.VERSION, device_type, self.sequence, self.player_controller_id,
                         ue_key_code, ue_char_code, event_type)
        data_mouse = (settings.VERSION, device_type, self.sequence, self.player_controller_id, 
                      ue_key_code, ue_char_code)
        if (device_type == settings.DEVICE_KEYBOARD):
            message = struct.pack(settings.PACKET_FORMAT_KEY, *data_keyboard)
        elif (device_type == settings.DEVICE_MOUSE):
            message = struct.pack(settings.PACKET_FORMAT_MOUSE, *data_mouse)
        self.sock.sendto(message, (self.ip_address, settings.UDP_PORT))
        self.sequence += 1 

    def send_quit_command(self):
        """Sends a quit command to the game engine (CloudyPlayerManager module)"""
        json_data = {
           "command" : "quit",
           "controller" : self.player_controller_id
        }
        quit_command = json.dumps(json_data)
        try:
            tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            tcp_socket.connect((self.ip_address, settings.TCP_STREAMING_PORT))
            tcp_socket.sendall(quit_command.encode("utf-8"))
        except socket.error as error:
            logging.warning(os.strerror(error.errno));
        finally:
            tcp_socket.close()