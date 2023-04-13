package com.android.server.appbinding.finders;

import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.UserHandle;
import android.service.carrier.CarrierMessagingClientService;
import android.service.carrier.ICarrierMessagingClientService;
import android.text.TextUtils;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.CollectionUtils;
import com.android.server.appbinding.AppBindingConstants;
import java.util.function.BiConsumer;
/* loaded from: classes.dex */
public class CarrierMessagingClientServiceFinder extends AppServiceFinder<CarrierMessagingClientService, ICarrierMessagingClientService> {
    public final OnRoleHoldersChangedListener mRoleHolderChangedListener;
    public final RoleManager mRoleManager;

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public String getAppDescription() {
        return "[Default SMS app]";
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public String getServiceAction() {
        return "android.telephony.action.CARRIER_MESSAGING_CLIENT_SERVICE";
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public String getServicePermission() {
        return "android.permission.BIND_CARRIER_MESSAGING_CLIENT_SERVICE";
    }

    public CarrierMessagingClientServiceFinder(Context context, BiConsumer<AppServiceFinder, Integer> biConsumer, Handler handler) {
        super(context, biConsumer, handler);
        this.mRoleHolderChangedListener = new OnRoleHoldersChangedListener() { // from class: com.android.server.appbinding.finders.CarrierMessagingClientServiceFinder$$ExternalSyntheticLambda0
            public final void onRoleHoldersChanged(String str, UserHandle userHandle) {
                CarrierMessagingClientServiceFinder.this.lambda$new$0(str, userHandle);
            }
        };
        this.mRoleManager = (RoleManager) context.getSystemService(RoleManager.class);
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public boolean isEnabled(AppBindingConstants appBindingConstants) {
        return appBindingConstants.SMS_SERVICE_ENABLED && this.mContext.getResources().getBoolean(17891864);
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public Class<CarrierMessagingClientService> getServiceClass() {
        return CarrierMessagingClientService.class;
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public ICarrierMessagingClientService asInterface(IBinder iBinder) {
        return ICarrierMessagingClientService.Stub.asInterface(iBinder);
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public String getTargetPackage(int i) {
        return (String) CollectionUtils.firstOrNull(this.mRoleManager.getRoleHoldersAsUser("android.app.role.SMS", UserHandle.of(i)));
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public void startMonitoring() {
        this.mRoleManager.addOnRoleHoldersChangedListenerAsUser(BackgroundThread.getExecutor(), this.mRoleHolderChangedListener, UserHandle.ALL);
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public String validateService(ServiceInfo serviceInfo) {
        String str = serviceInfo.packageName;
        String str2 = serviceInfo.processName;
        if (str2 == null || TextUtils.equals(str, str2)) {
            return "Service must not run on the main process";
        }
        return null;
    }

    @Override // com.android.server.appbinding.finders.AppServiceFinder
    public int getBindFlags(AppBindingConstants appBindingConstants) {
        return appBindingConstants.SMS_APP_BIND_FLAGS;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str, UserHandle userHandle) {
        if ("android.app.role.SMS".equals(str)) {
            this.mListener.accept(this, Integer.valueOf(userHandle.getIdentifier()));
        }
    }
}
