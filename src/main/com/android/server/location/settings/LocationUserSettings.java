package com.android.server.location.settings;

import android.content.res.Resources;
import com.android.server.location.settings.SettingsStore;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
/* loaded from: classes.dex */
public final class LocationUserSettings implements SettingsStore.VersionedSettings {
    public final boolean mAdasGnssLocationEnabled;

    @Override // com.android.server.location.settings.SettingsStore.VersionedSettings
    public int getVersion() {
        return 1;
    }

    public LocationUserSettings(boolean z) {
        this.mAdasGnssLocationEnabled = z;
    }

    public boolean isAdasGnssLocationEnabled() {
        return this.mAdasGnssLocationEnabled;
    }

    public LocationUserSettings withAdasGnssLocationEnabled(boolean z) {
        return z == this.mAdasGnssLocationEnabled ? this : new LocationUserSettings(z);
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeBoolean(this.mAdasGnssLocationEnabled);
    }

    public static LocationUserSettings read(Resources resources, int i, DataInput dataInput) throws IOException {
        boolean readBoolean;
        if (i != 1) {
            readBoolean = resources.getBoolean(17891591);
        } else {
            readBoolean = dataInput.readBoolean();
        }
        return new LocationUserSettings(readBoolean);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof LocationUserSettings) && this.mAdasGnssLocationEnabled == ((LocationUserSettings) obj).mAdasGnssLocationEnabled;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mAdasGnssLocationEnabled));
    }
}
