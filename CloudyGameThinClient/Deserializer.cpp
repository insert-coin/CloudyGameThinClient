#include "Datagrams.h"

int deserialize_int8(unsigned char *buffer)
{
	int value = 0;

	value |= buffer[0];
	return value;
}

int deserialize_int16(unsigned char *buffer)
{
	int value = 0;

	value |= buffer[0] << 8;
	value |= buffer[1];
	return value;
}

int deserialize_int32(unsigned char *buffer)
{
	int value = 0;

	value |= buffer[0] << 24;
	value |= buffer[1] << 16;
	value |= buffer[2] << 8;
	value |= buffer[3];
	return value;
}

PacketKeyboard deserialize_PacketKeyboard(unsigned char *buffer)
{
	PacketKeyboard outputPacketKeyboard;

	outputPacketKeyboard.keycode = deserialize_int16(buffer);
	outputPacketKeyboard.keyEvent = deserialize_int8(buffer + 2);

	return outputPacketKeyboard;
}