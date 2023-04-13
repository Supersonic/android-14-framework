package com.android.server.p011pm;

import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
@VisibleForTesting
/* renamed from: com.android.server.pm.ModuleInfoProvider */
/* loaded from: classes2.dex */
public class ModuleInfoProvider {
    public final ApexManager mApexManager;
    public final Context mContext;
    public volatile boolean mMetadataLoaded;
    public final Map<String, ModuleInfo> mModuleInfo;
    public IPackageManager mPackageManager;
    public volatile String mPackageName;

    public ModuleInfoProvider(Context context) {
        this.mContext = context;
        this.mApexManager = ApexManager.getInstance();
        this.mModuleInfo = new ArrayMap();
    }

    @VisibleForTesting
    public ModuleInfoProvider(XmlResourceParser xmlResourceParser, Resources resources, ApexManager apexManager) {
        this.mContext = null;
        this.mApexManager = apexManager;
        this.mModuleInfo = new ArrayMap();
        loadModuleMetadata(xmlResourceParser, resources);
    }

    public final IPackageManager getPackageManager() {
        if (this.mPackageManager == null) {
            this.mPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
        }
        return this.mPackageManager;
    }

    public void systemReady() {
        this.mPackageName = this.mContext.getResources().getString(17039887);
        if (TextUtils.isEmpty(this.mPackageName)) {
            Slog.w("PackageManager.ModuleInfoProvider", "No configured module metadata provider.");
            return;
        }
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(this.mPackageName, 128L, 0);
            Resources resources = this.mContext.createPackageContext(this.mPackageName, 0).getResources();
            loadModuleMetadata(resources.getXml(packageInfo.applicationInfo.metaData.getInt("android.content.pm.MODULE_METADATA")), resources);
        } catch (PackageManager.NameNotFoundException | RemoteException e) {
            Slog.w("PackageManager.ModuleInfoProvider", "Unable to discover metadata package: " + this.mPackageName, e);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x001e, code lost:
        android.util.Slog.w("PackageManager.ModuleInfoProvider", "Unexpected metadata element: " + r7.getName());
        r6.mModuleInfo.clear();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void loadModuleMetadata(XmlResourceParser xmlResourceParser, Resources resources) {
        try {
            try {
                XmlUtils.beginDocument(xmlResourceParser, "module-metadata");
                while (true) {
                    XmlUtils.nextElement(xmlResourceParser);
                    if (xmlResourceParser.getEventType() == 1) {
                        break;
                    } else if (!"module".equals(xmlResourceParser.getName())) {
                        break;
                    } else {
                        CharSequence text = resources.getText(Integer.parseInt(xmlResourceParser.getAttributeValue(null, "name").substring(1)));
                        String readStringAttribute = XmlUtils.readStringAttribute(xmlResourceParser, "packageName");
                        boolean readBooleanAttribute = XmlUtils.readBooleanAttribute(xmlResourceParser, "isHidden");
                        ModuleInfo moduleInfo = new ModuleInfo();
                        moduleInfo.setHidden(readBooleanAttribute);
                        moduleInfo.setPackageName(readStringAttribute);
                        moduleInfo.setName(text);
                        moduleInfo.setApexModuleName(this.mApexManager.getApexModuleNameForPackageName(readStringAttribute));
                        this.mModuleInfo.put(readStringAttribute, moduleInfo);
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                Slog.w("PackageManager.ModuleInfoProvider", "Error parsing module metadata", e);
                this.mModuleInfo.clear();
            }
        } finally {
            xmlResourceParser.close();
            this.mMetadataLoaded = true;
        }
    }

    public List<ModuleInfo> getInstalledModules(int i) {
        if (this.mMetadataLoaded) {
            if ((131072 & i) != 0) {
                return new ArrayList(this.mModuleInfo.values());
            }
            try {
                List<PackageInfo> list = getPackageManager().getInstalledPackages(i | 1073741824, UserHandle.getCallingUserId()).getList();
                ArrayList arrayList = new ArrayList(list.size());
                for (PackageInfo packageInfo : list) {
                    ModuleInfo moduleInfo = this.mModuleInfo.get(packageInfo.packageName);
                    if (moduleInfo != null) {
                        arrayList.add(moduleInfo);
                    }
                }
                return arrayList;
            } catch (RemoteException e) {
                Slog.w("PackageManager.ModuleInfoProvider", "Unable to retrieve all package names", e);
                return Collections.emptyList();
            }
        }
        throw new IllegalStateException("Call to getInstalledModules before metadata loaded");
    }

    public ModuleInfo getModuleInfo(String str, int i) {
        if (this.mMetadataLoaded) {
            if ((i & 1) != 0) {
                for (ModuleInfo moduleInfo : this.mModuleInfo.values()) {
                    if (str.equals(moduleInfo.getApexModuleName())) {
                        return moduleInfo;
                    }
                }
                return null;
            }
            return this.mModuleInfo.get(str);
        }
        throw new IllegalStateException("Call to getModuleInfo before metadata loaded");
    }

    public String getPackageName() {
        if (!this.mMetadataLoaded) {
            throw new IllegalStateException("Call to getVersion before metadata loaded");
        }
        return this.mPackageName;
    }
}
