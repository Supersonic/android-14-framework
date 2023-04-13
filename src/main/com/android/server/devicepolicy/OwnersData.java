package com.android.server.devicepolicy;

import android.app.admin.SystemUpdateInfo;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class OwnersData {
    public OwnerInfo mDeviceOwner;
    @Deprecated
    public ArrayMap<String, List<String>> mDeviceOwnerProtectedPackages;
    public final PolicyPathProvider mPathProvider;
    public LocalDate mSystemUpdateFreezeEnd;
    public LocalDate mSystemUpdateFreezeStart;
    public SystemUpdateInfo mSystemUpdateInfo;
    public SystemUpdatePolicy mSystemUpdatePolicy;
    public int mDeviceOwnerUserId = -10000;
    public final ArrayMap<String, Integer> mDeviceOwnerTypes = new ArrayMap<>();
    public final ArrayMap<Integer, OwnerInfo> mProfileOwners = new ArrayMap<>();
    public boolean mMigratedToPolicyEngine = false;

    public OwnersData(PolicyPathProvider policyPathProvider) {
        this.mPathProvider = policyPathProvider;
    }

    public void load(int[] iArr) {
        new DeviceOwnerReadWriter().readFromFileLocked();
        for (int i : iArr) {
            new ProfileOwnerReadWriter(i).readFromFileLocked();
        }
        OwnerInfo ownerInfo = this.mProfileOwners.get(Integer.valueOf(this.mDeviceOwnerUserId));
        ComponentName componentName = ownerInfo != null ? ownerInfo.admin : null;
        if (this.mDeviceOwner == null || componentName == null) {
            return;
        }
        Slog.w("DevicePolicyManagerService", String.format("User %d has both DO and PO, which is not supported", Integer.valueOf(this.mDeviceOwnerUserId)));
    }

    public boolean writeDeviceOwner() {
        return new DeviceOwnerReadWriter().writeToFileLocked();
    }

    public boolean writeProfileOwner(int i) {
        return new ProfileOwnerReadWriter(i).writeToFileLocked();
    }

    public void dump(IndentingPrintWriter indentingPrintWriter) {
        boolean z;
        boolean z2 = true;
        if (this.mDeviceOwner != null) {
            indentingPrintWriter.println("Device Owner: ");
            indentingPrintWriter.increaseIndent();
            this.mDeviceOwner.dump(indentingPrintWriter);
            indentingPrintWriter.println("User ID: " + this.mDeviceOwnerUserId);
            indentingPrintWriter.decreaseIndent();
            z = true;
        } else {
            z = false;
        }
        if (this.mSystemUpdatePolicy != null) {
            if (z) {
                indentingPrintWriter.println();
            }
            indentingPrintWriter.println("System Update Policy: " + this.mSystemUpdatePolicy);
            z = true;
        }
        ArrayMap<Integer, OwnerInfo> arrayMap = this.mProfileOwners;
        if (arrayMap != null) {
            for (Map.Entry<Integer, OwnerInfo> entry : arrayMap.entrySet()) {
                if (z) {
                    indentingPrintWriter.println();
                }
                indentingPrintWriter.println("Profile Owner (User " + entry.getKey() + "): ");
                indentingPrintWriter.increaseIndent();
                entry.getValue().dump(indentingPrintWriter);
                indentingPrintWriter.decreaseIndent();
                z = true;
            }
        }
        if (this.mSystemUpdateInfo != null) {
            if (z) {
                indentingPrintWriter.println();
            }
            indentingPrintWriter.println("Pending System Update: " + this.mSystemUpdateInfo);
        } else {
            z2 = z;
        }
        if (this.mSystemUpdateFreezeStart == null && this.mSystemUpdateFreezeEnd == null) {
            return;
        }
        if (z2) {
            indentingPrintWriter.println();
        }
        indentingPrintWriter.println("System update freeze record: " + getSystemUpdateFreezePeriodRecordAsString());
    }

    public String getSystemUpdateFreezePeriodRecordAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("start: ");
        LocalDate localDate = this.mSystemUpdateFreezeStart;
        if (localDate != null) {
            sb.append(localDate.toString());
        } else {
            sb.append("null");
        }
        sb.append("; end: ");
        LocalDate localDate2 = this.mSystemUpdateFreezeEnd;
        if (localDate2 != null) {
            sb.append(localDate2.toString());
        } else {
            sb.append("null");
        }
        return sb.toString();
    }

    @VisibleForTesting
    public File getDeviceOwnerFile() {
        return new File(this.mPathProvider.getDataSystemDirectory(), "device_owner_2.xml");
    }

    @VisibleForTesting
    public File getProfileOwnerFile(int i) {
        return new File(this.mPathProvider.getUserSystemDirectory(i), "profile_owner.xml");
    }

    /* loaded from: classes.dex */
    public static abstract class FileReadWriter {
        public final File mFile;

        public abstract boolean readInner(TypedXmlPullParser typedXmlPullParser, int i, String str);

        public abstract boolean shouldWrite();

        public abstract void writeInner(TypedXmlSerializer typedXmlSerializer) throws IOException;

        public FileReadWriter(File file) {
            this.mFile = file;
        }

        public boolean writeToFileLocked() {
            FileOutputStream startWrite;
            if (!shouldWrite()) {
                if (this.mFile.exists() && !this.mFile.delete()) {
                    Slog.e("DevicePolicyManagerService", "Failed to remove " + this.mFile.getPath());
                }
                return true;
            }
            AtomicFile atomicFile = new AtomicFile(this.mFile);
            FileOutputStream fileOutputStream = null;
            try {
                startWrite = atomicFile.startWrite();
            } catch (IOException e) {
                e = e;
            }
            try {
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(startWrite);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                resolveSerializer.startTag((String) null, "root");
                writeInner(resolveSerializer);
                resolveSerializer.endTag((String) null, "root");
                resolveSerializer.endDocument();
                resolveSerializer.flush();
                atomicFile.finishWrite(startWrite);
                return true;
            } catch (IOException e2) {
                e = e2;
                fileOutputStream = startWrite;
                Slog.e("DevicePolicyManagerService", "Exception when writing", e);
                if (fileOutputStream != null) {
                    atomicFile.failWrite(fileOutputStream);
                    return false;
                }
                return false;
            }
        }

        public void readFromFileLocked() {
            if (!this.mFile.exists()) {
                return;
            }
            FileInputStream fileInputStream = null;
            try {
                try {
                    fileInputStream = new AtomicFile(this.mFile).openRead();
                    TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                    int i = 0;
                    while (true) {
                        int next = resolvePullParser.next();
                        if (next == 1) {
                            break;
                        } else if (next == 2) {
                            i++;
                            String name = resolvePullParser.getName();
                            if (i == 1) {
                                if (!"root".equals(name)) {
                                    Slog.e("DevicePolicyManagerService", "Invalid root tag: " + name);
                                    return;
                                }
                            } else if (!readInner(resolvePullParser, i, name)) {
                                return;
                            }
                        } else if (next == 3) {
                            i--;
                        }
                    }
                } catch (IOException | XmlPullParserException e) {
                    Slog.e("DevicePolicyManagerService", "Error parsing owners information file", e);
                }
            } finally {
                IoUtils.closeQuietly(fileInputStream);
            }
        }
    }

    /* loaded from: classes.dex */
    public class DeviceOwnerReadWriter extends FileReadWriter {
        public DeviceOwnerReadWriter() {
            super(OwnersData.this.getDeviceOwnerFile());
        }

        @Override // com.android.server.devicepolicy.OwnersData.FileReadWriter
        public boolean shouldWrite() {
            OwnersData ownersData = OwnersData.this;
            return (ownersData.mDeviceOwner == null && ownersData.mSystemUpdatePolicy == null && ownersData.mSystemUpdateInfo == null) ? false : true;
        }

        @Override // com.android.server.devicepolicy.OwnersData.FileReadWriter
        public void writeInner(TypedXmlSerializer typedXmlSerializer) throws IOException {
            OwnerInfo ownerInfo = OwnersData.this.mDeviceOwner;
            if (ownerInfo != null) {
                ownerInfo.writeToXml(typedXmlSerializer, "device-owner");
                typedXmlSerializer.startTag((String) null, "device-owner-context");
                typedXmlSerializer.attributeInt((String) null, "userId", OwnersData.this.mDeviceOwnerUserId);
                typedXmlSerializer.endTag((String) null, "device-owner-context");
            }
            if (!OwnersData.this.mDeviceOwnerTypes.isEmpty()) {
                for (Map.Entry<String, Integer> entry : OwnersData.this.mDeviceOwnerTypes.entrySet()) {
                    typedXmlSerializer.startTag((String) null, "device-owner-type");
                    typedXmlSerializer.attribute((String) null, "package", entry.getKey());
                    typedXmlSerializer.attributeInt((String) null, "value", entry.getValue().intValue());
                    typedXmlSerializer.endTag((String) null, "device-owner-type");
                }
            }
            if (OwnersData.this.mSystemUpdatePolicy != null) {
                typedXmlSerializer.startTag((String) null, "system-update-policy");
                OwnersData.this.mSystemUpdatePolicy.saveToXml(typedXmlSerializer);
                typedXmlSerializer.endTag((String) null, "system-update-policy");
            }
            SystemUpdateInfo systemUpdateInfo = OwnersData.this.mSystemUpdateInfo;
            if (systemUpdateInfo != null) {
                systemUpdateInfo.writeToXml(typedXmlSerializer, "pending-ota-info");
            }
            OwnersData ownersData = OwnersData.this;
            if (ownersData.mSystemUpdateFreezeStart != null || ownersData.mSystemUpdateFreezeEnd != null) {
                typedXmlSerializer.startTag((String) null, "freeze-record");
                LocalDate localDate = OwnersData.this.mSystemUpdateFreezeStart;
                if (localDate != null) {
                    typedXmlSerializer.attribute((String) null, "start", localDate.toString());
                }
                LocalDate localDate2 = OwnersData.this.mSystemUpdateFreezeEnd;
                if (localDate2 != null) {
                    typedXmlSerializer.attribute((String) null, "end", localDate2.toString());
                }
                typedXmlSerializer.endTag((String) null, "freeze-record");
            }
            typedXmlSerializer.startTag((String) null, "policy-engine-migration");
            typedXmlSerializer.attributeBoolean((String) null, "migratedToPolicyEngine", OwnersData.this.mMigratedToPolicyEngine);
            typedXmlSerializer.endTag((String) null, "policy-engine-migration");
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code restructure failed: missing block: B:29:0x0053, code lost:
            if (r9.equals("device-owner-context") == false) goto L7;
         */
        @Override // com.android.server.devicepolicy.OwnersData.FileReadWriter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public boolean readInner(TypedXmlPullParser typedXmlPullParser, int i, String str) {
            char c = 2;
            if (i > 2) {
                return true;
            }
            str.hashCode();
            switch (str.hashCode()) {
                case -2101756875:
                    if (str.equals("pending-ota-info")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -2020438916:
                    if (str.equals("device-owner")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -1900517026:
                    break;
                case -725102274:
                    if (str.equals("policy-engine-migration")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case -465393379:
                    if (str.equals("device-owner-protected-packages")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 544117227:
                    if (str.equals("device-owner-type")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case 1303827527:
                    if (str.equals("freeze-record")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 1748301720:
                    if (str.equals("system-update-policy")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    OwnersData.this.mSystemUpdateInfo = SystemUpdateInfo.readFromXml(typedXmlPullParser);
                    return true;
                case 1:
                    OwnersData.this.mDeviceOwner = OwnerInfo.readFromXml(typedXmlPullParser);
                    OwnersData.this.mDeviceOwnerUserId = 0;
                    return true;
                case 2:
                    OwnersData ownersData = OwnersData.this;
                    ownersData.mDeviceOwnerUserId = typedXmlPullParser.getAttributeInt((String) null, "userId", ownersData.mDeviceOwnerUserId);
                    return true;
                case 3:
                    OwnersData.this.mMigratedToPolicyEngine = typedXmlPullParser.getAttributeBoolean((String) null, "migratedToPolicyEngine", false);
                    Slog.e("DevicePolicyManagerService", "Unexpected tag: " + str);
                    return false;
                case 4:
                    String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "package");
                    int attributeInt = typedXmlPullParser.getAttributeInt((String) null, "size", 0);
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = 0; i2 < attributeInt; i2++) {
                        arrayList.add(typedXmlPullParser.getAttributeValue((String) null, "name" + i2));
                    }
                    OwnersData ownersData2 = OwnersData.this;
                    if (ownersData2.mDeviceOwnerProtectedPackages == null) {
                        ownersData2.mDeviceOwnerProtectedPackages = new ArrayMap<>();
                    }
                    OwnersData.this.mDeviceOwnerProtectedPackages.put(attributeValue, arrayList);
                    return true;
                case 5:
                    OwnersData.this.mDeviceOwnerTypes.put(typedXmlPullParser.getAttributeValue((String) null, "package"), Integer.valueOf(typedXmlPullParser.getAttributeInt((String) null, "value", 0)));
                    return true;
                case 6:
                    String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "start");
                    String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "end");
                    if (attributeValue2 != null && attributeValue3 != null) {
                        OwnersData.this.mSystemUpdateFreezeStart = LocalDate.parse(attributeValue2);
                        OwnersData.this.mSystemUpdateFreezeEnd = LocalDate.parse(attributeValue3);
                        OwnersData ownersData3 = OwnersData.this;
                        if (ownersData3.mSystemUpdateFreezeStart.isAfter(ownersData3.mSystemUpdateFreezeEnd)) {
                            Slog.e("DevicePolicyManagerService", "Invalid system update freeze record loaded");
                            OwnersData ownersData4 = OwnersData.this;
                            ownersData4.mSystemUpdateFreezeStart = null;
                            ownersData4.mSystemUpdateFreezeEnd = null;
                        }
                    }
                    return true;
                case 7:
                    OwnersData.this.mSystemUpdatePolicy = SystemUpdatePolicy.restoreFromXml(typedXmlPullParser);
                    return true;
                default:
                    Slog.e("DevicePolicyManagerService", "Unexpected tag: " + str);
                    return false;
            }
        }
    }

    /* loaded from: classes.dex */
    public class ProfileOwnerReadWriter extends FileReadWriter {
        public final int mUserId;

        public ProfileOwnerReadWriter(int i) {
            super(OwnersData.this.getProfileOwnerFile(i));
            this.mUserId = i;
        }

        @Override // com.android.server.devicepolicy.OwnersData.FileReadWriter
        public boolean shouldWrite() {
            return OwnersData.this.mProfileOwners.get(Integer.valueOf(this.mUserId)) != null;
        }

        @Override // com.android.server.devicepolicy.OwnersData.FileReadWriter
        public void writeInner(TypedXmlSerializer typedXmlSerializer) throws IOException {
            OwnerInfo ownerInfo = OwnersData.this.mProfileOwners.get(Integer.valueOf(this.mUserId));
            if (ownerInfo != null) {
                ownerInfo.writeToXml(typedXmlSerializer, "profile-owner");
            }
        }

        @Override // com.android.server.devicepolicy.OwnersData.FileReadWriter
        public boolean readInner(TypedXmlPullParser typedXmlPullParser, int i, String str) {
            if (i > 2) {
                return true;
            }
            str.hashCode();
            if (str.equals("profile-owner")) {
                OwnersData.this.mProfileOwners.put(Integer.valueOf(this.mUserId), OwnerInfo.readFromXml(typedXmlPullParser));
                return true;
            }
            Slog.e("DevicePolicyManagerService", "Unexpected tag: " + str);
            return false;
        }
    }

    /* loaded from: classes.dex */
    public static class OwnerInfo {
        public final ComponentName admin;
        public boolean isOrganizationOwnedDevice;
        public final String packageName;
        public String remoteBugreportHash;
        public String remoteBugreportUri;

        public OwnerInfo(ComponentName componentName, String str, String str2, boolean z) {
            this.admin = componentName;
            this.packageName = componentName.getPackageName();
            this.remoteBugreportUri = str;
            this.remoteBugreportHash = str2;
            this.isOrganizationOwnedDevice = z;
        }

        public void writeToXml(TypedXmlSerializer typedXmlSerializer, String str) throws IOException {
            typedXmlSerializer.startTag((String) null, str);
            ComponentName componentName = this.admin;
            if (componentName != null) {
                typedXmlSerializer.attribute((String) null, "component", componentName.flattenToString());
            }
            String str2 = this.remoteBugreportUri;
            if (str2 != null) {
                typedXmlSerializer.attribute((String) null, "remoteBugreportUri", str2);
            }
            String str3 = this.remoteBugreportHash;
            if (str3 != null) {
                typedXmlSerializer.attribute((String) null, "remoteBugreportHash", str3);
            }
            boolean z = this.isOrganizationOwnedDevice;
            if (z) {
                typedXmlSerializer.attributeBoolean((String) null, "isPoOrganizationOwnedDevice", z);
            }
            typedXmlSerializer.endTag((String) null, str);
        }

        public static OwnerInfo readFromXml(TypedXmlPullParser typedXmlPullParser) {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "component");
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "remoteBugreportUri");
            String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "remoteBugreportHash");
            boolean equals = "true".equals(typedXmlPullParser.getAttributeValue((String) null, "isPoOrganizationOwnedDevice")) | "true".equals(typedXmlPullParser.getAttributeValue((String) null, "canAccessDeviceIds"));
            if (attributeValue == null) {
                Slog.e("DevicePolicyManagerService", "Owner component not found");
                return null;
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(attributeValue);
            if (unflattenFromString == null) {
                Slog.e("DevicePolicyManagerService", "Owner component not parsable: " + attributeValue);
                return null;
            }
            return new OwnerInfo(unflattenFromString, attributeValue2, attributeValue3, equals);
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.println("admin=" + this.admin);
            indentingPrintWriter.println("package=" + this.packageName);
            indentingPrintWriter.println("isOrganizationOwnedDevice=" + this.isOrganizationOwnedDevice);
        }
    }
}
