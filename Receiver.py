from twisted.internet.protocol import DatagramProtocol
from twisted.internet import reactor

class Echo(DatagramProtocol):
    def datagramReceived(self, data, address):
        self.transport.write(data, address)
        print(" ".join(hex(ord(n)) for n in data))


reactor.listenUDP(9999, Echo())
reactor.run()