package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
/* loaded from: classes3.dex */
public final class SignalStrengthUpdateRequest implements Parcelable {
    public static final Parcelable.Creator<SignalStrengthUpdateRequest> CREATOR = new Parcelable.Creator<SignalStrengthUpdateRequest>() { // from class: android.telephony.SignalStrengthUpdateRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalStrengthUpdateRequest createFromParcel(Parcel source) {
            return new SignalStrengthUpdateRequest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SignalStrengthUpdateRequest[] newArray(int size) {
            return new SignalStrengthUpdateRequest[size];
        }
    };
    private final boolean mIsReportingRequestedWhileIdle;
    private final boolean mIsSystemThresholdReportingRequestedWhileIdle;
    private final IBinder mLiveToken;
    private final List<SignalThresholdInfo> mSignalThresholdInfos;

    private SignalStrengthUpdateRequest(List<SignalThresholdInfo> signalThresholdInfos, boolean isReportingRequestedWhileIdle, boolean isSystemThresholdReportingRequestedWhileIdle) {
        validate(signalThresholdInfos, isSystemThresholdReportingRequestedWhileIdle);
        this.mSignalThresholdInfos = signalThresholdInfos;
        this.mIsReportingRequestedWhileIdle = isReportingRequestedWhileIdle;
        this.mIsSystemThresholdReportingRequestedWhileIdle = isSystemThresholdReportingRequestedWhileIdle;
        this.mLiveToken = new Binder();
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private List<SignalThresholdInfo> mSignalThresholdInfos = null;
        private boolean mIsReportingRequestedWhileIdle = false;
        private boolean mIsSystemThresholdReportingRequestedWhileIdle = false;

        public Builder setSignalThresholdInfos(Collection<SignalThresholdInfo> signalThresholdInfos) {
            Objects.requireNonNull(signalThresholdInfos, "SignalThresholdInfo collection must not be null");
            for (SignalThresholdInfo info : signalThresholdInfos) {
                Objects.requireNonNull(info, "SignalThresholdInfo in the collection must not be null");
            }
            ArrayList arrayList = new ArrayList(signalThresholdInfos);
            this.mSignalThresholdInfos = arrayList;
            arrayList.sort(Comparator.comparingInt(new ToIntFunction() { // from class: android.telephony.SignalStrengthUpdateRequest$Builder$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return ((SignalThresholdInfo) obj).getRadioAccessNetworkType();
                }
            }).thenComparing(new Function() { // from class: android.telephony.SignalStrengthUpdateRequest$Builder$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return Integer.valueOf(((SignalThresholdInfo) obj).getSignalMeasurementType());
                }
            }));
            return this;
        }

        public Builder setReportingRequestedWhileIdle(boolean isReportingRequestedWhileIdle) {
            this.mIsReportingRequestedWhileIdle = isReportingRequestedWhileIdle;
            return this;
        }

        @SystemApi
        public Builder setSystemThresholdReportingRequestedWhileIdle(boolean isSystemThresholdReportingRequestedWhileIdle) {
            this.mIsSystemThresholdReportingRequestedWhileIdle = isSystemThresholdReportingRequestedWhileIdle;
            return this;
        }

        public SignalStrengthUpdateRequest build() {
            return new SignalStrengthUpdateRequest(this.mSignalThresholdInfos, this.mIsReportingRequestedWhileIdle, this.mIsSystemThresholdReportingRequestedWhileIdle);
        }
    }

    private SignalStrengthUpdateRequest(Parcel in) {
        this.mSignalThresholdInfos = in.createTypedArrayList(SignalThresholdInfo.CREATOR);
        this.mIsReportingRequestedWhileIdle = in.readBoolean();
        this.mIsSystemThresholdReportingRequestedWhileIdle = in.readBoolean();
        this.mLiveToken = in.readStrongBinder();
    }

    public Collection<SignalThresholdInfo> getSignalThresholdInfos() {
        return Collections.unmodifiableList(this.mSignalThresholdInfos);
    }

    public boolean isReportingRequestedWhileIdle() {
        return this.mIsReportingRequestedWhileIdle;
    }

    @SystemApi
    public boolean isSystemThresholdReportingRequestedWhileIdle() {
        return this.mIsSystemThresholdReportingRequestedWhileIdle;
    }

    public IBinder getLiveToken() {
        return this.mLiveToken;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mSignalThresholdInfos);
        dest.writeBoolean(this.mIsReportingRequestedWhileIdle);
        dest.writeBoolean(this.mIsSystemThresholdReportingRequestedWhileIdle);
        dest.writeStrongBinder(this.mLiveToken);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof SignalStrengthUpdateRequest) {
            SignalStrengthUpdateRequest request = (SignalStrengthUpdateRequest) other;
            return this.mSignalThresholdInfos.equals(request.mSignalThresholdInfos) && this.mIsReportingRequestedWhileIdle == request.mIsReportingRequestedWhileIdle && this.mIsSystemThresholdReportingRequestedWhileIdle == request.mIsSystemThresholdReportingRequestedWhileIdle;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mSignalThresholdInfos, Boolean.valueOf(this.mIsReportingRequestedWhileIdle), Boolean.valueOf(this.mIsSystemThresholdReportingRequestedWhileIdle));
    }

    public String toString() {
        return "SignalStrengthUpdateRequest{mSignalThresholdInfos=" + this.mSignalThresholdInfos + " mIsReportingRequestedWhileIdle=" + this.mIsReportingRequestedWhileIdle + " mIsSystemThresholdReportingRequestedWhileIdle=" + this.mIsSystemThresholdReportingRequestedWhileIdle + " mLiveToken" + this.mLiveToken + "}";
    }

    private static void validate(Collection<SignalThresholdInfo> infos, boolean isSystemThresholdReportingRequestedWhileIdle) {
        if (infos == null || (infos.isEmpty() && !isSystemThresholdReportingRequestedWhileIdle)) {
            throw new IllegalArgumentException("SignalThresholdInfo collection is null or empty");
        }
        Map<Integer, Set<Integer>> ranToTypes = new HashMap<>(infos.size());
        for (SignalThresholdInfo info : infos) {
            int ran = info.getRadioAccessNetworkType();
            int type = info.getSignalMeasurementType();
            ranToTypes.putIfAbsent(Integer.valueOf(ran), new HashSet<>());
            if (!ranToTypes.get(Integer.valueOf(ran)).add(Integer.valueOf(type))) {
                throw new IllegalArgumentException("SignalMeasurementType " + type + " for RAN " + ran + " is not unique");
            }
        }
    }
}
