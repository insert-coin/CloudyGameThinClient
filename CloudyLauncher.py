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
import thin_client


JOIN = "join"
QUIT = "quit"
EXIT = "exit"


if __name__ == "__main__":

    ## to connect with CloudyPanel
    TCP_IP = "127.0.0.1"
    TCP_PORT = 55550
    BUFFER_SIZE = 1024
    
    while (True):

        try:
            cpsocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            cpsocket.connect((TCP_IP, TCP_PORT))
            
        except socket.error as error:
            print("Socket error.\n")
            print(str(error) + "\n")
            
            if (error.errno == errno.WSAECONNRESET):
                print ("socket error")
            else:

                print("other error.\n")
                print(error)

        try:
            playerInput = input("Enter command: ")

            if (playerInput == EXIT):
                break

            elif (playerInput.split(' ')[0] == JOIN):
                
                MESSAGE = playerInput
                MESSAGE_BYTE = MESSAGE.encode("utf-8")
                cpsocket.sendall(MESSAGE_BYTE)
                response = cpsocket.recv(BUFFER_SIZE)
                response = response.decode("utf-8")

                if (response.isdigit()):
                    print("call thin client with controllerid '" + str(response) + "'\n")
                    thin_client.startClient(int(response))

                else:
                    print(response)
                    print("\n")

            elif (playerInput.split(' ')[0] == QUIT):

                MESSAGE = playerInput
                MESSAGE_BYTE = MESSAGE.encode("utf-8")
                cpsocket.sendall(MESSAGE_BYTE)
                response = cpsocket.recv(BUFFER_SIZE)
                response = response.decode("utf-8")

                if (response == "done"):
                    print("quit game successful.\n")
                else:
                    print("quit game unsuccessful.\n")

            else:
                print("Invalid command.\n")

        except error:
            print("error in player joining/quitting.\n")
            
            
        finally:
            cpsocket.close()


