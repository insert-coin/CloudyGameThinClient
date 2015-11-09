#include <cstdint>

struct PacketKeyboard
{
	uint8_t version;
	uint8_t type;
	uint32_t sequence;
	uint8_t controllerID;
	uint16_t keycode;
	uint8_t keyEvent;
};

struct PacketMouse
{
	uint8_t version;
	uint8_t type;
	uint32_t sequence;
	uint8_t controllerID;
	uint8_t button;
	uint8_t mouseEvent;
};

struct PacketQuit
{
	uint8_t version;
	uint8_t type;
	uint32_t sequence;
	uint8_t controllerID;
};