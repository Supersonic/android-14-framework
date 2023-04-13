package android.view;

import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes4.dex */
public final class InputChannel implements Parcelable {
    private static final boolean DEBUG = false;
    private static final String TAG = "InputChannel";
    private long mPtr;
    private static final NativeAllocationRegistry sRegistry = NativeAllocationRegistry.createMalloced(InputChannel.class.getClassLoader(), nativeGetFinalizer());
    public static final Parcelable.Creator<InputChannel> CREATOR = new Parcelable.Creator<InputChannel>() { // from class: android.view.InputChannel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputChannel createFromParcel(Parcel source) {
            InputChannel result = new InputChannel();
            result.readFromParcel(source);
            return result;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InputChannel[] newArray(int size) {
            return new InputChannel[size];
        }
    };

    private native void nativeDispose(long j);

    private native long nativeDup(long j);

    private static native long nativeGetFinalizer();

    private native String nativeGetName(long j);

    private native IBinder nativeGetToken(long j);

    private static native long[] nativeOpenInputChannelPair(String str);

    private native long nativeReadFromParcel(Parcel parcel);

    private native void nativeWriteToParcel(Parcel parcel, long j);

    private void setNativeInputChannel(long nativeChannel) {
        if (nativeChannel != 0) {
            if (this.mPtr != 0) {
                throw new IllegalArgumentException("Already has native input channel.");
            }
            sRegistry.registerNativeAllocation(this, nativeChannel);
            this.mPtr = nativeChannel;
            return;
        }
        throw new IllegalArgumentException("Attempting to set native input channel to null.");
    }

    public static InputChannel[] openInputChannelPair(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        InputChannel[] channels = new InputChannel[2];
        long[] nativeChannels = nativeOpenInputChannelPair(name);
        for (int i = 0; i < 2; i++) {
            channels[i] = new InputChannel();
            channels[i].setNativeInputChannel(nativeChannels[i]);
        }
        return channels;
    }

    public String getName() {
        String name = nativeGetName(this.mPtr);
        return name != null ? name : "uninitialized";
    }

    public void dispose() {
        nativeDispose(this.mPtr);
    }

    public void release() {
    }

    public void copyTo(InputChannel outParameter) {
        if (outParameter == null) {
            throw new IllegalArgumentException("outParameter must not be null");
        }
        if (outParameter.mPtr != 0) {
            throw new IllegalArgumentException("Other object already has a native input channel.");
        }
        outParameter.setNativeInputChannel(nativeDup(this.mPtr));
    }

    public InputChannel dup() {
        InputChannel target = new InputChannel();
        target.setNativeInputChannel(nativeDup(this.mPtr));
        return target;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 1;
    }

    public void readFromParcel(Parcel in) {
        if (in == null) {
            throw new IllegalArgumentException("in must not be null");
        }
        long nativeIn = nativeReadFromParcel(in);
        if (nativeIn != 0) {
            setNativeInputChannel(nativeIn);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (out == null) {
            throw new IllegalArgumentException("out must not be null");
        }
        nativeWriteToParcel(out, this.mPtr);
        if ((flags & 1) != 0) {
            dispose();
        }
    }

    public String toString() {
        return getName();
    }

    public IBinder getToken() {
        return nativeGetToken(this.mPtr);
    }
}
