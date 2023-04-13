package android.telephony.mbms;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes3.dex */
public final class FileServiceInfo extends ServiceInfo implements Parcelable {
    public static final Parcelable.Creator<FileServiceInfo> CREATOR = new Parcelable.Creator<FileServiceInfo>() { // from class: android.telephony.mbms.FileServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FileServiceInfo createFromParcel(Parcel source) {
            return new FileServiceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FileServiceInfo[] newArray(int size) {
            return new FileServiceInfo[size];
        }
    };
    private final List<FileInfo> files;

    @SystemApi
    public FileServiceInfo(Map<Locale, String> newNames, String newClassName, List<Locale> newLocales, String newServiceId, Date start, Date end, List<FileInfo> newFiles) {
        super(newNames, newClassName, newLocales, newServiceId, start, end);
        this.files = new ArrayList(newFiles);
    }

    FileServiceInfo(Parcel in) {
        super(in);
        ArrayList arrayList = new ArrayList();
        this.files = arrayList;
        in.readList(arrayList, FileInfo.class.getClassLoader(), FileInfo.class);
    }

    @Override // android.telephony.mbms.ServiceInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.files);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<FileInfo> getFiles() {
        return this.files;
    }
}
