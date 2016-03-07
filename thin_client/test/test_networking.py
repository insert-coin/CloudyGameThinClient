import unittest
import thin_client.networking as networking
import socket

class test_networking(unittest.TestCase):
    # Check if packAndSend increments the sequence number correctly       
    def test_packAndSend_sequence(self):
        socketName = socket.socket(socket.AF_INET, # Internet
                                   socket.SOCK_DGRAM) # UDP
        
        sequence = 0
        finalSequence = networking.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 1, "packAndSend: fail to increase sequence from 0 to 1.")
        
        sequence = 1
        finalSequence = networking.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 2, "packAndSend: fail to increase sequence from 1 to 2.")
        
        sequence = 2
        finalSequence = networking.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 3, "packAndSend: fail to increase sequence from 2 to 3.")
        
        sequence = 3
        finalSequence = networking.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 4, "packAndSend: fail to increase sequence from 3 to 4.")
        
    if __name__ == '__main__':
        unittest.main()