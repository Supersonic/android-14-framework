package com.android.server.devicepolicy;

import android.app.admin.PolicyKey;
import android.app.admin.PolicyValue;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
/* loaded from: classes.dex */
public abstract class PolicySerializer<V> {
    public abstract PolicyValue<V> readFromXml(TypedXmlPullParser typedXmlPullParser, String str);

    public abstract void saveToXml(PolicyKey policyKey, TypedXmlSerializer typedXmlSerializer, String str, V v) throws IOException;
}
