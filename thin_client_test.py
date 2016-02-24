import unittest
import thin_client
import socket
from wheel.signatures import assertTrue
            
class thin_client_test(unittest.TestCase):
    # Check if packAndSend increments the sequence number correctly       
    def test_packAndSend_sequence(self):
        socketName = socket.socket(socket.AF_INET, # Internet
                                   socket.SOCK_DGRAM) # UDP
        
        sequence = 0
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 1, "packAndSend: fail to increase sequence from 0 to 1.")
        
        sequence = 1
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 2, "packAndSend: fail to increase sequence from 1 to 2.")
        
        sequence = 2
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 3, "packAndSend: fail to increase sequence from 2 to 3.")
        
        sequence = 3
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 4, "packAndSend: fail to increase sequence from 3 to 4.")
        
    # Check if pygame is initialized successfully
    def test_initializePygame_30_fps(self):
        try:
            thin_client.initializePygame(30)
        except:
            self.fail("Fail to initialize 30 FPS")

    def test_initializePygame_60_fps(self):
        try:
            thin_client.initializePygame(60)
        except:
            self.fail("Fail to initialize 60 FPS")    

if __name__ == '__main__':
    unittest.main()