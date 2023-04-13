package android.window;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteCallback;
import android.window.IOnBackInvokedCallback;
/* loaded from: classes4.dex */
public final class BackNavigationInfo implements Parcelable {
    public static final Parcelable.Creator<BackNavigationInfo> CREATOR = new Parcelable.Creator<BackNavigationInfo>() { // from class: android.window.BackNavigationInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackNavigationInfo createFromParcel(Parcel in) {
            return new BackNavigationInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BackNavigationInfo[] newArray(int size) {
            return new BackNavigationInfo[size];
        }
    };
    public static final String KEY_TRIGGER_BACK = "TriggerBack";
    public static final int TYPE_CALLBACK = 4;
    public static final int TYPE_CROSS_ACTIVITY = 2;
    public static final int TYPE_CROSS_TASK = 3;
    public static final int TYPE_DIALOG_CLOSE = 0;
    public static final int TYPE_RETURN_TO_HOME = 1;
    public static final int TYPE_UNDEFINED = -1;
    private final CustomAnimationInfo mCustomAnimationInfo;
    private final IOnBackInvokedCallback mOnBackInvokedCallback;
    private final RemoteCallback mOnBackNavigationDone;
    private final boolean mPrepareRemoteAnimation;
    private final int mType;

    /* loaded from: classes4.dex */
    public @interface BackTargetType {
    }

    private BackNavigationInfo(int type, RemoteCallback onBackNavigationDone, IOnBackInvokedCallback onBackInvokedCallback, boolean isPrepareRemoteAnimation, CustomAnimationInfo customAnimationInfo) {
        this.mType = type;
        this.mOnBackNavigationDone = onBackNavigationDone;
        this.mOnBackInvokedCallback = onBackInvokedCallback;
        this.mPrepareRemoteAnimation = isPrepareRemoteAnimation;
        this.mCustomAnimationInfo = customAnimationInfo;
    }

    private BackNavigationInfo(Parcel in) {
        this.mType = in.readInt();
        this.mOnBackNavigationDone = (RemoteCallback) in.readTypedObject(RemoteCallback.CREATOR);
        this.mOnBackInvokedCallback = IOnBackInvokedCallback.Stub.asInterface(in.readStrongBinder());
        this.mPrepareRemoteAnimation = in.readBoolean();
        this.mCustomAnimationInfo = (CustomAnimationInfo) in.readTypedObject(CustomAnimationInfo.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeTypedObject(this.mOnBackNavigationDone, flags);
        dest.writeStrongInterface(this.mOnBackInvokedCallback);
        dest.writeBoolean(this.mPrepareRemoteAnimation);
        dest.writeTypedObject(this.mCustomAnimationInfo, flags);
    }

    public int getType() {
        return this.mType;
    }

    public IOnBackInvokedCallback getOnBackInvokedCallback() {
        return this.mOnBackInvokedCallback;
    }

    public boolean isPrepareRemoteAnimation() {
        return this.mPrepareRemoteAnimation;
    }

    public void onBackNavigationFinished(boolean triggerBack) {
        if (this.mOnBackNavigationDone != null) {
            Bundle result = new Bundle();
            result.putBoolean(KEY_TRIGGER_BACK, triggerBack);
            this.mOnBackNavigationDone.sendResult(result);
        }
    }

    public CustomAnimationInfo getCustomAnimationInfo() {
        return this.mCustomAnimationInfo;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "BackNavigationInfo{mType=" + typeToString(this.mType) + " (" + this.mType + "), mOnBackNavigationDone=" + this.mOnBackNavigationDone + ", mOnBackInvokedCallback=" + this.mOnBackInvokedCallback + ", mCustomizeAnimationInfo=" + this.mCustomAnimationInfo + '}';
    }

    public static String typeToString(int type) {
        switch (type) {
            case -1:
                return "TYPE_UNDEFINED";
            case 0:
                return "TYPE_DIALOG_CLOSE";
            case 1:
                return "TYPE_RETURN_TO_HOME";
            case 2:
                return "TYPE_CROSS_ACTIVITY";
            case 3:
                return "TYPE_CROSS_TASK";
            case 4:
                return "TYPE_CALLBACK";
            default:
                return String.valueOf(type);
        }
    }

    /* loaded from: classes4.dex */
    public static final class CustomAnimationInfo implements Parcelable {
        public static final Parcelable.Creator<CustomAnimationInfo> CREATOR = new Parcelable.Creator<CustomAnimationInfo>() { // from class: android.window.BackNavigationInfo.CustomAnimationInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CustomAnimationInfo createFromParcel(Parcel in) {
                return new CustomAnimationInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CustomAnimationInfo[] newArray(int size) {
                return new CustomAnimationInfo[size];
            }
        };
        private final String mPackageName;
        private int mWindowAnimations;

        public String getPackageName() {
            return this.mPackageName;
        }

        public int getWindowAnimations() {
            return this.mWindowAnimations;
        }

        public CustomAnimationInfo(String packageName) {
            this.mPackageName = packageName;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString8(this.mPackageName);
            dest.writeInt(this.mWindowAnimations);
        }

        private CustomAnimationInfo(Parcel in) {
            this.mPackageName = in.readString8();
            this.mWindowAnimations = in.readInt();
        }

        public String toString() {
            return "CustomAnimationInfo, package name= " + this.mPackageName;
        }
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private CustomAnimationInfo mCustomAnimationInfo;
        private boolean mPrepareRemoteAnimation;
        private int mType = -1;
        private RemoteCallback mOnBackNavigationDone = null;
        private IOnBackInvokedCallback mOnBackInvokedCallback = null;

        public Builder setType(int type) {
            this.mType = type;
            return this;
        }

        public Builder setOnBackNavigationDone(RemoteCallback onBackNavigationDone) {
            this.mOnBackNavigationDone = onBackNavigationDone;
            return this;
        }

        public Builder setOnBackInvokedCallback(IOnBackInvokedCallback onBackInvokedCallback) {
            this.mOnBackInvokedCallback = onBackInvokedCallback;
            return this;
        }

        public Builder setPrepareRemoteAnimation(boolean prepareRemoteAnimation) {
            this.mPrepareRemoteAnimation = prepareRemoteAnimation;
            return this;
        }

        public Builder setWindowAnimations(String packageName, int windowAnimations) {
            CustomAnimationInfo customAnimationInfo = new CustomAnimationInfo(packageName);
            this.mCustomAnimationInfo = customAnimationInfo;
            customAnimationInfo.mWindowAnimations = windowAnimations;
            return this;
        }

        public BackNavigationInfo build() {
            return new BackNavigationInfo(this.mType, this.mOnBackNavigationDone, this.mOnBackInvokedCallback, this.mPrepareRemoteAnimation, this.mCustomAnimationInfo);
        }
    }
}
