import unittest
import thin_client.protocol as protocol
import socket

class ProtocolTest(unittest.TestCase):
    # Check if packAndSend increments the sequence number correctly       
    def test_pack_and_send_sequence_0_to_1(self):
        socket_name = socket.socket(socket.AF_INET, # Internet
                                   socket.SOCK_DGRAM) # UDP
        sequence = 0
        final_sequence = protocol.pack_and_send(1, sequence, 0, 0, 0, 0, socket_name)
        self.assertEqual(final_sequence, 1, "packAndSend: fail to increase sequence from 0 to 1.")
        
    def test_pack_and_send_sequence_1_to_2(self):  
        socket_name = socket.socket(socket.AF_INET, # Internet
                                   socket.SOCK_DGRAM) # UDP
        sequence = 1
        final_sequence = protocol.pack_and_send(1, sequence, 0, 0, 0, 0, socket_name)
        self.assertEqual(final_sequence, 2, "packAndSend: fail to increase sequence from 1 to 2.")
    
    def test_pack_and_send_sequence_2_to_3(self):    
        socket_name = socket.socket(socket.AF_INET, # Internet
                                   socket.SOCK_DGRAM) # UDP
        sequence = 2
        final_sequence = protocol.pack_and_send(1, sequence, 0, 0, 0, 0, socket_name)
        self.assertEqual(final_sequence, 3, "packAndSend: fail to increase sequence from 2 to 3.")
    
    def test_pack_and_send_sequence_3_to_4(self): 
        socket_name = socket.socket(socket.AF_INET, # Internet
                                   socket.SOCK_DGRAM) # UDP   
        sequence = 3
        final_sequence = protocol.pack_and_send(1, sequence, 0, 0, 0, 0, socket_name)
        self.assertEqual(final_sequence, 4, "packAndSend: fail to increase sequence from 3 to 4.")
        
    if __name__ == '__main__':
        unittest.main()