package android.util;

import android.content.res.Configuration;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.io.PrintWriter;
/* loaded from: classes3.dex */
public class MergedConfiguration implements Parcelable {
    public static final Parcelable.Creator<MergedConfiguration> CREATOR = new Parcelable.Creator<MergedConfiguration>() { // from class: android.util.MergedConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MergedConfiguration createFromParcel(Parcel in) {
            return new MergedConfiguration(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MergedConfiguration[] newArray(int size) {
            return new MergedConfiguration[size];
        }
    };
    private final Configuration mGlobalConfig;
    private final Configuration mMergedConfig;
    private final Configuration mOverrideConfig;

    public MergedConfiguration() {
        this.mGlobalConfig = new Configuration();
        this.mOverrideConfig = new Configuration();
        this.mMergedConfig = new Configuration();
    }

    public MergedConfiguration(Configuration globalConfig, Configuration overrideConfig) {
        this.mGlobalConfig = new Configuration();
        this.mOverrideConfig = new Configuration();
        this.mMergedConfig = new Configuration();
        setConfiguration(globalConfig, overrideConfig);
    }

    public MergedConfiguration(Configuration globalConfig) {
        this.mGlobalConfig = new Configuration();
        this.mOverrideConfig = new Configuration();
        this.mMergedConfig = new Configuration();
        setGlobalConfiguration(globalConfig);
    }

    public MergedConfiguration(MergedConfiguration mergedConfiguration) {
        this.mGlobalConfig = new Configuration();
        this.mOverrideConfig = new Configuration();
        this.mMergedConfig = new Configuration();
        setConfiguration(mergedConfiguration.getGlobalConfiguration(), mergedConfiguration.getOverrideConfiguration());
    }

    private MergedConfiguration(Parcel in) {
        this.mGlobalConfig = new Configuration();
        this.mOverrideConfig = new Configuration();
        this.mMergedConfig = new Configuration();
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mGlobalConfig.writeToParcel(dest, flags);
        this.mOverrideConfig.writeToParcel(dest, flags);
        this.mMergedConfig.writeToParcel(dest, flags);
    }

    public void readFromParcel(Parcel source) {
        this.mGlobalConfig.readFromParcel(source);
        this.mOverrideConfig.readFromParcel(source);
        this.mMergedConfig.readFromParcel(source);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void setConfiguration(Configuration globalConfig, Configuration overrideConfig) {
        this.mGlobalConfig.setTo(globalConfig);
        this.mOverrideConfig.setTo(overrideConfig);
        updateMergedConfig();
    }

    public void setGlobalConfiguration(Configuration globalConfig) {
        this.mGlobalConfig.setTo(globalConfig);
        updateMergedConfig();
    }

    public void setOverrideConfiguration(Configuration overrideConfig) {
        this.mOverrideConfig.setTo(overrideConfig);
        updateMergedConfig();
    }

    public void setTo(MergedConfiguration config) {
        setConfiguration(config.mGlobalConfig, config.mOverrideConfig);
    }

    public void unset() {
        this.mGlobalConfig.unset();
        this.mOverrideConfig.unset();
        updateMergedConfig();
    }

    public Configuration getGlobalConfiguration() {
        return this.mGlobalConfig;
    }

    public Configuration getOverrideConfiguration() {
        return this.mOverrideConfig;
    }

    public Configuration getMergedConfiguration() {
        return this.mMergedConfig;
    }

    private void updateMergedConfig() {
        this.mMergedConfig.setTo(this.mGlobalConfig);
        this.mMergedConfig.updateFrom(this.mOverrideConfig);
    }

    public String toString() {
        return "{mGlobalConfig=" + this.mGlobalConfig + " mOverrideConfig=" + this.mOverrideConfig + "}";
    }

    public int hashCode() {
        return this.mMergedConfig.hashCode();
    }

    public boolean equals(Object that) {
        if (!(that instanceof MergedConfiguration)) {
            return false;
        }
        if (that == this) {
            return true;
        }
        return this.mMergedConfig.equals(((MergedConfiguration) that).mMergedConfig);
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println(prefix + "mGlobalConfig=" + this.mGlobalConfig);
        pw.println(prefix + "mOverrideConfig=" + this.mOverrideConfig);
    }
}
