package android.media.p007tv.interactive;

import android.annotation.NonNull;
import android.content.ComponentName;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.media.tv.interactive.AppLinkInfo */
/* loaded from: classes2.dex */
public final class AppLinkInfo implements Parcelable {
    public static final Parcelable.Creator<AppLinkInfo> CREATOR = new Parcelable.Creator<AppLinkInfo>() { // from class: android.media.tv.interactive.AppLinkInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppLinkInfo[] newArray(int size) {
            return new AppLinkInfo[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AppLinkInfo createFromParcel(Parcel in) {
            return new AppLinkInfo(in);
        }
    };
    private ComponentName mComponentName;
    private Uri mUri;

    public AppLinkInfo(String packageName, String className, String uriString) {
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) packageName);
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) className);
        this.mComponentName = new ComponentName(packageName, className);
        this.mUri = Uri.parse(uriString);
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public String toString() {
        return "AppLinkInfo { packageName = " + this.mComponentName.getPackageName() + ", className = " + this.mComponentName.getClassName() + ", uri = " + this.mUri.toString() + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mComponentName.writeToParcel(dest, flags);
        Uri uri = this.mUri;
        String uriString = uri == null ? null : uri.toString();
        dest.writeString(uriString);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AppLinkInfo(Parcel in) {
        ComponentName readFromParcel = ComponentName.readFromParcel(in);
        this.mComponentName = readFromParcel;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) readFromParcel.getPackageName());
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) this.mComponentName.getClassName());
        String uriString = in.readString();
        this.mUri = uriString != null ? Uri.parse(uriString) : null;
    }
}
