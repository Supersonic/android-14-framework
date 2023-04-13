package com.android.server.grammaticalinflection;

import android.app.IGrammaticalInflectionManager;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p014wm.ActivityTaskManagerInternal;
/* loaded from: classes.dex */
public class GrammaticalInflectionService extends SystemService {
    public final String TAG;
    public final ActivityTaskManagerInternal mActivityTaskManagerInternal;
    public final GrammaticalInflectionBackupHelper mBackupHelper;
    public PackageManagerInternal mPackageManagerInternal;
    public final IBinder mService;

    public GrammaticalInflectionService(Context context) {
        super(context);
        this.TAG = "GrammaticalInflection";
        this.mService = new IGrammaticalInflectionManager.Stub() { // from class: com.android.server.grammaticalinflection.GrammaticalInflectionService.1
            public void setRequestedApplicationGrammaticalGender(String str, int i, int i2) {
                GrammaticalInflectionService.this.setRequestedApplicationGrammaticalGender(str, i, i2);
            }
        };
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mBackupHelper = new GrammaticalInflectionBackupHelper(this, context.getPackageManager());
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("grammatical_inflection", this.mService);
        LocalServices.addService(GrammaticalInflectionManagerInternal.class, new GrammaticalInflectionManagerInternalImpl());
    }

    /* loaded from: classes.dex */
    public final class GrammaticalInflectionManagerInternalImpl extends GrammaticalInflectionManagerInternal {
        public GrammaticalInflectionManagerInternalImpl() {
        }

        @Override // com.android.server.grammaticalinflection.GrammaticalInflectionManagerInternal
        public byte[] getBackupPayload(int i) {
            checkCallerIsSystem();
            return GrammaticalInflectionService.this.mBackupHelper.getBackupPayload(i);
        }

        @Override // com.android.server.grammaticalinflection.GrammaticalInflectionManagerInternal
        public void stageAndApplyRestoredPayload(byte[] bArr, int i) {
            GrammaticalInflectionService.this.mBackupHelper.stageAndApplyRestoredPayload(bArr, i);
        }

        public final void checkCallerIsSystem() {
            if (Binder.getCallingUid() != 1000) {
                throw new SecurityException("Caller is not system.");
            }
        }
    }

    public int getApplicationGrammaticalGender(String str, int i) {
        Integer num;
        ActivityTaskManagerInternal.PackageConfig applicationConfig = this.mActivityTaskManagerInternal.getApplicationConfig(str, i);
        if (applicationConfig == null || (num = applicationConfig.mGrammaticalGender) == null) {
            return 0;
        }
        return num.intValue();
    }

    public void setRequestedApplicationGrammaticalGender(String str, int i, int i2) {
        int applicationGrammaticalGender = getApplicationGrammaticalGender(str, i);
        ActivityTaskManagerInternal.PackageConfigurationUpdater createPackageConfigurationUpdater = this.mActivityTaskManagerInternal.createPackageConfigurationUpdater(str, i);
        if (SystemProperties.getBoolean("i18n.grammatical_Inflection.enabled", true)) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.GRAMMATICAL_INFLECTION_CHANGED, 2, this.mPackageManagerInternal.getPackageUid(str, 0L, i), i2 != 0, applicationGrammaticalGender != 0);
            createPackageConfigurationUpdater.setGrammaticalGender(i2).commit();
        } else if (applicationGrammaticalGender != 0) {
            Log.d("GrammaticalInflection", "Clearing the user's grammatical gender setting");
            createPackageConfigurationUpdater.setGrammaticalGender(0).commit();
        }
    }
}
