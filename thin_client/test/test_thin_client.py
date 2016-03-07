import unittest
import thin_client.thin_client_main as thin_client
            
class test_thin_client(unittest.TestCase):      
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