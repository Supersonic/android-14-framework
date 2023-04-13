package android.print;

import android.app.PendingIntent;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public final class PrinterInfo implements Parcelable {
    public static final Parcelable.Creator<PrinterInfo> CREATOR = new Parcelable.Creator<PrinterInfo>() { // from class: android.print.PrinterInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrinterInfo createFromParcel(Parcel parcel) {
            return new PrinterInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PrinterInfo[] newArray(int size) {
            return new PrinterInfo[size];
        }
    };
    public static final int STATUS_BUSY = 2;
    public static final int STATUS_IDLE = 1;
    public static final int STATUS_UNAVAILABLE = 3;
    private final PrinterCapabilitiesInfo mCapabilities;
    private final int mCustomPrinterIconGen;
    private final String mDescription;
    private final boolean mHasCustomPrinterIcon;
    private final int mIconResourceId;
    private final PrinterId mId;
    private final PendingIntent mInfoIntent;
    private final String mName;
    private final int mStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Status {
    }

    private PrinterInfo(PrinterId printerId, String name, int status, int iconResourceId, boolean hasCustomPrinterIcon, String description, PendingIntent infoIntent, PrinterCapabilitiesInfo capabilities, int customPrinterIconGen) {
        this.mId = printerId;
        this.mName = name;
        this.mStatus = status;
        this.mIconResourceId = iconResourceId;
        this.mHasCustomPrinterIcon = hasCustomPrinterIcon;
        this.mDescription = description;
        this.mInfoIntent = infoIntent;
        this.mCapabilities = capabilities;
        this.mCustomPrinterIconGen = customPrinterIconGen;
    }

    public PrinterId getId() {
        return this.mId;
    }

    public Drawable loadIcon(Context context) {
        Drawable drawable = null;
        PackageManager packageManager = context.getPackageManager();
        if (this.mHasCustomPrinterIcon) {
            PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
            Icon icon = printManager.getCustomPrinterIcon(this.mId);
            if (icon != null) {
                drawable = icon.loadDrawable(context);
            }
        }
        if (drawable == null) {
            try {
                String packageName = this.mId.getServiceName().getPackageName();
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                int i = this.mIconResourceId;
                if (i != 0) {
                    drawable = packageManager.getDrawable(packageName, i, appInfo);
                }
                if (drawable == null) {
                    return appInfo.loadIcon(packageManager);
                }
                return drawable;
            } catch (PackageManager.NameNotFoundException e) {
                return drawable;
            }
        }
        return drawable;
    }

    public boolean getHasCustomPrinterIcon() {
        return this.mHasCustomPrinterIcon;
    }

    public String getName() {
        return this.mName;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public PendingIntent getInfoIntent() {
        return this.mInfoIntent;
    }

    public PrinterCapabilitiesInfo getCapabilities() {
        return this.mCapabilities;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PrinterId checkPrinterId(PrinterId printerId) {
        return (PrinterId) Preconditions.checkNotNull(printerId, "printerId cannot be null.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int checkStatus(int status) {
        if (status != 1 && status != 2 && status != 3) {
            throw new IllegalArgumentException("status is invalid.");
        }
        return status;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String checkName(String name) {
        return (String) Preconditions.checkStringNotEmpty(name, "name cannot be empty.");
    }

    private PrinterInfo(Parcel parcel) {
        this.mId = checkPrinterId((PrinterId) parcel.readParcelable(null, PrinterId.class));
        this.mName = checkName(parcel.readString());
        this.mStatus = checkStatus(parcel.readInt());
        this.mDescription = parcel.readString();
        this.mCapabilities = (PrinterCapabilitiesInfo) parcel.readParcelable(null, PrinterCapabilitiesInfo.class);
        this.mIconResourceId = parcel.readInt();
        this.mHasCustomPrinterIcon = parcel.readByte() != 0;
        this.mCustomPrinterIconGen = parcel.readInt();
        this.mInfoIntent = (PendingIntent) parcel.readParcelable(null, PendingIntent.class);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeParcelable(this.mId, flags);
        parcel.writeString(this.mName);
        parcel.writeInt(this.mStatus);
        parcel.writeString(this.mDescription);
        parcel.writeParcelable(this.mCapabilities, flags);
        parcel.writeInt(this.mIconResourceId);
        parcel.writeByte(this.mHasCustomPrinterIcon ? (byte) 1 : (byte) 0);
        parcel.writeInt(this.mCustomPrinterIconGen);
        parcel.writeParcelable(this.mInfoIntent, flags);
    }

    public int hashCode() {
        int result = (1 * 31) + this.mId.hashCode();
        int result2 = ((((result * 31) + this.mName.hashCode()) * 31) + this.mStatus) * 31;
        String str = this.mDescription;
        int result3 = (result2 + (str != null ? str.hashCode() : 0)) * 31;
        PrinterCapabilitiesInfo printerCapabilitiesInfo = this.mCapabilities;
        int result4 = (((((((result3 + (printerCapabilitiesInfo != null ? printerCapabilitiesInfo.hashCode() : 0)) * 31) + this.mIconResourceId) * 31) + (this.mHasCustomPrinterIcon ? 1 : 0)) * 31) + this.mCustomPrinterIconGen) * 31;
        PendingIntent pendingIntent = this.mInfoIntent;
        return result4 + (pendingIntent != null ? pendingIntent.hashCode() : 0);
    }

    public boolean equalsIgnoringStatus(PrinterInfo other) {
        if (this.mId.equals(other.mId) && this.mName.equals(other.mName) && TextUtils.equals(this.mDescription, other.mDescription)) {
            PrinterCapabilitiesInfo printerCapabilitiesInfo = this.mCapabilities;
            if (printerCapabilitiesInfo == null) {
                if (other.mCapabilities != null) {
                    return false;
                }
            } else if (!printerCapabilitiesInfo.equals(other.mCapabilities)) {
                return false;
            }
            if (this.mIconResourceId == other.mIconResourceId && this.mHasCustomPrinterIcon == other.mHasCustomPrinterIcon && this.mCustomPrinterIconGen == other.mCustomPrinterIconGen) {
                PendingIntent pendingIntent = this.mInfoIntent;
                return pendingIntent == null ? other.mInfoIntent == null : pendingIntent.equals(other.mInfoIntent);
            }
            return false;
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrinterInfo other = (PrinterInfo) obj;
        if (equalsIgnoringStatus(other) && this.mStatus == other.mStatus) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrinterInfo{");
        builder.append("id=").append(this.mId);
        builder.append(", name=").append(this.mName);
        builder.append(", status=").append(this.mStatus);
        builder.append(", description=").append(this.mDescription);
        builder.append(", capabilities=").append(this.mCapabilities);
        builder.append(", iconResId=").append(this.mIconResourceId);
        builder.append(", hasCustomPrinterIcon=").append(this.mHasCustomPrinterIcon);
        builder.append(", customPrinterIconGen=").append(this.mCustomPrinterIconGen);
        builder.append(", infoIntent=").append(this.mInfoIntent);
        builder.append("\"}");
        return builder.toString();
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private PrinterCapabilitiesInfo mCapabilities;
        private int mCustomPrinterIconGen;
        private String mDescription;
        private boolean mHasCustomPrinterIcon;
        private int mIconResourceId;
        private PendingIntent mInfoIntent;
        private String mName;
        private PrinterId mPrinterId;
        private int mStatus;

        public Builder(PrinterId printerId, String name, int status) {
            this.mPrinterId = PrinterInfo.checkPrinterId(printerId);
            this.mName = PrinterInfo.checkName(name);
            this.mStatus = PrinterInfo.checkStatus(status);
        }

        public Builder(PrinterInfo other) {
            this.mPrinterId = other.mId;
            this.mName = other.mName;
            this.mStatus = other.mStatus;
            this.mIconResourceId = other.mIconResourceId;
            this.mHasCustomPrinterIcon = other.mHasCustomPrinterIcon;
            this.mDescription = other.mDescription;
            this.mInfoIntent = other.mInfoIntent;
            this.mCapabilities = other.mCapabilities;
            this.mCustomPrinterIconGen = other.mCustomPrinterIconGen;
        }

        public Builder setStatus(int status) {
            this.mStatus = PrinterInfo.checkStatus(status);
            return this;
        }

        public Builder setIconResourceId(int iconResourceId) {
            this.mIconResourceId = Preconditions.checkArgumentNonnegative(iconResourceId, "iconResourceId can't be negative");
            return this;
        }

        public Builder setHasCustomPrinterIcon(boolean hasCustomPrinterIcon) {
            this.mHasCustomPrinterIcon = hasCustomPrinterIcon;
            return this;
        }

        public Builder setName(String name) {
            this.mName = PrinterInfo.checkName(name);
            return this;
        }

        public Builder setDescription(String description) {
            this.mDescription = description;
            return this;
        }

        public Builder setInfoIntent(PendingIntent infoIntent) {
            this.mInfoIntent = infoIntent;
            return this;
        }

        public Builder setCapabilities(PrinterCapabilitiesInfo capabilities) {
            this.mCapabilities = capabilities;
            return this;
        }

        public PrinterInfo build() {
            return new PrinterInfo(this.mPrinterId, this.mName, this.mStatus, this.mIconResourceId, this.mHasCustomPrinterIcon, this.mDescription, this.mInfoIntent, this.mCapabilities, this.mCustomPrinterIconGen);
        }

        public Builder incCustomPrinterIconGen() {
            this.mCustomPrinterIconGen++;
            return this;
        }
    }
}
