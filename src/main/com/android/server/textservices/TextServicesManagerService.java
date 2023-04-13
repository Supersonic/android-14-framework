package com.android.server.textservices;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.inputmethod.SubtypeLocaleUtils;
import com.android.internal.textservice.ISpellCheckerService;
import com.android.internal.textservice.ISpellCheckerServiceCallback;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager;
import com.android.internal.textservice.ITextServicesSessionListener;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.textservices.TextServicesManagerService;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class TextServicesManagerService extends ITextServicesManager.Stub {
    public static final String TAG = TextServicesManagerService.class.getSimpleName();
    public final Context mContext;
    public final TextServicesMonitor mMonitor;
    public final UserManager mUserManager;
    public final SparseArray<TextServicesData> mUserData = new SparseArray<>();
    public final Object mLock = new Object();

    /* loaded from: classes2.dex */
    public static class TextServicesData {
        public final Context mContext;
        public final ContentResolver mResolver;
        public final int mUserId;
        public int mUpdateCount = 0;
        public final HashMap<String, SpellCheckerInfo> mSpellCheckerMap = new HashMap<>();
        public final ArrayList<SpellCheckerInfo> mSpellCheckerList = new ArrayList<>();
        public final HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups = new HashMap<>();

        public TextServicesData(int i, Context context) {
            this.mUserId = i;
            this.mContext = context;
            this.mResolver = context.getContentResolver();
        }

        public final void putString(String str, String str2) {
            Settings.Secure.putStringForUser(this.mResolver, str, str2, this.mUserId);
        }

        public final String getString(String str, String str2) {
            String stringForUser = Settings.Secure.getStringForUser(this.mResolver, str, this.mUserId);
            return stringForUser != null ? stringForUser : str2;
        }

        public final void putInt(String str, int i) {
            Settings.Secure.putIntForUser(this.mResolver, str, i, this.mUserId);
        }

        public final int getInt(String str, int i) {
            return Settings.Secure.getIntForUser(this.mResolver, str, i, this.mUserId);
        }

        public final boolean getBoolean(String str, boolean z) {
            return getInt(str, z ? 1 : 0) == 1;
        }

        public final void putSelectedSpellChecker(String str) {
            putString("selected_spell_checker", str);
        }

        public final void putSelectedSpellCheckerSubtype(int i) {
            putInt("selected_spell_checker_subtype", i);
        }

        public final String getSelectedSpellChecker() {
            return getString("selected_spell_checker", "");
        }

        public int getSelectedSpellCheckerSubtype(int i) {
            return getInt("selected_spell_checker_subtype", i);
        }

        public boolean isSpellCheckerEnabled() {
            return getBoolean("spell_checker_enabled", true);
        }

        public SpellCheckerInfo getCurrentSpellChecker() {
            String selectedSpellChecker = getSelectedSpellChecker();
            if (TextUtils.isEmpty(selectedSpellChecker)) {
                return null;
            }
            return this.mSpellCheckerMap.get(selectedSpellChecker);
        }

        public void setCurrentSpellChecker(SpellCheckerInfo spellCheckerInfo) {
            if (spellCheckerInfo != null) {
                putSelectedSpellChecker(spellCheckerInfo.getId());
            } else {
                putSelectedSpellChecker("");
            }
            putSelectedSpellCheckerSubtype(0);
        }

        public final void initializeTextServicesData() {
            this.mSpellCheckerList.clear();
            this.mSpellCheckerMap.clear();
            this.mUpdateCount++;
            List queryIntentServicesAsUser = this.mContext.getPackageManager().queryIntentServicesAsUser(new Intent("android.service.textservice.SpellCheckerService"), 128, this.mUserId);
            int size = queryIntentServicesAsUser.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = (ResolveInfo) queryIntentServicesAsUser.get(i);
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                if (!"android.permission.BIND_TEXT_SERVICE".equals(serviceInfo.permission)) {
                    Slog.w(TextServicesManagerService.TAG, "Skipping text service " + componentName + ": it does not require the permission android.permission.BIND_TEXT_SERVICE");
                } else {
                    try {
                        SpellCheckerInfo spellCheckerInfo = new SpellCheckerInfo(this.mContext, resolveInfo);
                        if (spellCheckerInfo.getSubtypeCount() <= 0) {
                            Slog.w(TextServicesManagerService.TAG, "Skipping text service " + componentName + ": it does not contain subtypes.");
                        } else {
                            this.mSpellCheckerList.add(spellCheckerInfo);
                            this.mSpellCheckerMap.put(spellCheckerInfo.getId(), spellCheckerInfo);
                        }
                    } catch (IOException e) {
                        Slog.w(TextServicesManagerService.TAG, "Unable to load the spell checker " + componentName, e);
                    } catch (XmlPullParserException e2) {
                        Slog.w(TextServicesManagerService.TAG, "Unable to load the spell checker " + componentName, e2);
                    }
                }
            }
        }

        public final void dump(PrintWriter printWriter) {
            printWriter.println("  User #" + this.mUserId);
            printWriter.println("  Spell Checkers:");
            printWriter.println("  Spell Checkers: mUpdateCount=" + this.mUpdateCount);
            int i = 0;
            for (SpellCheckerInfo spellCheckerInfo : this.mSpellCheckerMap.values()) {
                printWriter.println("  Spell Checker #" + i);
                spellCheckerInfo.dump(printWriter, "    ");
                i++;
            }
            printWriter.println("");
            printWriter.println("  Spell Checker Bind Groups:");
            for (Map.Entry<String, SpellCheckerBindGroup> entry : this.mSpellCheckerBindGroups.entrySet()) {
                SpellCheckerBindGroup value = entry.getValue();
                printWriter.println("    " + entry.getKey() + " " + value + XmlUtils.STRING_ARRAY_SEPARATOR);
                StringBuilder sb = new StringBuilder();
                sb.append("      mInternalConnection=");
                sb.append(value.mInternalConnection);
                printWriter.println(sb.toString());
                printWriter.println("      mSpellChecker=" + value.mSpellChecker);
                printWriter.println("      mUnbindCalled=" + value.mUnbindCalled);
                printWriter.println("      mConnected=" + value.mConnected);
                int size = value.mPendingSessionRequests.size();
                for (int i2 = 0; i2 < size; i2++) {
                    SessionRequest sessionRequest = (SessionRequest) value.mPendingSessionRequests.get(i2);
                    printWriter.println("      Pending Request #" + i2 + XmlUtils.STRING_ARRAY_SEPARATOR);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("        mTsListener=");
                    sb2.append(sessionRequest.mTsListener);
                    printWriter.println(sb2.toString());
                    printWriter.println("        mScListener=" + sessionRequest.mScListener);
                    printWriter.println("        mScLocale=" + sessionRequest.mLocale + " mUid=" + sessionRequest.mUid);
                }
                int size2 = value.mOnGoingSessionRequests.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    SessionRequest sessionRequest2 = (SessionRequest) value.mOnGoingSessionRequests.get(i3);
                    printWriter.println("      On going Request #" + i3 + XmlUtils.STRING_ARRAY_SEPARATOR);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("        mTsListener=");
                    sb3.append(sessionRequest2.mTsListener);
                    printWriter.println(sb3.toString());
                    printWriter.println("        mScListener=" + sessionRequest2.mScListener);
                    printWriter.println("        mScLocale=" + sessionRequest2.mLocale + " mUid=" + sessionRequest2.mUid);
                }
                int registeredCallbackCount = value.mListeners.getRegisteredCallbackCount();
                for (int i4 = 0; i4 < registeredCallbackCount; i4++) {
                    printWriter.println("      Listener #" + i4 + XmlUtils.STRING_ARRAY_SEPARATOR);
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("        mScListener=");
                    sb4.append(value.mListeners.getRegisteredCallbackItem(i4));
                    printWriter.println(sb4.toString());
                    printWriter.println("        mGroup=" + value);
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Lifecycle extends SystemService {
        public TextServicesManagerService mService;

        public Lifecycle(Context context) {
            super(context);
            this.mService = new TextServicesManagerService(context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            LocalServices.addService(TextServicesManagerInternal.class, new TextServicesManagerInternal() { // from class: com.android.server.textservices.TextServicesManagerService.Lifecycle.1
                @Override // com.android.server.textservices.TextServicesManagerInternal
                public SpellCheckerInfo getCurrentSpellCheckerForUser(int i) {
                    return Lifecycle.this.mService.getCurrentSpellCheckerForUser(i);
                }
            });
            publishBinderService("textservices", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            this.mService.onStopUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mService.onUnlockUser(targetUser.getUserIdentifier());
        }
    }

    public void onStopUser(int i) {
        synchronized (this.mLock) {
            TextServicesData textServicesData = this.mUserData.get(i);
            if (textServicesData == null) {
                return;
            }
            unbindServiceLocked(textServicesData);
            this.mUserData.remove(i);
        }
    }

    public void onUnlockUser(int i) {
        synchronized (this.mLock) {
            initializeInternalStateLocked(i);
        }
    }

    public TextServicesManagerService(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        TextServicesMonitor textServicesMonitor = new TextServicesMonitor();
        this.mMonitor = textServicesMonitor;
        textServicesMonitor.register(context, (Looper) null, UserHandle.ALL, true);
    }

    @GuardedBy({"mLock"})
    public final void initializeInternalStateLocked(int i) {
        TextServicesData textServicesData = this.mUserData.get(i);
        if (textServicesData == null) {
            textServicesData = new TextServicesData(i, this.mContext);
            this.mUserData.put(i, textServicesData);
        }
        textServicesData.initializeTextServicesData();
        if (textServicesData.getCurrentSpellChecker() == null) {
            setCurrentSpellCheckerLocked(findAvailSystemSpellCheckerLocked(null, textServicesData), textServicesData);
        }
    }

    /* loaded from: classes2.dex */
    public final class TextServicesMonitor extends PackageMonitor {
        public TextServicesMonitor() {
        }

        public void onSomePackagesChanged() {
            SpellCheckerInfo findAvailSystemSpellCheckerLocked;
            int changingUserId = getChangingUserId();
            synchronized (TextServicesManagerService.this.mLock) {
                TextServicesData textServicesData = (TextServicesData) TextServicesManagerService.this.mUserData.get(changingUserId);
                if (textServicesData == null) {
                    return;
                }
                SpellCheckerInfo currentSpellChecker = textServicesData.getCurrentSpellChecker();
                textServicesData.initializeTextServicesData();
                if (textServicesData.isSpellCheckerEnabled()) {
                    if (currentSpellChecker == null) {
                        TextServicesManagerService.this.setCurrentSpellCheckerLocked(TextServicesManagerService.this.findAvailSystemSpellCheckerLocked(null, textServicesData), textServicesData);
                    } else {
                        String packageName = currentSpellChecker.getPackageName();
                        int isPackageDisappearing = isPackageDisappearing(packageName);
                        if ((isPackageDisappearing == 3 || isPackageDisappearing == 2) && ((findAvailSystemSpellCheckerLocked = TextServicesManagerService.this.findAvailSystemSpellCheckerLocked(packageName, textServicesData)) == null || !findAvailSystemSpellCheckerLocked.getId().equals(currentSpellChecker.getId()))) {
                            TextServicesManagerService.this.setCurrentSpellCheckerLocked(findAvailSystemSpellCheckerLocked, textServicesData);
                        }
                    }
                }
            }
        }
    }

    public final boolean bindCurrentSpellCheckerService(Intent intent, ServiceConnection serviceConnection, int i, int i2) {
        if (intent == null || serviceConnection == null) {
            String str = TAG;
            Slog.e(str, "--- bind failed: service = " + intent + ", conn = " + serviceConnection + ", userId =" + i2);
            return false;
        }
        return this.mContext.bindServiceAsUser(intent, serviceConnection, i, UserHandle.of(i2));
    }

    public final void unbindServiceLocked(TextServicesData textServicesData) {
        HashMap hashMap = textServicesData.mSpellCheckerBindGroups;
        for (SpellCheckerBindGroup spellCheckerBindGroup : hashMap.values()) {
            spellCheckerBindGroup.removeAllLocked();
        }
        hashMap.clear();
    }

    public final SpellCheckerInfo findAvailSystemSpellCheckerLocked(String str, TextServicesData textServicesData) {
        ArrayList arrayList = new ArrayList();
        Iterator it = textServicesData.mSpellCheckerList.iterator();
        while (it.hasNext()) {
            SpellCheckerInfo spellCheckerInfo = (SpellCheckerInfo) it.next();
            if ((1 & spellCheckerInfo.getServiceInfo().applicationInfo.flags) != 0) {
                arrayList.add(spellCheckerInfo);
            }
        }
        int size = arrayList.size();
        if (size == 0) {
            Slog.w(TAG, "no available spell checker services found");
            return null;
        }
        if (str != null) {
            for (int i = 0; i < size; i++) {
                SpellCheckerInfo spellCheckerInfo2 = (SpellCheckerInfo) arrayList.get(i);
                if (str.equals(spellCheckerInfo2.getPackageName())) {
                    return spellCheckerInfo2;
                }
            }
        }
        ArrayList<Locale> suitableLocalesForSpellChecker = LocaleUtils.getSuitableLocalesForSpellChecker(this.mContext.getResources().getConfiguration().locale);
        int size2 = suitableLocalesForSpellChecker.size();
        for (int i2 = 0; i2 < size2; i2++) {
            Locale locale = suitableLocalesForSpellChecker.get(i2);
            for (int i3 = 0; i3 < size; i3++) {
                SpellCheckerInfo spellCheckerInfo3 = (SpellCheckerInfo) arrayList.get(i3);
                int subtypeCount = spellCheckerInfo3.getSubtypeCount();
                for (int i4 = 0; i4 < subtypeCount; i4++) {
                    if (locale.equals(SubtypeLocaleUtils.constructLocaleFromString(spellCheckerInfo3.getSubtypeAt(i4).getLocale()))) {
                        return spellCheckerInfo3;
                    }
                }
            }
        }
        if (size > 1) {
            Slog.w(TAG, "more than one spell checker service found, picking first");
        }
        return (SpellCheckerInfo) arrayList.get(0);
    }

    public final SpellCheckerInfo getCurrentSpellCheckerForUser(int i) {
        SpellCheckerInfo currentSpellChecker;
        synchronized (this.mLock) {
            TextServicesData textServicesData = this.mUserData.get(i);
            currentSpellChecker = textServicesData != null ? textServicesData.getCurrentSpellChecker() : null;
        }
        return currentSpellChecker;
    }

    public SpellCheckerInfo getCurrentSpellChecker(int i, String str) {
        verifyUser(i);
        synchronized (this.mLock) {
            TextServicesData dataFromCallingUserIdLocked = getDataFromCallingUserIdLocked(i);
            if (dataFromCallingUserIdLocked == null) {
                return null;
            }
            return dataFromCallingUserIdLocked.getCurrentSpellChecker();
        }
    }

    public SpellCheckerSubtype getCurrentSpellCheckerSubtype(int i, boolean z) {
        verifyUser(i);
        synchronized (this.mLock) {
            TextServicesData dataFromCallingUserIdLocked = getDataFromCallingUserIdLocked(i);
            SpellCheckerSubtype spellCheckerSubtype = null;
            if (dataFromCallingUserIdLocked == null) {
                return null;
            }
            int i2 = 0;
            int selectedSpellCheckerSubtype = dataFromCallingUserIdLocked.getSelectedSpellCheckerSubtype(0);
            SpellCheckerInfo currentSpellChecker = dataFromCallingUserIdLocked.getCurrentSpellChecker();
            Locale locale = this.mContext.getResources().getConfiguration().locale;
            if (currentSpellChecker != null && currentSpellChecker.getSubtypeCount() != 0) {
                if (selectedSpellCheckerSubtype == 0 && !z) {
                    return null;
                }
                int subtypeCount = currentSpellChecker.getSubtypeCount();
                if (selectedSpellCheckerSubtype != 0) {
                    while (i2 < subtypeCount) {
                        SpellCheckerSubtype subtypeAt = currentSpellChecker.getSubtypeAt(i2);
                        if (subtypeAt.hashCode() == selectedSpellCheckerSubtype) {
                            return subtypeAt;
                        }
                        i2++;
                    }
                    return null;
                } else if (locale == null) {
                    return null;
                } else {
                    while (i2 < currentSpellChecker.getSubtypeCount()) {
                        SpellCheckerSubtype subtypeAt2 = currentSpellChecker.getSubtypeAt(i2);
                        Locale localeObject = subtypeAt2.getLocaleObject();
                        if (Objects.equals(localeObject, locale)) {
                            return subtypeAt2;
                        }
                        if (spellCheckerSubtype == null && localeObject != null && TextUtils.equals(locale.getLanguage(), localeObject.getLanguage())) {
                            spellCheckerSubtype = subtypeAt2;
                        }
                        i2++;
                    }
                }
            }
            return spellCheckerSubtype;
        }
    }

    public void getSpellCheckerService(int i, String str, String str2, ITextServicesSessionListener iTextServicesSessionListener, ISpellCheckerSessionListener iSpellCheckerSessionListener, Bundle bundle, int i2) {
        verifyUser(i);
        if (TextUtils.isEmpty(str) || iTextServicesSessionListener == null || iSpellCheckerSessionListener == null) {
            Slog.e(TAG, "getSpellCheckerService: Invalid input.");
            return;
        }
        synchronized (this.mLock) {
            TextServicesData dataFromCallingUserIdLocked = getDataFromCallingUserIdLocked(i);
            if (dataFromCallingUserIdLocked == null) {
                return;
            }
            HashMap hashMap = dataFromCallingUserIdLocked.mSpellCheckerMap;
            if (hashMap.containsKey(str)) {
                SpellCheckerInfo spellCheckerInfo = (SpellCheckerInfo) hashMap.get(str);
                int callingUid = Binder.getCallingUid();
                if (canCallerAccessSpellChecker(spellCheckerInfo, callingUid, i)) {
                    SpellCheckerBindGroup spellCheckerBindGroup = (SpellCheckerBindGroup) dataFromCallingUserIdLocked.mSpellCheckerBindGroups.get(str);
                    if (spellCheckerBindGroup == null) {
                        long clearCallingIdentity = Binder.clearCallingIdentity();
                        spellCheckerBindGroup = startSpellCheckerServiceInnerLocked(spellCheckerInfo, dataFromCallingUserIdLocked);
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        if (spellCheckerBindGroup == null) {
                            return;
                        }
                    }
                    spellCheckerBindGroup.getISpellCheckerSessionOrQueueLocked(new SessionRequest(callingUid, str2, iTextServicesSessionListener, iSpellCheckerSessionListener, bundle, i2));
                }
            }
        }
    }

    public boolean isSpellCheckerEnabled(int i) {
        verifyUser(i);
        synchronized (this.mLock) {
            TextServicesData dataFromCallingUserIdLocked = getDataFromCallingUserIdLocked(i);
            if (dataFromCallingUserIdLocked == null) {
                return false;
            }
            return dataFromCallingUserIdLocked.isSpellCheckerEnabled();
        }
    }

    public final SpellCheckerBindGroup startSpellCheckerServiceInnerLocked(SpellCheckerInfo spellCheckerInfo, TextServicesData textServicesData) {
        String id = spellCheckerInfo.getId();
        InternalServiceConnection internalServiceConnection = new InternalServiceConnection(id, textServicesData.mSpellCheckerBindGroups);
        Intent intent = new Intent("android.service.textservice.SpellCheckerService");
        intent.setComponent(spellCheckerInfo.getComponent());
        if (!bindCurrentSpellCheckerService(intent, internalServiceConnection, 8388609, textServicesData.mUserId)) {
            Slog.e(TAG, "Failed to get a spell checker service.");
            return null;
        }
        SpellCheckerBindGroup spellCheckerBindGroup = new SpellCheckerBindGroup(internalServiceConnection);
        textServicesData.mSpellCheckerBindGroups.put(id, spellCheckerBindGroup);
        return spellCheckerBindGroup;
    }

    public SpellCheckerInfo[] getEnabledSpellCheckers(int i) {
        verifyUser(i);
        synchronized (this.mLock) {
            TextServicesData dataFromCallingUserIdLocked = getDataFromCallingUserIdLocked(i);
            if (dataFromCallingUserIdLocked == null) {
                return null;
            }
            ArrayList arrayList = new ArrayList(dataFromCallingUserIdLocked.mSpellCheckerList);
            int size = arrayList.size();
            int callingUid = Binder.getCallingUid();
            for (int i2 = size - 1; i2 >= 0; i2--) {
                if (!canCallerAccessSpellChecker((SpellCheckerInfo) arrayList.get(i2), callingUid, i)) {
                    arrayList.remove(i2);
                }
            }
            if (arrayList.isEmpty()) {
                return null;
            }
            return (SpellCheckerInfo[]) arrayList.toArray(new SpellCheckerInfo[arrayList.size()]);
        }
    }

    public void finishSpellCheckerService(int i, ISpellCheckerSessionListener iSpellCheckerSessionListener) {
        verifyUser(i);
        synchronized (this.mLock) {
            TextServicesData dataFromCallingUserIdLocked = getDataFromCallingUserIdLocked(i);
            if (dataFromCallingUserIdLocked == null) {
                return;
            }
            ArrayList arrayList = new ArrayList();
            for (SpellCheckerBindGroup spellCheckerBindGroup : dataFromCallingUserIdLocked.mSpellCheckerBindGroups.values()) {
                if (spellCheckerBindGroup != null) {
                    arrayList.add(spellCheckerBindGroup);
                }
            }
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                ((SpellCheckerBindGroup) arrayList.get(i2)).removeListener(iSpellCheckerSessionListener);
            }
        }
    }

    public final void verifyUser(int i) {
        int callingUserId = UserHandle.getCallingUserId();
        if (i != callingUserId) {
            Context context = this.mContext;
            context.enforceCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "Cross-user interaction requires INTERACT_ACROSS_USERS_FULL. userId=" + i + " callingUserId=" + callingUserId);
        }
    }

    public final boolean canCallerAccessSpellChecker(SpellCheckerInfo spellCheckerInfo, int i, int i2) {
        SpellCheckerInfo currentSpellCheckerForUser = getCurrentSpellCheckerForUser(i2);
        if (currentSpellCheckerForUser == null || !currentSpellCheckerForUser.getId().equals(spellCheckerInfo.getId())) {
            return !((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).filterAppAccess(spellCheckerInfo.getPackageName(), i, i2);
        }
        return true;
    }

    public final void setCurrentSpellCheckerLocked(SpellCheckerInfo spellCheckerInfo, TextServicesData textServicesData) {
        if (spellCheckerInfo != null) {
            spellCheckerInfo.getId();
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            textServicesData.setCurrentSpellChecker(spellCheckerInfo);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, printWriter)) {
            if (strArr.length == 0 || (strArr.length == 1 && strArr[0].equals("-a"))) {
                synchronized (this.mLock) {
                    printWriter.println("Current Text Services Manager state:");
                    printWriter.println("  Users:");
                    int size = this.mUserData.size();
                    for (int i = 0; i < size; i++) {
                        this.mUserData.valueAt(i).dump(printWriter);
                    }
                }
            } else if (strArr.length != 2 || !strArr[0].equals("--user")) {
                printWriter.println("Invalid arguments to text services.");
            } else {
                int parseInt = Integer.parseInt(strArr[1]);
                if (this.mUserManager.getUserInfo(parseInt) == null) {
                    printWriter.println("Non-existent user.");
                    return;
                }
                TextServicesData textServicesData = this.mUserData.get(parseInt);
                if (textServicesData == null) {
                    printWriter.println("User needs to unlock first.");
                    return;
                }
                synchronized (this.mLock) {
                    printWriter.println("Current Text Services Manager state:");
                    printWriter.println("  User " + parseInt + XmlUtils.STRING_ARRAY_SEPARATOR);
                    textServicesData.dump(printWriter);
                }
            }
        }
    }

    @GuardedBy({"mLock"})
    public final TextServicesData getDataFromCallingUserIdLocked(int i) {
        return this.mUserData.get(i);
    }

    /* loaded from: classes2.dex */
    public static final class SessionRequest {
        public final Bundle mBundle;
        public final String mLocale;
        public final ISpellCheckerSessionListener mScListener;
        public final int mSupportedAttributes;
        public final ITextServicesSessionListener mTsListener;
        public final int mUid;

        public SessionRequest(int i, String str, ITextServicesSessionListener iTextServicesSessionListener, ISpellCheckerSessionListener iSpellCheckerSessionListener, Bundle bundle, int i2) {
            this.mUid = i;
            this.mLocale = str;
            this.mTsListener = iTextServicesSessionListener;
            this.mScListener = iSpellCheckerSessionListener;
            this.mBundle = bundle;
            this.mSupportedAttributes = i2;
        }
    }

    /* loaded from: classes2.dex */
    public final class SpellCheckerBindGroup {
        public boolean mConnected;
        public final InternalServiceConnection mInternalConnection;
        public ISpellCheckerService mSpellChecker;
        public HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups;
        public boolean mUnbindCalled;
        public final String TAG = SpellCheckerBindGroup.class.getSimpleName();
        public final ArrayList<SessionRequest> mPendingSessionRequests = new ArrayList<>();
        public final ArrayList<SessionRequest> mOnGoingSessionRequests = new ArrayList<>();
        public final InternalDeathRecipients mListeners = new InternalDeathRecipients(this);

        public SpellCheckerBindGroup(InternalServiceConnection internalServiceConnection) {
            this.mInternalConnection = internalServiceConnection;
            this.mSpellCheckerBindGroups = internalServiceConnection.mSpellCheckerBindGroups;
        }

        public void onServiceConnectedLocked(ISpellCheckerService iSpellCheckerService) {
            if (this.mUnbindCalled) {
                return;
            }
            this.mSpellChecker = iSpellCheckerService;
            this.mConnected = true;
            try {
                int size = this.mPendingSessionRequests.size();
                for (int i = 0; i < size; i++) {
                    SessionRequest sessionRequest = this.mPendingSessionRequests.get(i);
                    this.mSpellChecker.getISpellCheckerSession(sessionRequest.mLocale, sessionRequest.mScListener, sessionRequest.mBundle, sessionRequest.mSupportedAttributes, new ISpellCheckerServiceCallbackBinder(this, sessionRequest));
                    this.mOnGoingSessionRequests.add(sessionRequest);
                }
                this.mPendingSessionRequests.clear();
            } catch (RemoteException unused) {
                removeAllLocked();
            }
            cleanLocked();
        }

        public void onServiceDisconnectedLocked() {
            this.mSpellChecker = null;
            this.mConnected = false;
        }

        public void removeListener(ISpellCheckerSessionListener iSpellCheckerSessionListener) {
            synchronized (TextServicesManagerService.this.mLock) {
                this.mListeners.unregister(iSpellCheckerSessionListener);
                final IBinder asBinder = iSpellCheckerSessionListener.asBinder();
                Predicate<? super SessionRequest> predicate = new Predicate() { // from class: com.android.server.textservices.TextServicesManagerService$SpellCheckerBindGroup$$ExternalSyntheticLambda0
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$removeListener$0;
                        lambda$removeListener$0 = TextServicesManagerService.SpellCheckerBindGroup.lambda$removeListener$0(asBinder, (TextServicesManagerService.SessionRequest) obj);
                        return lambda$removeListener$0;
                    }
                };
                this.mPendingSessionRequests.removeIf(predicate);
                this.mOnGoingSessionRequests.removeIf(predicate);
                cleanLocked();
            }
        }

        public static /* synthetic */ boolean lambda$removeListener$0(IBinder iBinder, SessionRequest sessionRequest) {
            return sessionRequest.mScListener.asBinder() == iBinder;
        }

        public final void cleanLocked() {
            if (!this.mUnbindCalled && this.mListeners.getRegisteredCallbackCount() <= 0 && this.mPendingSessionRequests.isEmpty() && this.mOnGoingSessionRequests.isEmpty()) {
                String str = this.mInternalConnection.mSciId;
                if (this.mSpellCheckerBindGroups.get(str) == this) {
                    this.mSpellCheckerBindGroups.remove(str);
                }
                TextServicesManagerService.this.mContext.unbindService(this.mInternalConnection);
                this.mUnbindCalled = true;
            }
        }

        public void removeAllLocked() {
            Slog.e(this.TAG, "Remove the spell checker bind unexpectedly.");
            for (int registeredCallbackCount = this.mListeners.getRegisteredCallbackCount() - 1; registeredCallbackCount >= 0; registeredCallbackCount--) {
                InternalDeathRecipients internalDeathRecipients = this.mListeners;
                internalDeathRecipients.unregister(internalDeathRecipients.getRegisteredCallbackItem(registeredCallbackCount));
            }
            this.mPendingSessionRequests.clear();
            this.mOnGoingSessionRequests.clear();
            cleanLocked();
        }

        public void getISpellCheckerSessionOrQueueLocked(SessionRequest sessionRequest) {
            if (this.mUnbindCalled) {
                return;
            }
            this.mListeners.register(sessionRequest.mScListener);
            if (!this.mConnected) {
                this.mPendingSessionRequests.add(sessionRequest);
                return;
            }
            try {
                this.mSpellChecker.getISpellCheckerSession(sessionRequest.mLocale, sessionRequest.mScListener, sessionRequest.mBundle, sessionRequest.mSupportedAttributes, new ISpellCheckerServiceCallbackBinder(this, sessionRequest));
                this.mOnGoingSessionRequests.add(sessionRequest);
            } catch (RemoteException unused) {
                removeAllLocked();
            }
            cleanLocked();
        }

        public void onSessionCreated(ISpellCheckerSession iSpellCheckerSession, SessionRequest sessionRequest) {
            synchronized (TextServicesManagerService.this.mLock) {
                if (this.mUnbindCalled) {
                    return;
                }
                if (this.mOnGoingSessionRequests.remove(sessionRequest)) {
                    try {
                        sessionRequest.mTsListener.onServiceConnected(iSpellCheckerSession);
                    } catch (RemoteException unused) {
                    }
                }
                cleanLocked();
            }
        }
    }

    /* loaded from: classes2.dex */
    public final class InternalServiceConnection implements ServiceConnection {
        public final String mSciId;
        public final HashMap<String, SpellCheckerBindGroup> mSpellCheckerBindGroups;

        public InternalServiceConnection(String str, HashMap<String, SpellCheckerBindGroup> hashMap) {
            this.mSciId = str;
            this.mSpellCheckerBindGroups = hashMap;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            synchronized (TextServicesManagerService.this.mLock) {
                onServiceConnectedInnerLocked(componentName, iBinder);
            }
        }

        public final void onServiceConnectedInnerLocked(ComponentName componentName, IBinder iBinder) {
            ISpellCheckerService asInterface = ISpellCheckerService.Stub.asInterface(iBinder);
            SpellCheckerBindGroup spellCheckerBindGroup = this.mSpellCheckerBindGroups.get(this.mSciId);
            if (spellCheckerBindGroup == null || this != spellCheckerBindGroup.mInternalConnection) {
                return;
            }
            spellCheckerBindGroup.onServiceConnectedLocked(asInterface);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            synchronized (TextServicesManagerService.this.mLock) {
                onServiceDisconnectedInnerLocked(componentName);
            }
        }

        public final void onServiceDisconnectedInnerLocked(ComponentName componentName) {
            SpellCheckerBindGroup spellCheckerBindGroup = this.mSpellCheckerBindGroups.get(this.mSciId);
            if (spellCheckerBindGroup == null || this != spellCheckerBindGroup.mInternalConnection) {
                return;
            }
            spellCheckerBindGroup.onServiceDisconnectedLocked();
        }
    }

    /* loaded from: classes2.dex */
    public static final class InternalDeathRecipients extends RemoteCallbackList<ISpellCheckerSessionListener> {
        public final SpellCheckerBindGroup mGroup;

        public InternalDeathRecipients(SpellCheckerBindGroup spellCheckerBindGroup) {
            this.mGroup = spellCheckerBindGroup;
        }

        @Override // android.os.RemoteCallbackList
        public void onCallbackDied(ISpellCheckerSessionListener iSpellCheckerSessionListener) {
            this.mGroup.removeListener(iSpellCheckerSessionListener);
        }
    }

    /* loaded from: classes2.dex */
    public static final class ISpellCheckerServiceCallbackBinder extends ISpellCheckerServiceCallback.Stub {
        @GuardedBy({"mCallbackLock"})
        public WeakReference<SpellCheckerBindGroup> mBindGroup;
        public final Object mCallbackLock;
        @GuardedBy({"mCallbackLock"})
        public WeakReference<SessionRequest> mRequest;

        public ISpellCheckerServiceCallbackBinder(SpellCheckerBindGroup spellCheckerBindGroup, SessionRequest sessionRequest) {
            Object obj = new Object();
            this.mCallbackLock = obj;
            synchronized (obj) {
                this.mBindGroup = new WeakReference<>(spellCheckerBindGroup);
                this.mRequest = new WeakReference<>(sessionRequest);
            }
        }

        public void onSessionCreated(ISpellCheckerSession iSpellCheckerSession) {
            synchronized (this.mCallbackLock) {
                WeakReference<SpellCheckerBindGroup> weakReference = this.mBindGroup;
                if (weakReference != null && this.mRequest != null) {
                    SpellCheckerBindGroup spellCheckerBindGroup = weakReference.get();
                    SessionRequest sessionRequest = this.mRequest.get();
                    this.mBindGroup = null;
                    this.mRequest = null;
                    if (spellCheckerBindGroup == null || sessionRequest == null) {
                        return;
                    }
                    spellCheckerBindGroup.onSessionCreated(iSpellCheckerSession, sessionRequest);
                }
            }
        }
    }
}
