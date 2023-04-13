package com.android.server.signedconfig;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import com.android.server.signedconfig.SignedConfig;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public class GlobalSettingsConfigApplicator {
    public static final Set<String> ALLOWED_KEYS = Collections.unmodifiableSet(new ArraySet(Arrays.asList("hidden_api_policy", "hidden_api_blacklist_exemptions")));
    public static final Map<String, String> HIDDEN_API_POLICY_KEY_MAP;
    public static final Map<String, Map<String, String>> KEY_VALUE_MAPPERS;
    public final Context mContext;
    public final SignedConfigEvent mEvent;
    public final String mSourcePackage;
    public final SignatureVerifier mVerifier;

    static {
        Map<String, String> makeMap = makeMap("DEFAULT", String.valueOf(-1), "DISABLED", String.valueOf(0), "JUST_WARN", String.valueOf(1), "ENABLED", String.valueOf(2));
        HIDDEN_API_POLICY_KEY_MAP = makeMap;
        KEY_VALUE_MAPPERS = makeMap("hidden_api_policy", makeMap);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <K, V> Map<K, V> makeMap(Object... objArr) {
        if (objArr.length % 2 != 0) {
            throw new IllegalArgumentException();
        }
        int length = objArr.length / 2;
        ArrayMap arrayMap = new ArrayMap(length);
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            arrayMap.put(objArr[i2], objArr[i2 + 1]);
        }
        return Collections.unmodifiableMap(arrayMap);
    }

    public GlobalSettingsConfigApplicator(Context context, String str, SignedConfigEvent signedConfigEvent) {
        this.mContext = context;
        this.mSourcePackage = str;
        this.mEvent = signedConfigEvent;
        this.mVerifier = new SignatureVerifier(signedConfigEvent);
    }

    public final boolean checkSignature(String str, String str2) {
        try {
            return this.mVerifier.verifySignature(str, str2);
        } catch (GeneralSecurityException e) {
            Slog.e("SignedConfig", "Failed to verify signature", e);
            this.mEvent.status = 4;
            return false;
        }
    }

    public final int getCurrentConfigVersion() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "signed_config_version", 0);
    }

    public final void updateCurrentConfig(int i, Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            Settings.Global.putString(this.mContext.getContentResolver(), entry.getKey(), entry.getValue());
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "signed_config_version", i);
    }

    public void applyConfig(String str, String str2) {
        if (!checkSignature(str, str2)) {
            Slog.e("SignedConfig", "Signature check on global settings in package " + this.mSourcePackage + " failed; ignoring");
            return;
        }
        try {
            SignedConfig parse = SignedConfig.parse(str, ALLOWED_KEYS, KEY_VALUE_MAPPERS);
            this.mEvent.version = parse.version;
            int currentConfigVersion = getCurrentConfigVersion();
            if (currentConfigVersion >= parse.version) {
                Slog.i("SignedConfig", "Global settings from package " + this.mSourcePackage + " is older than existing: " + parse.version + "<=" + currentConfigVersion);
                this.mEvent.status = 6;
                return;
            }
            Slog.i("SignedConfig", "Got new global settings from package " + this.mSourcePackage + ": version " + parse.version + " replacing existing version " + currentConfigVersion);
            SignedConfig.PerSdkConfig matchingConfig = parse.getMatchingConfig(Build.VERSION.SDK_INT);
            if (matchingConfig == null) {
                Slog.i("SignedConfig", "Settings is not applicable to current SDK version; ignoring");
                this.mEvent.status = 8;
                return;
            }
            Slog.i("SignedConfig", "Updating global settings to version " + parse.version);
            updateCurrentConfig(parse.version, matchingConfig.values);
            this.mEvent.status = 1;
        } catch (InvalidConfigException e) {
            Slog.e("SignedConfig", "Failed to parse global settings from package " + this.mSourcePackage, e);
            this.mEvent.status = 5;
        }
    }
}
