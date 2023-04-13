package com.android.server.devicepolicy;

import android.app.admin.PolicyKey;
import android.app.admin.PolicyValue;
import android.app.admin.StringSetPolicyValue;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class StringSetPolicySerializer extends PolicySerializer<Set<String>> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, Set<String> set) throws IOException {
        Objects.requireNonNull(set);
        typedXmlSerializer.attribute((String) null, str + ":strings", String.join(";", set));
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public PolicyValue<Set<String>> readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, str + ":strings");
        if (attributeValue == null) {
            Log.e("DevicePolicyEngine", "Error parsing StringSet policy value.");
            return null;
        }
        return new StringSetPolicyValue(Set.of((Object[]) attributeValue.split(";")));
    }
}
