package com.android.internal.telephony.gsm;

import android.telephony.PhoneNumberUtils;
import com.android.internal.midi.MidiConstants;
import com.android.internal.telephony.GsmAlphabet;
import com.android.internal.telephony.SmsAddress;
import java.text.ParseException;
/* loaded from: classes3.dex */
public class GsmSmsAddress extends SmsAddress {
    static final int OFFSET_ADDRESS_LENGTH = 0;
    static final int OFFSET_ADDRESS_VALUE = 2;
    static final int OFFSET_TOA = 1;

    public GsmSmsAddress(byte[] data, int offset, int length) throws ParseException {
        this.origBytes = new byte[length];
        System.arraycopy(data, offset, this.origBytes, 0, length);
        int addressLength = this.origBytes[0] & 255;
        int toa = this.origBytes[1] & 255;
        this.ton = (toa >> 4) & 7;
        if ((toa & 128) != 128) {
            throw new ParseException("Invalid TOA - high bit must be set. toa = " + toa, offset + 1);
        }
        if (isAlphanumeric()) {
            int countSeptets = (addressLength * 4) / 7;
            this.address = GsmAlphabet.gsm7BitPackedToString(this.origBytes, 2, countSeptets);
            return;
        }
        byte lastByte = this.origBytes[length - 1];
        if ((addressLength & 1) == 1) {
            byte[] bArr = this.origBytes;
            int i = length - 1;
            bArr[i] = (byte) (bArr[i] | 240);
        }
        this.address = PhoneNumberUtils.calledPartyBCDToString(this.origBytes, 1, length - 1, 2);
        this.origBytes[length - 1] = lastByte;
    }

    @Override // com.android.internal.telephony.SmsAddress
    public String getAddressString() {
        return this.address;
    }

    @Override // com.android.internal.telephony.SmsAddress
    public boolean isAlphanumeric() {
        return this.ton == 5;
    }

    @Override // com.android.internal.telephony.SmsAddress
    public boolean isNetworkSpecific() {
        return this.ton == 3;
    }

    public boolean isCphsVoiceMessageIndicatorAddress() {
        return (this.origBytes[0] & 255) == 4 && isAlphanumeric() && (this.origBytes[1] & MidiConstants.STATUS_CHANNEL_MASK) == 0;
    }

    public boolean isCphsVoiceMessageSet() {
        return isCphsVoiceMessageIndicatorAddress() && (this.origBytes[2] & 255) == 17;
    }

    public boolean isCphsVoiceMessageClear() {
        return isCphsVoiceMessageIndicatorAddress() && (this.origBytes[2] & 255) == 16;
    }
}
