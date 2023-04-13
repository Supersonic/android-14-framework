package android.view;

import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import java.util.Objects;
/* loaded from: classes4.dex */
public class DisplayAdjustments {
    public static final DisplayAdjustments DEFAULT_DISPLAY_ADJUSTMENTS = new DisplayAdjustments();
    private volatile CompatibilityInfo mCompatInfo;
    private final Configuration mConfiguration;

    public DisplayAdjustments() {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mConfiguration = new Configuration(Configuration.EMPTY);
    }

    public DisplayAdjustments(Configuration configuration) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        Configuration configuration2 = new Configuration(Configuration.EMPTY);
        this.mConfiguration = configuration2;
        if (configuration != null) {
            configuration2.setTo(configuration);
        }
    }

    public DisplayAdjustments(DisplayAdjustments daj) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        Configuration configuration = new Configuration(Configuration.EMPTY);
        this.mConfiguration = configuration;
        setCompatibilityInfo(daj.mCompatInfo);
        configuration.setTo(daj.getConfiguration());
    }

    public void setCompatibilityInfo(CompatibilityInfo compatInfo) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setCompatbilityInfo: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        if (compatInfo != null && (compatInfo.isScalingRequired() || !compatInfo.supportsScreen())) {
            this.mCompatInfo = compatInfo;
        } else {
            this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        }
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatInfo;
    }

    public void setConfiguration(Configuration configuration) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setConfiguration: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        this.mConfiguration.setTo(configuration != null ? configuration : Configuration.EMPTY);
    }

    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public int hashCode() {
        int hash = (17 * 31) + Objects.hashCode(this.mCompatInfo);
        return (hash * 31) + Objects.hashCode(this.mConfiguration);
    }

    public boolean equals(Object o) {
        if (o instanceof DisplayAdjustments) {
            DisplayAdjustments daj = (DisplayAdjustments) o;
            return Objects.equals(daj.mCompatInfo, this.mCompatInfo) && Objects.equals(daj.mConfiguration, this.mConfiguration);
        }
        return false;
    }
}
