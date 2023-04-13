package android.telephony.mbms;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes3.dex */
public final class StreamingServiceInfo extends ServiceInfo implements Parcelable {
    public static final Parcelable.Creator<StreamingServiceInfo> CREATOR = new Parcelable.Creator<StreamingServiceInfo>() { // from class: android.telephony.mbms.StreamingServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StreamingServiceInfo createFromParcel(Parcel source) {
            return new StreamingServiceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StreamingServiceInfo[] newArray(int size) {
            return new StreamingServiceInfo[size];
        }
    };

    @SystemApi
    public StreamingServiceInfo(Map<Locale, String> names, String className, List<Locale> locales, String serviceId, Date start, Date end) {
        super(names, className, locales, serviceId, start, end);
    }

    private StreamingServiceInfo(Parcel in) {
        super(in);
    }

    @Override // android.telephony.mbms.ServiceInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
