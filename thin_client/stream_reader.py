import cv2
import numpy
import pygame
from thin_client import settings

def get_frame(capture_object, scale):
    """Reads the OpenCV video capture object, grabs a single frame, 
       and transforms it into a pygame readable image. This is done by converting
       the image from BGR to RGB, rotating it counter-clockwise by 90 degrees, then flipping
       the image across the horizontal axis, then scaling it to fit the pygame window, 
       and finally converting the image into a pygame surface.
    """
    retval, frame = capture_object.read()
    if (retval == True):
        frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        frame = numpy.flipud(numpy.rot90(frame))
        frame = cv2.resize(frame, (0, 0), fx = scale, fy = scale)
        frame = pygame.surfarray.make_surface(frame)
        return frame  
    else:
        return False
    
def get_scale_factor(capture_object):
    """Calculate the scale factor to resize the image in the x and y direction"""
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

def get_offset(scale, is_width_smaller, frame_width, frame_height):
    """Calculate the x and y direction offset, so that the image can be placed in the
       middle of the window
    """
    if (is_width_smaller):
        frame_height = frame_height * scale
        offset = (settings.RESO_HEIGHT - frame_height) / 2
    else:
        frame_width = frame_width * scale
        offset = (settings.RESO_WIDTH - frame_width) / 2
    
    return offset

def initialize_stream(ip, port):
    """Read the network video stream"""
    address = "{}:{}".format(ip, port)
    cap = cv2.VideoCapture(address)
    frame_width = int(cap.get(3))
    frame_height = int(cap.get(4))

    return (cap, frame_width, frame_height)

def setup_stream(ip, port):
    """Reads the stream, calculates the scale factor, and calculates the image offset 
       from the center of the window
    """
    cap, frame_width, frame_height = initialize_stream(ip, port)
    scale, is_width_smaller = get_scale_factor(cap)
    offset = get_offset(scale, is_width_smaller, frame_width, frame_height)
    
    return (scale, offset, is_width_smaller, cap)