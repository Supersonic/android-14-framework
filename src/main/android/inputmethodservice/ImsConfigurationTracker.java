package android.inputmethodservice;

import android.content.res.Configuration;
import android.content.res.Resources;
import com.android.internal.util.Preconditions;
/* loaded from: classes2.dex */
public final class ImsConfigurationTracker {
    private static final int CONFIG_CHANGED = -1;
    private Configuration mLastKnownConfig = null;
    private int mHandledConfigChanges = 0;
    private boolean mInitialized = false;

    public void onInitialize(int handledConfigChanges) {
        Preconditions.checkState(!this.mInitialized, "onInitialize can be called only once.");
        this.mInitialized = true;
        this.mHandledConfigChanges = handledConfigChanges;
    }

    public void onBindInput(Resources resources) {
        if (this.mInitialized && this.mLastKnownConfig == null && resources != null) {
            this.mLastKnownConfig = new Configuration(resources.getConfiguration());
        }
    }

    public void setHandledConfigChanges(int configChanges) {
        this.mHandledConfigChanges = configChanges;
    }

    public void onConfigurationChanged(Configuration newConfig, Runnable resetStateForNewConfigurationRunner) {
        if (!this.mInitialized) {
            return;
        }
        Configuration configuration = this.mLastKnownConfig;
        int diff = configuration != null ? configuration.diffPublicOnly(newConfig) : -1;
        int unhandledDiff = (~this.mHandledConfigChanges) & diff;
        if (unhandledDiff != 0) {
            resetStateForNewConfigurationRunner.run();
        }
        if (diff != 0) {
            this.mLastKnownConfig = new Configuration(newConfig);
        }
    }
}
