package com.vx6.tools;

public class MessagePart {
    private int DCS;
    private String UDH;
    private String MsgPart;

    public MessagePart(int DCS, String UDH, String msgPart) {
        this.DCS = DCS;
        this.UDH = UDH;
        MsgPart = msgPart;
    }

    public int getDCS() {
        return DCS;
    }

    public void setDCS(int DCS) {
        this.DCS = DCS;
    }

    public String getUDH() {
        return UDH;
    }

    public void setUDH(String UDH) {
        this.UDH = UDH;
    }

    public String getMsgPart() {
        return MsgPart;
    }

    public void setMsgPart(String msgPart) {
        MsgPart = msgPart;
    }
}
