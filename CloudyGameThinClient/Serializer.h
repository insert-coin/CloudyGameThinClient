
unsigned char * serialize_int8(unsigned char *buffer, int value);
unsigned char * serialize_int16(unsigned char *buffer, int value);
unsigned char * serialize_int32(unsigned char *buffer, int value);
unsigned char * serialize_char(unsigned char *buffer, char value);

unsigned char * serialize_PacketKeyboard(unsigned char *buffer, struct PacketKeyboard *value);
