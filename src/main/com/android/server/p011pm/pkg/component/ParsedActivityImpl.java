package com.android.server.p011pm.pkg.component;

import android.app.ActivityTaskManager;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.parsing.pkg.PackageImpl;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
/* renamed from: com.android.server.pm.pkg.component.ParsedActivityImpl */
/* loaded from: classes2.dex */
public class ParsedActivityImpl extends ParsedMainComponentImpl implements ParsedActivity {
    public static final Parcelable.Creator<ParsedActivityImpl> CREATOR = new Parcelable.Creator<ParsedActivityImpl>() { // from class: com.android.server.pm.pkg.component.ParsedActivityImpl.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedActivityImpl createFromParcel(Parcel parcel) {
            return new ParsedActivityImpl(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ParsedActivityImpl[] newArray(int i) {
            return new ParsedActivityImpl[i];
        }
    };
    public int colorMode;
    public int configChanges;
    public int documentLaunchMode;
    public int launchMode;
    public int lockTaskLaunchMode;
    public Set<String> mKnownActivityEmbeddingCerts;
    public String mRequiredDisplayCategory;
    public float maxAspectRatio;
    public int maxRecents;
    public float minAspectRatio;
    public String parentActivityName;
    public String permission;
    public int persistableMode;
    public int privateFlags;
    public String requestedVrComponent;
    public int resizeMode;
    public int rotationAnimation;
    public int screenOrientation;
    public int softInputMode;
    public boolean supportsSizeChanges;
    public String targetActivity;
    public String taskAffinity;
    public int theme;
    public int uiOptions;
    public ActivityInfo.WindowLayout windowLayout;

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponentImpl, com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static ParsedActivityImpl makeAppDetailsActivity(String str, String str2, int i, String str3, boolean z) {
        ParsedActivityImpl parsedActivityImpl = new ParsedActivityImpl();
        parsedActivityImpl.setPackageName(str);
        parsedActivityImpl.theme = 16973909;
        parsedActivityImpl.setExported(true);
        parsedActivityImpl.setName(PackageManager.APP_DETAILS_ACTIVITY_CLASS_NAME);
        parsedActivityImpl.setProcessName(str2);
        parsedActivityImpl.uiOptions = i;
        parsedActivityImpl.taskAffinity = str3;
        parsedActivityImpl.launchMode = 0;
        parsedActivityImpl.documentLaunchMode = 0;
        parsedActivityImpl.maxRecents = ActivityTaskManager.getDefaultAppRecentsLimitStatic();
        parsedActivityImpl.configChanges = ParsedActivityUtils.getActivityConfigChanges(0, 0);
        parsedActivityImpl.softInputMode = 0;
        parsedActivityImpl.persistableMode = 1;
        parsedActivityImpl.screenOrientation = -1;
        parsedActivityImpl.resizeMode = 4;
        parsedActivityImpl.lockTaskLaunchMode = 0;
        parsedActivityImpl.setDirectBootAware(false);
        parsedActivityImpl.rotationAnimation = -1;
        parsedActivityImpl.colorMode = 0;
        if (z) {
            parsedActivityImpl.setFlags(parsedActivityImpl.getFlags() | 512);
        }
        return parsedActivityImpl;
    }

    public static ParsedActivityImpl makeAlias(String str, ParsedActivity parsedActivity) {
        ParsedActivityImpl parsedActivityImpl = new ParsedActivityImpl();
        parsedActivityImpl.setPackageName(parsedActivity.getPackageName());
        parsedActivityImpl.setTargetActivity(str);
        parsedActivityImpl.configChanges = parsedActivity.getConfigChanges();
        parsedActivityImpl.setFlags(parsedActivity.getFlags());
        parsedActivityImpl.privateFlags = parsedActivity.getPrivateFlags();
        parsedActivityImpl.setIcon(parsedActivity.getIcon());
        parsedActivityImpl.setLogo(parsedActivity.getLogo());
        parsedActivityImpl.setBanner(parsedActivity.getBanner());
        parsedActivityImpl.setLabelRes(parsedActivity.getLabelRes());
        parsedActivityImpl.setNonLocalizedLabel(parsedActivity.getNonLocalizedLabel());
        parsedActivityImpl.launchMode = parsedActivity.getLaunchMode();
        parsedActivityImpl.lockTaskLaunchMode = parsedActivity.getLockTaskLaunchMode();
        parsedActivityImpl.documentLaunchMode = parsedActivity.getDocumentLaunchMode();
        parsedActivityImpl.setDescriptionRes(parsedActivity.getDescriptionRes());
        parsedActivityImpl.screenOrientation = parsedActivity.getScreenOrientation();
        parsedActivityImpl.taskAffinity = parsedActivity.getTaskAffinity();
        parsedActivityImpl.theme = parsedActivity.getTheme();
        parsedActivityImpl.softInputMode = parsedActivity.getSoftInputMode();
        parsedActivityImpl.uiOptions = parsedActivity.getUiOptions();
        parsedActivityImpl.parentActivityName = parsedActivity.getParentActivityName();
        parsedActivityImpl.maxRecents = parsedActivity.getMaxRecents();
        parsedActivityImpl.windowLayout = parsedActivity.getWindowLayout();
        parsedActivityImpl.resizeMode = parsedActivity.getResizeMode();
        parsedActivityImpl.maxAspectRatio = parsedActivity.getMaxAspectRatio();
        parsedActivityImpl.minAspectRatio = parsedActivity.getMinAspectRatio();
        parsedActivityImpl.supportsSizeChanges = parsedActivity.isSupportsSizeChanges();
        parsedActivityImpl.requestedVrComponent = parsedActivity.getRequestedVrComponent();
        parsedActivityImpl.setDirectBootAware(parsedActivity.isDirectBootAware());
        parsedActivityImpl.setProcessName(parsedActivity.getProcessName());
        parsedActivityImpl.setRequiredDisplayCategory(parsedActivity.getRequiredDisplayCategory());
        return parsedActivityImpl;
    }

    public ParsedActivityImpl setMaxAspectRatio(int i, float f) {
        if (i == 2 || i == 1 || (f < 1.0f && f != 0.0f)) {
            return this;
        }
        this.maxAspectRatio = f;
        return this;
    }

    public ParsedActivityImpl setMinAspectRatio(int i, float f) {
        if (i == 2 || i == 1 || (f < 1.0f && f != 0.0f)) {
            return this;
        }
        this.minAspectRatio = f;
        return this;
    }

    public ParsedActivityImpl setTargetActivity(String str) {
        this.targetActivity = TextUtils.safeIntern(str);
        return this;
    }

    public ParsedActivityImpl setPermission(String str) {
        this.permission = TextUtils.isEmpty(str) ? null : str.intern();
        return this;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public Set<String> getKnownActivityEmbeddingCerts() {
        Set<String> set = this.mKnownActivityEmbeddingCerts;
        return set == null ? Collections.emptySet() : set;
    }

    public void setKnownActivityEmbeddingCerts(Set<String> set) {
        this.mKnownActivityEmbeddingCerts = new ArraySet();
        for (String str : set) {
            this.mKnownActivityEmbeddingCerts.add(str.toUpperCase(Locale.US));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Activity{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        ComponentName.appendShortString(sb, getPackageName(), getName());
        sb.append('}');
        return sb.toString();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedMainComponentImpl, com.android.server.p011pm.pkg.component.ParsedComponentImpl, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(this.theme);
        parcel.writeInt(this.uiOptions);
        parcel.writeString(this.targetActivity);
        parcel.writeString(this.parentActivityName);
        parcel.writeString(this.taskAffinity);
        parcel.writeInt(this.privateFlags);
        PackageImpl.sForInternedString.parcel(this.permission, parcel, i);
        parcel.writeInt(this.launchMode);
        parcel.writeInt(this.documentLaunchMode);
        parcel.writeInt(this.maxRecents);
        parcel.writeInt(this.configChanges);
        parcel.writeInt(this.softInputMode);
        parcel.writeInt(this.persistableMode);
        parcel.writeInt(this.lockTaskLaunchMode);
        parcel.writeInt(this.screenOrientation);
        parcel.writeInt(this.resizeMode);
        parcel.writeValue(Float.valueOf(this.maxAspectRatio));
        parcel.writeValue(Float.valueOf(this.minAspectRatio));
        parcel.writeBoolean(this.supportsSizeChanges);
        parcel.writeString(this.requestedVrComponent);
        parcel.writeInt(this.rotationAnimation);
        parcel.writeInt(this.colorMode);
        parcel.writeBundle(getMetaData());
        if (this.windowLayout != null) {
            parcel.writeInt(1);
            this.windowLayout.writeToParcel(parcel);
        } else {
            parcel.writeBoolean(false);
        }
        PackageImpl.sForStringSet.parcel(this.mKnownActivityEmbeddingCerts, parcel, i);
        parcel.writeString8(this.mRequiredDisplayCategory);
    }

    public ParsedActivityImpl() {
        this.screenOrientation = -1;
        this.resizeMode = 2;
        this.maxAspectRatio = -1.0f;
        this.minAspectRatio = -1.0f;
        this.rotationAnimation = -1;
    }

    public ParsedActivityImpl(Parcel parcel) {
        super(parcel);
        this.screenOrientation = -1;
        this.resizeMode = 2;
        this.maxAspectRatio = -1.0f;
        this.minAspectRatio = -1.0f;
        this.rotationAnimation = -1;
        this.theme = parcel.readInt();
        this.uiOptions = parcel.readInt();
        this.targetActivity = parcel.readString();
        this.parentActivityName = parcel.readString();
        this.taskAffinity = parcel.readString();
        this.privateFlags = parcel.readInt();
        this.permission = PackageImpl.sForInternedString.unparcel(parcel);
        this.launchMode = parcel.readInt();
        this.documentLaunchMode = parcel.readInt();
        this.maxRecents = parcel.readInt();
        this.configChanges = parcel.readInt();
        this.softInputMode = parcel.readInt();
        this.persistableMode = parcel.readInt();
        this.lockTaskLaunchMode = parcel.readInt();
        this.screenOrientation = parcel.readInt();
        this.resizeMode = parcel.readInt();
        this.maxAspectRatio = ((Float) parcel.readValue(Float.class.getClassLoader())).floatValue();
        this.minAspectRatio = ((Float) parcel.readValue(Float.class.getClassLoader())).floatValue();
        this.supportsSizeChanges = parcel.readBoolean();
        this.requestedVrComponent = parcel.readString();
        this.rotationAnimation = parcel.readInt();
        this.colorMode = parcel.readInt();
        setMetaData(parcel.readBundle());
        if (parcel.readBoolean()) {
            this.windowLayout = new ActivityInfo.WindowLayout(parcel);
        }
        this.mKnownActivityEmbeddingCerts = PackageImpl.sForStringSet.unparcel(parcel);
        this.mRequiredDisplayCategory = parcel.readString8();
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getTheme() {
        return this.theme;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getUiOptions() {
        return this.uiOptions;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public String getTargetActivity() {
        return this.targetActivity;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public String getParentActivityName() {
        return this.parentActivityName;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public String getTaskAffinity() {
        return this.taskAffinity;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getPrivateFlags() {
        return this.privateFlags;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public String getPermission() {
        return this.permission;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getLaunchMode() {
        return this.launchMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getDocumentLaunchMode() {
        return this.documentLaunchMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getMaxRecents() {
        return this.maxRecents;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getConfigChanges() {
        return this.configChanges;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getSoftInputMode() {
        return this.softInputMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getPersistableMode() {
        return this.persistableMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getLockTaskLaunchMode() {
        return this.lockTaskLaunchMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getScreenOrientation() {
        return this.screenOrientation;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getResizeMode() {
        return this.resizeMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public float getMaxAspectRatio() {
        return this.maxAspectRatio;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public float getMinAspectRatio() {
        return this.minAspectRatio;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public boolean isSupportsSizeChanges() {
        return this.supportsSizeChanges;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public String getRequestedVrComponent() {
        return this.requestedVrComponent;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getRotationAnimation() {
        return this.rotationAnimation;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public int getColorMode() {
        return this.colorMode;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public ActivityInfo.WindowLayout getWindowLayout() {
        return this.windowLayout;
    }

    @Override // com.android.server.p011pm.pkg.component.ParsedActivity
    public String getRequiredDisplayCategory() {
        return this.mRequiredDisplayCategory;
    }

    public ParsedActivityImpl setTheme(int i) {
        this.theme = i;
        return this;
    }

    public ParsedActivityImpl setUiOptions(int i) {
        this.uiOptions = i;
        return this;
    }

    public ParsedActivityImpl setParentActivityName(String str) {
        this.parentActivityName = str;
        return this;
    }

    public ParsedActivityImpl setTaskAffinity(String str) {
        this.taskAffinity = str;
        return this;
    }

    public ParsedActivityImpl setPrivateFlags(int i) {
        this.privateFlags = i;
        return this;
    }

    public ParsedActivityImpl setLaunchMode(int i) {
        this.launchMode = i;
        return this;
    }

    public ParsedActivityImpl setDocumentLaunchMode(int i) {
        this.documentLaunchMode = i;
        return this;
    }

    public ParsedActivityImpl setMaxRecents(int i) {
        this.maxRecents = i;
        return this;
    }

    public ParsedActivityImpl setConfigChanges(int i) {
        this.configChanges = i;
        return this;
    }

    public ParsedActivityImpl setSoftInputMode(int i) {
        this.softInputMode = i;
        return this;
    }

    public ParsedActivityImpl setPersistableMode(int i) {
        this.persistableMode = i;
        return this;
    }

    public ParsedActivityImpl setLockTaskLaunchMode(int i) {
        this.lockTaskLaunchMode = i;
        return this;
    }

    public ParsedActivityImpl setScreenOrientation(int i) {
        this.screenOrientation = i;
        return this;
    }

    public ParsedActivityImpl setResizeMode(int i) {
        this.resizeMode = i;
        return this;
    }

    public ParsedActivityImpl setSupportsSizeChanges(boolean z) {
        this.supportsSizeChanges = z;
        return this;
    }

    public ParsedActivityImpl setRequestedVrComponent(String str) {
        this.requestedVrComponent = str;
        return this;
    }

    public ParsedActivityImpl setRotationAnimation(int i) {
        this.rotationAnimation = i;
        return this;
    }

    public ParsedActivityImpl setColorMode(int i) {
        this.colorMode = i;
        return this;
    }

    public ParsedActivityImpl setWindowLayout(ActivityInfo.WindowLayout windowLayout) {
        this.windowLayout = windowLayout;
        return this;
    }

    public ParsedActivityImpl setRequiredDisplayCategory(String str) {
        this.mRequiredDisplayCategory = str;
        return this;
    }
}
