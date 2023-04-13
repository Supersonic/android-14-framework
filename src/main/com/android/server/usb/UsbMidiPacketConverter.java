package com.android.server.usb;

import android.util.Log;
import java.io.ByteArrayOutputStream;
/* loaded from: classes2.dex */
public class UsbMidiPacketConverter {
    public ByteArrayOutputStream mEncoderOutputStream = new ByteArrayOutputStream();
    public UsbMidiDecoder mUsbMidiDecoder;
    public UsbMidiEncoder[] mUsbMidiEncoders;
    public static final int[] PAYLOAD_SIZE = {-1, -1, 2, 3, 3, 1, 2, 3, 3, 3, 3, 3, 2, 2, 3, 1};
    public static final int[] CODE_INDEX_NUMBER_FROM_SYSTEM_TYPE = {-1, 2, 3, 2, -1, -1, 5, -1, 5, -1, 5, 5, 5, -1, 5, 5};

    public void createEncoders(int i) {
        this.mUsbMidiEncoders = new UsbMidiEncoder[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.mUsbMidiEncoders[i2] = new UsbMidiEncoder(i2);
        }
    }

    public void encodeMidiPackets(byte[] bArr, int i, int i2) {
        if (i2 >= this.mUsbMidiEncoders.length) {
            Log.w("UsbMidiPacketConverter", "encoderId " + i2 + " invalid");
            i2 = 0;
        }
        byte[] encode = this.mUsbMidiEncoders[i2].encode(bArr, i);
        this.mEncoderOutputStream.write(encode, 0, encode.length);
    }

    public byte[] pullEncodedMidiPackets() {
        byte[] byteArray = this.mEncoderOutputStream.toByteArray();
        this.mEncoderOutputStream.reset();
        return byteArray;
    }

    public void createDecoders(int i) {
        this.mUsbMidiDecoder = new UsbMidiDecoder(i);
    }

    public void decodeMidiPackets(byte[] bArr, int i) {
        this.mUsbMidiDecoder.decode(bArr, i);
    }

    public byte[] pullDecodedMidiPackets(int i) {
        return this.mUsbMidiDecoder.pullBytes(i);
    }

    /* loaded from: classes2.dex */
    public class UsbMidiDecoder {
        public ByteArrayOutputStream[] mDecodedByteArrays;
        public int mNumJacks;

        public UsbMidiDecoder(int i) {
            this.mNumJacks = i;
            this.mDecodedByteArrays = new ByteArrayOutputStream[i];
            for (int i2 = 0; i2 < i; i2++) {
                this.mDecodedByteArrays[i2] = new ByteArrayOutputStream();
            }
        }

        public void decode(byte[] bArr, int i) {
            new ByteArrayOutputStream();
            if (i % 4 != 0) {
                Log.w("UsbMidiPacketConverter", "size " + i + " not multiple of 4");
            }
            for (int i2 = 0; i2 + 3 < i; i2 += 4) {
                byte b = bArr[i2];
                int i3 = (b >> 4) & 15;
                int i4 = UsbMidiPacketConverter.PAYLOAD_SIZE[b & 15];
                if (i4 >= 0) {
                    if (i3 >= this.mNumJacks) {
                        Log.w("UsbMidiPacketConverter", "cableNumber " + i3 + " invalid");
                        i3 = 0;
                    }
                    this.mDecodedByteArrays[i3].write(bArr, i2 + 1, i4);
                }
            }
        }

        public byte[] pullBytes(int i) {
            if (i >= this.mNumJacks) {
                Log.w("UsbMidiPacketConverter", "cableNumber " + i + " invalid");
                i = 0;
            }
            byte[] byteArray = this.mDecodedByteArrays[i].toByteArray();
            this.mDecodedByteArrays[i].reset();
            return byteArray;
        }
    }

    /* loaded from: classes2.dex */
    public class UsbMidiEncoder {
        public byte mShiftedCableNumber;
        public byte[] mStoredSystemExclusiveBytes = new byte[3];
        public int mNumStoredSystemExclusiveBytes = 0;
        public boolean mHasSystemExclusiveStarted = false;
        public byte[] mEmptyBytes = new byte[3];

        public UsbMidiEncoder(int i) {
            this.mShiftedCableNumber = (byte) (i << 4);
        }

        public byte[] encode(byte[] bArr, int i) {
            int i2;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i3 = 0;
            while (i3 < i) {
                byte b = bArr[i3];
                if (b >= 0) {
                    if (this.mHasSystemExclusiveStarted) {
                        byte[] bArr2 = this.mStoredSystemExclusiveBytes;
                        int i4 = this.mNumStoredSystemExclusiveBytes;
                        bArr2[i4] = b;
                        int i5 = i4 + 1;
                        this.mNumStoredSystemExclusiveBytes = i5;
                        if (i5 == 3) {
                            byteArrayOutputStream.write(this.mShiftedCableNumber | 4);
                            byteArrayOutputStream.write(this.mStoredSystemExclusiveBytes, 0, 3);
                            this.mNumStoredSystemExclusiveBytes = 0;
                        }
                    } else {
                        writeSingleByte(byteArrayOutputStream, b);
                    }
                } else {
                    if (b != -9 && this.mHasSystemExclusiveStarted) {
                        for (int i6 = 0; i6 < this.mNumStoredSystemExclusiveBytes; i6++) {
                            writeSingleByte(byteArrayOutputStream, this.mStoredSystemExclusiveBytes[i6]);
                        }
                        this.mNumStoredSystemExclusiveBytes = 0;
                        this.mHasSystemExclusiveStarted = false;
                    }
                    byte b2 = bArr[i3];
                    if (b2 < -16) {
                        byte b3 = (byte) ((b2 >> 4) & 15);
                        int i7 = UsbMidiPacketConverter.PAYLOAD_SIZE[b3];
                        i2 = i3 + i7;
                        if (i2 <= i) {
                            byteArrayOutputStream.write(b3 | this.mShiftedCableNumber);
                            byteArrayOutputStream.write(bArr, i3, i7);
                            byteArrayOutputStream.write(this.mEmptyBytes, 0, 3 - i7);
                            i3 = i2;
                        } else {
                            while (i3 < i) {
                                writeSingleByte(byteArrayOutputStream, bArr[i3]);
                                i3++;
                            }
                        }
                    } else if (b2 == -16) {
                        this.mHasSystemExclusiveStarted = true;
                        this.mStoredSystemExclusiveBytes[0] = b2;
                        this.mNumStoredSystemExclusiveBytes = 1;
                    } else if (b2 == -9) {
                        byteArrayOutputStream.write((this.mNumStoredSystemExclusiveBytes + 5) | this.mShiftedCableNumber);
                        byte[] bArr3 = this.mStoredSystemExclusiveBytes;
                        int i8 = this.mNumStoredSystemExclusiveBytes;
                        bArr3[i8] = bArr[i3];
                        int i9 = i8 + 1;
                        this.mNumStoredSystemExclusiveBytes = i9;
                        byteArrayOutputStream.write(bArr3, 0, i9);
                        byteArrayOutputStream.write(this.mEmptyBytes, 0, 3 - this.mNumStoredSystemExclusiveBytes);
                        this.mHasSystemExclusiveStarted = false;
                        this.mNumStoredSystemExclusiveBytes = 0;
                    } else {
                        int i10 = UsbMidiPacketConverter.CODE_INDEX_NUMBER_FROM_SYSTEM_TYPE[b2 & 15];
                        if (i10 < 0) {
                            writeSingleByte(byteArrayOutputStream, bArr[i3]);
                        } else {
                            int i11 = UsbMidiPacketConverter.PAYLOAD_SIZE[i10];
                            i2 = i3 + i11;
                            if (i2 <= i) {
                                byteArrayOutputStream.write(i10 | this.mShiftedCableNumber);
                                byteArrayOutputStream.write(bArr, i3, i11);
                                byteArrayOutputStream.write(this.mEmptyBytes, 0, 3 - i11);
                                i3 = i2;
                            } else {
                                while (i3 < i) {
                                    writeSingleByte(byteArrayOutputStream, bArr[i3]);
                                    i3++;
                                }
                            }
                        }
                    }
                }
                i3++;
            }
            return byteArrayOutputStream.toByteArray();
        }

        public final void writeSingleByte(ByteArrayOutputStream byteArrayOutputStream, byte b) {
            byteArrayOutputStream.write(this.mShiftedCableNumber | 15);
            byteArrayOutputStream.write(b);
            byteArrayOutputStream.write(0);
            byteArrayOutputStream.write(0);
        }
    }
}
