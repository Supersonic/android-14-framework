package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class DelayedMessageBuffer {
    public final ArrayList<HdmiCecMessage> mBuffer = new ArrayList<>();
    public final HdmiCecLocalDevice mDevice;

    public DelayedMessageBuffer(HdmiCecLocalDevice hdmiCecLocalDevice) {
        this.mDevice = hdmiCecLocalDevice;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0024  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void add(HdmiCecMessage hdmiCecMessage) {
        boolean z;
        int opcode = hdmiCecMessage.getOpcode();
        if (opcode != 114) {
            if (opcode == 130) {
                removeActiveSource();
                this.mBuffer.add(hdmiCecMessage);
                z = true;
                if (z) {
                }
            } else if (opcode != 192) {
                z = false;
                if (z) {
                    HdmiLogger.debug("Buffering message:" + hdmiCecMessage, new Object[0]);
                    return;
                }
                return;
            }
        }
        this.mBuffer.add(hdmiCecMessage);
        z = true;
        if (z) {
        }
    }

    public void removeActiveSource() {
        Iterator<HdmiCecMessage> it = this.mBuffer.iterator();
        while (it.hasNext()) {
            if (it.next().getOpcode() == 130) {
                it.remove();
            }
        }
    }

    public boolean isBuffered(int i) {
        Iterator<HdmiCecMessage> it = this.mBuffer.iterator();
        while (it.hasNext()) {
            if (it.next().getOpcode() == i) {
                return true;
            }
        }
        return false;
    }

    public void processAllMessages() {
        ArrayList arrayList = new ArrayList(this.mBuffer);
        this.mBuffer.clear();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            HdmiCecMessage hdmiCecMessage = (HdmiCecMessage) it.next();
            this.mDevice.onMessage(hdmiCecMessage);
            HdmiLogger.debug("Processing message:" + hdmiCecMessage, new Object[0]);
        }
    }

    public void processMessagesForDevice(int i) {
        ArrayList arrayList = new ArrayList(this.mBuffer);
        this.mBuffer.clear();
        HdmiLogger.debug("Checking message for address:" + i, new Object[0]);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            HdmiCecMessage hdmiCecMessage = (HdmiCecMessage) it.next();
            if (hdmiCecMessage.getSource() != i) {
                this.mBuffer.add(hdmiCecMessage);
            } else if (hdmiCecMessage.getOpcode() == 130 && !this.mDevice.isInputReady(HdmiDeviceInfo.idForCecDevice(i))) {
                this.mBuffer.add(hdmiCecMessage);
            } else {
                this.mDevice.onMessage(hdmiCecMessage);
                HdmiLogger.debug("Processing message:" + hdmiCecMessage, new Object[0]);
            }
        }
    }

    public void processActiveSource(int i) {
        ArrayList arrayList = new ArrayList(this.mBuffer);
        this.mBuffer.clear();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            HdmiCecMessage hdmiCecMessage = (HdmiCecMessage) it.next();
            if (hdmiCecMessage.getOpcode() == 130 && hdmiCecMessage.getSource() == i) {
                this.mDevice.onMessage(hdmiCecMessage);
                HdmiLogger.debug("Processing message:" + hdmiCecMessage, new Object[0]);
            } else {
                this.mBuffer.add(hdmiCecMessage);
            }
        }
    }
}
