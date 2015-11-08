#include "Datagrams.h"

unsigned char * serialize_int8(unsigned char *buffer, int value)
{
	/* Write big-endian int value into buffer; assumes 8-bit int and 8-bit char. */
	buffer[0] = value;
	return buffer + 1;
}

unsigned char * serialize_int16(unsigned char *buffer, int value)
{
	/* Write big-endian int value into buffer; assumes 16-bit int and 8-bit char. */
	buffer[0] = value >> 8;
	buffer[1] = value;
	return buffer + 2;
}

unsigned char * serialize_int32(unsigned char *buffer, int value)
{
	/* Write big-endian int value into buffer; assumes 32-bit int and 8-bit char. */
	buffer[0] = value >> 24;
	buffer[1] = value >> 16;
	buffer[2] = value >> 8;
	buffer[3] = value;
	return buffer + 4;
}

unsigned char * serialize_char(unsigned char *buffer, char value)
{
	buffer[0] = value;
	return buffer + 1;
}

unsigned char * serialize_PacketKeyboard(unsigned char *buffer, struct PacketKeyboard *value)
{
	buffer = serialize_int16(buffer, value->keycode);
	buffer = serialize_int8(buffer, value->keyEvent);

	return buffer;
}

