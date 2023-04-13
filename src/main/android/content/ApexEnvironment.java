package android.content;

import android.annotation.SystemApi;
import android.p008os.Environment;
import android.p008os.UserHandle;
import java.io.File;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public class ApexEnvironment {
    private static final String APEX_DATA = "apexdata";
    private final String mApexModuleName;

    public static ApexEnvironment getApexEnvironment(String apexModuleName) {
        Objects.requireNonNull(apexModuleName, "apexModuleName cannot be null");
        return new ApexEnvironment(apexModuleName);
    }

    private ApexEnvironment(String apexModuleName) {
        this.mApexModuleName = apexModuleName;
    }

    public File getDeviceProtectedDataDir() {
        return Environment.buildPath(Environment.getDataMiscDirectory(), APEX_DATA, this.mApexModuleName);
    }

    public File getDeviceProtectedDataDirForUser(UserHandle user) {
        return Environment.buildPath(Environment.getDataMiscDeDirectory(user.getIdentifier()), APEX_DATA, this.mApexModuleName);
    }

    public File getCredentialProtectedDataDirForUser(UserHandle user) {
        return Environment.buildPath(Environment.getDataMiscCeDirectory(user.getIdentifier()), APEX_DATA, this.mApexModuleName);
    }
}
