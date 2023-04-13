package com.android.internal.telephony.cat;

import com.android.internal.telephony.GsmAlphabet;
import java.io.ByteArrayOutputStream;
/* compiled from: ResponseData.java */
/* loaded from: classes.dex */
class LanguageResponseData extends ResponseData {
    private String mLang;

    public LanguageResponseData(String str) {
        this.mLang = str;
    }

    @Override // com.android.internal.telephony.cat.ResponseData
    public void format(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream == null) {
            return;
        }
        byteArrayOutputStream.write(ComprehensionTlvTag.LANGUAGE.value() | 128);
        String str = this.mLang;
        byte[] stringToGsm8BitPacked = (str == null || str.length() <= 0) ? new byte[0] : GsmAlphabet.stringToGsm8BitPacked(this.mLang);
        byteArrayOutputStream.write(stringToGsm8BitPacked.length);
        for (byte b : stringToGsm8BitPacked) {
            byteArrayOutputStream.write(b);
        }
    }
}
