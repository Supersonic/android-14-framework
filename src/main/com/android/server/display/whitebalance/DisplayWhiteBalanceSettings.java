package com.android.server.display.whitebalance;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.display.color.ColorDisplayService;
import com.android.server.display.whitebalance.DisplayWhiteBalanceController;
import java.io.PrintWriter;
import java.util.Objects;
/* loaded from: classes.dex */
public class DisplayWhiteBalanceSettings implements ColorDisplayService.DisplayWhiteBalanceListener {
    public boolean mActive;
    public DisplayWhiteBalanceController.Callbacks mCallbacks;
    public final ColorDisplayService.ColorDisplayServiceInternal mCdsi;
    public final Context mContext;
    public boolean mEnabled;
    public final Handler mHandler;
    public boolean mLoggingEnabled;

    public DisplayWhiteBalanceSettings(Context context, Handler handler) {
        validateArguments(context, handler);
        this.mLoggingEnabled = false;
        this.mContext = context;
        this.mHandler = new DisplayWhiteBalanceSettingsHandler(handler.getLooper());
        this.mCallbacks = null;
        ColorDisplayService.ColorDisplayServiceInternal colorDisplayServiceInternal = (ColorDisplayService.ColorDisplayServiceInternal) LocalServices.getService(ColorDisplayService.ColorDisplayServiceInternal.class);
        this.mCdsi = colorDisplayServiceInternal;
        setEnabled(colorDisplayServiceInternal.isDisplayWhiteBalanceEnabled());
        setActive(colorDisplayServiceInternal.setDisplayWhiteBalanceListener(this));
    }

    public boolean setCallbacks(DisplayWhiteBalanceController.Callbacks callbacks) {
        if (this.mCallbacks == callbacks) {
            return false;
        }
        this.mCallbacks = callbacks;
        return true;
    }

    public boolean setLoggingEnabled(boolean z) {
        if (this.mLoggingEnabled == z) {
            return false;
        }
        this.mLoggingEnabled = z;
        return true;
    }

    public boolean isEnabled() {
        return this.mEnabled && this.mActive;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("DisplayWhiteBalanceSettings");
        printWriter.println("  mLoggingEnabled=" + this.mLoggingEnabled);
        printWriter.println("  mContext=" + this.mContext);
        printWriter.println("  mHandler=" + this.mHandler);
        printWriter.println("  mEnabled=" + this.mEnabled);
        printWriter.println("  mActive=" + this.mActive);
        printWriter.println("  mCallbacks=" + this.mCallbacks);
    }

    @Override // com.android.server.display.color.ColorDisplayService.DisplayWhiteBalanceListener
    public void onDisplayWhiteBalanceStatusChanged(boolean z) {
        this.mHandler.obtainMessage(1, z ? 1 : 0, 0).sendToTarget();
    }

    public final void validateArguments(Context context, Handler handler) {
        Objects.requireNonNull(context, "context must not be null");
        Objects.requireNonNull(handler, "handler must not be null");
    }

    public final void setEnabled(boolean z) {
        if (this.mEnabled == z) {
            return;
        }
        if (this.mLoggingEnabled) {
            Slog.d("DisplayWhiteBalanceSettings", "Setting: " + z);
        }
        this.mEnabled = z;
        DisplayWhiteBalanceController.Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.updateWhiteBalance();
        }
    }

    public final void setActive(boolean z) {
        if (this.mActive == z) {
            return;
        }
        if (this.mLoggingEnabled) {
            Slog.d("DisplayWhiteBalanceSettings", "Active: " + z);
        }
        this.mActive = z;
        DisplayWhiteBalanceController.Callbacks callbacks = this.mCallbacks;
        if (callbacks != null) {
            callbacks.updateWhiteBalance();
        }
    }

    /* loaded from: classes.dex */
    public final class DisplayWhiteBalanceSettingsHandler extends Handler {
        public DisplayWhiteBalanceSettingsHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            DisplayWhiteBalanceSettings.this.setActive(message.arg1 != 0);
            DisplayWhiteBalanceSettings displayWhiteBalanceSettings = DisplayWhiteBalanceSettings.this;
            displayWhiteBalanceSettings.setEnabled(displayWhiteBalanceSettings.mCdsi.isDisplayWhiteBalanceEnabled());
        }
    }
}
