package android.telecom;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes3.dex */
public final class StreamingCall implements Parcelable {
    public static final Parcelable.Creator<StreamingCall> CREATOR = new Parcelable.Creator<StreamingCall>() { // from class: android.telecom.StreamingCall.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StreamingCall createFromParcel(Parcel in) {
            return new StreamingCall(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StreamingCall[] newArray(int size) {
            return new StreamingCall[size];
        }
    };
    public static final int STATE_DISCONNECTED = 3;
    public static final int STATE_HOLDING = 2;
    public static final int STATE_STREAMING = 1;
    private StreamingCallAdapter mAdapter;
    private final Uri mAddress;
    private final ComponentName mComponentName;
    private final CharSequence mDisplayName;
    private final Bundle mExtras;
    private int mState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface StreamingCallState {
    }

    private StreamingCall(Parcel in) {
        this.mAdapter = null;
        this.mComponentName = (ComponentName) in.readParcelable(ComponentName.class.getClassLoader());
        this.mDisplayName = in.readCharSequence();
        this.mAddress = (Uri) in.readParcelable(Uri.class.getClassLoader());
        this.mExtras = in.readBundle();
        this.mState = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mComponentName, flags);
        dest.writeCharSequence(this.mDisplayName);
        dest.writeParcelable(this.mAddress, flags);
        dest.writeBundle(this.mExtras);
        dest.writeInt(this.mState);
    }

    public StreamingCall(ComponentName componentName, CharSequence displayName, Uri address, Bundle extras) {
        this.mAdapter = null;
        this.mComponentName = componentName;
        this.mDisplayName = displayName;
        this.mAddress = address;
        this.mExtras = extras;
        this.mState = 1;
    }

    public void setAdapter(StreamingCallAdapter adapter) {
        this.mAdapter = adapter;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public CharSequence getDisplayName() {
        return this.mDisplayName;
    }

    public Uri getAddress() {
        return this.mAddress;
    }

    public int getState() {
        return this.mState;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public void requestStreamingState(int state) {
        this.mAdapter.setStreamingState(state);
    }
}
