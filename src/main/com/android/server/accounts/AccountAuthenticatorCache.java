package com.android.server.accounts;

import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.XmlSerializerAndParser;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.android.internal.R;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AccountAuthenticatorCache extends RegisteredServicesCache<AuthenticatorDescription> implements IAccountAuthenticatorCache {
    public static final MySerializer sSerializer = new MySerializer();

    @Override // com.android.server.accounts.IAccountAuthenticatorCache
    public /* bridge */ /* synthetic */ RegisteredServicesCache.ServiceInfo getServiceInfo(AuthenticatorDescription authenticatorDescription, int i) {
        return super.getServiceInfo(authenticatorDescription, i);
    }

    public AccountAuthenticatorCache(Context context) {
        super(context, "android.accounts.AccountAuthenticator", "android.accounts.AccountAuthenticator", "account-authenticator", sSerializer);
    }

    /* renamed from: parseServiceAttributes */
    public AuthenticatorDescription m876parseServiceAttributes(Resources resources, String str, AttributeSet attributeSet) {
        TypedArray obtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.AccountAuthenticator);
        try {
            String string = obtainAttributes.getString(2);
            int resourceId = obtainAttributes.getResourceId(0, 0);
            int resourceId2 = obtainAttributes.getResourceId(1, 0);
            int resourceId3 = obtainAttributes.getResourceId(3, 0);
            int resourceId4 = obtainAttributes.getResourceId(4, 0);
            boolean z = obtainAttributes.getBoolean(5, false);
            if (!TextUtils.isEmpty(string)) {
                return new AuthenticatorDescription(string, str, resourceId, resourceId2, resourceId3, resourceId4, z);
            }
            obtainAttributes.recycle();
            return null;
        } finally {
            obtainAttributes.recycle();
        }
    }

    /* loaded from: classes.dex */
    public static class MySerializer implements XmlSerializerAndParser<AuthenticatorDescription> {
        public MySerializer() {
        }

        public void writeAsXml(AuthenticatorDescription authenticatorDescription, TypedXmlSerializer typedXmlSerializer) throws IOException {
            typedXmlSerializer.attribute((String) null, "type", authenticatorDescription.type);
        }

        /* renamed from: createFromXml */
        public AuthenticatorDescription m877createFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
            return AuthenticatorDescription.newKey(typedXmlPullParser.getAttributeValue((String) null, "type"));
        }
    }
}
