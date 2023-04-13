package com.android.server.devicepolicy;

import android.app.admin.BooleanPolicyValue;
import android.app.admin.PolicyKey;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class BooleanPolicySerializer extends PolicySerializer<Boolean> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, Boolean bool) throws IOException {
        Objects.requireNonNull(bool);
        typedXmlSerializer.attributeBoolean((String) null, str, bool.booleanValue());
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public BooleanPolicyValue readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        try {
            return new BooleanPolicyValue(typedXmlPullParser.getAttributeBoolean((String) null, str));
        } catch (XmlPullParserException e) {
            Log.e("DevicePolicyEngine", "Error parsing Boolean policy value", e);
            return null;
        }
    }
}
