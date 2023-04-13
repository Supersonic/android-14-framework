package android.content;

import android.app.ActivityThread;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArraySet;
import android.util.Log;
import android.view.contentcapture.ContentCaptureManager;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class ContentCaptureOptions implements Parcelable {
    public final boolean disableFlushForViewTreeAppearing;
    public final int idleFlushingFrequencyMs;
    public final boolean lite;
    public final int logHistorySize;
    public final int loggingLevel;
    public final int maxBufferSize;
    public final int textChangeFlushingFrequencyMs;
    public final ArraySet<ComponentName> whitelistedComponents;
    private static final String TAG = ContentCaptureOptions.class.getSimpleName();
    public static final Parcelable.Creator<ContentCaptureOptions> CREATOR = new Parcelable.Creator<ContentCaptureOptions>() { // from class: android.content.ContentCaptureOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentCaptureOptions createFromParcel(Parcel parcel) {
            boolean lite = parcel.readBoolean();
            int loggingLevel = parcel.readInt();
            if (lite) {
                return new ContentCaptureOptions(loggingLevel);
            }
            int maxBufferSize = parcel.readInt();
            int idleFlushingFrequencyMs = parcel.readInt();
            int textChangeFlushingFrequencyMs = parcel.readInt();
            int logHistorySize = parcel.readInt();
            boolean disableFlushForViewTreeAppearing = parcel.readBoolean();
            return new ContentCaptureOptions(loggingLevel, maxBufferSize, idleFlushingFrequencyMs, textChangeFlushingFrequencyMs, logHistorySize, disableFlushForViewTreeAppearing, parcel.readArraySet(null));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentCaptureOptions[] newArray(int size) {
            return new ContentCaptureOptions[size];
        }
    };

    public ContentCaptureOptions(int loggingLevel) {
        this(true, loggingLevel, 0, 0, 0, 0, false, null);
    }

    public ContentCaptureOptions(int loggingLevel, int maxBufferSize, int idleFlushingFrequencyMs, int textChangeFlushingFrequencyMs, int logHistorySize, ArraySet<ComponentName> whitelistedComponents) {
        this(false, loggingLevel, maxBufferSize, idleFlushingFrequencyMs, textChangeFlushingFrequencyMs, logHistorySize, false, whitelistedComponents);
    }

    public ContentCaptureOptions(int loggingLevel, int maxBufferSize, int idleFlushingFrequencyMs, int textChangeFlushingFrequencyMs, int logHistorySize, boolean disableFlushForViewTreeAppearing, ArraySet<ComponentName> whitelistedComponents) {
        this(false, loggingLevel, maxBufferSize, idleFlushingFrequencyMs, textChangeFlushingFrequencyMs, logHistorySize, disableFlushForViewTreeAppearing, whitelistedComponents);
    }

    public ContentCaptureOptions(ArraySet<ComponentName> whitelistedComponents) {
        this(2, 500, 5000, 1000, 10, false, whitelistedComponents);
    }

    private ContentCaptureOptions(boolean lite, int loggingLevel, int maxBufferSize, int idleFlushingFrequencyMs, int textChangeFlushingFrequencyMs, int logHistorySize, boolean disableFlushForViewTreeAppearing, ArraySet<ComponentName> whitelistedComponents) {
        this.lite = lite;
        this.loggingLevel = loggingLevel;
        this.maxBufferSize = maxBufferSize;
        this.idleFlushingFrequencyMs = idleFlushingFrequencyMs;
        this.textChangeFlushingFrequencyMs = textChangeFlushingFrequencyMs;
        this.logHistorySize = logHistorySize;
        this.disableFlushForViewTreeAppearing = disableFlushForViewTreeAppearing;
        this.whitelistedComponents = whitelistedComponents;
    }

    public static ContentCaptureOptions forWhitelistingItself() {
        ActivityThread at = ActivityThread.currentActivityThread();
        if (at == null) {
            throw new IllegalStateException("No ActivityThread");
        }
        String packageName = at.getApplication().getPackageName();
        if (!"android.contentcaptureservice.cts".equals(packageName) && !"android.translation.cts".equals(packageName)) {
            Log.m110e(TAG, "forWhitelistingItself(): called by " + packageName);
            throw new SecurityException("Thou shall not pass!");
        }
        ContentCaptureOptions options = new ContentCaptureOptions((ArraySet<ComponentName>) null);
        Log.m108i(TAG, "forWhitelistingItself(" + packageName + "): " + options);
        return options;
    }

    public boolean isWhitelisted(Context context) {
        if (this.whitelistedComponents == null) {
            return true;
        }
        ContentCaptureManager.ContentCaptureClient client = context.getContentCaptureClient();
        if (client == null) {
            Log.m104w(TAG, "isWhitelisted(): no ContentCaptureClient on " + context);
            return false;
        }
        return this.whitelistedComponents.contains(client.contentCaptureClientGetComponentName());
    }

    public String toString() {
        if (this.lite) {
            return "ContentCaptureOptions [loggingLevel=" + this.loggingLevel + " (lite)]";
        }
        StringBuilder string = new StringBuilder("ContentCaptureOptions [");
        string.append("loggingLevel=").append(this.loggingLevel).append(", maxBufferSize=").append(this.maxBufferSize).append(", idleFlushingFrequencyMs=").append(this.idleFlushingFrequencyMs).append(", textChangeFlushingFrequencyMs=").append(this.textChangeFlushingFrequencyMs).append(", logHistorySize=").append(this.logHistorySize).append(", disableFlushForViewTreeAppearing=").append(this.disableFlushForViewTreeAppearing);
        if (this.whitelistedComponents != null) {
            string.append(", whitelisted=").append(this.whitelistedComponents);
        }
        return string.append(']').toString();
    }

    public void dumpShort(PrintWriter pw) {
        pw.print("logLvl=");
        pw.print(this.loggingLevel);
        if (this.lite) {
            pw.print(", lite");
            return;
        }
        pw.print(", bufferSize=");
        pw.print(this.maxBufferSize);
        pw.print(", idle=");
        pw.print(this.idleFlushingFrequencyMs);
        pw.print(", textIdle=");
        pw.print(this.textChangeFlushingFrequencyMs);
        pw.print(", logSize=");
        pw.print(this.logHistorySize);
        pw.print(", disableFlushForViewTreeAppearing=");
        pw.print(this.disableFlushForViewTreeAppearing);
        if (this.whitelistedComponents != null) {
            pw.print(", whitelisted=");
            pw.print(this.whitelistedComponents);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBoolean(this.lite);
        parcel.writeInt(this.loggingLevel);
        if (this.lite) {
            return;
        }
        parcel.writeInt(this.maxBufferSize);
        parcel.writeInt(this.idleFlushingFrequencyMs);
        parcel.writeInt(this.textChangeFlushingFrequencyMs);
        parcel.writeInt(this.logHistorySize);
        parcel.writeBoolean(this.disableFlushForViewTreeAppearing);
        parcel.writeArraySet(this.whitelistedComponents);
    }
}
