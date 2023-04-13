package android.net;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.IBinder;
import android.p008os.ServiceManager;
import com.android.net.module.util.PermissionUtils;
@SystemApi
/* loaded from: classes2.dex */
public class NetworkStack {
    @SystemApi
    public static final String PERMISSION_MAINLINE_NETWORK_STACK = "android.permission.MAINLINE_NETWORK_STACK";
    private static volatile IBinder sMockService;

    @SystemApi
    public static IBinder getService() {
        IBinder mockService = sMockService;
        return mockService != null ? mockService : ServiceManager.getService(Context.NETWORK_STACK_SERVICE);
    }

    public static void setServiceForTest(IBinder mockService) {
        sMockService = mockService;
    }

    private NetworkStack() {
    }

    @Deprecated
    public static void checkNetworkStackPermission(Context context) {
        PermissionUtils.enforceNetworkStackPermission(context);
    }

    @Deprecated
    public static void checkNetworkStackPermissionOr(Context context, String... otherPermissions) {
        PermissionUtils.enforceNetworkStackPermissionOr(context, otherPermissions);
    }
}
