struct PacketKeyboard
{
	_int8 version;
	_int8 type;
	_int32 sequence;
	_int8 controllerID;
	_int16 keycode;
	_int8 keyEvent;
};

struct PacketMouse
{
	_int8 version;
	_int8 type;
	_int32 sequence;
	_int8 controllerID;
	_int8 button;
	_int8 mouseEvent;
};

struct PacketQuit
{
	_int8 version;
	_int8 type;
	_int32 sequence;
	_int8 controllerID;
};