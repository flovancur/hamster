package de.hsrm.cs.wwwvs.hamster.server;

import java.nio.ByteBuffer;



public class ServerHeader {
    byte version,flag;
    short messageId, payloadLength, rpcCall;

    ServerHeader(ByteBuffer buff){
        version = buff.get();
        flag = buff.get();
        messageId = buff.getShort();
        payloadLength = buff.getShort();
        rpcCall = buff.getShort();
    }
}
