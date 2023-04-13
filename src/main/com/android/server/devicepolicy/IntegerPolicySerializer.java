package com.android.server.devicepolicy;

import android.app.admin.IntegerPolicyValue;
import android.app.admin.PolicyKey;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class IntegerPolicySerializer extends PolicySerializer<Integer> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, Integer num) throws IOException {
        Objects.requireNonNull(num);
        typedXmlSerializer.attributeInt((String) null, str, num.intValue());
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public IntegerPolicyValue readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        try {
            return new IntegerPolicyValue(typedXmlPullParser.getAttributeInt((String) null, str));
        } catch (XmlPullParserException e) {
            Log.e("DevicePolicyEngine", "Error parsing Integer policy value", e);
            return null;
        }
    }
}
