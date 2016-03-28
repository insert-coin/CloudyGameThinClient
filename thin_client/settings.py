import string

"""
Packet formats:
B = unsigned char (1 byte)
I = unsigned int (4 bytes)
i = signed int (4 bytes)
H = unsigned short (2 bytes)
h = signed short (2 bytes)
"""
# Networking constants
PACKET_FORMAT_KEY = "=BBIBHHB"
PACKET_FORMAT_MOUSE = "=BBIBhh"
UDP_PORT = 55555              # Port to RemoteController
TCP_STREAMING_PORT = 55556    # Port to StreamingPlugin
TCP_CLOUDYWEBAPI_PORT = 55557 # Port to CloudyWebAPI
VERSION = 0

# Shared by networking and Pygame input detection
DEVICE_KEYBOARD = 1
DEVICE_MOUSE = 2

# Pygame constants
RESO_WIDTH = 640
RESO_HEIGHT = 480
DEVICE_KEYBOARD = 1
DEVICE_MOUSE = 2
FPS = 30
TEXT_WINDOW_TITLE = "CloudyGame Thin Client"
TEXT_LOADING = "Loading..."
TEXT_INSTRUCTIONS = "Press ESC to lock/unlock mouse."
TEXT_FONT_SIZE = 30
TEXT_COLOUR = (255, 255, 255)

# Keyboard mappings
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