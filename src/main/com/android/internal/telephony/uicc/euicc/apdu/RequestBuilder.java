package com.android.internal.telephony.uicc.euicc.apdu;

import com.android.internal.telephony.PhoneConfigurationManager;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RequestBuilder {
    private final int mChannel;
    private final List<ApduCommand> mCommands = new ArrayList();
    private final int mMaxApduDataLen;

    public void addApdu(int i, int i2, int i3, int i4, int i5, String str) {
        this.mCommands.add(new ApduCommand(this.mChannel, i, i2, i3, i4, i5, str));
    }

    public void addApdu(int i, int i2, int i3, int i4, String str) {
        this.mCommands.add(new ApduCommand(this.mChannel, i, i2, i3, i4, str.length() / 2, str));
    }

    public void addApdu(int i, int i2, int i3, int i4) {
        this.mCommands.add(new ApduCommand(this.mChannel, i, i2, i3, i4, 0, PhoneConfigurationManager.SSSS));
    }

    public void addStoreData(String str) {
        int i;
        int i2 = this.mMaxApduDataLen * 2;
        int length = str.length() / 2;
        if (length == 0) {
            i = 1;
        } else {
            int i3 = this.mMaxApduDataLen;
            i = ((length + i3) - 1) / i3;
        }
        int i4 = 0;
        int i5 = 1;
        while (i5 < i) {
            int i6 = i4 + i2;
            addApdu(128, 226, 17, i5 - 1, str.substring(i4, i6));
            i5++;
            i4 = i6;
        }
        addApdu(128, 226, 145, i - 1, str.substring(i4));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<ApduCommand> getCommands() {
        return this.mCommands;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RequestBuilder(int i, boolean z) {
        this.mChannel = i;
        this.mMaxApduDataLen = z ? 65535 : 255;
    }
}
