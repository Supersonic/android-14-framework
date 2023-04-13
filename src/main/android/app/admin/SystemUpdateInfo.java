package android.app.admin;

import android.p008os.Build;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class SystemUpdateInfo implements Parcelable {
    private static final String ATTR_ORIGINAL_BUILD = "original-build";
    private static final String ATTR_RECEIVED_TIME = "received-time";
    private static final String ATTR_SECURITY_PATCH_STATE = "security-patch-state";
    public static final Parcelable.Creator<SystemUpdateInfo> CREATOR = new Parcelable.Creator<SystemUpdateInfo>() { // from class: android.app.admin.SystemUpdateInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SystemUpdateInfo createFromParcel(Parcel in) {
            return new SystemUpdateInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SystemUpdateInfo[] newArray(int size) {
            return new SystemUpdateInfo[size];
        }
    };
    public static final int SECURITY_PATCH_STATE_FALSE = 1;
    public static final int SECURITY_PATCH_STATE_TRUE = 2;
    public static final int SECURITY_PATCH_STATE_UNKNOWN = 0;
    private static final String TAG = "SystemUpdateInfo";
    private final long mReceivedTime;
    private final int mSecurityPatchState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SecurityPatchState {
    }

    private SystemUpdateInfo(long receivedTime, int securityPatchState) {
        this.mReceivedTime = receivedTime;
        this.mSecurityPatchState = securityPatchState;
    }

    private SystemUpdateInfo(Parcel in) {
        this.mReceivedTime = in.readLong();
        this.mSecurityPatchState = in.readInt();
    }

    /* renamed from: of */
    public static SystemUpdateInfo m196of(long receivedTime) {
        if (receivedTime == -1) {
            return null;
        }
        return new SystemUpdateInfo(receivedTime, 0);
    }

    /* renamed from: of */
    public static SystemUpdateInfo m195of(long receivedTime, boolean isSecurityPatch) {
        if (receivedTime == -1) {
            return null;
        }
        return new SystemUpdateInfo(receivedTime, isSecurityPatch ? 2 : 1);
    }

    public long getReceivedTime() {
        return this.mReceivedTime;
    }

    public int getSecurityPatchState() {
        return this.mSecurityPatchState;
    }

    public void writeToXml(TypedXmlSerializer out, String tag) throws IOException {
        out.startTag(null, tag);
        out.attributeLong(null, ATTR_RECEIVED_TIME, this.mReceivedTime);
        out.attributeInt(null, ATTR_SECURITY_PATCH_STATE, this.mSecurityPatchState);
        out.attribute(null, ATTR_ORIGINAL_BUILD, Build.FINGERPRINT);
        out.endTag(null, tag);
    }

    public static SystemUpdateInfo readFromXml(TypedXmlPullParser parser) {
        String buildFingerprint = parser.getAttributeValue(null, ATTR_ORIGINAL_BUILD);
        if (Build.FINGERPRINT.equals(buildFingerprint)) {
            try {
                long receivedTime = parser.getAttributeLong(null, ATTR_RECEIVED_TIME);
                int securityPatchState = parser.getAttributeInt(null, ATTR_SECURITY_PATCH_STATE);
                return new SystemUpdateInfo(receivedTime, securityPatchState);
            } catch (XmlPullParserException e) {
                Log.m103w(TAG, "Load xml failed", e);
                return null;
            }
        }
        return null;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getReceivedTime());
        dest.writeInt(getSecurityPatchState());
    }

    public String toString() {
        return String.format("SystemUpdateInfo (receivedTime = %d, securityPatchState = %s)", Long.valueOf(this.mReceivedTime), securityPatchStateToString(this.mSecurityPatchState));
    }

    private static String securityPatchStateToString(int state) {
        switch (state) {
            case 0:
                return "unknown";
            case 1:
                return "false";
            case 2:
                return "true";
            default:
                throw new IllegalArgumentException("Unrecognized security patch state: " + state);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SystemUpdateInfo that = (SystemUpdateInfo) o;
        if (this.mReceivedTime == that.mReceivedTime && this.mSecurityPatchState == that.mSecurityPatchState) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mReceivedTime), Integer.valueOf(this.mSecurityPatchState));
    }
}
