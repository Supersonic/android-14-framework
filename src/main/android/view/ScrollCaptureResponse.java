package android.view;

import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.view.IScrollCaptureConnection;
import com.android.internal.util.AnnotationValidations;
import com.android.net.module.util.NetworkStackConstants;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public class ScrollCaptureResponse implements Parcelable {
    public static final Parcelable.Creator<ScrollCaptureResponse> CREATOR = new Parcelable.Creator<ScrollCaptureResponse>() { // from class: android.view.ScrollCaptureResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ScrollCaptureResponse[] newArray(int size) {
            return new ScrollCaptureResponse[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ScrollCaptureResponse createFromParcel(Parcel in) {
            return new ScrollCaptureResponse(in);
        }
    };
    private Rect mBoundsInWindow;
    private IScrollCaptureConnection mConnection;
    private String mDescription;
    private ArrayList<String> mMessages;
    private String mPackageName;
    private Rect mWindowBounds;
    private String mWindowTitle;

    public boolean isConnected() {
        IScrollCaptureConnection iScrollCaptureConnection = this.mConnection;
        return iScrollCaptureConnection != null && iScrollCaptureConnection.asBinder().isBinderAlive();
    }

    public void close() {
        IScrollCaptureConnection iScrollCaptureConnection = this.mConnection;
        if (iScrollCaptureConnection != null) {
            try {
                iScrollCaptureConnection.close();
            } catch (RemoteException e) {
            }
            this.mConnection = null;
        }
    }

    ScrollCaptureResponse(String description, IScrollCaptureConnection connection, Rect windowBounds, Rect boundsInWindow, String windowTitle, String packageName, ArrayList<String> messages) {
        this.mDescription = "";
        this.mConnection = null;
        this.mWindowBounds = null;
        this.mBoundsInWindow = null;
        this.mWindowTitle = null;
        this.mPackageName = null;
        this.mMessages = new ArrayList<>();
        this.mDescription = description;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) description);
        this.mConnection = connection;
        this.mWindowBounds = windowBounds;
        this.mBoundsInWindow = boundsInWindow;
        this.mWindowTitle = windowTitle;
        this.mPackageName = packageName;
        this.mMessages = messages;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) messages);
    }

    public String getDescription() {
        return this.mDescription;
    }

    public IScrollCaptureConnection getConnection() {
        return this.mConnection;
    }

    public Rect getWindowBounds() {
        return this.mWindowBounds;
    }

    public Rect getBoundsInWindow() {
        return this.mBoundsInWindow;
    }

    public String getWindowTitle() {
        return this.mWindowTitle;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public ArrayList<String> getMessages() {
        return this.mMessages;
    }

    public String toString() {
        return "ScrollCaptureResponse { description = " + this.mDescription + ", connection = " + this.mConnection + ", windowBounds = " + this.mWindowBounds + ", boundsInWindow = " + this.mBoundsInWindow + ", windowTitle = " + this.mWindowTitle + ", packageName = " + this.mPackageName + ", messages = " + this.mMessages + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mConnection != null ? (byte) (0 | 2) : (byte) 0;
        if (this.mWindowBounds != null) {
            flg = (byte) (flg | 4);
        }
        if (this.mBoundsInWindow != null) {
            flg = (byte) (flg | 8);
        }
        if (this.mWindowTitle != null) {
            flg = (byte) (flg | 16);
        }
        if (this.mPackageName != null) {
            flg = (byte) (flg | NetworkStackConstants.TCPHDR_URG);
        }
        dest.writeByte(flg);
        dest.writeString(this.mDescription);
        IScrollCaptureConnection iScrollCaptureConnection = this.mConnection;
        if (iScrollCaptureConnection != null) {
            dest.writeStrongInterface(iScrollCaptureConnection);
        }
        Rect rect = this.mWindowBounds;
        if (rect != null) {
            dest.writeTypedObject(rect, flags);
        }
        Rect rect2 = this.mBoundsInWindow;
        if (rect2 != null) {
            dest.writeTypedObject(rect2, flags);
        }
        String str = this.mWindowTitle;
        if (str != null) {
            dest.writeString(str);
        }
        String str2 = this.mPackageName;
        if (str2 != null) {
            dest.writeString(str2);
        }
        dest.writeStringList(this.mMessages);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    protected ScrollCaptureResponse(Parcel in) {
        this.mDescription = "";
        this.mConnection = null;
        this.mWindowBounds = null;
        this.mBoundsInWindow = null;
        this.mWindowTitle = null;
        this.mPackageName = null;
        this.mMessages = new ArrayList<>();
        byte flg = in.readByte();
        String description = in.readString();
        IScrollCaptureConnection connection = (flg & 2) == 0 ? null : IScrollCaptureConnection.Stub.asInterface(in.readStrongBinder());
        Rect windowBounds = (flg & 4) == 0 ? null : (Rect) in.readTypedObject(Rect.CREATOR);
        Rect boundsInWindow = (flg & 8) == 0 ? null : (Rect) in.readTypedObject(Rect.CREATOR);
        String windowTitle = (flg & 16) == 0 ? null : in.readString();
        String packageName = (flg & NetworkStackConstants.TCPHDR_URG) == 0 ? null : in.readString();
        ArrayList<String> messages = new ArrayList<>();
        in.readStringList(messages);
        this.mDescription = description;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) description);
        this.mConnection = connection;
        this.mWindowBounds = windowBounds;
        this.mBoundsInWindow = boundsInWindow;
        this.mWindowTitle = windowTitle;
        this.mPackageName = packageName;
        this.mMessages = messages;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) messages);
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private Rect mBoundsInWindow;
        private long mBuilderFieldsSet = 0;
        private IScrollCaptureConnection mConnection;
        private String mDescription;
        private ArrayList<String> mMessages;
        private String mPackageName;
        private Rect mWindowBounds;
        private String mWindowTitle;

        public Builder setDescription(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mDescription = value;
            return this;
        }

        public Builder setConnection(IScrollCaptureConnection value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mConnection = value;
            return this;
        }

        public Builder setWindowBounds(Rect value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mWindowBounds = value;
            return this;
        }

        public Builder setBoundsInWindow(Rect value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mBoundsInWindow = value;
            return this;
        }

        public Builder setWindowTitle(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mWindowTitle = value;
            return this;
        }

        public Builder setPackageName(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mPackageName = value;
            return this;
        }

        public Builder setMessages(ArrayList<String> value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 64;
            this.mMessages = value;
            return this;
        }

        public Builder addMessage(String value) {
            if (this.mMessages == null) {
                setMessages(new ArrayList<>());
            }
            this.mMessages.add(value);
            return this;
        }

        public ScrollCaptureResponse build() {
            checkNotUsed();
            long j = this.mBuilderFieldsSet | 128;
            this.mBuilderFieldsSet = j;
            if ((1 & j) == 0) {
                this.mDescription = "";
            }
            if ((2 & j) == 0) {
                this.mConnection = null;
            }
            if ((4 & j) == 0) {
                this.mWindowBounds = null;
            }
            if ((8 & j) == 0) {
                this.mBoundsInWindow = null;
            }
            if ((16 & j) == 0) {
                this.mWindowTitle = null;
            }
            if ((32 & j) == 0) {
                this.mPackageName = null;
            }
            if ((j & 64) == 0) {
                this.mMessages = new ArrayList<>();
            }
            ScrollCaptureResponse o = new ScrollCaptureResponse(this.mDescription, this.mConnection, this.mWindowBounds, this.mBoundsInWindow, this.mWindowTitle, this.mPackageName, this.mMessages);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 128) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }

    @Deprecated
    private void __metadata() {
    }
}
