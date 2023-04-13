package android.p008os;

import android.media.AudioAttributes;
import android.p008os.IBinder;
import android.p008os.IExternalVibrationController;
import android.p008os.Parcelable;
import android.p008os.VibrationAttributes;
import android.util.Slog;
import com.android.internal.util.Preconditions;
/* renamed from: android.os.ExternalVibration */
/* loaded from: classes3.dex */
public class ExternalVibration implements Parcelable {
    public static final Parcelable.Creator<ExternalVibration> CREATOR = new Parcelable.Creator<ExternalVibration>() { // from class: android.os.ExternalVibration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExternalVibration createFromParcel(Parcel in) {
            return new ExternalVibration(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ExternalVibration[] newArray(int size) {
            return new ExternalVibration[size];
        }
    };
    private static final String TAG = "ExternalVibration";
    private AudioAttributes mAttrs;
    private IExternalVibrationController mController;
    private String mPkg;
    private IBinder mToken;
    private int mUid;

    public ExternalVibration(int uid, String pkg, AudioAttributes attrs, IExternalVibrationController controller) {
        this(uid, pkg, attrs, controller, new Binder());
    }

    public ExternalVibration(int uid, String pkg, AudioAttributes attrs, IExternalVibrationController controller, IBinder token) {
        this.mUid = uid;
        this.mPkg = (String) Preconditions.checkNotNull(pkg);
        this.mAttrs = (AudioAttributes) Preconditions.checkNotNull(attrs);
        this.mController = (IExternalVibrationController) Preconditions.checkNotNull(controller);
        this.mToken = (IBinder) Preconditions.checkNotNull(token);
        Binder.allowBlocking(this.mController.asBinder());
    }

    private ExternalVibration(Parcel in) {
        this(in.readInt(), in.readString(), readAudioAttributes(in), IExternalVibrationController.Stub.asInterface(in.readStrongBinder()), in.readStrongBinder());
    }

    private static AudioAttributes readAudioAttributes(Parcel in) {
        int usage = in.readInt();
        int contentType = in.readInt();
        int capturePreset = in.readInt();
        int flags = in.readInt();
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        return builder.setUsage(usage).setContentType(contentType).setCapturePreset(capturePreset).setFlags(flags).build();
    }

    public int getUid() {
        return this.mUid;
    }

    public String getPackage() {
        return this.mPkg;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAttrs;
    }

    public IBinder getToken() {
        return this.mToken;
    }

    public VibrationAttributes getVibrationAttributes() {
        return new VibrationAttributes.Builder(this.mAttrs).build();
    }

    public boolean mute() {
        try {
            this.mController.mute();
            return true;
        } catch (RemoteException e) {
            Slog.wtf(TAG, "Failed to mute vibration stream: " + this, e);
            return false;
        }
    }

    public boolean unmute() {
        try {
            this.mController.unmute();
            return true;
        } catch (RemoteException e) {
            Slog.wtf(TAG, "Failed to unmute vibration stream: " + this, e);
            return false;
        }
    }

    public void linkToDeath(IBinder.DeathRecipient recipient) {
        try {
            this.mToken.linkToDeath(recipient, 0);
        } catch (RemoteException e) {
        }
    }

    public void unlinkToDeath(IBinder.DeathRecipient recipient) {
        this.mToken.unlinkToDeath(recipient, 0);
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ExternalVibration)) {
            return false;
        }
        ExternalVibration other = (ExternalVibration) o;
        return this.mToken.equals(other.mToken);
    }

    public String toString() {
        return "ExternalVibration{uid=" + this.mUid + ", pkg=" + this.mPkg + ", attrs=" + this.mAttrs + ", controller=" + this.mController + "token=" + this.mToken + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mUid);
        out.writeString(this.mPkg);
        writeAudioAttributes(this.mAttrs, out, flags);
        out.writeStrongBinder(this.mController.asBinder());
        out.writeStrongBinder(this.mToken);
    }

    private static void writeAudioAttributes(AudioAttributes attrs, Parcel out, int flags) {
        out.writeInt(attrs.getUsage());
        out.writeInt(attrs.getContentType());
        out.writeInt(attrs.getCapturePreset());
        out.writeInt(attrs.getAllFlags());
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
