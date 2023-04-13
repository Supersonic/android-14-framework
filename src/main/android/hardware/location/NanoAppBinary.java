package android.hardware.location;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
@SystemApi
/* loaded from: classes2.dex */
public final class NanoAppBinary implements Parcelable {
    private static final int EXPECTED_HEADER_VERSION = 1;
    private static final int EXPECTED_MAGIC_VALUE = 1330528590;
    private static final int HEADER_SIZE_BYTES = 40;
    private static final int NANOAPP_ENCRYPTED_FLAG_BIT = 2;
    private static final int NANOAPP_SIGNED_FLAG_BIT = 1;
    private static final String TAG = "NanoAppBinary";
    private int mFlags;
    private boolean mHasValidHeader;
    private int mHeaderVersion;
    private long mHwHubType;
    private int mMagic;
    private byte[] mNanoAppBinary;
    private long mNanoAppId;
    private int mNanoAppVersion;
    private byte mTargetChreApiMajorVersion;
    private byte mTargetChreApiMinorVersion;
    private static final ByteOrder HEADER_ORDER = ByteOrder.LITTLE_ENDIAN;
    public static final Parcelable.Creator<NanoAppBinary> CREATOR = new Parcelable.Creator<NanoAppBinary>() { // from class: android.hardware.location.NanoAppBinary.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NanoAppBinary createFromParcel(Parcel in) {
            return new NanoAppBinary(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NanoAppBinary[] newArray(int size) {
            return new NanoAppBinary[size];
        }
    };

    public NanoAppBinary(byte[] appBinary) {
        this.mHasValidHeader = false;
        this.mNanoAppBinary = appBinary;
        parseBinaryHeader();
    }

    private void parseBinaryHeader() {
        ByteBuffer buf = ByteBuffer.wrap(this.mNanoAppBinary).order(HEADER_ORDER);
        this.mHasValidHeader = false;
        try {
            int i = buf.getInt();
            this.mHeaderVersion = i;
            if (i != 1) {
                Log.m110e(TAG, "Unexpected header version " + this.mHeaderVersion + " while parsing header (expected 1" + NavigationBarInflaterView.KEY_CODE_END);
                return;
            }
            this.mMagic = buf.getInt();
            this.mNanoAppId = buf.getLong();
            this.mNanoAppVersion = buf.getInt();
            this.mFlags = buf.getInt();
            this.mHwHubType = buf.getLong();
            this.mTargetChreApiMajorVersion = buf.get();
            this.mTargetChreApiMinorVersion = buf.get();
            if (this.mMagic != EXPECTED_MAGIC_VALUE) {
                Log.m110e(TAG, "Unexpected magic value " + String.format("0x%08X", Integer.valueOf(this.mMagic)) + "while parsing header (expected " + String.format("0x%08X", Integer.valueOf((int) EXPECTED_MAGIC_VALUE)) + NavigationBarInflaterView.KEY_CODE_END);
            } else {
                this.mHasValidHeader = true;
            }
        } catch (BufferUnderflowException e) {
            Log.m110e(TAG, "Not enough contents in nanoapp header");
        }
    }

    public byte[] getBinary() {
        return this.mNanoAppBinary;
    }

    public byte[] getBinaryNoHeader() {
        byte[] bArr = this.mNanoAppBinary;
        if (bArr.length < 40) {
            throw new IndexOutOfBoundsException("NanoAppBinary binary byte size (" + this.mNanoAppBinary.length + ") is less than header size (40" + NavigationBarInflaterView.KEY_CODE_END);
        }
        return Arrays.copyOfRange(bArr, 40, bArr.length);
    }

    public boolean hasValidHeader() {
        return this.mHasValidHeader;
    }

    public int getHeaderVersion() {
        return this.mHeaderVersion;
    }

    public long getNanoAppId() {
        return this.mNanoAppId;
    }

    public int getNanoAppVersion() {
        return this.mNanoAppVersion;
    }

    public long getHwHubType() {
        return this.mHwHubType;
    }

    public byte getTargetChreApiMajorVersion() {
        return this.mTargetChreApiMajorVersion;
    }

    public byte getTargetChreApiMinorVersion() {
        return this.mTargetChreApiMinorVersion;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isSigned() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isEncrypted() {
        return (this.mFlags & 2) != 0;
    }

    private NanoAppBinary(Parcel in) {
        this.mHasValidHeader = false;
        int binaryLength = in.readInt();
        byte[] bArr = new byte[binaryLength];
        this.mNanoAppBinary = bArr;
        in.readByteArray(bArr);
        parseBinaryHeader();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mNanoAppBinary.length);
        out.writeByteArray(this.mNanoAppBinary);
    }
}
