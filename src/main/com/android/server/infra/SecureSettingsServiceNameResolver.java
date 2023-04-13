package com.android.server.infra;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public final class SecureSettingsServiceNameResolver extends ServiceNameBaseResolver {
    public final String mProperty;
    public final TextUtils.SimpleStringSplitter mStringColonSplitter;

    public SecureSettingsServiceNameResolver(Context context, String str) {
        this(context, str, false);
    }

    public SecureSettingsServiceNameResolver(Context context, String str, boolean z) {
        super(context, z);
        this.mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        this.mProperty = str;
    }

    @Override // com.android.server.infra.ServiceNameResolver
    public void dumpShort(PrintWriter printWriter) {
        printWriter.print("SecureSettingsServiceNamer: prop=");
        printWriter.print(this.mProperty);
    }

    @Override // com.android.server.infra.ServiceNameBaseResolver, com.android.server.infra.ServiceNameResolver
    public void dumpShort(PrintWriter printWriter, int i) {
        printWriter.print("defaultService=");
        printWriter.print(getDefaultServiceName(i));
    }

    @Override // com.android.server.infra.ServiceNameBaseResolver
    public String toString() {
        return "SecureSettingsServiceNameResolver[" + this.mProperty + "]";
    }

    @Override // com.android.server.infra.ServiceNameBaseResolver
    public String[] readServiceNameList(int i) {
        return parseColonDelimitedServiceNames(Settings.Secure.getStringForUser(this.mContext.getContentResolver(), this.mProperty, i));
    }

    @Override // com.android.server.infra.ServiceNameBaseResolver
    public String readServiceName(int i) {
        return Settings.Secure.getStringForUser(this.mContext.getContentResolver(), this.mProperty, i);
    }

    @Override // com.android.server.infra.ServiceNameResolver
    public void setServiceNameList(List<String> list, int i) {
        if (list == null || list.isEmpty()) {
            Settings.Secure.putStringForUser(this.mContext.getContentResolver(), this.mProperty, null, i);
            return;
        }
        StringBuilder sb = new StringBuilder(list.get(0));
        for (int i2 = 1; i2 < list.size(); i2++) {
            sb.append(':');
            sb.append(list.get(i2));
        }
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), this.mProperty, sb.toString(), i);
    }

    public final String[] parseColonDelimitedServiceNames(String str) {
        ArraySet arraySet = new ArraySet();
        if (!TextUtils.isEmpty(str)) {
            TextUtils.SimpleStringSplitter simpleStringSplitter = this.mStringColonSplitter;
            simpleStringSplitter.setString(str);
            while (simpleStringSplitter.hasNext()) {
                String next = simpleStringSplitter.next();
                if (!TextUtils.isEmpty(next)) {
                    arraySet.add(next);
                }
            }
        }
        return (String[]) arraySet.toArray(new String[arraySet.size()]);
    }
}
