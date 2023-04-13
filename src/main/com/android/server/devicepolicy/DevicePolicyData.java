package com.android.server.devicepolicy;

import android.app.admin.DeviceAdminInfo;
import android.content.ComponentName;
import android.os.FileUtils;
import android.os.PersistableBundle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.IndentingPrintWriter;
import android.util.Xml;
import com.android.internal.util.JournaledFile;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.utils.Slogf;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DevicePolicyData {
    public String mCurrentRoleHolder;
    public int mFactoryResetFlags;
    public String mFactoryResetReason;
    public ActiveAdmin mPermissionBasedAdmin;
    public int mPermissionPolicy;
    public ComponentName mRestrictionsProvider;
    @Deprecated
    public List<String> mUserControlDisabledPackages;
    public final int mUserId;
    public int mUserProvisioningState;
    public int mFailedPasswordAttempts = 0;
    public boolean mPasswordValidAtLastCheckpoint = true;
    public int mPasswordOwner = -1;
    public long mLastMaximumTimeToLock = -1;
    public boolean mUserSetupComplete = false;
    public boolean mBypassDevicePolicyManagementRoleQualifications = false;
    public boolean mPaired = false;
    public boolean mDeviceProvisioningConfigApplied = false;
    public final ArrayMap<ComponentName, ActiveAdmin> mAdminMap = new ArrayMap<>();
    public final ArrayList<ActiveAdmin> mAdminList = new ArrayList<>();
    public final ArrayList<ComponentName> mRemovingAdmins = new ArrayList<>();
    public final ArraySet<String> mAcceptedCaCertificates = new ArraySet<>();
    public List<String> mLockTaskPackages = new ArrayList();
    public int mLockTaskFeatures = 16;
    public boolean mStatusBarDisabled = false;
    public final ArrayMap<String, List<String>> mDelegationMap = new ArrayMap<>();
    public boolean mDoNotAskCredentialsOnBoot = false;
    public Set<String> mAffiliationIds = new ArraySet();
    public long mLastSecurityLogRetrievalTime = -1;
    public long mLastBugReportRequestTime = -1;
    public long mLastNetworkLogsRetrievalTime = -1;
    public boolean mCurrentInputMethodSet = false;
    public boolean mSecondaryLockscreenEnabled = false;
    public Set<String> mOwnerInstalledCaCerts = new ArraySet();
    public boolean mAdminBroadcastPending = false;
    public PersistableBundle mInitBundle = null;
    public long mPasswordTokenHandle = 0;
    public boolean mAppsSuspended = false;
    public String mNewUserDisclaimer = "not_needed";

    public ActiveAdmin createOrGetPermissionBasedAdmin(int i) {
        if (this.mPermissionBasedAdmin == null) {
            this.mPermissionBasedAdmin = new ActiveAdmin(i, true);
        }
        return this.mPermissionBasedAdmin;
    }

    public DevicePolicyData(int i) {
        this.mUserId = i;
    }

    public static boolean store(DevicePolicyData devicePolicyData, JournaledFile journaledFile) {
        File file;
        FileOutputStream fileOutputStream;
        String str;
        String str2 = "do-not-ask-credentials-on-boot";
        String str3 = "statusbar";
        try {
            file = journaledFile.chooseForWrite();
            try {
                String str4 = "affiliation-id";
                FileOutputStream fileOutputStream2 = new FileOutputStream(file, false);
                try {
                    TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(fileOutputStream2);
                    try {
                        fileOutputStream = fileOutputStream2;
                        try {
                            resolveSerializer.startDocument((String) null, Boolean.TRUE);
                            resolveSerializer.startTag((String) null, "policies");
                            ComponentName componentName = devicePolicyData.mRestrictionsProvider;
                            if (componentName != null) {
                                str = "policies";
                                resolveSerializer.attribute((String) null, "permission-provider", componentName.flattenToString());
                            } else {
                                str = "policies";
                            }
                            if (devicePolicyData.mUserSetupComplete) {
                                resolveSerializer.attributeBoolean((String) null, "setup-complete", true);
                            }
                            if (devicePolicyData.mPaired) {
                                resolveSerializer.attributeBoolean((String) null, "device-paired", true);
                            }
                            if (devicePolicyData.mDeviceProvisioningConfigApplied) {
                                resolveSerializer.attributeBoolean((String) null, "device-provisioning-config-applied", true);
                            }
                            int i = devicePolicyData.mUserProvisioningState;
                            if (i != 0) {
                                resolveSerializer.attributeInt((String) null, "provisioning-state", i);
                            }
                            int i2 = devicePolicyData.mPermissionPolicy;
                            if (i2 != 0) {
                                resolveSerializer.attributeInt((String) null, "permission-policy", i2);
                            }
                            if ("needed".equals(devicePolicyData.mNewUserDisclaimer)) {
                                resolveSerializer.attribute((String) null, "new-user-disclaimer", devicePolicyData.mNewUserDisclaimer);
                            }
                            int i3 = devicePolicyData.mFactoryResetFlags;
                            if (i3 != 0) {
                                resolveSerializer.attributeInt((String) null, "factory-reset-flags", i3);
                            }
                            String str5 = devicePolicyData.mFactoryResetReason;
                            if (str5 != null) {
                                resolveSerializer.attribute((String) null, "factory-reset-reason", str5);
                            }
                            for (int i4 = 0; i4 < devicePolicyData.mDelegationMap.size(); i4++) {
                                String keyAt = devicePolicyData.mDelegationMap.keyAt(i4);
                                Iterator<String> it = devicePolicyData.mDelegationMap.valueAt(i4).iterator();
                                while (it.hasNext()) {
                                    Iterator<String> it2 = it;
                                    String next = it.next();
                                    resolveSerializer.startTag((String) null, "delegation");
                                    resolveSerializer.attribute((String) null, "delegatePackage", keyAt);
                                    resolveSerializer.attribute((String) null, "scope", next);
                                    resolveSerializer.endTag((String) null, "delegation");
                                    str2 = str2;
                                    it = it2;
                                    str3 = str3;
                                }
                            }
                            String str6 = str2;
                            String str7 = str3;
                            int size = devicePolicyData.mAdminList.size();
                            for (int i5 = 0; i5 < size; i5++) {
                                ActiveAdmin activeAdmin = devicePolicyData.mAdminList.get(i5);
                                if (activeAdmin != null) {
                                    resolveSerializer.startTag((String) null, "admin");
                                    resolveSerializer.attribute((String) null, "name", activeAdmin.info.getComponent().flattenToString());
                                    activeAdmin.writeToXml(resolveSerializer);
                                    resolveSerializer.endTag((String) null, "admin");
                                }
                            }
                            if (devicePolicyData.mPermissionBasedAdmin != null) {
                                resolveSerializer.startTag((String) null, "permission-based-admin");
                                devicePolicyData.mPermissionBasedAdmin.writeToXml(resolveSerializer);
                                resolveSerializer.endTag((String) null, "permission-based-admin");
                            }
                            if (devicePolicyData.mPasswordOwner >= 0) {
                                resolveSerializer.startTag((String) null, "password-owner");
                                resolveSerializer.attributeInt((String) null, "value", devicePolicyData.mPasswordOwner);
                                resolveSerializer.endTag((String) null, "password-owner");
                            }
                            if (devicePolicyData.mFailedPasswordAttempts != 0) {
                                resolveSerializer.startTag((String) null, "failed-password-attempts");
                                resolveSerializer.attributeInt((String) null, "value", devicePolicyData.mFailedPasswordAttempts);
                                resolveSerializer.endTag((String) null, "failed-password-attempts");
                            }
                            for (int i6 = 0; i6 < devicePolicyData.mAcceptedCaCertificates.size(); i6++) {
                                resolveSerializer.startTag((String) null, "accepted-ca-certificate");
                                resolveSerializer.attribute((String) null, "name", devicePolicyData.mAcceptedCaCertificates.valueAt(i6));
                                resolveSerializer.endTag((String) null, "accepted-ca-certificate");
                            }
                            for (int i7 = 0; i7 < devicePolicyData.mLockTaskPackages.size(); i7++) {
                                resolveSerializer.startTag((String) null, "lock-task-component");
                                resolveSerializer.attribute((String) null, "name", devicePolicyData.mLockTaskPackages.get(i7));
                                resolveSerializer.endTag((String) null, "lock-task-component");
                            }
                            if (devicePolicyData.mLockTaskFeatures != 0) {
                                resolveSerializer.startTag((String) null, "lock-task-features");
                                resolveSerializer.attributeInt((String) null, "value", devicePolicyData.mLockTaskFeatures);
                                resolveSerializer.endTag((String) null, "lock-task-features");
                            }
                            if (devicePolicyData.mSecondaryLockscreenEnabled) {
                                resolveSerializer.startTag((String) null, "secondary-lock-screen");
                                resolveSerializer.attributeBoolean((String) null, "value", true);
                                resolveSerializer.endTag((String) null, "secondary-lock-screen");
                            }
                            if (devicePolicyData.mStatusBarDisabled) {
                                resolveSerializer.startTag((String) null, str7);
                                resolveSerializer.attributeBoolean((String) null, "disabled", devicePolicyData.mStatusBarDisabled);
                                resolveSerializer.endTag((String) null, str7);
                            }
                            if (devicePolicyData.mDoNotAskCredentialsOnBoot) {
                                resolveSerializer.startTag((String) null, str6);
                                resolveSerializer.endTag((String) null, str6);
                            }
                            for (String str8 : devicePolicyData.mAffiliationIds) {
                                String str9 = str4;
                                resolveSerializer.startTag((String) null, str9);
                                resolveSerializer.attribute((String) null, "id", str8);
                                resolveSerializer.endTag((String) null, str9);
                                str4 = str9;
                            }
                            if (devicePolicyData.mLastSecurityLogRetrievalTime >= 0) {
                                resolveSerializer.startTag((String) null, "last-security-log-retrieval");
                                resolveSerializer.attributeLong((String) null, "value", devicePolicyData.mLastSecurityLogRetrievalTime);
                                resolveSerializer.endTag((String) null, "last-security-log-retrieval");
                            }
                            if (devicePolicyData.mLastBugReportRequestTime >= 0) {
                                resolveSerializer.startTag((String) null, "last-bug-report-request");
                                resolveSerializer.attributeLong((String) null, "value", devicePolicyData.mLastBugReportRequestTime);
                                resolveSerializer.endTag((String) null, "last-bug-report-request");
                            }
                            if (devicePolicyData.mLastNetworkLogsRetrievalTime >= 0) {
                                resolveSerializer.startTag((String) null, "last-network-log-retrieval");
                                resolveSerializer.attributeLong((String) null, "value", devicePolicyData.mLastNetworkLogsRetrievalTime);
                                resolveSerializer.endTag((String) null, "last-network-log-retrieval");
                            }
                            if (devicePolicyData.mAdminBroadcastPending) {
                                resolveSerializer.startTag((String) null, "admin-broadcast-pending");
                                resolveSerializer.attributeBoolean((String) null, "value", devicePolicyData.mAdminBroadcastPending);
                                resolveSerializer.endTag((String) null, "admin-broadcast-pending");
                            }
                            if (devicePolicyData.mInitBundle != null) {
                                resolveSerializer.startTag((String) null, "initialization-bundle");
                                devicePolicyData.mInitBundle.saveToXml(resolveSerializer);
                                resolveSerializer.endTag((String) null, "initialization-bundle");
                            }
                            if (devicePolicyData.mPasswordTokenHandle != 0) {
                                resolveSerializer.startTag((String) null, "password-token");
                                resolveSerializer.attributeLong((String) null, "value", devicePolicyData.mPasswordTokenHandle);
                                resolveSerializer.endTag((String) null, "password-token");
                            }
                            if (devicePolicyData.mCurrentInputMethodSet) {
                                resolveSerializer.startTag((String) null, "current-ime-set");
                                resolveSerializer.endTag((String) null, "current-ime-set");
                            }
                            for (String str10 : devicePolicyData.mOwnerInstalledCaCerts) {
                                resolveSerializer.startTag((String) null, "owner-installed-ca-cert");
                                resolveSerializer.attribute((String) null, "alias", str10);
                                resolveSerializer.endTag((String) null, "owner-installed-ca-cert");
                            }
                            if (devicePolicyData.mAppsSuspended) {
                                resolveSerializer.startTag((String) null, "apps-suspended");
                                resolveSerializer.attributeBoolean((String) null, "value", devicePolicyData.mAppsSuspended);
                                resolveSerializer.endTag((String) null, "apps-suspended");
                            }
                            if (devicePolicyData.mBypassDevicePolicyManagementRoleQualifications) {
                                resolveSerializer.startTag((String) null, "bypass-role-qualifications");
                                resolveSerializer.attribute((String) null, "value", devicePolicyData.mCurrentRoleHolder);
                                resolveSerializer.endTag((String) null, "bypass-role-qualifications");
                            }
                            resolveSerializer.endTag((String) null, str);
                            resolveSerializer.endDocument();
                            fileOutputStream.flush();
                            FileUtils.sync(fileOutputStream);
                            fileOutputStream.close();
                            journaledFile.commit();
                            return true;
                        } catch (IOException | XmlPullParserException e) {
                            e = e;
                            file = file;
                            Slogf.m10w("DevicePolicyManager", e, "failed writing file %s", file);
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException unused) {
                                }
                            }
                            journaledFile.rollback();
                            return false;
                        }
                    } catch (IOException | XmlPullParserException e2) {
                        e = e2;
                        fileOutputStream = fileOutputStream2;
                    }
                } catch (IOException | XmlPullParserException e3) {
                    e = e3;
                    fileOutputStream = fileOutputStream2;
                }
            } catch (IOException | XmlPullParserException e4) {
                e = e4;
                fileOutputStream = null;
            }
        } catch (IOException | XmlPullParserException e5) {
            e = e5;
            file = null;
            fileOutputStream = null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:147:0x030c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void load(DevicePolicyData devicePolicyData, JournaledFile journaledFile, Function<ComponentName, DeviceAdminInfo> function, ComponentName componentName) {
        FileInputStream fileInputStream;
        TypedXmlPullParser resolvePullParser;
        String name;
        File chooseForRead = journaledFile.chooseForRead();
        FileInputStream fileInputStream2 = null;
        try {
            fileInputStream = new FileInputStream(chooseForRead);
            try {
                try {
                    resolvePullParser = Xml.resolvePullParser(fileInputStream);
                    while (true) {
                        int next = resolvePullParser.next();
                        if (next == 1 || next == 2) {
                            break;
                        }
                    }
                    name = resolvePullParser.getName();
                } catch (FileNotFoundException unused) {
                    fileInputStream2 = fileInputStream;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                    }
                    devicePolicyData.mAdminList.addAll(devicePolicyData.mAdminMap.values());
                }
            } catch (IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException e) {
                e = e;
                fileInputStream2 = fileInputStream;
                Slogf.m10w("DevicePolicyManager", e, "failed parsing %s", chooseForRead);
                fileInputStream = fileInputStream2;
                if (fileInputStream != null) {
                }
                devicePolicyData.mAdminList.addAll(devicePolicyData.mAdminMap.values());
            }
        } catch (FileNotFoundException unused2) {
        } catch (IOException | IndexOutOfBoundsException | NullPointerException | NumberFormatException | XmlPullParserException e2) {
            e = e2;
        }
        if (!"policies".equals(name)) {
            throw new XmlPullParserException("Settings do not start with policies tag: found " + name);
        }
        String attributeValue = resolvePullParser.getAttributeValue((String) null, "permission-provider");
        if (attributeValue != null) {
            devicePolicyData.mRestrictionsProvider = ComponentName.unflattenFromString(attributeValue);
        }
        if (Boolean.toString(true).equals(resolvePullParser.getAttributeValue((String) null, "setup-complete"))) {
            devicePolicyData.mUserSetupComplete = true;
        }
        if (Boolean.toString(true).equals(resolvePullParser.getAttributeValue((String) null, "device-paired"))) {
            devicePolicyData.mPaired = true;
        }
        if (Boolean.toString(true).equals(resolvePullParser.getAttributeValue((String) null, "device-provisioning-config-applied"))) {
            devicePolicyData.mDeviceProvisioningConfigApplied = true;
        }
        int attributeInt = resolvePullParser.getAttributeInt((String) null, "provisioning-state", -1);
        if (attributeInt != -1) {
            devicePolicyData.mUserProvisioningState = attributeInt;
        }
        int attributeInt2 = resolvePullParser.getAttributeInt((String) null, "permission-policy", -1);
        if (attributeInt2 != -1) {
            devicePolicyData.mPermissionPolicy = attributeInt2;
        }
        devicePolicyData.mNewUserDisclaimer = resolvePullParser.getAttributeValue((String) null, "new-user-disclaimer");
        devicePolicyData.mFactoryResetFlags = resolvePullParser.getAttributeInt((String) null, "factory-reset-flags", 0);
        devicePolicyData.mFactoryResetReason = resolvePullParser.getAttributeValue((String) null, "factory-reset-reason");
        int depth = resolvePullParser.getDepth();
        devicePolicyData.mLockTaskPackages.clear();
        devicePolicyData.mAdminList.clear();
        devicePolicyData.mAdminMap.clear();
        devicePolicyData.mPermissionBasedAdmin = null;
        devicePolicyData.mAffiliationIds.clear();
        devicePolicyData.mOwnerInstalledCaCerts.clear();
        devicePolicyData.mUserControlDisabledPackages = null;
        while (true) {
            int next2 = resolvePullParser.next();
            if (next2 == 1 || (next2 == 3 && resolvePullParser.getDepth() <= depth)) {
                break;
            } else if (next2 != 3 && next2 != 4) {
                String name2 = resolvePullParser.getName();
                if ("admin".equals(name2)) {
                    String attributeValue2 = resolvePullParser.getAttributeValue((String) null, "name");
                    try {
                        DeviceAdminInfo apply = function.apply(ComponentName.unflattenFromString(attributeValue2));
                        if (apply != null) {
                            boolean z = !apply.getComponent().equals(componentName);
                            ActiveAdmin activeAdmin = new ActiveAdmin(apply, false);
                            activeAdmin.readFromXml(resolvePullParser, z);
                            devicePolicyData.mAdminMap.put(activeAdmin.info.getComponent(), activeAdmin);
                        }
                    } catch (RuntimeException e3) {
                        Slogf.m10w("DevicePolicyManager", e3, "Failed loading admin %s", attributeValue2);
                    }
                } else if ("permission-based-admin".equals(name2)) {
                    ActiveAdmin activeAdmin2 = new ActiveAdmin(devicePolicyData.mUserId, true);
                    activeAdmin2.readFromXml(resolvePullParser, false);
                    devicePolicyData.mPermissionBasedAdmin = activeAdmin2;
                } else if ("delegation".equals(name2)) {
                    String attributeValue3 = resolvePullParser.getAttributeValue((String) null, "delegatePackage");
                    String attributeValue4 = resolvePullParser.getAttributeValue((String) null, "scope");
                    List<String> list = devicePolicyData.mDelegationMap.get(attributeValue3);
                    if (list == null) {
                        list = new ArrayList<>();
                        devicePolicyData.mDelegationMap.put(attributeValue3, list);
                    }
                    if (!list.contains(attributeValue4)) {
                        list.add(attributeValue4);
                    }
                } else if ("failed-password-attempts".equals(name2)) {
                    devicePolicyData.mFailedPasswordAttempts = resolvePullParser.getAttributeInt((String) null, "value");
                } else if ("password-owner".equals(name2)) {
                    devicePolicyData.mPasswordOwner = resolvePullParser.getAttributeInt((String) null, "value");
                } else if ("accepted-ca-certificate".equals(name2)) {
                    devicePolicyData.mAcceptedCaCertificates.add(resolvePullParser.getAttributeValue((String) null, "name"));
                } else if ("lock-task-component".equals(name2)) {
                    devicePolicyData.mLockTaskPackages.add(resolvePullParser.getAttributeValue((String) null, "name"));
                } else if ("lock-task-features".equals(name2)) {
                    devicePolicyData.mLockTaskFeatures = resolvePullParser.getAttributeInt((String) null, "value");
                } else if ("secondary-lock-screen".equals(name2)) {
                    devicePolicyData.mSecondaryLockscreenEnabled = resolvePullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("statusbar".equals(name2)) {
                    devicePolicyData.mStatusBarDisabled = resolvePullParser.getAttributeBoolean((String) null, "disabled", false);
                } else if ("do-not-ask-credentials-on-boot".equals(name2)) {
                    devicePolicyData.mDoNotAskCredentialsOnBoot = true;
                } else if ("affiliation-id".equals(name2)) {
                    devicePolicyData.mAffiliationIds.add(resolvePullParser.getAttributeValue((String) null, "id"));
                } else if ("last-security-log-retrieval".equals(name2)) {
                    devicePolicyData.mLastSecurityLogRetrievalTime = resolvePullParser.getAttributeLong((String) null, "value");
                } else if ("last-bug-report-request".equals(name2)) {
                    devicePolicyData.mLastBugReportRequestTime = resolvePullParser.getAttributeLong((String) null, "value");
                } else if ("last-network-log-retrieval".equals(name2)) {
                    devicePolicyData.mLastNetworkLogsRetrievalTime = resolvePullParser.getAttributeLong((String) null, "value");
                } else if ("admin-broadcast-pending".equals(name2)) {
                    devicePolicyData.mAdminBroadcastPending = Boolean.toString(true).equals(resolvePullParser.getAttributeValue((String) null, "value"));
                } else if ("initialization-bundle".equals(name2)) {
                    devicePolicyData.mInitBundle = PersistableBundle.restoreFromXml(resolvePullParser);
                } else if ("password-token".equals(name2)) {
                    devicePolicyData.mPasswordTokenHandle = resolvePullParser.getAttributeLong((String) null, "value");
                } else if ("current-ime-set".equals(name2)) {
                    devicePolicyData.mCurrentInputMethodSet = true;
                } else if ("owner-installed-ca-cert".equals(name2)) {
                    devicePolicyData.mOwnerInstalledCaCerts.add(resolvePullParser.getAttributeValue((String) null, "alias"));
                } else if ("apps-suspended".equals(name2)) {
                    devicePolicyData.mAppsSuspended = resolvePullParser.getAttributeBoolean((String) null, "value", false);
                } else if ("bypass-role-qualifications".equals(name2)) {
                    devicePolicyData.mBypassDevicePolicyManagementRoleQualifications = true;
                    devicePolicyData.mCurrentRoleHolder = resolvePullParser.getAttributeValue((String) null, "value");
                } else if ("protected-packages".equals(name2)) {
                    if (devicePolicyData.mUserControlDisabledPackages == null) {
                        devicePolicyData.mUserControlDisabledPackages = new ArrayList();
                    }
                    devicePolicyData.mUserControlDisabledPackages.add(resolvePullParser.getAttributeValue((String) null, "name"));
                } else {
                    Slogf.m12w("DevicePolicyManager", "Unknown tag: %s", name2);
                    XmlUtils.skipCurrentTag(resolvePullParser);
                }
            }
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
            } catch (IOException unused3) {
            }
        }
        devicePolicyData.mAdminList.addAll(devicePolicyData.mAdminMap.values());
    }

    public void validatePasswordOwner() {
        if (this.mPasswordOwner >= 0) {
            boolean z = true;
            int size = this.mAdminList.size() - 1;
            while (true) {
                if (size < 0) {
                    z = false;
                    break;
                } else if (this.mAdminList.get(size).getUid() == this.mPasswordOwner) {
                    break;
                } else {
                    size--;
                }
            }
            if (z) {
                return;
            }
            Slogf.m12w("DevicePolicyManager", "Previous password owner %s no longer active; disabling", Integer.valueOf(this.mPasswordOwner));
            this.mPasswordOwner = -1;
        }
    }

    public void setDelayedFactoryReset(String str, boolean z, boolean z2, boolean z3) {
        this.mFactoryResetReason = str;
        this.mFactoryResetFlags = 1;
        if (z) {
            this.mFactoryResetFlags = 1 | 2;
        }
        if (z2) {
            this.mFactoryResetFlags |= 4;
        }
        if (z3) {
            this.mFactoryResetFlags |= 8;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public boolean isNewUserDisclaimerAcknowledged() {
        String str = this.mNewUserDisclaimer;
        if (str == null) {
            int i = this.mUserId;
            if (i == 0) {
                return true;
            }
            Slogf.m12w("DevicePolicyManager", "isNewUserDisclaimerAcknowledged(%d): mNewUserDisclaimer is null", Integer.valueOf(i));
            return false;
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1238968671:
                if (str.equals("not_needed")) {
                    c = 0;
                    break;
                }
                break;
            case -1049376843:
                if (str.equals("needed")) {
                    c = 1;
                    break;
                }
                break;
            case 92636904:
                if (str.equals("acked")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 2:
                return true;
            case 1:
                break;
            default:
                Slogf.m12w("DevicePolicyManager", "isNewUserDisclaimerAcknowledged(%d): invalid value %d", Integer.valueOf(this.mUserId), this.mNewUserDisclaimer);
                break;
        }
        return false;
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        indentingPrintWriter.println();
        indentingPrintWriter.println("Enabled Device Admins (User " + this.mUserId + ", provisioningState: " + this.mUserProvisioningState + "):");
        int size = this.mAdminList.size();
        for (int i = 0; i < size; i++) {
            ActiveAdmin activeAdmin = this.mAdminList.get(i);
            if (activeAdmin != null) {
                indentingPrintWriter.increaseIndent();
                indentingPrintWriter.print(activeAdmin.info.getComponent().flattenToShortString());
                indentingPrintWriter.println(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                indentingPrintWriter.increaseIndent();
                activeAdmin.dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
                indentingPrintWriter.decreaseIndent();
            }
        }
        if (!this.mRemovingAdmins.isEmpty()) {
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Removing Device Admins (User " + this.mUserId + "): " + this.mRemovingAdmins);
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.println();
        indentingPrintWriter.increaseIndent();
        indentingPrintWriter.print("mPasswordOwner=");
        indentingPrintWriter.println(this.mPasswordOwner);
        indentingPrintWriter.print("mPasswordTokenHandle=");
        indentingPrintWriter.println(Long.toHexString(this.mPasswordTokenHandle));
        indentingPrintWriter.print("mAppsSuspended=");
        indentingPrintWriter.println(this.mAppsSuspended);
        indentingPrintWriter.print("mUserSetupComplete=");
        indentingPrintWriter.println(this.mUserSetupComplete);
        indentingPrintWriter.print("mAffiliationIds=");
        indentingPrintWriter.println(this.mAffiliationIds);
        indentingPrintWriter.print("mNewUserDisclaimer=");
        indentingPrintWriter.println(this.mNewUserDisclaimer);
        if (this.mFactoryResetFlags != 0) {
            indentingPrintWriter.print("mFactoryResetFlags=");
            indentingPrintWriter.print(this.mFactoryResetFlags);
            indentingPrintWriter.print(" (");
            indentingPrintWriter.print(factoryResetFlagsToString(this.mFactoryResetFlags));
            indentingPrintWriter.println(')');
        }
        if (this.mFactoryResetReason != null) {
            indentingPrintWriter.print("mFactoryResetReason=");
            indentingPrintWriter.println(this.mFactoryResetReason);
        }
        if (this.mDelegationMap.size() != 0) {
            indentingPrintWriter.println("mDelegationMap=");
            indentingPrintWriter.increaseIndent();
            for (int i2 = 0; i2 < this.mDelegationMap.size(); i2++) {
                List<String> valueAt = this.mDelegationMap.valueAt(i2);
                indentingPrintWriter.println(this.mDelegationMap.keyAt(i2) + "[size=" + valueAt.size() + "]");
                indentingPrintWriter.increaseIndent();
                for (int i3 = 0; i3 < valueAt.size(); i3++) {
                    indentingPrintWriter.println(i3 + ": " + valueAt.get(i3));
                }
                indentingPrintWriter.decreaseIndent();
            }
            indentingPrintWriter.decreaseIndent();
        }
        indentingPrintWriter.decreaseIndent();
    }

    public static String factoryResetFlagsToString(int i) {
        return DebugUtils.flagsToString(DevicePolicyData.class, "FACTORY_RESET_FLAG_", i);
    }
}
