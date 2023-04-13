package android.content;

import android.app.ActivityThread;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemClock;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.autofill.AutofillManager;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class AutofillOptions implements Parcelable {
    public long appDisabledExpiration;
    public boolean augmentedAutofillEnabled;
    public final boolean compatModeEnabled;
    public ArrayMap<String, Long> disabledActivities;
    public final int loggingLevel;
    public ArraySet<ComponentName> whitelistedActivitiesForAugmentedAutofill;
    private static final String TAG = AutofillOptions.class.getSimpleName();
    public static final Parcelable.Creator<AutofillOptions> CREATOR = new Parcelable.Creator<AutofillOptions>() { // from class: android.content.AutofillOptions.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutofillOptions createFromParcel(Parcel parcel) {
            int loggingLevel = parcel.readInt();
            boolean compatMode = parcel.readBoolean();
            AutofillOptions options = new AutofillOptions(loggingLevel, compatMode);
            options.augmentedAutofillEnabled = parcel.readBoolean();
            options.whitelistedActivitiesForAugmentedAutofill = parcel.readArraySet(null);
            options.appDisabledExpiration = parcel.readLong();
            int size = parcel.readInt();
            if (size > 0) {
                options.disabledActivities = new ArrayMap<>();
                for (int i = 0; i < size; i++) {
                    options.disabledActivities.put(parcel.readString(), Long.valueOf(parcel.readLong()));
                }
            }
            return options;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutofillOptions[] newArray(int size) {
            return new AutofillOptions[size];
        }
    };

    public AutofillOptions(int loggingLevel, boolean compatModeEnabled) {
        this.loggingLevel = loggingLevel;
        this.compatModeEnabled = compatModeEnabled;
    }

    public boolean isAugmentedAutofillEnabled(Context context) {
        AutofillManager.AutofillClient autofillClient;
        if (this.augmentedAutofillEnabled && (autofillClient = context.getAutofillClient()) != null) {
            ComponentName component = autofillClient.autofillClientGetComponentName();
            ArraySet<ComponentName> arraySet = this.whitelistedActivitiesForAugmentedAutofill;
            return arraySet == null || arraySet.contains(component);
        }
        return false;
    }

    public boolean isAutofillDisabledLocked(ComponentName componentName) {
        Long expiration;
        long elapsedTime = SystemClock.elapsedRealtime();
        String component = componentName.flattenToString();
        if (this.appDisabledExpiration >= elapsedTime) {
            return true;
        }
        ArrayMap<String, Long> arrayMap = this.disabledActivities;
        if (arrayMap != null && (expiration = arrayMap.get(component)) != null) {
            if (expiration.longValue() >= elapsedTime) {
                return true;
            }
            this.disabledActivities.remove(component);
        }
        this.appDisabledExpiration = 0L;
        return false;
    }

    public static AutofillOptions forWhitelistingItself() {
        ActivityThread at = ActivityThread.currentActivityThread();
        if (at == null) {
            throw new IllegalStateException("No ActivityThread");
        }
        String packageName = at.getApplication().getPackageName();
        if (!"android.autofillservice.cts".equals(packageName)) {
            Log.m110e(TAG, "forWhitelistingItself(): called by " + packageName);
            throw new SecurityException("Thou shall not pass!");
        }
        AutofillOptions options = new AutofillOptions(4, true);
        options.augmentedAutofillEnabled = true;
        Log.m108i(TAG, "forWhitelistingItself(" + packageName + "): " + options);
        return options;
    }

    public String toString() {
        return "AutofillOptions [loggingLevel=" + this.loggingLevel + ", compatMode=" + this.compatModeEnabled + ", augmentedAutofillEnabled=" + this.augmentedAutofillEnabled + ", appDisabledExpiration=" + this.appDisabledExpiration + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public void dumpShort(PrintWriter pw) {
        pw.print("logLvl=");
        pw.print(this.loggingLevel);
        pw.print(", compatMode=");
        pw.print(this.compatModeEnabled);
        pw.print(", augmented=");
        pw.print(this.augmentedAutofillEnabled);
        if (this.whitelistedActivitiesForAugmentedAutofill != null) {
            pw.print(", whitelistedActivitiesForAugmentedAutofill=");
            pw.print(this.whitelistedActivitiesForAugmentedAutofill);
        }
        pw.print(", appDisabledExpiration=");
        pw.print(this.appDisabledExpiration);
        if (this.disabledActivities != null) {
            pw.print(", disabledActivities=");
            pw.print(this.disabledActivities);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.loggingLevel);
        parcel.writeBoolean(this.compatModeEnabled);
        parcel.writeBoolean(this.augmentedAutofillEnabled);
        parcel.writeArraySet(this.whitelistedActivitiesForAugmentedAutofill);
        parcel.writeLong(this.appDisabledExpiration);
        ArrayMap<String, Long> arrayMap = this.disabledActivities;
        int size = arrayMap != null ? arrayMap.size() : 0;
        parcel.writeInt(size);
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String key = this.disabledActivities.keyAt(i);
                parcel.writeString(key);
                parcel.writeLong(this.disabledActivities.get(key).longValue());
            }
        }
    }
}
