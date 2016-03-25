import unittest
from thin_client.session import GameSession
import socket

class ProtocolTest(unittest.TestCase):

    def setUp(self):
        ip_address = "127.0.0.1"
        player_controller_id = 0
        self.session = GameSession(ip_address, player_controller_id)

    
    def test_pack_and_send_sequence_0_to_1(self):
        """Check if packAndSend increments the sequence number correctly"""

        # Arrange 
        device_type, ue_key_code, ue_char_code, event_typ = (1, 0, 0, 0)
        current_sequence = self.session.sequence

        # Act
        self.session.pack_and_send(device_type, ue_key_code, ue_char_code, event_typ)

        # Assert
        self.assertEqual(self.session.sequence, current_sequence + 1,
            "pack_and_send did not increment the sequence by 1")
        

if __name__ == '__main__':
    unittest.main()