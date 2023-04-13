package android.view.translation;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.translation.TranslationContext;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TranslationCapability implements Parcelable {
    public static final Parcelable.Creator<TranslationCapability> CREATOR = new Parcelable.Creator<TranslationCapability>() { // from class: android.view.translation.TranslationCapability.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationCapability[] newArray(int size) {
            return new TranslationCapability[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TranslationCapability createFromParcel(Parcel in) {
            return new TranslationCapability(in);
        }
    };
    public static final int STATE_AVAILABLE_TO_DOWNLOAD = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_NOT_AVAILABLE = 4;
    public static final int STATE_ON_DEVICE = 3;
    public static final int STATE_REMOVED_AND_AVAILABLE = 1000;
    private final TranslationSpec mSourceSpec;
    private final int mState;
    private final int mSupportedTranslationFlags;
    private final TranslationSpec mTargetSpec;
    private final boolean mUiTranslationEnabled;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ModelState {
    }

    @SystemApi
    public TranslationCapability(int state, TranslationSpec sourceSpec, TranslationSpec targetSpec, boolean uiTranslationEnabled, int supportedTranslationFlags) {
        Objects.requireNonNull(sourceSpec, "sourceSpec should not be null");
        Objects.requireNonNull(targetSpec, "targetSpec should not be null");
        this.mState = state;
        this.mSourceSpec = sourceSpec;
        this.mTargetSpec = targetSpec;
        this.mUiTranslationEnabled = uiTranslationEnabled;
        this.mSupportedTranslationFlags = supportedTranslationFlags;
    }

    public static String modelStateToString(int value) {
        switch (value) {
            case 1:
                return "STATE_AVAILABLE_TO_DOWNLOAD";
            case 2:
                return "STATE_DOWNLOADING";
            case 3:
                return "STATE_ON_DEVICE";
            case 4:
                return "STATE_NOT_AVAILABLE";
            case 1000:
                return "STATE_REMOVED_AND_AVAILABLE";
            default:
                return Integer.toHexString(value);
        }
    }

    public int getState() {
        return this.mState;
    }

    public TranslationSpec getSourceSpec() {
        return this.mSourceSpec;
    }

    public TranslationSpec getTargetSpec() {
        return this.mTargetSpec;
    }

    public boolean isUiTranslationEnabled() {
        return this.mUiTranslationEnabled;
    }

    public int getSupportedTranslationFlags() {
        return this.mSupportedTranslationFlags;
    }

    public String toString() {
        return "TranslationCapability { state = " + modelStateToString(this.mState) + ", sourceSpec = " + this.mSourceSpec + ", targetSpec = " + this.mTargetSpec + ", uiTranslationEnabled = " + this.mUiTranslationEnabled + ", supportedTranslationFlags = " + this.mSupportedTranslationFlags + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mUiTranslationEnabled ? (byte) (0 | 8) : (byte) 0;
        dest.writeByte(flg);
        dest.writeInt(this.mState);
        dest.writeTypedObject(this.mSourceSpec, flags);
        dest.writeTypedObject(this.mTargetSpec, flags);
        dest.writeInt(this.mSupportedTranslationFlags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    TranslationCapability(Parcel in) {
        byte flg = in.readByte();
        boolean uiTranslationEnabled = (flg & 8) != 0;
        int state = in.readInt();
        TranslationSpec sourceSpec = (TranslationSpec) in.readTypedObject(TranslationSpec.CREATOR);
        TranslationSpec targetSpec = (TranslationSpec) in.readTypedObject(TranslationSpec.CREATOR);
        int supportedTranslationFlags = in.readInt();
        this.mState = state;
        if (state != 1 && state != 2 && state != 3 && state != 4 && state != 1000) {
            throw new IllegalArgumentException("state was " + state + " but must be one of: STATE_AVAILABLE_TO_DOWNLOAD(1), STATE_DOWNLOADING(2), STATE_ON_DEVICE(3), STATE_NOT_AVAILABLE(4), STATE_REMOVED_AND_AVAILABLE(1000" + NavigationBarInflaterView.KEY_CODE_END);
        }
        this.mSourceSpec = sourceSpec;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) sourceSpec);
        this.mTargetSpec = targetSpec;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) targetSpec);
        this.mUiTranslationEnabled = uiTranslationEnabled;
        this.mSupportedTranslationFlags = supportedTranslationFlags;
        AnnotationValidations.validate((Class<? extends Annotation>) TranslationContext.TranslationFlag.class, (Annotation) null, supportedTranslationFlags);
    }

    @Deprecated
    private void __metadata() {
    }
}
