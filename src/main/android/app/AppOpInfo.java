package android.app;

import android.app.AppOpsManager;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppOpInfo {
    public final AppOpsManager.RestrictionBypass allowSystemRestrictionBypass;
    public final int code;
    public final int defaultMode;
    public final boolean disableReset;
    public final boolean forceCollectNotes;
    public final String name;
    public final String permission;
    public final boolean restrictRead;
    public final String restriction;
    public final String simpleName;
    public final int switchCode;

    AppOpInfo(int code, int switchCode, String name, String simpleName, String permission, String restriction, AppOpsManager.RestrictionBypass allowSystemRestrictionBypass, int defaultMode, boolean disableReset, boolean restrictRead, boolean forceCollectNotes) {
        if (code < -1) {
            throw new IllegalArgumentException();
        }
        if (switchCode < -1) {
            throw new IllegalArgumentException();
        }
        Objects.requireNonNull(name);
        Objects.requireNonNull(simpleName);
        this.code = code;
        this.switchCode = switchCode;
        this.name = name;
        this.simpleName = simpleName;
        this.permission = permission;
        this.restriction = restriction;
        this.allowSystemRestrictionBypass = allowSystemRestrictionBypass;
        this.defaultMode = defaultMode;
        this.disableReset = disableReset;
        this.restrictRead = restrictRead;
        this.forceCollectNotes = forceCollectNotes;
    }

    /* loaded from: classes.dex */
    static class Builder {
        private int mCode;
        private String mName;
        private String mSimpleName;
        private int mSwitchCode;
        private String mPermission = null;
        private String mRestriction = null;
        private AppOpsManager.RestrictionBypass mAllowSystemRestrictionBypass = null;
        private int mDefaultMode = 3;
        private boolean mDisableReset = false;
        private boolean mRestrictRead = false;
        private boolean mForceCollectNotes = false;

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder(int code, String name, String simpleName) {
            if (code < -1) {
                throw new IllegalArgumentException();
            }
            Objects.requireNonNull(name);
            Objects.requireNonNull(simpleName);
            this.mCode = code;
            this.mSwitchCode = code;
            this.mName = name;
            this.mSimpleName = simpleName;
        }

        public Builder setCode(int value) {
            this.mCode = value;
            return this;
        }

        public Builder setSwitchCode(int value) {
            this.mSwitchCode = value;
            return this;
        }

        public Builder setName(String value) {
            this.mName = value;
            return this;
        }

        public Builder setSimpleName(String value) {
            this.mSimpleName = value;
            return this;
        }

        public Builder setPermission(String value) {
            this.mPermission = value;
            return this;
        }

        public Builder setRestriction(String value) {
            this.mRestriction = value;
            return this;
        }

        public Builder setAllowSystemRestrictionBypass(AppOpsManager.RestrictionBypass value) {
            this.mAllowSystemRestrictionBypass = value;
            return this;
        }

        public Builder setDefaultMode(int value) {
            this.mDefaultMode = value;
            return this;
        }

        public Builder setDisableReset(boolean value) {
            this.mDisableReset = value;
            return this;
        }

        public Builder setRestrictRead(boolean value) {
            this.mRestrictRead = value;
            return this;
        }

        public Builder setForceCollectNotes(boolean value) {
            this.mForceCollectNotes = value;
            return this;
        }

        public AppOpInfo build() {
            return new AppOpInfo(this.mCode, this.mSwitchCode, this.mName, this.mSimpleName, this.mPermission, this.mRestriction, this.mAllowSystemRestrictionBypass, this.mDefaultMode, this.mDisableReset, this.mRestrictRead, this.mForceCollectNotes);
        }
    }
}
