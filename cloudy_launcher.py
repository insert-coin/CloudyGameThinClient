'''
Simulates the launcher for CloudyGame

Starts a loop that does: 
1. get user input:
       join <username>
       quit <username>
       exit
2. send join/quit command to CloudyPanel
3a. receive controllerId if success.
    start thin client with given controllerId.
3b. receive error message if fail.

'''

import socket
import errno
import os
import thin_client


JOIN = "join"
QUIT = "quit"
EXIT = "exit"
COMMANDS = """
Commands:
  connect and join user -- join <username>
  connect and quit user -- quit <username>
  exit cloudy_launcher  -- exit
"""

def main():
    ## to connect with CloudyPanel
    TCP_IP = "127.0.0.1"
    TCP_PORT = 55550
    BUFFER_SIZE = 1024

    print(COMMANDS)
    
    while (True):

        playerInput = input("Enter command: ")
        print(playerInput)

        if (playerInput == EXIT):
            break

        elif (len(playerInput.split()) != 2):
            print("Invalid command.\n")
            print(COMMANDS)

        elif (playerInput.split()[0] == JOIN):
            
            MESSAGE = playerInput
            MESSAGE_BYTE = MESSAGE.encode("utf-8")
            print("Message to send: " + MESSAGE)

            try:
                cpsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                cpsocket.connect((TCP_IP, TCP_PORT))

                cpsocket.sendall(MESSAGE_BYTE)
                response = cpsocket.recv(BUFFER_SIZE)
                response = response.decode("utf-8")
                print(response)

                if (response.isdigit()):
                    print("open process to launch thin client with controllerid '" + str(response) + "'\n")
                    thin_client.startClient(int(response))

                else:
                    print(response)
                    print()
            
            except socket.error as error:
                print(os.strerror(error.errno));

            finally:
                cpsocket.close()

        elif (playerInput.split()[0] == QUIT):

            MESSAGE = playerInput
            MESSAGE_BYTE = MESSAGE.encode("utf-8")

            try:
                cpsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                cpsocket.connect((TCP_IP, TCP_PORT))

                cpsocket.sendall(MESSAGE_BYTE)
                response = cpsocket.recv(BUFFER_SIZE)
                response = response.decode("utf-8")

                if (response == "done"):
                    print("quit game successful.\n")
                else:
                    print("quit game unsuccessful.\n")

            except socket.error as error:
                print(os.strerror(error.errno));

            finally:
                cpsocket.close()

        else:
            print("Invalid command.\n")

if __name__ == "__main__":
    main()
    
