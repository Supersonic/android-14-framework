package com.android.server.firewall;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Intent;
import android.os.RemoteException;
import android.os.UserHandle;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SenderPackageFilter implements Filter {
    public static final FilterFactory FACTORY = new FilterFactory("sender-package") { // from class: com.android.server.firewall.SenderPackageFilter.1
        @Override // com.android.server.firewall.FilterFactory
        public Filter newFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            String attributeValue = xmlPullParser.getAttributeValue(null, "name");
            if (attributeValue == null) {
                throw new XmlPullParserException("A package name must be specified.", xmlPullParser, null);
            }
            return new SenderPackageFilter(attributeValue);
        }
    };
    public final String mPackageName;

    public SenderPackageFilter(String str) {
        this.mPackageName = str;
    }

    @Override // com.android.server.firewall.Filter
    public boolean matches(IntentFirewall intentFirewall, ComponentName componentName, Intent intent, int i, int i2, String str, int i3) {
        int i4;
        try {
            i4 = AppGlobals.getPackageManager().getPackageUid(this.mPackageName, 4194304L, 0);
        } catch (RemoteException unused) {
            i4 = -1;
        }
        if (i4 == -1) {
            return false;
        }
        return UserHandle.isSameApp(i4, i);
    }
}
