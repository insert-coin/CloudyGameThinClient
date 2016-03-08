import sys
import vlc
import logging

# Taken from https://gist.github.com/smathot/1521059 with modifications
def initialize_stream(IP, port, pygame):
    # Tested formats: rtmp, rtsp, http
    # Get more test links here: http://www.vlc.eu.pn/
    # http://futuretv.cdn.mangomolo.com/futuretv/smil:futuretv.smil/gmswf.m3u8
    # rtmp://wowza-bnr.cdp.triple-it.nl/bnr/BNRstudio1
    # rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov
   
    movie_address = "http://" + str(IP) + ":" + str(port)
    logging.info("Connecting to:", movie_address)
    movie_address = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"

    # Create instane of VLC and create reference to movieAddress.
    vlc_instance = vlc.Instance()
    media = vlc_instance.media_new(movie_address)
    media.get_mrl()
    
    # Create new instance of vlc player
    player = vlc_instance.media_player_new()
        
    # Pass pygame window id to vlc player, so it can render its contents there.
    win_id = pygame.display.get_wm_info()['window']
    if sys.platform == "linux2": # for Linux using the X Server
        player.set_xwindow(win_id)
    elif sys.platform == "win32": # for Windows
        player.set_hwnd(win_id)
    elif sys.platform == "darwin": # for MacOS
        player.set_agl(win_id)
    
    # Load movieAddress into vlc player instance
    player.set_media(media)
    
    # Disable VLC event handling so pygame can handle them
    player.video_set_mouse_input(False)
    player.video_set_key_input(False)
    
    # Quit pygame mixer to allow vlc full access to audio device (REINIT AFTER MOVIE PLAYBACK IS FINISHED!)
    pygame.mixer.quit()
    
    # Start movieAddress playback
    player.play()
    
    return pygame