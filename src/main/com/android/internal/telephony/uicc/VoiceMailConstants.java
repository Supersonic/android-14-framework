package com.android.internal.telephony.uicc;

import android.compat.annotation.UnsupportedAppUsage;
import android.os.Environment;
import android.util.Xml;
import com.android.internal.telephony.util.XmlUtils;
import com.android.telephony.Rlog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
class VoiceMailConstants {
    private HashMap<String, String[]> CarrierVmMap = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public VoiceMailConstants() {
        loadVoiceMail();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean containsCarrier(String str) {
        return this.CarrierVmMap.containsKey(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getVoiceMailNumber(String str) {
        return this.CarrierVmMap.get(str)[1];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getVoiceMailTag(String str) {
        return this.CarrierVmMap.get(str)[2];
    }

    private void loadVoiceMail() {
        XmlPullParser newPullParser;
        try {
            FileReader fileReader = new FileReader(new File(Environment.getRootDirectory(), "etc/voicemail-conf.xml"));
            try {
                try {
                    newPullParser = Xml.newPullParser();
                    newPullParser.setInput(fileReader);
                    XmlUtils.beginDocument(newPullParser, "voicemail");
                } catch (Throwable th) {
                    try {
                        fileReader.close();
                    } catch (IOException unused) {
                    }
                    throw th;
                }
            } catch (IOException e) {
                Rlog.w("VoiceMailConstants", "Exception in Voicemail parser " + e);
            } catch (XmlPullParserException e2) {
                Rlog.w("VoiceMailConstants", "Exception in Voicemail parser " + e2);
            }
            while (true) {
                XmlUtils.nextElement(newPullParser);
                if ("voicemail".equals(newPullParser.getName())) {
                    this.CarrierVmMap.put(newPullParser.getAttributeValue(null, "numeric"), new String[]{newPullParser.getAttributeValue(null, "carrier"), newPullParser.getAttributeValue(null, "vmnumber"), newPullParser.getAttributeValue(null, "vmtag")});
                }
                try {
                    break;
                } catch (IOException unused2) {
                    return;
                }
            }
            fileReader.close();
        } catch (FileNotFoundException unused3) {
            Rlog.w("VoiceMailConstants", "Can't open " + Environment.getRootDirectory() + "/etc/voicemail-conf.xml");
        }
    }
}
