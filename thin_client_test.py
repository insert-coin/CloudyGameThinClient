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
        self.assertEqual(finalSequence, 1, "packAndSend sequence from 0 to 1 fail")
        
        sequence = 1
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 2, "packAndSend sequence from 1 to 2 fail")
        
        sequence = 2
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 3, "packAndSend sequence from 2 to 3 fail")
        
        sequence = 3
        finalSequence = thin_client.packAndSend(1, sequence, 0, 0, 0, 0, socketName)
        self.assertEqual(finalSequence, 4, "packAndSend sequence from 3 to 4 fail")
        
    # Check if pygame is initialized successfully
    def test_initializePygame_30FPS(self):
        assertTrue(thin_client.initializePygame(30), "initializePygame at 30 FPS fail")
        assertTrue(thin_client.initializePygame(60), "initializePygame at 60 FPS fail")       

if __name__ == '__main__':
    unittest.main()