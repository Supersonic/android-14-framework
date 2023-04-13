package com.android.internal.telephony.uicc;

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
public class CarrierTestOverride {
    private HashMap<String, String> mCarrierTestParamMap = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public CarrierTestOverride(int i) {
        loadCarrierTestOverrides(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInTestMode() {
        return this.mCarrierTestParamMap.containsKey("isInTestMode") && this.mCarrierTestParamMap.get("isInTestMode").equals("true");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFakeSpn() {
        try {
            String str = this.mCarrierTestParamMap.get("spn");
            Rlog.d("CarrierTestOverride", "reading spn from CarrierTestConfig file: " + str);
            return str;
        } catch (NullPointerException unused) {
            Rlog.w("CarrierTestOverride", "No spn in CarrierTestConfig file ");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFakeIMSI() {
        try {
            String str = this.mCarrierTestParamMap.get("imsi");
            Rlog.d("CarrierTestOverride", "reading imsi from CarrierTestConfig file: " + str);
            return str;
        } catch (NullPointerException unused) {
            Rlog.w("CarrierTestOverride", "No imsi in CarrierTestConfig file ");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFakeGid1() {
        try {
            String str = this.mCarrierTestParamMap.get("gid1");
            Rlog.d("CarrierTestOverride", "reading gid1 from CarrierTestConfig file: " + str);
            return str;
        } catch (NullPointerException unused) {
            Rlog.w("CarrierTestOverride", "No gid1 in CarrierTestConfig file ");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFakeGid2() {
        try {
            String str = this.mCarrierTestParamMap.get("gid2");
            Rlog.d("CarrierTestOverride", "reading gid2 from CarrierTestConfig file: " + str);
            return str;
        } catch (NullPointerException unused) {
            Rlog.w("CarrierTestOverride", "No gid2 in CarrierTestConfig file ");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFakePnnHomeName() {
        try {
            String str = this.mCarrierTestParamMap.get("pnn");
            Rlog.d("CarrierTestOverride", "reading pnn from CarrierTestConfig file: " + str);
            return str;
        } catch (NullPointerException unused) {
            Rlog.w("CarrierTestOverride", "No pnn in CarrierTestConfig file ");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getFakeIccid() {
        try {
            String str = this.mCarrierTestParamMap.get("iccid");
            Rlog.d("CarrierTestOverride", "reading iccid from CarrierTestConfig file: " + str);
            return str;
        } catch (NullPointerException unused) {
            Rlog.w("CarrierTestOverride", "No iccid in CarrierTestConfig file ");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void override(String str, String str2, String str3, String str4, String str5, String str6, String str7) {
        this.mCarrierTestParamMap.put("isInTestMode", "true");
        this.mCarrierTestParamMap.put("mccmnc", str);
        this.mCarrierTestParamMap.put("imsi", str2);
        this.mCarrierTestParamMap.put("iccid", str3);
        this.mCarrierTestParamMap.put("gid1", str4);
        this.mCarrierTestParamMap.put("gid2", str5);
        this.mCarrierTestParamMap.put("pnn", str6);
        this.mCarrierTestParamMap.put("spn", str7);
    }

    private void loadCarrierTestOverrides(int i) {
        File file;
        String str = "/user_de/0/com.android.phone/files/carrier_test_conf_sim" + Integer.toString(i) + ".xml";
        Rlog.d("CarrierTestOverride", "File path : " + str);
        try {
            FileReader fileReader = new FileReader(new File(Environment.getDataDirectory(), str));
            Rlog.d("CarrierTestOverride", "CarrierTestConfig file Modified Timestamp: " + file.lastModified());
            try {
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(fileReader);
                XmlUtils.beginDocument(newPullParser, "carrierTestOverrides");
                while (true) {
                    XmlUtils.nextElement(newPullParser);
                    if ("carrierTestOverride".equals(newPullParser.getName())) {
                        String attributeValue = newPullParser.getAttributeValue(null, "key");
                        String attributeValue2 = newPullParser.getAttributeValue(null, "value");
                        Rlog.d("CarrierTestOverride", "extracting key-values from CarrierTestConfig file: " + attributeValue + "|" + attributeValue2);
                        this.mCarrierTestParamMap.put(attributeValue, attributeValue2);
                    } else {
                        fileReader.close();
                        return;
                    }
                }
            } catch (IOException e) {
                Rlog.w("CarrierTestOverride", "Exception in carrier_test_conf parser " + e);
            } catch (XmlPullParserException e2) {
                Rlog.w("CarrierTestOverride", "Exception in carrier_test_conf parser " + e2);
            }
        } catch (FileNotFoundException unused) {
            Rlog.w("CarrierTestOverride", "Can not open " + file.getAbsolutePath());
        }
    }
}
