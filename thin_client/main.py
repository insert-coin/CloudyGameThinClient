import sys
import os
sys.path.append(os.path.dirname(os.getcwd()))
import logging
import pygame
import argparse
from pygame.locals import * 
from thin_client import vlc_addon
from thin_client import protocol
from thin_client import settings

def initialize_pygame(fps):
    pygame.init()
    screen = pygame.display.set_mode((settings.RESO_WIDTH, settings.RESO_HEIGHT))
    pygame.display.set_caption(settings.TEXT_WINDOW_TITLE)
    pygame.mouse.set_visible(False) # Makes mouse invisible
    pygame.event.set_grab(True) # confines the mouse cursor to the window
    frame_interval = int((1/fps)*1000)
    pygame.key.set_repeat(frame_interval, frame_interval) # 1 input per frame
    
    my_font = pygame.font.Font(None, settings.TEXT_FONT_SIZE)
    label = my_font.render(settings.TEXT_LOADING, True, settings.TEXT_COLOUR)
    mouse_label = my_font.render(settings.TEXT_INSTRUCTIONS, True, settings.TEXT_COLOUR)
    text_rect = label.get_rect()
    render_pos_x = screen.get_rect().centerx - text_rect.centerx
    render_pos_y = screen.get_rect().centery - text_rect.centery
    screen.blit(label, (render_pos_x, render_pos_y))
    screen.blit(mouse_label, (render_pos_x - 100, render_pos_y + 50))
    pygame.display.update()

    return pygame

def process_mouse_buttons(player_controller_id, sequence, sock, pygame, event):
    left_mouse_button, middle_mouse_button, right_mouse_button = pygame.mouse.get_pressed()
    if (left_mouse_button == 1):
        ue_char_code = 1
    elif (middle_mouse_button == 1):
        ue_char_code = 4
    elif (right_mouse_button == 1):
        ue_char_code = 2
    else:
        ue_char_code = 0
    ue_key_code = ue_char_code
    if (event.type == MOUSEBUTTONDOWN):
        sequence = protocol.pack_and_send(settings.DEVICE_KEYBOARD, sequence, 
                                          player_controller_id, ue_key_code, 
                                          ue_char_code, 2, sock)
    elif (event.type == MOUSEBUTTONUP):
        sequence = protocol.pack_and_send(settings.DEVICE_KEYBOARD, sequence, 
                                          player_controller_id, ue_key_code, 
                                          ue_char_code, 3, sock)
    logging.info(pygame.mouse.get_pressed(), "=>", ue_key_code)
    
def process_mouse_motion(player_controller_id, sequence, sock, pygame, event):
    x, y = pygame.mouse.get_rel()
    sequence = protocol.pack_and_send(settings.DEVICE_MOUSE, sequence, 
                                      player_controller_id, x, y, event.type, sock)
    logging.info("Mouse:", x,y)

def process_keyboard_press(player_controller_id, sequence, sock, pygame, event):
    logging.info("ASCII Key is:", event.key)
    ue_key_code = settings.ASCII_TO_UE_KEYCODE.get(event.key, 0)
    ue_char_code = settings.ASCII_TO_UE_CHARCODE.get(event.key, ue_key_code)
    ue_key_code = ue_char_code or ue_key_code # This code is redundant. It changes nothing.
    logging.info(ue_key_code, ue_char_code)
    sequence = protocol.pack_and_send(settings.DEVICE_KEYBOARD, sequence, 
                                      player_controller_id, ue_key_code,
                                      ue_char_code, event.type, sock)
    logging.info(event.key, "=>", ue_key_code)

def toggle_mouse_grab(pygame, is_mouse_grabbed):
    if (is_mouse_grabbed == True):
        is_mouse_grabbed = False
        pygame.event.set_grab(False)
        pygame.mouse.set_visible(True)
    else:
        is_mouse_grabbed = True
        pygame.event.set_grab(True)
        pygame.mouse.set_visible(False)
        
    return is_mouse_grabbed

def start_client(ip, port, player_controller_id):
    sequence = 0
    
    sock = protocol.initialize_udp_socket()
    pygame = initialize_pygame(settings.FPS) #FPS
    pygame = vlc_addon.initialize_stream(ip, port, pygame)
    is_running = True
    is_mouse_grabbed = True

    while (is_running):
        event = pygame.event.wait() # program will sleep if there are no events in the queue

        if (event.type == KEYDOWN or event.type == KEYUP):
            process_keyboard_press(player_controller_id, sequence, sock, pygame, event)
            
            # To toggle mouse grabbing within the window
            if (event.type == KEYUP and event.key == K_ESCAPE):
                is_mouse_grabbed = toggle_mouse_grab(pygame, is_mouse_grabbed)

        if (event.type == pygame.MOUSEMOTION):
            process_mouse_motion(player_controller_id, sequence, sock, pygame, event)
        if (event.type == MOUSEBUTTONDOWN or event.type == MOUSEBUTTONUP):
            process_mouse_buttons(player_controller_id, sequence, sock, pygame, event)            
        if (event.type == QUIT):
            protocol.send_quit_command(player_controller_id)
            is_running = False

    pygame.quit()

def main(ip, port, player_controller_id):
    start_client(ip, port, int(player_controller_id))
    
if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument('string', metavar='ip', type=str, default="127.0.0.1",
                        help="IP address to obtain video stream from")
    parser.add_argument('integer', metavar='port', type=int, default=30000, 
                        choices=range(30000, 30004),
                        help="Port of the IP address you are connecting to. Value from 30000 to 30003")
    parser.add_argument('integer', metavar='player_id', type=int, default=0, 
                        choices=range(0, 4),
                        help="Player controller ID of the player. Value from 0 to 3.")
    
    args = parser.parse_args()
    
    main(sys.argv[1], sys.argv[2], sys.argv[3])