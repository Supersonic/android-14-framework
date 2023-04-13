package com.android.server.devicepolicy;

import android.app.admin.ComponentNamePolicyValue;
import android.app.admin.PolicyKey;
import android.content.ComponentName;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ComponentNamePolicySerializer extends PolicySerializer<ComponentName> {
    @Override // com.android.server.devicepolicy.PolicySerializer
    public void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, ComponentName componentName) throws IOException {
        Objects.requireNonNull(componentName);
        typedXmlSerializer.attribute((String) null, str + ":package-name", componentName.getPackageName());
        typedXmlSerializer.attribute((String) null, str + ":class-name", componentName.getClassName());
    }

    @Override // com.android.server.devicepolicy.PolicySerializer
    public ComponentNamePolicyValue readFromXml(TypedXmlPullParser typedXmlPullParser, String str) {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, str + ":package-name");
        String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, str + ":class-name");
        if (attributeValue == null || attributeValue2 == null) {
            Log.e("DevicePolicyEngine", "Error parsing ComponentName policy.");
            return null;
        }
        return new ComponentNamePolicyValue(new ComponentName(attributeValue, attributeValue2));
    }
}
