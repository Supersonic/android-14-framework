package com.android.server.usb.descriptors;

import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;
import com.android.internal.util.dump.DualDumpOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class UsbMidiBlockParser {
    public ArrayList<GroupTerminalBlock> mGroupTerminalBlocks = new ArrayList<>();
    public int mHeaderDescriptorSubtype;
    public int mHeaderDescriptorType;
    public int mHeaderLength;
    public int mTotalLength;

    /* loaded from: classes2.dex */
    public static class GroupTerminalBlock {
        public int mBlockItem;
        public int mDescriptorSubtype;
        public int mDescriptorType;
        public int mGroupBlockId;
        public int mGroupTerminal;
        public int mGroupTerminalBlockType;
        public int mLength;
        public int mMaxInputBandwidth;
        public int mMaxOutputBandwidth;
        public int mMidiProtocol;
        public int mNumGroupTerminals;

        public int parseRawDescriptors(ByteStream byteStream) {
            this.mLength = byteStream.getUnsignedByte();
            this.mDescriptorType = byteStream.getUnsignedByte();
            this.mDescriptorSubtype = byteStream.getUnsignedByte();
            this.mGroupBlockId = byteStream.getUnsignedByte();
            this.mGroupTerminalBlockType = byteStream.getUnsignedByte();
            this.mGroupTerminal = byteStream.getUnsignedByte();
            this.mNumGroupTerminals = byteStream.getUnsignedByte();
            this.mBlockItem = byteStream.getUnsignedByte();
            this.mMidiProtocol = byteStream.getUnsignedByte();
            this.mMaxInputBandwidth = byteStream.unpackUsbShort();
            this.mMaxOutputBandwidth = byteStream.unpackUsbShort();
            return this.mLength;
        }

        public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
            long start = dualDumpOutputStream.start(str, j);
            dualDumpOutputStream.write("length", 1120986464257L, this.mLength);
            dualDumpOutputStream.write("descriptor_type", 1120986464258L, this.mDescriptorType);
            dualDumpOutputStream.write("descriptor_subtype", 1120986464259L, this.mDescriptorSubtype);
            dualDumpOutputStream.write("group_block_id", 1120986464260L, this.mGroupBlockId);
            dualDumpOutputStream.write("group_terminal_block_type", 1120986464261L, this.mGroupTerminalBlockType);
            dualDumpOutputStream.write("group_terminal", 1120986464262L, this.mGroupTerminal);
            dualDumpOutputStream.write("num_group_terminals", 1120986464263L, this.mNumGroupTerminals);
            dualDumpOutputStream.write("block_item", 1120986464264L, this.mBlockItem);
            dualDumpOutputStream.write("midi_protocol", 1120986464265L, this.mMidiProtocol);
            dualDumpOutputStream.write("max_input_bandwidth", 1120986464266L, this.mMaxInputBandwidth);
            dualDumpOutputStream.write("max_output_bandwidth", 1120986464267L, this.mMaxOutputBandwidth);
            dualDumpOutputStream.end(start);
        }
    }

    public int parseRawDescriptors(ByteStream byteStream) {
        this.mHeaderLength = byteStream.getUnsignedByte();
        this.mHeaderDescriptorType = byteStream.getUnsignedByte();
        this.mHeaderDescriptorSubtype = byteStream.getUnsignedByte();
        this.mTotalLength = byteStream.unpackUsbShort();
        while (byteStream.available() >= 13) {
            GroupTerminalBlock groupTerminalBlock = new GroupTerminalBlock();
            groupTerminalBlock.parseRawDescriptors(byteStream);
            this.mGroupTerminalBlocks.add(groupTerminalBlock);
        }
        return this.mTotalLength;
    }

    public int calculateMidiType(UsbDeviceConnection usbDeviceConnection, int i, int i2) {
        byte[] bArr = new byte[5];
        int i3 = i2 + 9728;
        try {
            int controlTransfer = usbDeviceConnection.controlTransfer(129, 6, i3, i, bArr, 5, 2000);
            if (controlTransfer <= 0) {
                Log.e("UsbMidiBlockParser", "first transfer failed: " + controlTransfer);
            } else if (bArr[1] != 38) {
                Log.e("UsbMidiBlockParser", "Incorrect descriptor type: " + ((int) bArr[1]));
                return 1;
            } else if (bArr[2] != 1) {
                Log.e("UsbMidiBlockParser", "Incorrect descriptor subtype: " + ((int) bArr[2]));
                return 1;
            } else {
                int i4 = (bArr[3] & 255) + ((bArr[4] & 255) << 8);
                if (i4 <= 0) {
                    Log.e("UsbMidiBlockParser", "Parsed a non-positive block terminal size: " + i4);
                    return 1;
                }
                byte[] bArr2 = new byte[i4];
                int controlTransfer2 = usbDeviceConnection.controlTransfer(129, 6, i3, i, bArr2, i4, 2000);
                if (controlTransfer2 > 0) {
                    parseRawDescriptors(new ByteStream(bArr2));
                    if (this.mGroupTerminalBlocks.isEmpty()) {
                        Log.e("UsbMidiBlockParser", "Group Terminal Blocks failed parsing: 1");
                        return 1;
                    }
                    Log.d("UsbMidiBlockParser", "MIDI protocol: " + this.mGroupTerminalBlocks.get(0).mMidiProtocol);
                    return this.mGroupTerminalBlocks.get(0).mMidiProtocol;
                }
                Log.e("UsbMidiBlockParser", "second transfer failed: " + controlTransfer2);
            }
        } catch (Exception e) {
            Log.e("UsbMidiBlockParser", "Can not communicate with USB device", e);
        }
        return 1;
    }

    public void dump(DualDumpOutputStream dualDumpOutputStream, String str, long j) {
        long start = dualDumpOutputStream.start(str, j);
        dualDumpOutputStream.write("length", 1120986464257L, this.mHeaderLength);
        dualDumpOutputStream.write("descriptor_type", 1120986464258L, this.mHeaderDescriptorType);
        dualDumpOutputStream.write("descriptor_subtype", 1120986464259L, this.mHeaderDescriptorSubtype);
        dualDumpOutputStream.write("total_length", 1120986464260L, this.mTotalLength);
        Iterator<GroupTerminalBlock> it = this.mGroupTerminalBlocks.iterator();
        while (it.hasNext()) {
            it.next().dump(dualDumpOutputStream, "block", 2246267895813L);
        }
        dualDumpOutputStream.end(start);
    }
}
