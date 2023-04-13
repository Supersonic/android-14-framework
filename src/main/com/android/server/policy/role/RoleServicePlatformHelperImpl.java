package com.android.server.policy.role;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.CollectionUtils;
import com.android.server.LocalServices;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.role.RoleServicePlatformHelper;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import libcore.util.HexEncoding;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class RoleServicePlatformHelperImpl implements RoleServicePlatformHelper {
    public static final String LOG_TAG = "RoleServicePlatformHelperImpl";
    public final Context mContext;

    public RoleServicePlatformHelperImpl(Context context) {
        this.mContext = context;
    }

    @Override // com.android.server.role.RoleServicePlatformHelper
    public Map<String, Set<String>> getLegacyRoleState(int i) {
        Map<String, Set<String>> readFile = readFile(i);
        return readFile == null ? readFromLegacySettings(i) : readFile;
    }

    public final Map<String, Set<String>> readFile(int i) {
        File file = getFile(i);
        try {
            try {
                FileInputStream openRead = new AtomicFile(file).openRead();
                try {
                    XmlPullParser newPullParser = Xml.newPullParser();
                    newPullParser.setInput(openRead, null);
                    Map<String, Set<String>> parseXml = parseXml(newPullParser);
                    Slog.i(LOG_TAG, "Read legacy roles.xml successfully");
                    if (openRead != null) {
                        openRead.close();
                    }
                    return parseXml;
                } catch (Throwable th) {
                    if (openRead != null) {
                        try {
                            openRead.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException | XmlPullParserException e) {
                String str = LOG_TAG;
                Slog.wtf(str, "Failed to parse legacy roles.xml: " + file, e);
                return null;
            }
        } catch (FileNotFoundException unused) {
            Slog.i(LOG_TAG, "Legacy roles.xml not found");
            return null;
        }
    }

    public final Map<String, Set<String>> parseXml(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        int depth;
        int depth2 = xmlPullParser.getDepth() + 1;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || ((depth = xmlPullParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2 && xmlPullParser.getName().equals("roles")) {
                return parseRoles(xmlPullParser);
            }
        }
        throw new IOException("Missing <roles> in roles.xml");
    }

    public final Map<String, Set<String>> parseRoles(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        int depth;
        ArrayMap arrayMap = new ArrayMap();
        int depth2 = xmlPullParser.getDepth() + 1;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || ((depth = xmlPullParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2 && xmlPullParser.getName().equals("role")) {
                arrayMap.put(xmlPullParser.getAttributeValue(null, "name"), parseRoleHoldersLocked(xmlPullParser));
            }
        }
        return arrayMap;
    }

    public final Set<String> parseRoleHoldersLocked(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        int depth;
        ArraySet arraySet = new ArraySet();
        int depth2 = xmlPullParser.getDepth() + 1;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || ((depth = xmlPullParser.getDepth()) < depth2 && next == 3)) {
                break;
            } else if (depth <= depth2 && next == 2 && xmlPullParser.getName().equals("holder")) {
                arraySet.add(xmlPullParser.getAttributeValue(null, "name"));
            }
        }
        return arraySet;
    }

    public static File getFile(int i) {
        return new File(Environment.getUserSystemDirectory(i), "roles.xml");
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x003e, code lost:
        if (android.text.TextUtils.isEmpty(r2) == false) goto L8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Map<String, Set<String>> readFromLegacySettings(int i) {
        String string;
        ActivityInfo activityInfo;
        ComponentName unflattenFromString;
        ArrayMap arrayMap = new ArrayMap();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        String stringForUser = Settings.Secure.getStringForUser(contentResolver, "assistant", i);
        PackageManager packageManager = this.mContext.getPackageManager();
        String str = null;
        if (stringForUser != null) {
            if (!stringForUser.isEmpty() && (unflattenFromString = ComponentName.unflattenFromString(stringForUser)) != null) {
                string = unflattenFromString.getPackageName();
            }
            string = null;
        } else {
            if (packageManager.isDeviceUpgrading()) {
                string = this.mContext.getString(17039393);
            }
            string = null;
        }
        if (string != null) {
            arrayMap.put("android.app.role.ASSISTANT", Collections.singleton(string));
        }
        String removeLegacyDefaultBrowserPackageName = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).removeLegacyDefaultBrowserPackageName(i);
        if (removeLegacyDefaultBrowserPackageName != null) {
            arrayMap.put("android.app.role.BROWSER", Collections.singleton(removeLegacyDefaultBrowserPackageName));
        }
        String stringForUser2 = Settings.Secure.getStringForUser(contentResolver, "dialer_default_application", i);
        if (TextUtils.isEmpty(stringForUser2)) {
            stringForUser2 = packageManager.isDeviceUpgrading() ? this.mContext.getString(17039395) : null;
        }
        if (stringForUser2 != null) {
            arrayMap.put("android.app.role.DIALER", Collections.singleton(stringForUser2));
        }
        String stringForUser3 = Settings.Secure.getStringForUser(contentResolver, "sms_default_application", i);
        if (TextUtils.isEmpty(stringForUser3)) {
            stringForUser3 = this.mContext.getPackageManager().isDeviceUpgrading() ? this.mContext.getString(17039396) : null;
        }
        if (stringForUser3 != null) {
            arrayMap.put("android.app.role.SMS", Collections.singleton(stringForUser3));
        }
        if (packageManager.isDeviceUpgrading()) {
            ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 851968, i);
            String str2 = (resolveActivityAsUser == null || (activityInfo = resolveActivityAsUser.activityInfo) == null) ? null : activityInfo.packageName;
            if (str2 == null || !isSettingsApplication(str2, i)) {
                str = str2;
            }
        }
        if (str != null) {
            arrayMap.put("android.app.role.HOME", Collections.singleton(str));
        }
        String stringForUser4 = Settings.Secure.getStringForUser(contentResolver, "emergency_assistance_application", i);
        if (stringForUser4 != null) {
            arrayMap.put("android.app.role.EMERGENCY", Collections.singleton(stringForUser4));
        }
        return arrayMap;
    }

    public final boolean isSettingsApplication(String str, int i) {
        ActivityInfo activityInfo;
        ResolveInfo resolveActivityAsUser = this.mContext.getPackageManager().resolveActivityAsUser(new Intent("android.settings.SETTINGS"), 851968, i);
        if (resolveActivityAsUser == null || (activityInfo = resolveActivityAsUser.activityInfo) == null) {
            return false;
        }
        return Objects.equals(str, activityInfo.packageName);
    }

    @Override // com.android.server.role.RoleServicePlatformHelper
    public String computePackageStateHash(final int i) {
        final PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        MessageDigestOutputStream messageDigestOutputStream = new MessageDigestOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(messageDigestOutputStream));
        packageManagerInternal.forEachInstalledPackage(new Consumer() { // from class: com.android.server.policy.role.RoleServicePlatformHelperImpl$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                RoleServicePlatformHelperImpl.lambda$computePackageStateHash$0(dataOutputStream, packageManagerInternal, i, (AndroidPackage) obj);
            }
        }, i);
        return messageDigestOutputStream.getDigestAsString();
    }

    public static /* synthetic */ void lambda$computePackageStateHash$0(DataOutputStream dataOutputStream, PackageManagerInternal packageManagerInternal, int i, AndroidPackage androidPackage) {
        try {
            dataOutputStream.writeUTF(androidPackage.getPackageName());
            dataOutputStream.writeLong(androidPackage.getLongVersionCode());
            dataOutputStream.writeInt(packageManagerInternal.getApplicationEnabledState(androidPackage.getPackageName(), i));
            List<String> requestedPermissions = androidPackage.getRequestedPermissions();
            int size = requestedPermissions.size();
            dataOutputStream.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                dataOutputStream.writeUTF(requestedPermissions.get(i2));
            }
            ArraySet<String> enabledComponents = packageManagerInternal.getEnabledComponents(androidPackage.getPackageName(), i);
            int size2 = CollectionUtils.size(enabledComponents);
            dataOutputStream.writeInt(size2);
            for (int i3 = 0; i3 < size2; i3++) {
                dataOutputStream.writeUTF(enabledComponents.valueAt(i3));
            }
            ArraySet<String> disabledComponents = packageManagerInternal.getDisabledComponents(androidPackage.getPackageName(), i);
            int size3 = CollectionUtils.size(disabledComponents);
            for (int i4 = 0; i4 < size3; i4++) {
                dataOutputStream.writeUTF(disabledComponents.valueAt(i4));
            }
            for (Signature signature : androidPackage.getSigningDetails().getSignatures()) {
                dataOutputStream.write(signature.toByteArray());
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    /* loaded from: classes2.dex */
    public static class MessageDigestOutputStream extends OutputStream {
        public final MessageDigest mMessageDigest;

        public MessageDigestOutputStream() {
            try {
                this.mMessageDigest = MessageDigest.getInstance("SHA256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Failed to create MessageDigest", e);
            }
        }

        public String getDigestAsString() {
            return HexEncoding.encodeToString(this.mMessageDigest.digest(), true);
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            this.mMessageDigest.update((byte) i);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            this.mMessageDigest.update(bArr);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            this.mMessageDigest.update(bArr, i, i2);
        }
    }
}
