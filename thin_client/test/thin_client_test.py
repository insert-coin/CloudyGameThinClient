import unittest
import thin_client.main as thin_client
            
class ThinClientTest (unittest.TestCase):      
    # Check if pygame is initialized successfully
    def test_initialize_pygame_30_fps(self):
        try:
            thin_client.initialize_pygame(30)
        except:
            self.fail("Fail to initialize 30 FPS")

    def test_initialize_pygame_60_fps(self):
        try:
            thin_client.initialize_pygame(60)
        except:
            self.fail("Fail to initialize 60 FPS")    

if __name__ == '__main__':
    unittest.main()