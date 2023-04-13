package com.android.server.devicepolicy;

import android.app.admin.LongPolicyValue;
import android.app.admin.PolicyKey;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class LongPolicySerializer extends PolicySerializer<Long> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, Long l) throws IOException {
        Objects.requireNonNull(l);
        typedXmlSerializer.attributeLong((String) null, str, l.longValue());
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public LongPolicyValue readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        try {
            return new LongPolicyValue(typedXmlPullParser.getAttributeLong((String) null, str));
        } catch (XmlPullParserException e) {
            Log.e("DevicePolicyEngine", "Error parsing Long policy value", e);
            return null;
        }
    }
}
