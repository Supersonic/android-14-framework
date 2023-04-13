package com.android.server.devicepolicy;

import android.app.admin.LockTaskPolicy;
import android.app.admin.PolicyKey;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class LockTaskPolicySerializer extends PolicySerializer<LockTaskPolicy> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, LockTaskPolicy lockTaskPolicy) throws IOException {
        Objects.requireNonNull(lockTaskPolicy);
        if (lockTaskPolicy.getPackages() == null || lockTaskPolicy.getPackages().isEmpty()) {
            throw new IllegalArgumentException("Error saving LockTaskPolicy to file, lock task packages must be present");
        }
        typedXmlSerializer.attribute((String) null, str + ":packages", String.join(";", lockTaskPolicy.getPackages()));
        typedXmlSerializer.attributeInt((String) null, str + ":flags", lockTaskPolicy.getFlags());
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public LockTaskPolicy readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, str + ":packages");
        if (attributeValue == null) {
            Log.e("DevicePolicyEngine", "Error parsing LockTask policy value.");
            return null;
        }
        Set of = Set.of((Object[]) attributeValue.split(";"));
        try {
            return new LockTaskPolicy(of, typedXmlPullParser.getAttributeInt((String) null, str + ":flags"));
        } catch (XmlPullParserException e) {
            Log.e("DevicePolicyEngine", "Error parsing LockTask policy value", e);
            return null;
        }
    }
}
