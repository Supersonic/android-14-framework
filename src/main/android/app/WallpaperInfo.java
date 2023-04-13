package android.app;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.service.wallpaper.WallpaperService;
import android.util.AttributeSet;
import android.util.Printer;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class WallpaperInfo implements Parcelable {
    public static final Parcelable.Creator<WallpaperInfo> CREATOR = new Parcelable.Creator<WallpaperInfo>() { // from class: android.app.WallpaperInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WallpaperInfo createFromParcel(Parcel source) {
            return new WallpaperInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WallpaperInfo[] newArray(int size) {
            return new WallpaperInfo[size];
        }
    };
    static final String TAG = "WallpaperInfo";
    final int mAuthorResource;
    final int mContextDescriptionResource;
    final int mContextUriResource;
    final int mDescriptionResource;
    final ResolveInfo mService;
    final String mSettingsActivityName;
    final String mSettingsSliceUri;
    final boolean mShouldUseDefaultUnfoldTransition;
    final boolean mShowMetadataInPreview;
    final boolean mSupportMultipleDisplays;
    final boolean mSupportsAmbientMode;
    final int mThumbnailResource;

    public WallpaperInfo(Context context, ResolveInfo service) throws XmlPullParserException, IOException {
        this.mService = service;
        ServiceInfo si = service.serviceInfo;
        PackageManager pm = context.getPackageManager();
        XmlResourceParser parser = null;
        try {
            try {
                parser = si.loadXmlMetaData(pm, WallpaperService.SERVICE_META_DATA);
                if (parser == null) {
                    throw new XmlPullParserException("No android.service.wallpaper meta-data");
                }
                Resources res = pm.getResourcesForApplication(si.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser.getName();
                if (!Context.WALLPAPER_SERVICE.equals(nodeName)) {
                    throw new XmlPullParserException("Meta-data does not start with wallpaper tag");
                }
                TypedArray sa = res.obtainAttributes(attrs, C4057R.styleable.Wallpaper);
                this.mSettingsActivityName = sa.getString(1);
                this.mThumbnailResource = sa.getResourceId(2, -1);
                this.mAuthorResource = sa.getResourceId(3, -1);
                this.mDescriptionResource = sa.getResourceId(0, -1);
                this.mContextUriResource = sa.getResourceId(4, -1);
                this.mContextDescriptionResource = sa.getResourceId(5, -1);
                this.mShowMetadataInPreview = sa.getBoolean(6, false);
                this.mSupportsAmbientMode = sa.getBoolean(7, false);
                this.mShouldUseDefaultUnfoldTransition = sa.getBoolean(10, true);
                this.mSettingsSliceUri = sa.getString(8);
                this.mSupportMultipleDisplays = sa.getBoolean(9, false);
                sa.recycle();
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to create context for: " + si.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    WallpaperInfo(Parcel source) {
        this.mSettingsActivityName = source.readString();
        this.mThumbnailResource = source.readInt();
        this.mAuthorResource = source.readInt();
        this.mDescriptionResource = source.readInt();
        this.mContextUriResource = source.readInt();
        this.mContextDescriptionResource = source.readInt();
        this.mShowMetadataInPreview = source.readInt() != 0;
        this.mSupportsAmbientMode = source.readInt() != 0;
        this.mSettingsSliceUri = source.readString();
        this.mSupportMultipleDisplays = source.readInt() != 0;
        this.mShouldUseDefaultUnfoldTransition = source.readInt() != 0;
        this.mService = ResolveInfo.CREATOR.createFromParcel(source);
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    public String getServiceName() {
        return this.mService.serviceInfo.name;
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public CharSequence loadLabel(PackageManager pm) {
        return this.mService.loadLabel(pm);
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public Drawable loadThumbnail(PackageManager pm) {
        if (this.mThumbnailResource < 0) {
            return null;
        }
        return pm.getDrawable(this.mService.serviceInfo.packageName, this.mThumbnailResource, this.mService.serviceInfo.applicationInfo);
    }

    public CharSequence loadAuthor(PackageManager pm) throws Resources.NotFoundException {
        if (this.mAuthorResource <= 0) {
            throw new Resources.NotFoundException();
        }
        String packageName = this.mService.resolvePackageName;
        ApplicationInfo applicationInfo = null;
        if (packageName == null) {
            packageName = this.mService.serviceInfo.packageName;
            applicationInfo = this.mService.serviceInfo.applicationInfo;
        }
        return pm.getText(packageName, this.mAuthorResource, applicationInfo);
    }

    public CharSequence loadDescription(PackageManager pm) throws Resources.NotFoundException {
        String packageName = this.mService.resolvePackageName;
        ApplicationInfo applicationInfo = null;
        if (packageName == null) {
            packageName = this.mService.serviceInfo.packageName;
            applicationInfo = this.mService.serviceInfo.applicationInfo;
        }
        if (this.mService.serviceInfo.descriptionRes != 0) {
            return pm.getText(packageName, this.mService.serviceInfo.descriptionRes, applicationInfo);
        }
        int i = this.mDescriptionResource;
        if (i <= 0) {
            throw new Resources.NotFoundException();
        }
        return pm.getText(packageName, i, this.mService.serviceInfo.applicationInfo);
    }

    public Uri loadContextUri(PackageManager pm) throws Resources.NotFoundException {
        if (this.mContextUriResource <= 0) {
            throw new Resources.NotFoundException();
        }
        String packageName = this.mService.resolvePackageName;
        ApplicationInfo applicationInfo = null;
        if (packageName == null) {
            packageName = this.mService.serviceInfo.packageName;
            applicationInfo = this.mService.serviceInfo.applicationInfo;
        }
        String contextUriString = pm.getText(packageName, this.mContextUriResource, applicationInfo).toString();
        if (contextUriString == null) {
            return null;
        }
        return Uri.parse(contextUriString);
    }

    public CharSequence loadContextDescription(PackageManager pm) throws Resources.NotFoundException {
        if (this.mContextDescriptionResource <= 0) {
            throw new Resources.NotFoundException();
        }
        String packageName = this.mService.resolvePackageName;
        ApplicationInfo applicationInfo = null;
        if (packageName == null) {
            packageName = this.mService.serviceInfo.packageName;
            applicationInfo = this.mService.serviceInfo.applicationInfo;
        }
        return pm.getText(packageName, this.mContextDescriptionResource, applicationInfo).toString();
    }

    public boolean getShowMetadataInPreview() {
        return this.mShowMetadataInPreview;
    }

    @SystemApi
    public boolean supportsAmbientMode() {
        return this.mSupportsAmbientMode;
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public Uri getSettingsSliceUri() {
        String str = this.mSettingsSliceUri;
        if (str == null) {
            return null;
        }
        return Uri.parse(str);
    }

    public boolean supportsMultipleDisplays() {
        return this.mSupportMultipleDisplays;
    }

    public boolean shouldUseDefaultUnfoldTransition() {
        return this.mShouldUseDefaultUnfoldTransition;
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + "Service:");
        this.mService.dump(pw, prefix + "  ");
        pw.println(prefix + "mSettingsActivityName=" + this.mSettingsActivityName);
    }

    public String toString() {
        return "WallpaperInfo{" + this.mService.serviceInfo.name + ", settings: " + this.mSettingsActivityName + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSettingsActivityName);
        dest.writeInt(this.mThumbnailResource);
        dest.writeInt(this.mAuthorResource);
        dest.writeInt(this.mDescriptionResource);
        dest.writeInt(this.mContextUriResource);
        dest.writeInt(this.mContextDescriptionResource);
        dest.writeInt(this.mShowMetadataInPreview ? 1 : 0);
        dest.writeInt(this.mSupportsAmbientMode ? 1 : 0);
        dest.writeString(this.mSettingsSliceUri);
        dest.writeInt(this.mSupportMultipleDisplays ? 1 : 0);
        dest.writeInt(this.mShouldUseDefaultUnfoldTransition ? 1 : 0);
        this.mService.writeToParcel(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
