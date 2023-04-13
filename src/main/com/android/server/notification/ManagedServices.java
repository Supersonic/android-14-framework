package com.android.server.notification;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IntArray;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseSetArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.TriPredicate;
import com.android.internal.util.jobs.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.notification.ManagedServices;
import com.android.server.notification.NotificationManagerService;
import com.android.server.utils.TimingsTraceAndSlog;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public abstract class ManagedServices {
    public final boolean DEBUG;
    public final String TAG;
    public int mApprovalLevel;
    public final ArrayMap<Integer, ArrayMap<Boolean, ArraySet<String>>> mApproved;
    public final Config mConfig;
    public final Context mContext;
    public final ArraySet<ComponentName> mDefaultComponents;
    public final ArraySet<String> mDefaultPackages;
    public final Object mDefaultsLock;
    public ArraySet<ComponentName> mEnabledServicesForCurrentProfiles;
    public ArraySet<String> mEnabledServicesPackageNames;
    public final Handler mHandler;
    public ArrayMap<Integer, Boolean> mIsUserChanged;
    public final Object mMutex;
    public final IPackageManager mPm;
    @GuardedBy({"mMutex"})
    public final ArrayList<ManagedServiceInfo> mServices;
    public final ArrayList<Pair<ComponentName, Integer>> mServicesBound;
    public final ArraySet<Pair<ComponentName, Integer>> mServicesRebinding;
    public final SparseSetArray<ComponentName> mSnoozing;
    public final UserManager mUm;
    public boolean mUseXml;
    public final UserProfiles mUserProfiles;
    @GuardedBy({"mApproved"})
    public ArrayMap<Integer, ArraySet<String>> mUserSetServices;

    /* loaded from: classes2.dex */
    public static class Config {
        public String bindPermission;
        public String caption;
        public int clientLabel;
        public String secondarySettingName;
        public String secureSettingName;
        public String serviceInterface;
        public String settingsAction;
        public String xmlTag;
    }

    public abstract IInterface asInterface(IBinder iBinder);

    public abstract boolean checkType(IInterface iInterface);

    public abstract void ensureFilters(ServiceInfo serviceInfo, int i);

    public int getBindFlags() {
        return 83886081;
    }

    public abstract Config getConfig();

    public abstract String getRequiredPermission();

    public abstract void loadDefaultsFromConfig();

    public void onBootPhaseAppsCanStart() {
    }

    public abstract void onServiceAdded(ManagedServiceInfo managedServiceInfo);

    public void onServiceRemovedLocked(ManagedServiceInfo managedServiceInfo) {
    }

    public void readExtraAttributes(String str, TypedXmlPullParser typedXmlPullParser, int i) throws IOException {
    }

    public void readExtraTag(String str, TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
    }

    public boolean shouldReflectToSettings() {
        return false;
    }

    public void upgradeUserSet() {
    }

    public void writeExtraAttributes(TypedXmlSerializer typedXmlSerializer, int i) throws IOException {
    }

    public void writeExtraXmlTags(TypedXmlSerializer typedXmlSerializer) throws IOException {
    }

    public ManagedServices(Context context, Object obj, UserProfiles userProfiles, IPackageManager iPackageManager) {
        String simpleName = getClass().getSimpleName();
        this.TAG = simpleName;
        this.DEBUG = Log.isLoggable(simpleName, 3);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mServices = new ArrayList<>();
        this.mServicesBound = new ArrayList<>();
        this.mServicesRebinding = new ArraySet<>();
        this.mDefaultsLock = new Object();
        this.mDefaultComponents = new ArraySet<>();
        this.mDefaultPackages = new ArraySet<>();
        this.mEnabledServicesForCurrentProfiles = new ArraySet<>();
        this.mEnabledServicesPackageNames = new ArraySet<>();
        this.mSnoozing = new SparseSetArray<>();
        this.mApproved = new ArrayMap<>();
        this.mUserSetServices = new ArrayMap<>();
        this.mIsUserChanged = new ArrayMap<>();
        this.mContext = context;
        this.mMutex = obj;
        this.mUserProfiles = userProfiles;
        this.mPm = iPackageManager;
        this.mConfig = getConfig();
        this.mApprovalLevel = 1;
        this.mUm = (UserManager) context.getSystemService("user");
    }

    public final String getCaption() {
        return this.mConfig.caption;
    }

    public List<ManagedServiceInfo> getServices() {
        ArrayList arrayList;
        synchronized (this.mMutex) {
            arrayList = new ArrayList(this.mServices);
        }
        return arrayList;
    }

    public void addDefaultComponentOrPackage(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        synchronized (this.mDefaultsLock) {
            if (this.mApprovalLevel == 0) {
                this.mDefaultPackages.add(str);
                return;
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString == null || this.mApprovalLevel != 1) {
                return;
            }
            this.mDefaultPackages.add(unflattenFromString.getPackageName());
            this.mDefaultComponents.add(unflattenFromString);
        }
    }

    public boolean isDefaultComponentOrPackage(String str) {
        synchronized (this.mDefaultsLock) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
            if (unflattenFromString == null) {
                return this.mDefaultPackages.contains(str);
            }
            return this.mDefaultComponents.contains(unflattenFromString);
        }
    }

    public ArraySet<ComponentName> getDefaultComponents() {
        ArraySet<ComponentName> arraySet;
        synchronized (this.mDefaultsLock) {
            arraySet = new ArraySet<>(this.mDefaultComponents);
        }
        return arraySet;
    }

    public ArraySet<String> getDefaultPackages() {
        ArraySet<String> arraySet;
        synchronized (this.mDefaultsLock) {
            arraySet = new ArraySet<>(this.mDefaultPackages);
        }
        return arraySet;
    }

    public ArrayMap<Boolean, ArrayList<ComponentName>> resetComponents(String str, int i) {
        boolean z;
        ArrayList<ComponentName> arrayList = new ArrayList<>(this.mDefaultComponents.size());
        ArrayList<ComponentName> arrayList2 = new ArrayList<>(this.mDefaultComponents.size());
        ArraySet arraySet = new ArraySet(getAllowedComponents(i));
        synchronized (this.mDefaultsLock) {
            for (int i2 = 0; i2 < this.mDefaultComponents.size() && arraySet.size() > 0; i2++) {
                ComponentName valueAt = this.mDefaultComponents.valueAt(i2);
                if (str.equals(valueAt.getPackageName()) && !arraySet.contains(valueAt)) {
                    arrayList.add(valueAt);
                }
            }
            synchronized (this.mApproved) {
                ArrayMap<Boolean, ArraySet<String>> arrayMap = this.mApproved.get(Integer.valueOf(i));
                if (arrayMap != null) {
                    int size = arrayMap.size();
                    z = false;
                    for (int i3 = 0; i3 < size; i3++) {
                        ArraySet<String> valueAt2 = arrayMap.valueAt(i3);
                        for (int i4 = 0; i4 < arraySet.size(); i4++) {
                            ComponentName componentName = (ComponentName) arraySet.valueAt(i4);
                            if (str.equals(componentName.getPackageName()) && !this.mDefaultComponents.contains(componentName) && valueAt2.remove(componentName.flattenToString())) {
                                arrayList2.add(componentName);
                                clearUserSetFlagLocked(componentName, i);
                                z = true;
                            }
                        }
                        for (int i5 = 0; i5 < arrayList.size(); i5++) {
                            z |= valueAt2.add(arrayList.get(i5).flattenToString());
                        }
                    }
                } else {
                    z = false;
                }
            }
        }
        if (z) {
            rebindServices(false, -1);
        }
        ArrayMap<Boolean, ArrayList<ComponentName>> arrayMap2 = new ArrayMap<>();
        arrayMap2.put(Boolean.TRUE, arrayList);
        arrayMap2.put(Boolean.FALSE, arrayList2);
        return arrayMap2;
    }

    @GuardedBy({"mApproved"})
    public final boolean clearUserSetFlagLocked(ComponentName componentName, int i) {
        String approvedValue = getApprovedValue(componentName.flattenToString());
        ArraySet<String> arraySet = this.mUserSetServices.get(Integer.valueOf(i));
        return arraySet != null && arraySet.remove(approvedValue);
    }

    public final ManagedServiceInfo newServiceInfo(IInterface iInterface, ComponentName componentName, int i, boolean z, ServiceConnection serviceConnection, int i2, int i3) {
        return new ManagedServiceInfo(iInterface, componentName, i, z, serviceConnection, i2, i3);
    }

    public void dump(PrintWriter printWriter, NotificationManagerService.DumpFilter dumpFilter) {
        int i;
        SparseSetArray sparseSetArray;
        printWriter.println("    Allowed " + getCaption() + "s:");
        synchronized (this.mApproved) {
            int size = this.mApproved.size();
            for (int i2 = 0; i2 < size; i2++) {
                int intValue = this.mApproved.keyAt(i2).intValue();
                ArrayMap<Boolean, ArraySet<String>> valueAt = this.mApproved.valueAt(i2);
                Boolean bool = this.mIsUserChanged.get(Integer.valueOf(intValue));
                if (valueAt != null) {
                    int size2 = valueAt.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        boolean booleanValue = valueAt.keyAt(i3).booleanValue();
                        ArraySet<String> valueAt2 = valueAt.valueAt(i3);
                        if (valueAt.size() > 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("      ");
                            sb.append(String.join(XmlUtils.STRING_ARRAY_SEPARATOR, valueAt2));
                            sb.append(" (user: ");
                            sb.append(intValue);
                            sb.append(" isPrimary: ");
                            sb.append(booleanValue);
                            sb.append(bool == null ? "" : " isUserChanged: " + bool);
                            sb.append(")");
                            printWriter.println(sb.toString());
                        }
                    }
                }
            }
            printWriter.println("    Has user set:");
            for (Integer num : this.mUserSetServices.keySet()) {
                int intValue2 = num.intValue();
                if (this.mIsUserChanged.get(Integer.valueOf(intValue2)) == null) {
                    printWriter.println("      userId=" + intValue2 + " value=" + this.mUserSetServices.get(Integer.valueOf(intValue2)));
                }
            }
        }
        printWriter.println("    All " + getCaption() + "s (" + this.mEnabledServicesForCurrentProfiles.size() + ") enabled for current profiles:");
        Iterator<ComponentName> it = this.mEnabledServicesForCurrentProfiles.iterator();
        while (it.hasNext()) {
            ComponentName next = it.next();
            if (dumpFilter == null || dumpFilter.matches(next)) {
                printWriter.println("      " + next);
            }
        }
        synchronized (this.mMutex) {
            printWriter.println("    Live " + getCaption() + "s (" + this.mServices.size() + "):");
            Iterator<ManagedServiceInfo> it2 = this.mServices.iterator();
            while (it2.hasNext()) {
                ManagedServiceInfo next2 = it2.next();
                if (dumpFilter == null || dumpFilter.matches(next2.component)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("      ");
                    sb2.append(next2.component);
                    sb2.append(" (user ");
                    sb2.append(next2.userid);
                    sb2.append("): ");
                    sb2.append(next2.service);
                    sb2.append(next2.isSystem ? " SYSTEM" : "");
                    sb2.append(next2.isGuest(this) ? " GUEST" : "");
                    printWriter.println(sb2.toString());
                }
            }
        }
        synchronized (this.mSnoozing) {
            sparseSetArray = new SparseSetArray(this.mSnoozing);
        }
        printWriter.println("    Snoozed " + getCaption() + "s (" + sparseSetArray.size() + "):");
        for (i = 0; i < sparseSetArray.size(); i++) {
            printWriter.println("      User: " + sparseSetArray.keyAt(i));
            Iterator it3 = sparseSetArray.valuesAt(i).iterator();
            while (it3.hasNext()) {
                ComponentName componentName = (ComponentName) it3.next();
                ServiceInfo serviceInfo = getServiceInfo(componentName, sparseSetArray.keyAt(i));
                StringBuilder sb3 = new StringBuilder();
                sb3.append("        ");
                sb3.append(componentName.flattenToShortString());
                sb3.append(isAutobindAllowed(serviceInfo) ? "" : " (META_DATA_DEFAULT_AUTOBIND=false)");
                printWriter.println(sb3.toString());
            }
        }
    }

    public void dump(ProtoOutputStream protoOutputStream, NotificationManagerService.DumpFilter dumpFilter) {
        int i;
        protoOutputStream.write(1138166333441L, getCaption());
        synchronized (this.mApproved) {
            int size = this.mApproved.size();
            int i2 = 0;
            while (true) {
                long j = 2246267895810L;
                if (i2 >= size) {
                    break;
                }
                int intValue = this.mApproved.keyAt(i2).intValue();
                ArrayMap<Boolean, ArraySet<String>> valueAt = this.mApproved.valueAt(i2);
                if (valueAt != null) {
                    int size2 = valueAt.size();
                    int i3 = 0;
                    while (i3 < size2) {
                        boolean booleanValue = valueAt.keyAt(i3).booleanValue();
                        ArraySet<String> valueAt2 = valueAt.valueAt(i3);
                        if (valueAt.size() > 0) {
                            i = i2;
                            long start = protoOutputStream.start(j);
                            Iterator<String> it = valueAt2.iterator();
                            while (it.hasNext()) {
                                protoOutputStream.write(2237677961217L, it.next());
                            }
                            protoOutputStream.write(1120986464258L, intValue);
                            protoOutputStream.write(1133871366147L, booleanValue);
                            protoOutputStream.end(start);
                        } else {
                            i = i2;
                        }
                        i3++;
                        i2 = i;
                        j = 2246267895810L;
                    }
                }
                i2++;
            }
        }
        Iterator<ComponentName> it2 = this.mEnabledServicesForCurrentProfiles.iterator();
        while (it2.hasNext()) {
            ComponentName next = it2.next();
            if (dumpFilter == null || dumpFilter.matches(next)) {
                next.dumpDebug(protoOutputStream, 2246267895811L);
            }
        }
        synchronized (this.mMutex) {
            Iterator<ManagedServiceInfo> it3 = this.mServices.iterator();
            while (it3.hasNext()) {
                ManagedServiceInfo next2 = it3.next();
                if (dumpFilter == null || dumpFilter.matches(next2.component)) {
                    next2.dumpDebug(protoOutputStream, 2246267895812L, this);
                }
            }
        }
        synchronized (this.mSnoozing) {
            for (int i4 = 0; i4 < this.mSnoozing.size(); i4++) {
                long start2 = protoOutputStream.start(2246267895814L);
                protoOutputStream.write(1120986464257L, this.mSnoozing.keyAt(i4));
                Iterator it4 = this.mSnoozing.valuesAt(i4).iterator();
                while (it4.hasNext()) {
                    ((ComponentName) it4.next()).dumpDebug(protoOutputStream, 2246267895810L);
                }
                protoOutputStream.end(start2);
            }
        }
    }

    public void onSettingRestored(String str, String str2, int i, int i2) {
        String str3;
        if (this.mUseXml) {
            return;
        }
        Slog.d(this.TAG, "Restored managed service setting: " + str);
        if (this.mConfig.secureSettingName.equals(str) || ((str3 = this.mConfig.secondarySettingName) != null && str3.equals(str))) {
            if (i < 26) {
                String approved = getApproved(i2, this.mConfig.secureSettingName.equals(str));
                if (!TextUtils.isEmpty(approved)) {
                    str2 = TextUtils.isEmpty(str2) ? approved : str2 + XmlUtils.STRING_ARRAY_SEPARATOR + approved;
                }
            }
            if (shouldReflectToSettings()) {
                Settings.Secure.putStringForUser(this.mContext.getContentResolver(), str, str2, i2);
            }
            for (UserInfo userInfo : this.mUm.getUsers()) {
                addApprovedList(str2, userInfo.id, this.mConfig.secureSettingName.equals(str));
            }
            Slog.d(this.TAG, "Done loading approved values from settings");
            rebindServices(false, i2);
        }
    }

    public void writeDefaults(TypedXmlSerializer typedXmlSerializer) throws IOException {
        synchronized (this.mDefaultsLock) {
            ArrayList arrayList = new ArrayList(this.mDefaultComponents.size());
            for (int i = 0; i < this.mDefaultComponents.size(); i++) {
                arrayList.add(this.mDefaultComponents.valueAt(i).flattenToString());
            }
            typedXmlSerializer.attribute((String) null, "defaults", String.join(XmlUtils.STRING_ARRAY_SEPARATOR, arrayList));
        }
    }

    public void writeXml(TypedXmlSerializer typedXmlSerializer, boolean z, int i) throws IOException {
        typedXmlSerializer.startTag((String) null, getConfig().xmlTag);
        typedXmlSerializer.attributeInt((String) null, "version", Integer.parseInt("4"));
        writeDefaults(typedXmlSerializer);
        if (z) {
            trimApprovedListsAccordingToInstalledServices(i);
        }
        synchronized (this.mApproved) {
            int size = this.mApproved.size();
            for (int i2 = 0; i2 < size; i2++) {
                int intValue = this.mApproved.keyAt(i2).intValue();
                if (!z || intValue == i) {
                    ArrayMap<Boolean, ArraySet<String>> valueAt = this.mApproved.valueAt(i2);
                    Boolean bool = this.mIsUserChanged.get(Integer.valueOf(intValue));
                    if (valueAt != null) {
                        int size2 = valueAt.size();
                        for (int i3 = 0; i3 < size2; i3++) {
                            boolean booleanValue = valueAt.keyAt(i3).booleanValue();
                            ArraySet<String> valueAt2 = valueAt.valueAt(i3);
                            ArraySet<String> arraySet = this.mUserSetServices.get(Integer.valueOf(intValue));
                            if (valueAt2 != null || arraySet != null || bool != null) {
                                String join = valueAt2 == null ? "" : String.join(XmlUtils.STRING_ARRAY_SEPARATOR, valueAt2);
                                typedXmlSerializer.startTag((String) null, "service_listing");
                                typedXmlSerializer.attribute((String) null, "approved", join);
                                typedXmlSerializer.attributeInt((String) null, "user", intValue);
                                typedXmlSerializer.attributeBoolean((String) null, "primary", booleanValue);
                                if (bool != null) {
                                    typedXmlSerializer.attributeBoolean((String) null, "user_changed", bool.booleanValue());
                                } else if (arraySet != null) {
                                    typedXmlSerializer.attribute((String) null, "user_set_services", String.join(XmlUtils.STRING_ARRAY_SEPARATOR, arraySet));
                                }
                                writeExtraAttributes(typedXmlSerializer, intValue);
                                typedXmlSerializer.endTag((String) null, "service_listing");
                                if (!z && booleanValue && shouldReflectToSettings()) {
                                    Settings.Secure.putStringForUser(this.mContext.getContentResolver(), getConfig().secureSettingName, join, intValue);
                                }
                            }
                        }
                    }
                }
            }
        }
        writeExtraXmlTags(typedXmlSerializer);
        typedXmlSerializer.endTag((String) null, getConfig().xmlTag);
    }

    public final void migrateToXml() {
        for (UserInfo userInfo : this.mUm.getUsers()) {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            if (!TextUtils.isEmpty(getConfig().secureSettingName)) {
                addApprovedList(Settings.Secure.getStringForUser(contentResolver, getConfig().secureSettingName, userInfo.id), userInfo.id, true);
            }
            if (!TextUtils.isEmpty(getConfig().secondarySettingName)) {
                addApprovedList(Settings.Secure.getStringForUser(contentResolver, getConfig().secondarySettingName, userInfo.id), userInfo.id, false);
            }
        }
    }

    public void readDefaults(TypedXmlPullParser typedXmlPullParser) {
        String readStringAttribute = com.android.internal.util.XmlUtils.readStringAttribute(typedXmlPullParser, "defaults");
        if (TextUtils.isEmpty(readStringAttribute)) {
            return;
        }
        String[] split = readStringAttribute.split(XmlUtils.STRING_ARRAY_SEPARATOR);
        synchronized (this.mDefaultsLock) {
            for (int i = 0; i < split.length; i++) {
                if (!TextUtils.isEmpty(split[i])) {
                    ComponentName unflattenFromString = ComponentName.unflattenFromString(split[i]);
                    if (unflattenFromString != null) {
                        this.mDefaultPackages.add(unflattenFromString.getPackageName());
                        this.mDefaultComponents.add(unflattenFromString);
                    } else {
                        this.mDefaultPackages.add(split[i]);
                    }
                }
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x00ae, code lost:
        if (java.lang.Boolean.valueOf(r11).booleanValue() != false) goto L60;
     */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00f6  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x00ee A[ADDED_TO_REGION, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void readXml(TypedXmlPullParser typedXmlPullParser, TriPredicate<String, Integer, String> triPredicate, boolean z, int i) throws XmlPullParserException, IOException {
        boolean z2;
        String readStringAttribute = com.android.internal.util.XmlUtils.readStringAttribute(typedXmlPullParser, "version");
        readDefaults(typedXmlPullParser);
        boolean z3 = false;
        while (true) {
            int next = typedXmlPullParser.next();
            z2 = true;
            if (next == 1) {
                break;
            }
            String name = typedXmlPullParser.getName();
            if (next == 3 && getConfig().xmlTag.equals(name)) {
                break;
            } else if (next == 2) {
                if ("service_listing".equals(name)) {
                    Slog.i(this.TAG, "Read " + this.mConfig.caption + " permissions from xml");
                    String readStringAttribute2 = com.android.internal.util.XmlUtils.readStringAttribute(typedXmlPullParser, "approved");
                    int attributeInt = z ? i : typedXmlPullParser.getAttributeInt((String) null, "user", 0);
                    boolean attributeBoolean = typedXmlPullParser.getAttributeBoolean((String) null, "primary", true);
                    String readStringAttribute3 = com.android.internal.util.XmlUtils.readStringAttribute(typedXmlPullParser, "user_changed");
                    String readStringAttribute4 = com.android.internal.util.XmlUtils.readStringAttribute(typedXmlPullParser, "user_set");
                    String readStringAttribute5 = com.android.internal.util.XmlUtils.readStringAttribute(typedXmlPullParser, "user_set_services");
                    if ("4".equals(readStringAttribute)) {
                        if (readStringAttribute3 == null) {
                            readStringAttribute5 = TextUtils.emptyIfNull(readStringAttribute5);
                            readExtraAttributes(name, typedXmlPullParser, attributeInt);
                            if (triPredicate != null || triPredicate.test(getPackageName(readStringAttribute2), Integer.valueOf(attributeInt), getRequiredPermission()) || readStringAttribute2.isEmpty()) {
                                if (this.mUm.getUserInfo(attributeInt) != null) {
                                    addApprovedList(readStringAttribute2, attributeInt, attributeBoolean, readStringAttribute5);
                                }
                                this.mUseXml = true;
                            }
                        } else {
                            this.mIsUserChanged.put(Integer.valueOf(attributeInt), Boolean.valueOf(readStringAttribute3));
                        }
                    } else if (readStringAttribute5 == null) {
                        if (readStringAttribute4 != null && Boolean.valueOf(readStringAttribute4).booleanValue()) {
                            this.mIsUserChanged.put(Integer.valueOf(attributeInt), Boolean.TRUE);
                            z3 = false;
                            readStringAttribute5 = readStringAttribute2;
                            readExtraAttributes(name, typedXmlPullParser, attributeInt);
                            if (triPredicate != null) {
                            }
                            if (this.mUm.getUserInfo(attributeInt) != null) {
                            }
                            this.mUseXml = true;
                        } else {
                            z3 = true;
                            readStringAttribute5 = "";
                            readExtraAttributes(name, typedXmlPullParser, attributeInt);
                            if (triPredicate != null) {
                            }
                            if (this.mUm.getUserInfo(attributeInt) != null) {
                            }
                            this.mUseXml = true;
                        }
                    } else {
                        z3 = true;
                        readExtraAttributes(name, typedXmlPullParser, attributeInt);
                        if (triPredicate != null) {
                        }
                        if (this.mUm.getUserInfo(attributeInt) != null) {
                        }
                        this.mUseXml = true;
                    }
                } else {
                    readExtraTag(name, typedXmlPullParser);
                }
            }
        }
        if (!TextUtils.isEmpty(readStringAttribute) && !"1".equals(readStringAttribute) && !"2".equals(readStringAttribute) && !"3".equals(readStringAttribute)) {
            z2 = false;
        }
        if (z2) {
            upgradeDefaultsXmlVersion();
        }
        if (z3) {
            upgradeUserSet();
        }
        rebindServices(false, -1);
    }

    public void upgradeDefaultsXmlVersion() {
        if (this.mDefaultComponents.size() + this.mDefaultPackages.size() == 0) {
            if (this.mApprovalLevel == 1) {
                List<ComponentName> allowedComponents = getAllowedComponents(0);
                for (int i = 0; i < allowedComponents.size(); i++) {
                    addDefaultComponentOrPackage(allowedComponents.get(i).flattenToString());
                }
            }
            if (this.mApprovalLevel == 0) {
                List<String> allowedPackages = getAllowedPackages(0);
                for (int i2 = 0; i2 < allowedPackages.size(); i2++) {
                    addDefaultComponentOrPackage(allowedPackages.get(i2));
                }
            }
        }
        if (this.mDefaultComponents.size() + this.mDefaultPackages.size() == 0) {
            loadDefaultsFromConfig();
        }
    }

    public void addApprovedList(String str, int i, boolean z) {
        addApprovedList(str, i, z, str);
    }

    public void addApprovedList(String str, int i, boolean z, String str2) {
        if (TextUtils.isEmpty(str)) {
            str = "";
        }
        if (str2 == null) {
            str2 = str;
        }
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> arrayMap = this.mApproved.get(Integer.valueOf(i));
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mApproved.put(Integer.valueOf(i), arrayMap);
            }
            ArraySet<String> arraySet = arrayMap.get(Boolean.valueOf(z));
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                arrayMap.put(Boolean.valueOf(z), arraySet);
            }
            for (String str3 : str.split(XmlUtils.STRING_ARRAY_SEPARATOR)) {
                String approvedValue = getApprovedValue(str3);
                if (approvedValue != null) {
                    arraySet.add(approvedValue);
                }
            }
            ArraySet<String> arraySet2 = this.mUserSetServices.get(Integer.valueOf(i));
            if (arraySet2 == null) {
                arraySet2 = new ArraySet<>();
                this.mUserSetServices.put(Integer.valueOf(i), arraySet2);
            }
            for (String str4 : str2.split(XmlUtils.STRING_ARRAY_SEPARATOR)) {
                String approvedValue2 = getApprovedValue(str4);
                if (approvedValue2 != null) {
                    arraySet2.add(approvedValue2);
                }
            }
        }
    }

    public boolean isComponentEnabledForPackage(String str) {
        return this.mEnabledServicesPackageNames.contains(str);
    }

    public void setPackageOrComponentEnabled(String str, int i, boolean z, boolean z2) {
        setPackageOrComponentEnabled(str, i, z, z2, true);
    }

    public void setPackageOrComponentEnabled(String str, int i, boolean z, boolean z2, boolean z3) {
        String str2 = this.TAG;
        StringBuilder sb = new StringBuilder();
        sb.append(z2 ? " Allowing " : "Disallowing ");
        sb.append(this.mConfig.caption);
        sb.append(" ");
        sb.append(str);
        sb.append(" (userSet: ");
        sb.append(z3);
        sb.append(")");
        Slog.i(str2, sb.toString());
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> arrayMap = this.mApproved.get(Integer.valueOf(i));
            if (arrayMap == null) {
                arrayMap = new ArrayMap<>();
                this.mApproved.put(Integer.valueOf(i), arrayMap);
            }
            ArraySet<String> arraySet = arrayMap.get(Boolean.valueOf(z));
            if (arraySet == null) {
                arraySet = new ArraySet<>();
                arrayMap.put(Boolean.valueOf(z), arraySet);
            }
            String approvedValue = getApprovedValue(str);
            if (approvedValue != null) {
                if (z2) {
                    arraySet.add(approvedValue);
                } else {
                    arraySet.remove(approvedValue);
                }
            }
            ArraySet<String> arraySet2 = this.mUserSetServices.get(Integer.valueOf(i));
            if (arraySet2 == null) {
                arraySet2 = new ArraySet<>();
                this.mUserSetServices.put(Integer.valueOf(i), arraySet2);
            }
            if (z3) {
                arraySet2.add(str);
            } else {
                arraySet2.remove(str);
            }
        }
        rebindServices(false, i);
    }

    public final String getApprovedValue(String str) {
        if (this.mApprovalLevel == 1) {
            if (ComponentName.unflattenFromString(str) != null) {
                return str;
            }
            return null;
        }
        return getPackageName(str);
    }

    public String getApproved(int i, boolean z) {
        String join;
        synchronized (this.mApproved) {
            join = String.join(XmlUtils.STRING_ARRAY_SEPARATOR, this.mApproved.getOrDefault(Integer.valueOf(i), new ArrayMap<>()).getOrDefault(Boolean.valueOf(z), new ArraySet<>()));
        }
        return join;
    }

    public List<ComponentName> getAllowedComponents(int i) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> orDefault = this.mApproved.getOrDefault(Integer.valueOf(i), new ArrayMap<>());
            for (int i2 = 0; i2 < orDefault.size(); i2++) {
                ArraySet<String> valueAt = orDefault.valueAt(i2);
                for (int i3 = 0; i3 < valueAt.size(); i3++) {
                    ComponentName unflattenFromString = ComponentName.unflattenFromString(valueAt.valueAt(i3));
                    if (unflattenFromString != null) {
                        arrayList.add(unflattenFromString);
                    }
                }
            }
        }
        return arrayList;
    }

    public List<String> getAllowedPackages(int i) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> orDefault = this.mApproved.getOrDefault(Integer.valueOf(i), new ArrayMap<>());
            for (int i2 = 0; i2 < orDefault.size(); i2++) {
                ArraySet<String> valueAt = orDefault.valueAt(i2);
                for (int i3 = 0; i3 < valueAt.size(); i3++) {
                    String packageName = getPackageName(valueAt.valueAt(i3));
                    if (!TextUtils.isEmpty(packageName)) {
                        arrayList.add(packageName);
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean isPackageOrComponentAllowed(String str, int i) {
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> orDefault = this.mApproved.getOrDefault(Integer.valueOf(i), new ArrayMap<>());
            for (int i2 = 0; i2 < orDefault.size(); i2++) {
                if (orDefault.valueAt(i2).contains(str)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isPackageOrComponentUserSet(String str, int i) {
        boolean z;
        synchronized (this.mApproved) {
            ArraySet<String> arraySet = this.mUserSetServices.get(Integer.valueOf(i));
            z = arraySet != null && arraySet.contains(str);
        }
        return z;
    }

    public boolean isPackageAllowed(String str, int i) {
        if (str == null) {
            return false;
        }
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> orDefault = this.mApproved.getOrDefault(Integer.valueOf(i), new ArrayMap<>());
            for (int i2 = 0; i2 < orDefault.size(); i2++) {
                Iterator<String> it = orDefault.valueAt(i2).iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    ComponentName unflattenFromString = ComponentName.unflattenFromString(next);
                    if (unflattenFromString != null) {
                        if (str.equals(unflattenFromString.getPackageName())) {
                            return true;
                        }
                    } else if (str.equals(next)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void onPackagesChanged(boolean z, String[] strArr, int[] iArr) {
        boolean z2;
        if (this.DEBUG) {
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onPackagesChanged removingPackage=");
            sb.append(z);
            sb.append(" pkgList=");
            sb.append(strArr == null ? null : Arrays.asList(strArr));
            sb.append(" mEnabledServicesPackageNames=");
            sb.append(this.mEnabledServicesPackageNames);
            Slog.d(str, sb.toString());
        }
        if (strArr == null || strArr.length <= 0) {
            return;
        }
        if (!z || iArr == null) {
            z2 = false;
        } else {
            int min = Math.min(strArr.length, iArr.length);
            z2 = false;
            for (int i = 0; i < min; i++) {
                z2 = removeUninstalledItemsFromApprovedLists(UserHandle.getUserId(iArr[i]), strArr[i]);
            }
        }
        for (String str2 : strArr) {
            if (this.mEnabledServicesPackageNames.contains(str2)) {
                z2 = true;
            }
            if (iArr != null && iArr.length > 0) {
                for (int i2 : iArr) {
                    if (isPackageAllowed(str2, UserHandle.getUserId(i2))) {
                        z2 = true;
                    }
                }
            }
        }
        if (z2) {
            rebindServices(false, -1);
        }
    }

    public void onUserRemoved(int i) {
        String str = this.TAG;
        Slog.i(str, "Removing approved services for removed user " + i);
        synchronized (this.mApproved) {
            this.mApproved.remove(Integer.valueOf(i));
        }
        synchronized (this.mSnoozing) {
            this.mSnoozing.remove(i);
        }
        rebindServices(true, i);
    }

    public void onUserSwitched(int i) {
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.d(str, "onUserSwitched u=" + i);
        }
        unbindOtherUserServices(i);
        rebindServices(true, i);
    }

    public void onUserUnlocked(int i) {
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.d(str, "onUserUnlocked u=" + i);
        }
        rebindServices(false, i);
    }

    public final ManagedServiceInfo getServiceFromTokenLocked(IInterface iInterface) {
        if (iInterface == null) {
            return null;
        }
        IBinder asBinder = iInterface.asBinder();
        synchronized (this.mMutex) {
            int size = this.mServices.size();
            for (int i = 0; i < size; i++) {
                ManagedServiceInfo managedServiceInfo = this.mServices.get(i);
                if (managedServiceInfo.service.asBinder() == asBinder) {
                    return managedServiceInfo;
                }
            }
            return null;
        }
    }

    public boolean isServiceTokenValidLocked(IInterface iInterface) {
        return (iInterface == null || getServiceFromTokenLocked(iInterface) == null) ? false : true;
    }

    public ManagedServiceInfo checkServiceTokenLocked(IInterface iInterface) {
        checkNotNull(iInterface);
        ManagedServiceInfo serviceFromTokenLocked = getServiceFromTokenLocked(iInterface);
        if (serviceFromTokenLocked != null) {
            return serviceFromTokenLocked;
        }
        throw new SecurityException("Disallowed call from unknown " + getCaption() + ": " + iInterface + " " + iInterface.getClass());
    }

    public boolean isSameUser(IInterface iInterface, int i) {
        checkNotNull(iInterface);
        synchronized (this.mMutex) {
            ManagedServiceInfo serviceFromTokenLocked = getServiceFromTokenLocked(iInterface);
            if (serviceFromTokenLocked != null) {
                return serviceFromTokenLocked.isSameUser(i);
            }
            return false;
        }
    }

    public void unregisterService(IInterface iInterface, int i) {
        checkNotNull(iInterface);
        unregisterServiceImpl(iInterface, i);
    }

    public void registerSystemService(IInterface iInterface, ComponentName componentName, int i, int i2) {
        checkNotNull(iInterface);
        ManagedServiceInfo registerServiceImpl = registerServiceImpl(iInterface, componentName, i, FrameworkStatsLog.WIFI_BYTES_TRANSFER, i2);
        if (registerServiceImpl != null) {
            onServiceAdded(registerServiceImpl);
        }
    }

    public void registerGuestService(ManagedServiceInfo managedServiceInfo) {
        checkNotNull(managedServiceInfo.service);
        if (!checkType(managedServiceInfo.service)) {
            throw new IllegalArgumentException();
        }
        if (registerServiceImpl(managedServiceInfo) != null) {
            onServiceAdded(managedServiceInfo);
        }
    }

    public void setComponentState(ComponentName componentName, int i, boolean z) {
        synchronized (this.mSnoozing) {
            if ((!this.mSnoozing.contains(i, componentName)) == z) {
                return;
            }
            if (z) {
                this.mSnoozing.remove(i, componentName);
            } else {
                this.mSnoozing.add(i, componentName);
            }
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append(z ? "Enabling " : "Disabling ");
            sb.append("component ");
            sb.append(componentName.flattenToShortString());
            Slog.d(str, sb.toString());
            synchronized (this.mMutex) {
                if (z) {
                    if (!isPackageOrComponentAllowed(componentName.flattenToString(), i) && !isPackageOrComponentAllowed(componentName.getPackageName(), i)) {
                        String str2 = this.TAG;
                        Slog.d(str2, componentName + " no longer has permission to be bound");
                    }
                    registerServiceLocked(componentName, i);
                } else {
                    unregisterServiceLocked(componentName, i);
                }
            }
        }
    }

    public final ArraySet<ComponentName> loadComponentNamesFromValues(ArraySet<String> arraySet, int i) {
        if (arraySet == null || arraySet.size() == 0) {
            return new ArraySet<>();
        }
        ArraySet<ComponentName> arraySet2 = new ArraySet<>(arraySet.size());
        for (int i2 = 0; i2 < arraySet.size(); i2++) {
            String valueAt = arraySet.valueAt(i2);
            if (!TextUtils.isEmpty(valueAt)) {
                ComponentName unflattenFromString = ComponentName.unflattenFromString(valueAt);
                if (unflattenFromString != null) {
                    arraySet2.add(unflattenFromString);
                } else {
                    arraySet2.addAll(queryPackageForServices(valueAt, i));
                }
            }
        }
        return arraySet2;
    }

    public Set<ComponentName> queryPackageForServices(String str, int i) {
        return queryPackageForServices(str, 0, i);
    }

    public ArraySet<ComponentName> queryPackageForServices(String str, int i, int i2) {
        ArraySet<ComponentName> arraySet = new ArraySet<>();
        PackageManager packageManager = this.mContext.getPackageManager();
        Intent intent = new Intent(this.mConfig.serviceInterface);
        if (!TextUtils.isEmpty(str)) {
            intent.setPackage(str);
        }
        List queryIntentServicesAsUser = packageManager.queryIntentServicesAsUser(intent, i | 132, i2);
        if (this.DEBUG) {
            String str2 = this.TAG;
            Slog.v(str2, this.mConfig.serviceInterface + " services: " + queryIntentServicesAsUser);
        }
        if (queryIntentServicesAsUser != null) {
            int size = queryIntentServicesAsUser.size();
            for (int i3 = 0; i3 < size; i3++) {
                ServiceInfo serviceInfo = ((ResolveInfo) queryIntentServicesAsUser.get(i3)).serviceInfo;
                ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                if (!this.mConfig.bindPermission.equals(serviceInfo.permission)) {
                    String str3 = this.TAG;
                    Slog.w(str3, "Skipping " + getCaption() + " service " + serviceInfo.packageName + "/" + serviceInfo.name + ": it does not require the permission " + this.mConfig.bindPermission);
                } else {
                    arraySet.add(componentName);
                }
            }
        }
        return arraySet;
    }

    public final void trimApprovedListsAccordingToInstalledServices(int i) {
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> arrayMap = this.mApproved.get(Integer.valueOf(i));
            if (arrayMap == null) {
                return;
            }
            for (int i2 = 0; i2 < arrayMap.size(); i2++) {
                ArraySet<String> valueAt = arrayMap.valueAt(i2);
                for (int size = valueAt.size() - 1; size >= 0; size--) {
                    String valueAt2 = valueAt.valueAt(size);
                    if (!isValidEntry(valueAt2, i)) {
                        valueAt.removeAt(size);
                        Slog.v(this.TAG, "Removing " + valueAt2 + " from approved list; no matching services found");
                    } else if (this.DEBUG) {
                        Slog.v(this.TAG, "Keeping " + valueAt2 + " on approved list; matching services found");
                    }
                }
            }
        }
    }

    public final boolean removeUninstalledItemsFromApprovedLists(int i, String str) {
        synchronized (this.mApproved) {
            ArrayMap<Boolean, ArraySet<String>> arrayMap = this.mApproved.get(Integer.valueOf(i));
            if (arrayMap != null) {
                int size = arrayMap.size();
                for (int i2 = 0; i2 < size; i2++) {
                    ArraySet<String> valueAt = arrayMap.valueAt(i2);
                    for (int size2 = valueAt.size() - 1; size2 >= 0; size2--) {
                        String valueAt2 = valueAt.valueAt(size2);
                        if (TextUtils.equals(str, getPackageName(valueAt2))) {
                            valueAt.removeAt(size2);
                            if (this.DEBUG) {
                                Slog.v(this.TAG, "Removing " + valueAt2 + " from approved list; uninstalled");
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getPackageName(String str) {
        ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        return unflattenFromString != null ? unflattenFromString.getPackageName() : str;
    }

    public boolean isValidEntry(String str, int i) {
        return hasMatchingServices(str, i);
    }

    public final boolean hasMatchingServices(String str, int i) {
        return !TextUtils.isEmpty(str) && queryPackageForServices(getPackageName(str), i).size() > 0;
    }

    @VisibleForTesting
    public SparseArray<ArraySet<ComponentName>> getAllowedComponents(IntArray intArray) {
        int size = intArray.size();
        SparseArray<ArraySet<ComponentName>> sparseArray = new SparseArray<>();
        for (int i = 0; i < size; i++) {
            int i2 = intArray.get(i);
            synchronized (this.mApproved) {
                ArrayMap<Boolean, ArraySet<String>> arrayMap = this.mApproved.get(Integer.valueOf(i2));
                if (arrayMap != null) {
                    int size2 = arrayMap.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        ArraySet<ComponentName> arraySet = sparseArray.get(i2);
                        if (arraySet == null) {
                            arraySet = new ArraySet<>();
                            sparseArray.put(i2, arraySet);
                        }
                        arraySet.addAll((ArraySet<? extends ComponentName>) loadComponentNamesFromValues(arrayMap.valueAt(i3), i2));
                    }
                }
            }
        }
        return sparseArray;
    }

    @GuardedBy({"mMutex"})
    public void populateComponentsToBind(SparseArray<Set<ComponentName>> sparseArray, IntArray intArray, SparseArray<ArraySet<ComponentName>> sparseArray2) {
        this.mEnabledServicesForCurrentProfiles.clear();
        this.mEnabledServicesPackageNames.clear();
        int size = intArray.size();
        for (int i = 0; i < size; i++) {
            int i2 = intArray.get(i);
            ArraySet<ComponentName> arraySet = sparseArray2.get(i2);
            if (arraySet == null) {
                sparseArray.put(i2, new ArraySet());
            } else {
                HashSet hashSet = new HashSet(arraySet);
                ArraySet arraySet2 = this.mSnoozing.get(i2);
                if (arraySet2 != null) {
                    hashSet.removeAll(arraySet2);
                }
                sparseArray.put(i2, hashSet);
                this.mEnabledServicesForCurrentProfiles.addAll((ArraySet<? extends ComponentName>) arraySet);
                for (int i3 = 0; i3 < arraySet.size(); i3++) {
                    this.mEnabledServicesPackageNames.add(arraySet.valueAt(i3).getPackageName());
                }
            }
        }
    }

    @GuardedBy({"mMutex"})
    public Set<ManagedServiceInfo> getRemovableConnectedServices() {
        ArraySet arraySet = new ArraySet();
        Iterator<ManagedServiceInfo> it = this.mServices.iterator();
        while (it.hasNext()) {
            ManagedServiceInfo next = it.next();
            if (!next.isSystem && !next.isGuest(this)) {
                arraySet.add(next);
            }
        }
        return arraySet;
    }

    public void populateComponentsToUnbind(boolean z, Set<ManagedServiceInfo> set, SparseArray<Set<ComponentName>> sparseArray, SparseArray<Set<ComponentName>> sparseArray2) {
        for (ManagedServiceInfo managedServiceInfo : set) {
            Set<ComponentName> set2 = sparseArray.get(managedServiceInfo.userid);
            if (set2 != null && (z || !set2.contains(managedServiceInfo.component))) {
                Set<ComponentName> set3 = sparseArray2.get(managedServiceInfo.userid, new ArraySet());
                set3.add(managedServiceInfo.component);
                sparseArray2.put(managedServiceInfo.userid, set3);
            }
        }
    }

    public void rebindServices(boolean z, int i) {
        if (this.DEBUG) {
            String str = this.TAG;
            Slog.d(str, "rebindServices " + z + " " + i);
        }
        IntArray currentProfileIds = this.mUserProfiles.getCurrentProfileIds();
        if (i != -1) {
            currentProfileIds = new IntArray(1);
            currentProfileIds.add(i);
        }
        SparseArray<Set<ComponentName>> sparseArray = new SparseArray<>();
        SparseArray<Set<ComponentName>> sparseArray2 = new SparseArray<>();
        synchronized (this.mMutex) {
            SparseArray<ArraySet<ComponentName>> allowedComponents = getAllowedComponents(currentProfileIds);
            Set<ManagedServiceInfo> removableConnectedServices = getRemovableConnectedServices();
            populateComponentsToBind(sparseArray, currentProfileIds, allowedComponents);
            populateComponentsToUnbind(z, removableConnectedServices, sparseArray, sparseArray2);
        }
        unbindFromServices(sparseArray2);
        bindToServices(sparseArray);
    }

    @VisibleForTesting
    public void unbindOtherUserServices(int i) {
        TimingsTraceAndSlog timingsTraceAndSlog = new TimingsTraceAndSlog();
        timingsTraceAndSlog.traceBegin("ManagedServices.unbindOtherUserServices_current" + i);
        SparseArray<Set<ComponentName>> sparseArray = new SparseArray<>();
        synchronized (this.mMutex) {
            for (ManagedServiceInfo managedServiceInfo : getRemovableConnectedServices()) {
                int i2 = managedServiceInfo.userid;
                if (i2 != i) {
                    Set<ComponentName> set = sparseArray.get(i2, new ArraySet());
                    set.add(managedServiceInfo.component);
                    sparseArray.put(managedServiceInfo.userid, set);
                }
            }
        }
        unbindFromServices(sparseArray);
        timingsTraceAndSlog.traceEnd();
    }

    public void unbindFromServices(SparseArray<Set<ComponentName>> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            for (ComponentName componentName : sparseArray.get(keyAt)) {
                String str = this.TAG;
                Slog.v(str, "disabling " + getCaption() + " for user " + keyAt + ": " + componentName);
                unregisterService(componentName, keyAt);
            }
        }
    }

    public final void bindToServices(SparseArray<Set<ComponentName>> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            for (ComponentName componentName : sparseArray.get(keyAt)) {
                ServiceInfo serviceInfo = getServiceInfo(componentName, keyAt);
                if (serviceInfo == null) {
                    String str = this.TAG;
                    Slog.w(str, "Not binding " + getCaption() + " service " + componentName + ": service not found");
                } else if (!this.mConfig.bindPermission.equals(serviceInfo.permission)) {
                    String str2 = this.TAG;
                    Slog.w(str2, "Not binding " + getCaption() + " service " + componentName + ": it does not require the permission " + this.mConfig.bindPermission);
                } else if (!isAutobindAllowed(serviceInfo) && !isBoundOrRebinding(componentName, keyAt)) {
                    synchronized (this.mSnoozing) {
                        String str3 = this.TAG;
                        Slog.d(str3, "Not binding " + getCaption() + " service " + componentName + ": has META_DATA_DEFAULT_AUTOBIND = false");
                        this.mSnoozing.add(keyAt, componentName);
                    }
                } else {
                    String str4 = this.TAG;
                    Slog.v(str4, "enabling " + getCaption() + " for " + keyAt + ": " + componentName);
                    registerService(serviceInfo, keyAt);
                }
            }
        }
    }

    @VisibleForTesting
    public void registerService(ServiceInfo serviceInfo, int i) {
        ensureFilters(serviceInfo, i);
        registerService(serviceInfo.getComponentName(), i);
    }

    @VisibleForTesting
    public void registerService(ComponentName componentName, int i) {
        synchronized (this.mMutex) {
            registerServiceLocked(componentName, i);
        }
    }

    @VisibleForTesting
    public void reregisterService(ComponentName componentName, int i) {
        if (isPackageOrComponentAllowed(componentName.getPackageName(), i) || isPackageOrComponentAllowed(componentName.flattenToString(), i)) {
            registerService(componentName, i);
        }
    }

    @GuardedBy({"mMutex"})
    public final void registerServiceLocked(ComponentName componentName, int i) {
        registerServiceLocked(componentName, i, false);
    }

    @GuardedBy({"mMutex"})
    public final void registerServiceLocked(ComponentName componentName, int i, boolean z) {
        ApplicationInfo applicationInfo;
        if (this.DEBUG) {
            Slog.v(this.TAG, "registerService: " + componentName + " u=" + i);
        }
        Pair<ComponentName, Integer> create = Pair.create(componentName, Integer.valueOf(i));
        if (this.mServicesBound.contains(create)) {
            Slog.v(this.TAG, "Not registering " + componentName + " is already bound");
            return;
        }
        this.mServicesBound.add(create);
        for (int size = this.mServices.size() - 1; size >= 0; size--) {
            ManagedServiceInfo managedServiceInfo = this.mServices.get(size);
            if (componentName.equals(managedServiceInfo.component) && managedServiceInfo.userid == i) {
                Slog.v(this.TAG, "    disconnecting old " + getCaption() + ": " + managedServiceInfo.service);
                removeServiceLocked(size);
                ServiceConnection serviceConnection = managedServiceInfo.connection;
                if (serviceConnection != null) {
                    unbindService(serviceConnection, managedServiceInfo.component, managedServiceInfo.userid);
                }
            }
        }
        Intent intent = new Intent(this.mConfig.serviceInterface);
        intent.setComponent(componentName);
        intent.putExtra("android.intent.extra.client_label", this.mConfig.clientLabel);
        intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this.mContext, 0, new Intent(this.mConfig.settingsAction), 67108864));
        try {
            applicationInfo = this.mContext.getPackageManager().getApplicationInfo(componentName.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
        }
        int i2 = applicationInfo != null ? applicationInfo.targetSdkVersion : 1;
        int i3 = applicationInfo != null ? applicationInfo.uid : -1;
        try {
            Slog.v(this.TAG, "binding: " + intent);
            if (this.mContext.bindServiceAsUser(intent, new ServiceConnectionC11921(i, create, z, i2, i3), getBindFlags(), new UserHandle(i))) {
                return;
            }
            this.mServicesBound.remove(create);
            Slog.w(this.TAG, "Unable to bind " + getCaption() + " service: " + intent + " in user " + i);
        } catch (SecurityException e) {
            this.mServicesBound.remove(create);
            Slog.e(this.TAG, "Unable to bind " + getCaption() + " service: " + intent, e);
        }
    }

    /* renamed from: com.android.server.notification.ManagedServices$1 */
    /* loaded from: classes2.dex */
    public class ServiceConnectionC11921 implements ServiceConnection {
        public IInterface mService;
        public final /* synthetic */ boolean val$isSystem;
        public final /* synthetic */ Pair val$servicesBindingTag;
        public final /* synthetic */ int val$targetSdkVersion;
        public final /* synthetic */ int val$uid;
        public final /* synthetic */ int val$userid;

        public ServiceConnectionC11921(int i, Pair pair, boolean z, int i2, int i3) {
            this.val$userid = i;
            this.val$servicesBindingTag = pair;
            this.val$isSystem = z;
            this.val$targetSdkVersion = i2;
            this.val$uid = i3;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            boolean z;
            ManagedServiceInfo managedServiceInfo;
            String str = ManagedServices.this.TAG;
            Slog.v(str, this.val$userid + " " + ManagedServices.this.getCaption() + " service connected: " + componentName);
            synchronized (ManagedServices.this.mMutex) {
                ManagedServices.this.mServicesRebinding.remove(this.val$servicesBindingTag);
                z = false;
                managedServiceInfo = null;
                try {
                    IInterface asInterface = ManagedServices.this.asInterface(iBinder);
                    this.mService = asInterface;
                    managedServiceInfo = ManagedServices.this.newServiceInfo(asInterface, componentName, this.val$userid, this.val$isSystem, this, this.val$targetSdkVersion, this.val$uid);
                    iBinder.linkToDeath(managedServiceInfo, 0);
                    z = ManagedServices.this.mServices.add(managedServiceInfo);
                } catch (RemoteException e) {
                    Slog.e(ManagedServices.this.TAG, "Failed to linkToDeath, already dead", e);
                }
            }
            if (z) {
                ManagedServices.this.onServiceAdded(managedServiceInfo);
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            String str = ManagedServices.this.TAG;
            Slog.v(str, this.val$userid + " " + ManagedServices.this.getCaption() + " connection lost: " + componentName);
        }

        @Override // android.content.ServiceConnection
        public void onBindingDied(final ComponentName componentName) {
            String str = ManagedServices.this.TAG;
            Slog.w(str, this.val$userid + " " + ManagedServices.this.getCaption() + " binding died: " + componentName);
            synchronized (ManagedServices.this.mMutex) {
                ManagedServices.this.unbindService(this, componentName, this.val$userid);
                if (!ManagedServices.this.mServicesRebinding.contains(this.val$servicesBindingTag)) {
                    ManagedServices.this.mServicesRebinding.add(this.val$servicesBindingTag);
                    Handler handler = ManagedServices.this.mHandler;
                    final int i = this.val$userid;
                    handler.postDelayed(new Runnable() { // from class: com.android.server.notification.ManagedServices$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            ManagedServices.ServiceConnectionC11921.this.lambda$onBindingDied$0(componentName, i);
                        }
                    }, 10000L);
                } else {
                    String str2 = ManagedServices.this.TAG;
                    Slog.v(str2, ManagedServices.this.getCaption() + " not rebinding in user " + this.val$userid + " as a previous rebind attempt was made: " + componentName);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindingDied$0(ComponentName componentName, int i) {
            ManagedServices.this.reregisterService(componentName, i);
        }

        @Override // android.content.ServiceConnection
        public void onNullBinding(ComponentName componentName) {
            String str = ManagedServices.this.TAG;
            Slog.v(str, "onNullBinding() called with: name = [" + componentName + "]");
            ManagedServices.this.mContext.unbindService(this);
        }
    }

    public boolean isBound(ComponentName componentName, int i) {
        return this.mServicesBound.contains(Pair.create(componentName, Integer.valueOf(i)));
    }

    public boolean isBoundOrRebinding(ComponentName componentName, int i) {
        boolean z;
        synchronized (this.mMutex) {
            z = isBound(componentName, i) || this.mServicesRebinding.contains(Pair.create(componentName, Integer.valueOf(i)));
        }
        return z;
    }

    public final void unregisterService(ComponentName componentName, int i) {
        synchronized (this.mMutex) {
            unregisterServiceLocked(componentName, i);
        }
    }

    @GuardedBy({"mMutex"})
    public final void unregisterServiceLocked(ComponentName componentName, int i) {
        for (int size = this.mServices.size() - 1; size >= 0; size--) {
            ManagedServiceInfo managedServiceInfo = this.mServices.get(size);
            if (componentName.equals(managedServiceInfo.component) && managedServiceInfo.userid == i) {
                removeServiceLocked(size);
                ServiceConnection serviceConnection = managedServiceInfo.connection;
                if (serviceConnection != null) {
                    unbindService(serviceConnection, managedServiceInfo.component, managedServiceInfo.userid);
                }
            }
        }
    }

    public final ManagedServiceInfo removeServiceImpl(IInterface iInterface, int i) {
        ManagedServiceInfo managedServiceInfo;
        if (this.DEBUG) {
            Slog.d(this.TAG, "removeServiceImpl service=" + iInterface + " u=" + i);
        }
        synchronized (this.mMutex) {
            managedServiceInfo = null;
            for (int size = this.mServices.size() - 1; size >= 0; size--) {
                ManagedServiceInfo managedServiceInfo2 = this.mServices.get(size);
                if (managedServiceInfo2.service.asBinder() == iInterface.asBinder() && managedServiceInfo2.userid == i) {
                    Slog.d(this.TAG, "Removing active service " + managedServiceInfo2.component);
                    managedServiceInfo = removeServiceLocked(size);
                }
            }
        }
        return managedServiceInfo;
    }

    @GuardedBy({"mMutex"})
    public final ManagedServiceInfo removeServiceLocked(int i) {
        ManagedServiceInfo remove = this.mServices.remove(i);
        onServiceRemovedLocked(remove);
        return remove;
    }

    public final void checkNotNull(IInterface iInterface) {
        if (iInterface != null) {
            return;
        }
        throw new IllegalArgumentException(getCaption() + " must not be null");
    }

    public final ManagedServiceInfo registerServiceImpl(IInterface iInterface, ComponentName componentName, int i, int i2, int i3) {
        return registerServiceImpl(newServiceInfo(iInterface, componentName, i, true, null, i2, i3));
    }

    public final ManagedServiceInfo registerServiceImpl(ManagedServiceInfo managedServiceInfo) {
        synchronized (this.mMutex) {
            try {
                try {
                    managedServiceInfo.service.asBinder().linkToDeath(managedServiceInfo, 0);
                    this.mServices.add(managedServiceInfo);
                } catch (RemoteException unused) {
                    return null;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return managedServiceInfo;
    }

    public final void unregisterServiceImpl(IInterface iInterface, int i) {
        ManagedServiceInfo removeServiceImpl = removeServiceImpl(iInterface, i);
        if (removeServiceImpl == null || removeServiceImpl.connection == null || removeServiceImpl.isGuest(this)) {
            return;
        }
        unbindService(removeServiceImpl.connection, removeServiceImpl.component, removeServiceImpl.userid);
    }

    public final void unbindService(ServiceConnection serviceConnection, ComponentName componentName, int i) {
        try {
            this.mContext.unbindService(serviceConnection);
        } catch (IllegalArgumentException e) {
            String str = this.TAG;
            Slog.e(str, getCaption() + " " + componentName + " could not be unbound", e);
        }
        synchronized (this.mMutex) {
            this.mServicesBound.remove(Pair.create(componentName, Integer.valueOf(i)));
        }
    }

    public final ServiceInfo getServiceInfo(ComponentName componentName, int i) {
        try {
            return this.mPm.getServiceInfo(componentName, 786560L, i);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public final boolean isAutobindAllowed(ServiceInfo serviceInfo) {
        Bundle bundle;
        if (serviceInfo == null || (bundle = serviceInfo.metaData) == null || !bundle.containsKey("android.service.notification.default_autobind_listenerservice")) {
            return true;
        }
        return serviceInfo.metaData.getBoolean("android.service.notification.default_autobind_listenerservice", true);
    }

    /* loaded from: classes2.dex */
    public class ManagedServiceInfo implements IBinder.DeathRecipient {
        public ComponentName component;
        public ServiceConnection connection;
        public boolean isSystem;
        public Pair<ComponentName, Integer> mKey;
        public IInterface service;
        public int targetSdkVersion;
        public int uid;
        public int userid;

        public ManagedServiceInfo(IInterface iInterface, ComponentName componentName, int i, boolean z, ServiceConnection serviceConnection, int i2, int i3) {
            this.service = iInterface;
            this.component = componentName;
            this.userid = i;
            this.isSystem = z;
            this.connection = serviceConnection;
            this.targetSdkVersion = i2;
            this.uid = i3;
            this.mKey = Pair.create(componentName, Integer.valueOf(i));
        }

        public boolean isGuest(ManagedServices managedServices) {
            return ManagedServices.this != managedServices;
        }

        public ManagedServices getOwner() {
            return ManagedServices.this;
        }

        public IInterface getService() {
            return this.service;
        }

        public boolean isSystem() {
            return this.isSystem;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("ManagedServiceInfo[");
            sb.append("component=");
            sb.append(this.component);
            sb.append(",userid=");
            sb.append(this.userid);
            sb.append(",isSystem=");
            sb.append(this.isSystem);
            sb.append(",targetSdkVersion=");
            sb.append(this.targetSdkVersion);
            sb.append(",connection=");
            sb.append(this.connection == null ? null : "<connection>");
            sb.append(",service=");
            sb.append(this.service);
            sb.append(']');
            return sb.toString();
        }

        public void dumpDebug(ProtoOutputStream protoOutputStream, long j, ManagedServices managedServices) {
            long start = protoOutputStream.start(j);
            this.component.dumpDebug(protoOutputStream, 1146756268033L);
            protoOutputStream.write(1120986464258L, this.userid);
            protoOutputStream.write(1138166333443L, this.service.getClass().getName());
            protoOutputStream.write(1133871366148L, this.isSystem);
            protoOutputStream.write(1133871366149L, isGuest(managedServices));
            protoOutputStream.end(start);
        }

        public boolean isSameUser(int i) {
            if (isEnabledForCurrentProfiles()) {
                return i == -1 || i == this.userid;
            }
            return false;
        }

        public boolean enabledAndUserMatches(int i) {
            if (isEnabledForCurrentProfiles()) {
                int i2 = this.userid;
                if (i2 == -1 || this.isSystem || i == -1 || i == i2) {
                    return true;
                }
                return supportsProfiles() && ManagedServices.this.mUserProfiles.isCurrentProfile(i) && isPermittedForProfile(i);
            }
            return false;
        }

        public boolean supportsProfiles() {
            return this.targetSdkVersion >= 21;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            ManagedServices managedServices = ManagedServices.this;
            if (managedServices.DEBUG) {
                Slog.d(managedServices.TAG, "binderDied");
            }
            ManagedServices.this.removeServiceImpl(this.service, this.userid);
        }

        public boolean isEnabledForCurrentProfiles() {
            if (this.isSystem) {
                return true;
            }
            if (this.connection == null) {
                return false;
            }
            return ManagedServices.this.mEnabledServicesForCurrentProfiles.contains(this.component);
        }

        public boolean isPermittedForProfile(int i) {
            if (ManagedServices.this.mUserProfiles.isProfileUser(i)) {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) ManagedServices.this.mContext.getSystemService("device_policy");
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return devicePolicyManager.isNotificationListenerServicePermitted(this.component.getPackageName(), i);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return true;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ManagedServiceInfo managedServiceInfo = (ManagedServiceInfo) obj;
            return this.userid == managedServiceInfo.userid && this.isSystem == managedServiceInfo.isSystem && this.targetSdkVersion == managedServiceInfo.targetSdkVersion && Objects.equals(this.service, managedServiceInfo.service) && Objects.equals(this.component, managedServiceInfo.component) && Objects.equals(this.connection, managedServiceInfo.connection);
        }

        public int hashCode() {
            return Objects.hash(this.service, this.component, Integer.valueOf(this.userid), Boolean.valueOf(this.isSystem), this.connection, Integer.valueOf(this.targetSdkVersion));
        }
    }

    public boolean isComponentEnabledForCurrentProfiles(ComponentName componentName) {
        return this.mEnabledServicesForCurrentProfiles.contains(componentName);
    }

    /* loaded from: classes2.dex */
    public static class UserProfiles {
        public final SparseArray<UserInfo> mCurrentProfiles = new SparseArray<>();

        public void updateCache(Context context) {
            UserManager userManager = (UserManager) context.getSystemService("user");
            if (userManager != null) {
                List<UserInfo> profiles = userManager.getProfiles(ActivityManager.getCurrentUser());
                synchronized (this.mCurrentProfiles) {
                    this.mCurrentProfiles.clear();
                    for (UserInfo userInfo : profiles) {
                        this.mCurrentProfiles.put(userInfo.id, userInfo);
                    }
                }
            }
        }

        public IntArray getCurrentProfileIds() {
            IntArray intArray;
            synchronized (this.mCurrentProfiles) {
                intArray = new IntArray(this.mCurrentProfiles.size());
                int size = this.mCurrentProfiles.size();
                for (int i = 0; i < size; i++) {
                    intArray.add(this.mCurrentProfiles.keyAt(i));
                }
            }
            return intArray;
        }

        public boolean isCurrentProfile(int i) {
            boolean z;
            synchronized (this.mCurrentProfiles) {
                z = this.mCurrentProfiles.get(i) != null;
            }
            return z;
        }

        public boolean isProfileUser(int i) {
            synchronized (this.mCurrentProfiles) {
                UserInfo userInfo = this.mCurrentProfiles.get(i);
                if (userInfo == null) {
                    return false;
                }
                if (!userInfo.isManagedProfile() && !userInfo.isCloneProfile()) {
                    return false;
                }
                return true;
            }
        }
    }
}
