package com.android.server.biometrics.sensors.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.Fingerprint;
import com.android.internal.annotations.GuardedBy;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.biometrics.sensors.BiometricUserState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class FingerprintUserState extends BiometricUserState<Fingerprint> {
    @Override // com.android.server.biometrics.sensors.BiometricUserState
    public String getBiometricsTag() {
        return "fingerprints";
    }

    @Override // com.android.server.biometrics.sensors.BiometricUserState
    public int getNameTemplateResource() {
        return 17040337;
    }

    public FingerprintUserState(Context context, int i, String str) {
        super(context, i, str);
    }

    @Override // com.android.server.biometrics.sensors.BiometricUserState
    public ArrayList<Fingerprint> getCopy(ArrayList<Fingerprint> arrayList) {
        ArrayList<Fingerprint> arrayList2 = new ArrayList<>();
        Iterator<Fingerprint> it = arrayList.iterator();
        while (it.hasNext()) {
            Fingerprint next = it.next();
            arrayList2.add(new Fingerprint(next.getName(), next.getGroupId(), next.getBiometricId(), next.getDeviceId()));
        }
        return arrayList2;
    }

    @Override // com.android.server.biometrics.sensors.BiometricUserState
    public void doWriteState(TypedXmlSerializer typedXmlSerializer) throws Exception {
        ArrayList<Fingerprint> copy;
        synchronized (this) {
            copy = getCopy(this.mBiometrics);
        }
        typedXmlSerializer.startTag((String) null, "fingerprints");
        int size = copy.size();
        for (int i = 0; i < size; i++) {
            Fingerprint fingerprint = copy.get(i);
            typedXmlSerializer.startTag((String) null, "fingerprint");
            typedXmlSerializer.attributeInt((String) null, "fingerId", fingerprint.getBiometricId());
            typedXmlSerializer.attribute((String) null, "name", fingerprint.getName().toString());
            typedXmlSerializer.attributeInt((String) null, "groupId", fingerprint.getGroupId());
            typedXmlSerializer.attributeLong((String) null, "deviceId", fingerprint.getDeviceId());
            typedXmlSerializer.endTag((String) null, "fingerprint");
        }
        typedXmlSerializer.endTag((String) null, "fingerprints");
    }

    @Override // com.android.server.biometrics.sensors.BiometricUserState
    @GuardedBy({"this"})
    public void parseBiometricsLocked(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4 && typedXmlPullParser.getName().equals("fingerprint")) {
                this.mBiometrics.add(new Fingerprint(typedXmlPullParser.getAttributeValue((String) null, "name"), typedXmlPullParser.getAttributeInt((String) null, "groupId"), typedXmlPullParser.getAttributeInt((String) null, "fingerId"), typedXmlPullParser.getAttributeLong((String) null, "deviceId")));
            }
        }
    }
}
