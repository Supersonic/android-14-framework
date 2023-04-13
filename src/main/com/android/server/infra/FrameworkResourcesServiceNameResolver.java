package com.android.server.infra;

import android.content.Context;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class FrameworkResourcesServiceNameResolver extends ServiceNameBaseResolver {
    public final int mArrayResourceId;
    public final int mStringResourceId;

    public FrameworkResourcesServiceNameResolver(Context context, int i) {
        super(context, false);
        this.mStringResourceId = i;
        this.mArrayResourceId = -1;
    }

    public FrameworkResourcesServiceNameResolver(Context context, int i, boolean z) {
        super(context, z);
        if (!z) {
            throw new UnsupportedOperationException("Please use FrameworkResourcesServiceNameResolver(context, @StringRes int) constructor if single service mode is requested.");
        }
        this.mStringResourceId = -1;
        this.mArrayResourceId = i;
    }

    @Override // com.android.server.infra.ServiceNameBaseResolver
    public String[] readServiceNameList(int i) {
        return this.mContext.getResources().getStringArray(this.mArrayResourceId);
    }

    @Override // com.android.server.infra.ServiceNameBaseResolver
    public String readServiceName(int i) {
        return this.mContext.getResources().getString(this.mStringResourceId);
    }

    @Override // com.android.server.infra.ServiceNameResolver
    public void dumpShort(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.print("FrameworkResourcesServiceNamer: resId=");
            printWriter.print(this.mStringResourceId);
            printWriter.print(", numberTemps=");
            printWriter.print(this.mTemporaryServiceNamesList.size());
            printWriter.print(", enabledDefaults=");
            printWriter.print(this.mDefaultServicesDisabled.size());
        }
    }
}
