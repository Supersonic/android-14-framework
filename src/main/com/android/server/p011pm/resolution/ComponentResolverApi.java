package com.android.server.p011pm.resolution;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.Computer;
import com.android.server.p011pm.DumpState;
import com.android.server.p011pm.pkg.component.ParsedActivity;
import com.android.server.p011pm.pkg.component.ParsedProvider;
import com.android.server.p011pm.pkg.component.ParsedService;
import java.io.PrintWriter;
import java.util.List;
/* renamed from: com.android.server.pm.resolution.ComponentResolverApi */
/* loaded from: classes2.dex */
public interface ComponentResolverApi {
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    boolean componentExists(ComponentName componentName);

    void dumpActivityResolvers(PrintWriter printWriter, DumpState dumpState, String str);

    void dumpContentProviders(Computer computer, PrintWriter printWriter, DumpState dumpState, String str);

    void dumpProviderResolvers(PrintWriter printWriter, DumpState dumpState, String str);

    void dumpReceiverResolvers(PrintWriter printWriter, DumpState dumpState, String str);

    void dumpServicePermissions(PrintWriter printWriter, DumpState dumpState);

    void dumpServiceResolvers(PrintWriter printWriter, DumpState dumpState, String str);

    ParsedActivity getActivity(ComponentName componentName);

    ParsedProvider getProvider(ComponentName componentName);

    ParsedActivity getReceiver(ComponentName componentName);

    ParsedService getService(ComponentName componentName);

    List<ResolveInfo> queryActivities(Computer computer, Intent intent, String str, long j, int i);

    List<ResolveInfo> queryActivities(Computer computer, Intent intent, String str, long j, List<ParsedActivity> list, int i);

    ProviderInfo queryProvider(Computer computer, String str, long j, int i);

    List<ResolveInfo> queryProviders(Computer computer, Intent intent, String str, long j, int i);

    List<ResolveInfo> queryProviders(Computer computer, Intent intent, String str, long j, List<ParsedProvider> list, int i);

    List<ProviderInfo> queryProviders(Computer computer, String str, String str2, int i, long j, int i2);

    List<ResolveInfo> queryReceivers(Computer computer, Intent intent, String str, long j, int i);

    List<ResolveInfo> queryReceivers(Computer computer, Intent intent, String str, long j, List<ParsedActivity> list, int i);

    List<ResolveInfo> queryServices(Computer computer, Intent intent, String str, long j, int i);

    List<ResolveInfo> queryServices(Computer computer, Intent intent, String str, long j, List<ParsedService> list, int i);

    void querySyncProviders(Computer computer, List<String> list, List<ProviderInfo> list2, boolean z, int i);
}
