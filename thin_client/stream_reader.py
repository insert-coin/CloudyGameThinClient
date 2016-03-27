import cv2
import numpy
import pygame
from thin_client import settings

# Reads the capture object and transforms it into a pygame readable image
def get_frame(capture_object, scale):
    retval, frame = capture_object.read()
    if (retval == True):
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        frame = numpy.flipud(numpy.rot90(frame))
        frame = cv2.resize(frame, (0, 0), fx = scale, fy = scale)
        frame = pygame.surfarray.make_surface(frame)
        return frame  
    else:
        return False
    
# Scale up or down the received stream to fit the window
def get_scale_factor(capture_object):
    frame_width = int(capture_object.get(3))
    frame_height = int(capture_object.get(4))
    if (frame_width == 0 or frame_height == 0):
        frame_width = 1
        frame_height = 1
    widthScale = settings.RESO_WIDTH / frame_width
    heightScale = settings.RESO_HEIGHT / frame_height
    if (widthScale < heightScale):
        scale = widthScale
        is_width_smaller = True
    else:
        scale = heightScale
        is_width_smaller = False 
        
    return scale, is_width_smaller

# Offset to center the image in the window
def get_offset(scale, is_width_smaller, frame_width, frame_height):
    if (is_width_smaller):
        frame_height = frame_height * scale
        offset = (settings.RESO_HEIGHT - frame_height) / 2
    else:
        frame_width = frame_width * scale
        offset = (settings.RESO_WIDTH - frame_width) / 2
    
    return offset

# Reads the http stream
def initialize_stream(ip, port):
    address = "http://" + ip + ":" + port
    cap = cv2.VideoCapture(address)
    frame_width = int(cap.get(3))
    frame_height = int(cap.get(4))

    return (cap, frame_width, frame_height)

# Reads the stream, find the scale factor, and the image offset from the center of the window
def setup_stream(ip, port):
    cap, frame_width, frame_height = initialize_stream(ip, port)
    scale, is_width_smaller = get_scale_factor(cap)
    offset = get_offset(scale, is_width_smaller, frame_width, frame_height)
    
    return (scale, offset, is_width_smaller, cap)