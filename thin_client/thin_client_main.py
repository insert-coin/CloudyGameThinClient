import pygame
import sys
from pygame.locals import * 
import thin_client.vlc_addon as vlc_addon
import thin_client.networking as networking
import thin_client.settings as s
    
def initializePygame(FPS):
    pygame.init()
    screen = pygame.display.set_mode((s.RESO_WIDTH, s.RESO_HEIGHT))
    pygame.display.set_caption("Remote Keyboard")
    pygame.mouse.set_visible(False) # Makes mouse invisible
    pygame.event.set_grab(True) # confines the mouse cursor to the window
    frameInterval = int((1/FPS)*1000)
    pygame.key.set_repeat(frameInterval, frameInterval) # 1 input per frame
    
    myFont = pygame.font.Font(None, 30)
    label = myFont.render("Loading...", True, (255, 255, 255))
    mouseLabel = myFont.render("Press ESC to lock/unlock mouse.", True, (255, 255, 255))
    textRect = label.get_rect()
    renderPosX = screen.get_rect().centerx - textRect.centerx
    renderPosY = screen.get_rect().centery - textRect.centery
    screen.blit(label, (renderPosX, renderPosY))
    screen.blit(mouseLabel, (renderPosX - 100, renderPosY + 50))
    pygame.display.update()

    return pygame

def processMouseButtons(playerControllerID, sequence, sock, pygame, event):
    leftMouseButton, middleMouseButton, rightMouseButton = pygame.mouse.get_pressed()
    if (leftMouseButton == 1):
        UECharCode = 1
    elif (middleMouseButton == 1):
        UECharCode = 4
    elif (rightMouseButton == 1):
        UECharCode = 2
    else:
        UECharCode = 0
    UEKeyCode = UECharCode
    if (event.type == MOUSEBUTTONDOWN):
        sequence = networking.packAndSend(s.DEVICE_KEYBOARD, sequence, playerControllerID, UEKeyCode, UECharCode, 2, sock)
    elif (event.type == MOUSEBUTTONUP):
        sequence = networking.packAndSend(s.DEVICE_KEYBOARD, sequence, playerControllerID, UEKeyCode, UECharCode, 3, sock)
    print(pygame.mouse.get_pressed(), "=>", UEKeyCode)
    
def processMouseMotion(playerControllerID, sequence, sock, pygame, event):
    x, y = pygame.mouse.get_rel()
    sequence = networking.packAndSend(s.DEVICE_MOUSE, sequence, playerControllerID, x, y, event.type, sock)
    print("Mouse:", x,y)

def processKeyboardPress(playerControllerID, sequence, sock, pygame, event):
    print("ASCII Key is:", event.key)
    UEKeyCode = s.ASCII_TO_UE_KEYCODE.get(event.key, 0)
    UECharCode = s.ASCII_TO_UE_CHARCODE.get(event.key, UEKeyCode)
    UEKeyCode = UECharCode or UEKeyCode # This code is redundant. It changes nothing.
    print(UEKeyCode, UECharCode)
    sequence = networking.packAndSend(s.DEVICE_KEYBOARD, sequence, playerControllerID, UEKeyCode, UECharCode, event.type, sock)
    print(event.key, "=>", UEKeyCode)

def toggleMouseGrab(pygame, isMouseGrabbed):
    if (isMouseGrabbed == True):
        isMouseGrabbed = False
        pygame.event.set_grab(False)
        pygame.mouse.set_visible(True)
    else:
        isMouseGrabbed = True
        pygame.event.set_grab(True)
        pygame.mouse.set_visible(False)
        
    return isMouseGrabbed

def startClient(IP, port, playerControllerID):
    sequence = 0
    
    sock = networking.initializeUDPSocket()
    pygame = initializePygame(s.FPS) #FPS
    pygame = vlc_addon.initializeStream(IP, port, pygame)
    isRunning = True
    isMouseGrabbed = True

    while (isRunning):
        event = pygame.event.wait() # program will sleep if there are no events in the queue

        if (event.type == KEYDOWN or event.type == KEYUP):
            processKeyboardPress(playerControllerID, sequence, sock, pygame, event)
            
            # To toggle mouse grabbing within the window
            if (event.type == KEYUP and event.key == K_ESCAPE):
                isMouseGrabbed = toggleMouseGrab(pygame, isMouseGrabbed)

        if (event.type == pygame.MOUSEMOTION):
            processMouseMotion(playerControllerID, sequence, sock, pygame, event)
        if (event.type == MOUSEBUTTONDOWN or event.type == MOUSEBUTTONUP):
            processMouseButtons(playerControllerID, sequence, sock, pygame, event)            
        if (event.type == QUIT):
            networking.sendQuitCommand(playerControllerID)
            isRunning = False

    pygame.quit()

def main(IP, port, playerControllerID):
    startClient(IP, port, int(playerControllerID))
    
if __name__ == '__main__':
    # If an argument is passed with the script
    # [IP, port, controllerID]
    if len(sys.argv) > 3:
        main(sys.argv[1], sys.argv[2], sys.argv[3])
    # If none or missing argument, launch with these defaults
    else:
        main("127.0.0.1", 30000, 0)