package android.speech.tts;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.logging.nano.MetricsProto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes3.dex */
public class Voice implements Parcelable {
    public static final Parcelable.Creator<Voice> CREATOR = new Parcelable.Creator<Voice>() { // from class: android.speech.tts.Voice.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Voice createFromParcel(Parcel in) {
            return new Voice(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Voice[] newArray(int size) {
            return new Voice[size];
        }
    };
    public static final int LATENCY_HIGH = 400;
    public static final int LATENCY_LOW = 200;
    public static final int LATENCY_NORMAL = 300;
    public static final int LATENCY_VERY_HIGH = 500;
    public static final int LATENCY_VERY_LOW = 100;
    public static final int QUALITY_HIGH = 400;
    public static final int QUALITY_LOW = 200;
    public static final int QUALITY_NORMAL = 300;
    public static final int QUALITY_VERY_HIGH = 500;
    public static final int QUALITY_VERY_LOW = 100;
    private final Set<String> mFeatures;
    private final int mLatency;
    private final Locale mLocale;
    private final String mName;
    private final int mQuality;
    private final boolean mRequiresNetworkConnection;

    public Voice(String name, Locale locale, int quality, int latency, boolean requiresNetworkConnection, Set<String> features) {
        this.mName = name;
        this.mLocale = locale;
        this.mQuality = quality;
        this.mLatency = latency;
        this.mRequiresNetworkConnection = requiresNetworkConnection;
        this.mFeatures = features;
    }

    private Voice(Parcel in) {
        this.mName = in.readString();
        this.mLocale = (Locale) in.readSerializable(Locale.class.getClassLoader(), Locale.class);
        this.mQuality = in.readInt();
        this.mLatency = in.readInt();
        this.mRequiresNetworkConnection = in.readByte() == 1;
        HashSet hashSet = new HashSet();
        this.mFeatures = hashSet;
        Collections.addAll(hashSet, in.readStringArray());
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeSerializable(this.mLocale);
        dest.writeInt(this.mQuality);
        dest.writeInt(this.mLatency);
        dest.writeByte(this.mRequiresNetworkConnection ? (byte) 1 : (byte) 0);
        dest.writeStringList(new ArrayList(this.mFeatures));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    public int getQuality() {
        return this.mQuality;
    }

    public int getLatency() {
        return this.mLatency;
    }

    public boolean isNetworkConnectionRequired() {
        return this.mRequiresNetworkConnection;
    }

    public String getName() {
        return this.mName;
    }

    public Set<String> getFeatures() {
        return this.mFeatures;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(64);
        return builder.append("Voice[Name: ").append(this.mName).append(", locale: ").append(this.mLocale).append(", quality: ").append(this.mQuality).append(", latency: ").append(this.mLatency).append(", requiresNetwork: ").append(this.mRequiresNetworkConnection).append(", features: ").append(this.mFeatures.toString()).append(NavigationBarInflaterView.SIZE_MOD_END).toString();
    }

    public int hashCode() {
        int i = 1 * 31;
        Set<String> set = this.mFeatures;
        int result = i + (set == null ? 0 : set.hashCode());
        int result2 = ((result * 31) + this.mLatency) * 31;
        Locale locale = this.mLocale;
        int result3 = (result2 + (locale == null ? 0 : locale.hashCode())) * 31;
        String str = this.mName;
        return ((((result3 + (str != null ? str.hashCode() : 0)) * 31) + this.mQuality) * 31) + (this.mRequiresNetworkConnection ? MetricsProto.MetricsEvent.AUTOFILL_SERVICE_DISABLED_APP : MetricsProto.MetricsEvent.ANOMALY_TYPE_UNOPTIMIZED_BT);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Voice other = (Voice) obj;
        Set<String> set = this.mFeatures;
        if (set == null) {
            if (other.mFeatures != null) {
                return false;
            }
        } else if (!set.equals(other.mFeatures)) {
            return false;
        }
        if (this.mLatency != other.mLatency) {
            return false;
        }
        Locale locale = this.mLocale;
        if (locale == null) {
            if (other.mLocale != null) {
                return false;
            }
        } else if (!locale.equals(other.mLocale)) {
            return false;
        }
        String str = this.mName;
        if (str == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!str.equals(other.mName)) {
            return false;
        }
        if (this.mQuality == other.mQuality && this.mRequiresNetworkConnection == other.mRequiresNetworkConnection) {
            return true;
        }
        return false;
    }
}
