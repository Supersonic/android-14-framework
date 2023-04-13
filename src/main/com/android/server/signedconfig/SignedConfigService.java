package com.android.server.signedconfig;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Slog;
import com.android.server.LocalServices;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
/* loaded from: classes2.dex */
public class SignedConfigService {
    public final Context mContext;
    public final PackageManagerInternal mPacMan = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);

    /* loaded from: classes2.dex */
    public static class UpdateReceiver extends BroadcastReceiver {
        public UpdateReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            new SignedConfigService(context).handlePackageBroadcast(intent);
        }
    }

    public SignedConfigService(Context context) {
        this.mContext = context;
    }

    public void handlePackageBroadcast(Intent intent) {
        Uri data = intent.getData();
        String schemeSpecificPart = data == null ? null : data.getSchemeSpecificPart();
        if (schemeSpecificPart == null) {
            return;
        }
        int identifier = this.mContext.getUser().getIdentifier();
        PackageInfo packageInfo = this.mPacMan.getPackageInfo(schemeSpecificPart, 128L, 1000, identifier);
        if (packageInfo == null) {
            Slog.w("SignedConfig", "Got null PackageInfo for " + schemeSpecificPart + "; user " + identifier);
            return;
        }
        Bundle bundle = packageInfo.applicationInfo.metaData;
        if (bundle != null && bundle.containsKey("android.settings.global") && bundle.containsKey("android.settings.global.signature")) {
            SignedConfigEvent signedConfigEvent = new SignedConfigEvent();
            try {
                signedConfigEvent.type = 1;
                signedConfigEvent.fromPackage = schemeSpecificPart;
                String string = bundle.getString("android.settings.global");
                new GlobalSettingsConfigApplicator(this.mContext, schemeSpecificPart, signedConfigEvent).applyConfig(new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8), bundle.getString("android.settings.global.signature"));
            } catch (IllegalArgumentException unused) {
                Slog.e("SignedConfig", "Failed to base64 decode global settings config from " + schemeSpecificPart);
                signedConfigEvent.status = 2;
            } finally {
                signedConfigEvent.send();
            }
        }
    }

    public static void registerUpdateReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        context.registerReceiver(new UpdateReceiver(), intentFilter);
    }
}
