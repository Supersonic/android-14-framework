package android.printservice;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
@SystemApi
/* loaded from: classes3.dex */
public final class PrintServiceInfo implements Parcelable {
    private static final String TAG_PRINT_SERVICE = "print-service";
    private final String mAddPrintersActivityName;
    private final String mAdvancedPrintOptionsActivityName;
    private final String mId;
    private boolean mIsEnabled;
    private final ResolveInfo mResolveInfo;
    private final String mSettingsActivityName;
    private static final String LOG_TAG = PrintServiceInfo.class.getSimpleName();
    public static final Parcelable.Creator<PrintServiceInfo> CREATOR = new Parcelable.Creator<PrintServiceInfo>() { // from class: android.printservice.PrintServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrintServiceInfo createFromParcel(Parcel parcel) {
            return new PrintServiceInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrintServiceInfo[] newArray(int size) {
            return new PrintServiceInfo[size];
        }
    };

    public PrintServiceInfo(Parcel parcel) {
        this.mId = parcel.readString();
        this.mIsEnabled = parcel.readByte() != 0;
        this.mResolveInfo = (ResolveInfo) parcel.readParcelable(null, ResolveInfo.class);
        this.mSettingsActivityName = parcel.readString();
        this.mAddPrintersActivityName = parcel.readString();
        this.mAdvancedPrintOptionsActivityName = parcel.readString();
    }

    public PrintServiceInfo(ResolveInfo resolveInfo, String settingsActivityName, String addPrintersActivityName, String advancedPrintOptionsActivityName) {
        this.mId = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name).flattenToString();
        this.mResolveInfo = resolveInfo;
        this.mSettingsActivityName = settingsActivityName;
        this.mAddPrintersActivityName = addPrintersActivityName;
        this.mAdvancedPrintOptionsActivityName = advancedPrintOptionsActivityName;
    }

    public ComponentName getComponentName() {
        return new ComponentName(this.mResolveInfo.serviceInfo.packageName, this.mResolveInfo.serviceInfo.name);
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x00b8, code lost:
        if (r3 == null) goto L34;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static PrintServiceInfo create(Context context, ResolveInfo resolveInfo) {
        String settingsActivityName = null;
        String addPrintersActivityName = null;
        String advancedPrintOptionsActivityName = null;
        PackageManager packageManager = context.getPackageManager();
        XmlResourceParser parser = resolveInfo.serviceInfo.loadXmlMetaData(packageManager, PrintService.SERVICE_META_DATA);
        if (parser != null) {
            for (int type = 0; type != 1 && type != 2; type = parser.next()) {
                try {
                    try {
                        try {
                            try {
                            } catch (PackageManager.NameNotFoundException e) {
                                Log.m110e(LOG_TAG, "Unable to load resources for: " + resolveInfo.serviceInfo.packageName);
                                if (parser != null) {
                                }
                            }
                        } catch (IOException ioe) {
                            Log.m104w(LOG_TAG, "Error reading meta-data:" + ioe);
                        }
                    } catch (XmlPullParserException xppe) {
                        Log.m104w(LOG_TAG, "Error reading meta-data:" + xppe);
                        if (parser != null) {
                        }
                    }
                } finally {
                    if (parser != null) {
                        parser.close();
                    }
                }
            }
            String nodeName = parser.getName();
            if (TAG_PRINT_SERVICE.equals(nodeName)) {
                Resources resources = packageManager.getResourcesForApplication(resolveInfo.serviceInfo.applicationInfo);
                AttributeSet allAttributes = Xml.asAttributeSet(parser);
                TypedArray attributes = resources.obtainAttributes(allAttributes, C4057R.styleable.PrintService);
                settingsActivityName = attributes.getString(0);
                addPrintersActivityName = attributes.getString(1);
                advancedPrintOptionsActivityName = attributes.getString(3);
                attributes.recycle();
            } else {
                Log.m110e(LOG_TAG, "Ignoring meta-data that does not start with print-service tag");
            }
        }
        return new PrintServiceInfo(resolveInfo, settingsActivityName, addPrintersActivityName, advancedPrintOptionsActivityName);
    }

    public String getId() {
        return this.mId;
    }

    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.mIsEnabled = isEnabled;
    }

    public ResolveInfo getResolveInfo() {
        return this.mResolveInfo;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public String getAddPrintersActivityName() {
        return this.mAddPrintersActivityName;
    }

    public String getAdvancedOptionsActivityName() {
        return this.mAdvancedPrintOptionsActivityName;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flagz) {
        parcel.writeString(this.mId);
        parcel.writeByte(this.mIsEnabled ? (byte) 1 : (byte) 0);
        parcel.writeParcelable(this.mResolveInfo, 0);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeString(this.mAddPrintersActivityName);
        parcel.writeString(this.mAdvancedPrintOptionsActivityName);
    }

    public int hashCode() {
        String str = this.mId;
        return (str == null ? 0 : str.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintServiceInfo other = (PrintServiceInfo) obj;
        String str = this.mId;
        if (str == null) {
            if (other.mId != null) {
                return false;
            }
        } else if (!str.equals(other.mId)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrintServiceInfo{");
        builder.append("id=").append(this.mId);
        builder.append("isEnabled=").append(this.mIsEnabled);
        builder.append(", resolveInfo=").append(this.mResolveInfo);
        builder.append(", settingsActivityName=").append(this.mSettingsActivityName);
        builder.append(", addPrintersActivityName=").append(this.mAddPrintersActivityName);
        builder.append(", advancedPrintOptionsActivityName=").append(this.mAdvancedPrintOptionsActivityName);
        builder.append("}");
        return builder.toString();
    }
}
