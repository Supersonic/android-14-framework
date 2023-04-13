package com.android.internal.telephony.euicc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.euicc.DownloadSubscriptionResult;
import android.service.euicc.GetDefaultDownloadableSubscriptionListResult;
import android.service.euicc.GetDownloadableSubscriptionMetadataResult;
import android.service.euicc.GetEuiccProfileInfoListResult;
import android.service.euicc.IDeleteSubscriptionCallback;
import android.service.euicc.IDownloadSubscriptionCallback;
import android.service.euicc.IEraseSubscriptionsCallback;
import android.service.euicc.IEuiccService;
import android.service.euicc.IEuiccServiceDumpResultCallback;
import android.service.euicc.IGetDefaultDownloadableSubscriptionListCallback;
import android.service.euicc.IGetDownloadableSubscriptionMetadataCallback;
import android.service.euicc.IGetEidCallback;
import android.service.euicc.IGetEuiccInfoCallback;
import android.service.euicc.IGetEuiccProfileInfoListCallback;
import android.service.euicc.IGetOtaStatusCallback;
import android.service.euicc.IOtaStatusChangedCallback;
import android.service.euicc.IRetainSubscriptionsForFactoryResetCallback;
import android.service.euicc.ISwitchToSubscriptionCallback;
import android.service.euicc.IUpdateSubscriptionNicknameCallback;
import android.telephony.AnomalyReporter;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.UiccCardInfo;
import android.telephony.UiccSlotInfo;
import android.telephony.euicc.DownloadableSubscription;
import android.telephony.euicc.EuiccInfo;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.CallFailCause;
import com.android.internal.telephony.IState;
import com.android.internal.telephony.PackageChangeReceiver;
import com.android.internal.telephony.State;
import com.android.internal.telephony.StateMachine;
import com.android.internal.telephony.euicc.EuiccConnector;
import com.android.internal.telephony.nano.TelephonyProto$TelephonyEvent;
import com.android.internal.telephony.uicc.IccUtils;
import com.android.internal.telephony.uicc.UiccController;
import com.android.internal.telephony.util.NetworkStackConstants;
import com.android.internal.telephony.util.TelephonyUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
/* loaded from: classes.dex */
public class EuiccConnector extends StateMachine implements ServiceConnection {
    @VisibleForTesting
    static final int LINGER_TIMEOUT_MILLIS = 60000;
    private Set<BaseEuiccCommandCallback> mActiveCommandCallbacks;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public AvailableState mAvailableState;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public BindingState mBindingState;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public ConnectedState mConnectedState;
    private Context mContext;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public DisconnectedState mDisconnectedState;
    private IEuiccService mEuiccService;
    private final PackageChangeReceiver mPackageMonitor;
    private PackageManager mPm;
    private ServiceInfo mSelectedComponent;
    private SubscriptionManager mSm;
    private TelephonyManager mTm;
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public UnavailableState mUnavailableState;
    private final BroadcastReceiver mUserUnlockedReceiver;

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface BaseEuiccCommandCallback {
        void onEuiccServiceUnavailable();
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface DeleteCommandCallback extends BaseEuiccCommandCallback {
        void onDeleteComplete(int i);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface DownloadCommandCallback extends BaseEuiccCommandCallback {
        void onDownloadComplete(DownloadSubscriptionResult downloadSubscriptionResult);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface DumpEuiccServiceCommandCallback extends BaseEuiccCommandCallback {
        void onDumpEuiccServiceComplete(String str);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface EraseCommandCallback extends BaseEuiccCommandCallback {
        void onEraseComplete(int i);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface GetDefaultListCommandCallback extends BaseEuiccCommandCallback {
        void onGetDefaultListComplete(int i, GetDefaultDownloadableSubscriptionListResult getDefaultDownloadableSubscriptionListResult);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface GetEidCommandCallback extends BaseEuiccCommandCallback {
        void onGetEidComplete(String str);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface GetEuiccInfoCommandCallback extends BaseEuiccCommandCallback {
        void onGetEuiccInfoComplete(EuiccInfo euiccInfo);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface GetEuiccProfileInfoListCommandCallback extends BaseEuiccCommandCallback {
        void onListComplete(GetEuiccProfileInfoListResult getEuiccProfileInfoListResult);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface GetMetadataCommandCallback extends BaseEuiccCommandCallback {
        void onGetMetadataComplete(int i, GetDownloadableSubscriptionMetadataResult getDownloadableSubscriptionMetadataResult);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface GetOtaStatusCommandCallback extends BaseEuiccCommandCallback {
        void onGetOtaStatusComplete(int i);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface OtaStatusChangedCallback extends BaseEuiccCommandCallback {
        void onOtaStatusChanged(int i);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface RetainSubscriptionsCommandCallback extends BaseEuiccCommandCallback {
        void onRetainSubscriptionsComplete(int i);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface SwitchCommandCallback extends BaseEuiccCommandCallback {
        void onSwitchComplete(int i);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /* loaded from: classes.dex */
    public interface UpdateNicknameCommandCallback extends BaseEuiccCommandCallback {
        void onUpdateNicknameComplete(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isEuiccCommand(int i) {
        return i >= 100;
    }

    public static ActivityInfo findBestActivity(PackageManager packageManager, Intent intent) {
        ActivityInfo activityInfo = (ActivityInfo) findBestComponent(packageManager, packageManager.queryIntentActivities(intent, 269484096));
        if (activityInfo == null) {
            Log.w("EuiccConnector", "No valid component found for intent: " + intent);
        }
        return activityInfo;
    }

    public static ComponentInfo findBestComponent(PackageManager packageManager) {
        ComponentInfo findBestComponent = findBestComponent(packageManager, packageManager.queryIntentServices(new Intent("android.service.euicc.EuiccService"), 269484096));
        if (findBestComponent == null) {
            Log.w("EuiccConnector", "No valid EuiccService implementation found");
        }
        return findBestComponent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class GetMetadataRequest {
        GetMetadataCommandCallback mCallback;
        boolean mForceDeactivateSim;
        int mPortIndex;
        DownloadableSubscription mSubscription;
        boolean mSwitchAfterDownload;

        GetMetadataRequest() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DownloadRequest {
        DownloadCommandCallback mCallback;
        boolean mForceDeactivateSim;
        int mPortIndex;
        Bundle mResolvedBundle;
        DownloadableSubscription mSubscription;
        boolean mSwitchAfterDownload;

        DownloadRequest() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class GetDefaultListRequest {
        GetDefaultListCommandCallback mCallback;
        boolean mForceDeactivateSim;

        GetDefaultListRequest() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DeleteRequest {
        DeleteCommandCallback mCallback;
        String mIccid;

        DeleteRequest() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SwitchRequest {
        SwitchCommandCallback mCallback;
        boolean mForceDeactivateSim;
        String mIccid;
        boolean mUsePortIndex;

        SwitchRequest() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UpdateNicknameRequest {
        UpdateNicknameCommandCallback mCallback;
        String mIccid;
        String mNickname;

        UpdateNicknameRequest() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EuiccConnector(Context context) {
        super("EuiccConnector");
        this.mPackageMonitor = new EuiccPackageMonitor();
        this.mUserUnlockedReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.euicc.EuiccConnector.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    EuiccConnector.this.sendMessage(1);
                }
            }
        };
        this.mActiveCommandCallbacks = new ArraySet();
        init(context);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public EuiccConnector(Context context, Looper looper) {
        super("EuiccConnector", looper);
        this.mPackageMonitor = new EuiccPackageMonitor();
        this.mUserUnlockedReceiver = new BroadcastReceiver() { // from class: com.android.internal.telephony.euicc.EuiccConnector.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if ("android.intent.action.USER_UNLOCKED".equals(intent.getAction())) {
                    EuiccConnector.this.sendMessage(1);
                }
            }
        };
        this.mActiveCommandCallbacks = new ArraySet();
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mTm = (TelephonyManager) context.getSystemService("phone");
        this.mSm = (SubscriptionManager) context.getSystemService("telephony_subscription_service");
        setDbg(true);
        UnavailableState unavailableState = new UnavailableState();
        this.mUnavailableState = unavailableState;
        addState(unavailableState);
        AvailableState availableState = new AvailableState();
        this.mAvailableState = availableState;
        addState(availableState, this.mUnavailableState);
        BindingState bindingState = new BindingState();
        this.mBindingState = bindingState;
        addState(bindingState);
        DisconnectedState disconnectedState = new DisconnectedState();
        this.mDisconnectedState = disconnectedState;
        addState(disconnectedState);
        ConnectedState connectedState = new ConnectedState();
        this.mConnectedState = connectedState;
        addState(connectedState, this.mDisconnectedState);
        ServiceInfo findBestComponent = findBestComponent();
        this.mSelectedComponent = findBestComponent;
        setInitialState(findBestComponent != null ? this.mAvailableState : this.mUnavailableState);
        start();
        this.mPackageMonitor.register(this.mContext, (Looper) null, (UserHandle) null);
        this.mContext.registerReceiver(this.mUserUnlockedReceiver, new IntentFilter("android.intent.action.USER_UNLOCKED"));
    }

    @Override // com.android.internal.telephony.StateMachine
    public void onHalting() {
        this.mPackageMonitor.unregister();
        this.mContext.unregisterReceiver(this.mUserUnlockedReceiver);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void getEid(int i, GetEidCommandCallback getEidCommandCallback) {
        sendMessage(100, i, 0, getEidCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void getOtaStatus(int i, GetOtaStatusCommandCallback getOtaStatusCommandCallback) {
        sendMessage(111, i, 0, getOtaStatusCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void startOtaIfNecessary(int i, OtaStatusChangedCallback otaStatusChangedCallback) {
        sendMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT, i, 0, otaStatusChangedCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void getDownloadableSubscriptionMetadata(int i, int i2, DownloadableSubscription downloadableSubscription, boolean z, boolean z2, GetMetadataCommandCallback getMetadataCommandCallback) {
        GetMetadataRequest getMetadataRequest = new GetMetadataRequest();
        getMetadataRequest.mSubscription = downloadableSubscription;
        getMetadataRequest.mForceDeactivateSim = z2;
        getMetadataRequest.mSwitchAfterDownload = z;
        getMetadataRequest.mPortIndex = i2;
        getMetadataRequest.mCallback = getMetadataCommandCallback;
        sendMessage(101, i, 0, getMetadataRequest);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void downloadSubscription(int i, int i2, DownloadableSubscription downloadableSubscription, boolean z, boolean z2, Bundle bundle, DownloadCommandCallback downloadCommandCallback) {
        DownloadRequest downloadRequest = new DownloadRequest();
        downloadRequest.mSubscription = downloadableSubscription;
        downloadRequest.mSwitchAfterDownload = z;
        downloadRequest.mForceDeactivateSim = z2;
        downloadRequest.mResolvedBundle = bundle;
        downloadRequest.mCallback = downloadCommandCallback;
        downloadRequest.mPortIndex = i2;
        sendMessage(CallFailCause.RECOVERY_ON_TIMER_EXPIRY, i, 0, downloadRequest);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getEuiccProfileInfoList(int i, GetEuiccProfileInfoListCommandCallback getEuiccProfileInfoListCommandCallback) {
        sendMessage(103, i, 0, getEuiccProfileInfoListCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void getDefaultDownloadableSubscriptionList(int i, boolean z, GetDefaultListCommandCallback getDefaultListCommandCallback) {
        GetDefaultListRequest getDefaultListRequest = new GetDefaultListRequest();
        getDefaultListRequest.mForceDeactivateSim = z;
        getDefaultListRequest.mCallback = getDefaultListCommandCallback;
        sendMessage(104, i, 0, getDefaultListRequest);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void getEuiccInfo(int i, GetEuiccInfoCommandCallback getEuiccInfoCommandCallback) {
        sendMessage(105, i, 0, getEuiccInfoCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void deleteSubscription(int i, String str, DeleteCommandCallback deleteCommandCallback) {
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.mIccid = str;
        deleteRequest.mCallback = deleteCommandCallback;
        sendMessage(106, i, 0, deleteRequest);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void switchToSubscription(int i, int i2, String str, boolean z, SwitchCommandCallback switchCommandCallback, boolean z2) {
        SwitchRequest switchRequest = new SwitchRequest();
        switchRequest.mIccid = str;
        switchRequest.mForceDeactivateSim = z;
        switchRequest.mCallback = switchCommandCallback;
        switchRequest.mUsePortIndex = z2;
        sendMessage(107, i, i2, switchRequest);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void updateSubscriptionNickname(int i, String str, String str2, UpdateNicknameCommandCallback updateNicknameCommandCallback) {
        UpdateNicknameRequest updateNicknameRequest = new UpdateNicknameRequest();
        updateNicknameRequest.mIccid = str;
        updateNicknameRequest.mNickname = str2;
        updateNicknameRequest.mCallback = updateNicknameCommandCallback;
        sendMessage(108, i, 0, updateNicknameRequest);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void eraseSubscriptions(int i, EraseCommandCallback eraseCommandCallback) {
        sendMessage(109, i, 0, eraseCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void eraseSubscriptionsWithOptions(int i, int i2, EraseCommandCallback eraseCommandCallback) {
        sendMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR, i, i2, eraseCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void retainSubscriptions(int i, RetainSubscriptionsCommandCallback retainSubscriptionsCommandCallback) {
        sendMessage(110, i, 0, retainSubscriptionsCommandCallback);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void dumpEuiccService(DumpEuiccServiceCommandCallback dumpEuiccServiceCommandCallback) {
        sendMessage(TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN, -1, 0, dumpEuiccServiceCommandCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UnavailableState extends State {
        private UnavailableState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                euiccConnector.mSelectedComponent = euiccConnector.findBestComponent();
                if (EuiccConnector.this.mSelectedComponent != null) {
                    EuiccConnector euiccConnector2 = EuiccConnector.this;
                    euiccConnector2.transitionTo(euiccConnector2.mAvailableState);
                    EuiccConnector.this.updateSubscriptionInfoListForAllAccessibleEuiccs();
                } else {
                    IState currentState = EuiccConnector.this.getCurrentState();
                    EuiccConnector euiccConnector3 = EuiccConnector.this;
                    UnavailableState unavailableState = euiccConnector3.mUnavailableState;
                    if (currentState != unavailableState) {
                        euiccConnector3.transitionTo(unavailableState);
                    }
                }
                return true;
            } else if (EuiccConnector.isEuiccCommand(i)) {
                EuiccConnector.getCallback(message).onEuiccServiceUnavailable();
                return true;
            } else {
                return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AvailableState extends State {
        private AvailableState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (EuiccConnector.isEuiccCommand(message.what)) {
                EuiccConnector.this.deferMessage(message);
                EuiccConnector euiccConnector = EuiccConnector.this;
                euiccConnector.transitionTo(euiccConnector.mBindingState);
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BindingState extends State {
        private BindingState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            if (EuiccConnector.this.createBinding()) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                euiccConnector.transitionTo(euiccConnector.mDisconnectedState);
                return;
            }
            EuiccConnector euiccConnector2 = EuiccConnector.this;
            euiccConnector2.transitionTo(euiccConnector2.mAvailableState);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            EuiccConnector.this.deferMessage(message);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DisconnectedState extends State {
        private DisconnectedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            EuiccConnector.this.sendMessageDelayed(2, 30000L);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == 4) {
                EuiccConnector.this.mEuiccService = (IEuiccService) message.obj;
                EuiccConnector euiccConnector = EuiccConnector.this;
                euiccConnector.transitionTo(euiccConnector.mConnectedState);
                return true;
            }
            boolean z = false;
            if (i != 1) {
                if (i == 2) {
                    EuiccConnector euiccConnector2 = EuiccConnector.this;
                    euiccConnector2.transitionTo(euiccConnector2.mAvailableState);
                    return true;
                } else if (EuiccConnector.isEuiccCommand(i)) {
                    EuiccConnector.this.deferMessage(message);
                    return true;
                } else {
                    return false;
                }
            }
            ServiceInfo findBestComponent = EuiccConnector.this.findBestComponent();
            String str = (String) message.obj;
            boolean z2 = findBestComponent != null ? EuiccConnector.this.mSelectedComponent == null || new ComponentName(findBestComponent.packageName, findBestComponent.name).equals(new ComponentName(EuiccConnector.this.mSelectedComponent.packageName, EuiccConnector.this.mSelectedComponent.name)) : EuiccConnector.this.mSelectedComponent != null;
            if (findBestComponent != null && Objects.equals(findBestComponent.packageName, str)) {
                z = true;
            }
            if (!z2 || z) {
                EuiccConnector.this.unbind();
                EuiccConnector.this.mSelectedComponent = findBestComponent;
                if (EuiccConnector.this.mSelectedComponent == null) {
                    EuiccConnector euiccConnector3 = EuiccConnector.this;
                    euiccConnector3.transitionTo(euiccConnector3.mUnavailableState);
                } else {
                    EuiccConnector euiccConnector4 = EuiccConnector.this;
                    euiccConnector4.transitionTo(euiccConnector4.mBindingState);
                }
                EuiccConnector.this.updateSubscriptionInfoListForAllAccessibleEuiccs();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ConnectedState extends State {
        private ConnectedState() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void enter() {
            EuiccConnector.this.removeMessages(2);
            EuiccConnector.this.sendMessageDelayed(3, 60000L);
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == 5) {
                EuiccConnector.this.mEuiccService = null;
                EuiccConnector euiccConnector = EuiccConnector.this;
                euiccConnector.transitionTo(euiccConnector.mDisconnectedState);
                return true;
            } else if (i == 3) {
                EuiccConnector.this.unbind();
                EuiccConnector euiccConnector2 = EuiccConnector.this;
                euiccConnector2.transitionTo(euiccConnector2.mAvailableState);
                return true;
            } else if (i == 6) {
                ((Runnable) message.obj).run();
                return true;
            } else if (EuiccConnector.isEuiccCommand(i)) {
                BaseEuiccCommandCallback callback = EuiccConnector.getCallback(message);
                EuiccConnector.this.onCommandStart(callback);
                int i2 = message.arg1;
                int slotIdFromCardId = EuiccConnector.this.getSlotIdFromCardId(i2);
                try {
                    switch (message.what) {
                        case 100:
                            EuiccConnector.this.mEuiccService.getEid(slotIdFromCardId, new C01841(callback));
                            break;
                        case 101:
                            GetMetadataRequest getMetadataRequest = (GetMetadataRequest) message.obj;
                            EuiccConnector.this.mEuiccService.getDownloadableSubscriptionMetadata(slotIdFromCardId, getMetadataRequest.mPortIndex, getMetadataRequest.mSubscription, getMetadataRequest.mSwitchAfterDownload, getMetadataRequest.mForceDeactivateSim, new C01912(callback, i2));
                            break;
                        case CallFailCause.RECOVERY_ON_TIMER_EXPIRY /* 102 */:
                            DownloadRequest downloadRequest = (DownloadRequest) message.obj;
                            EuiccConnector.this.mEuiccService.downloadSubscription(slotIdFromCardId, downloadRequest.mPortIndex, downloadRequest.mSubscription, downloadRequest.mSwitchAfterDownload, downloadRequest.mForceDeactivateSim, downloadRequest.mResolvedBundle, new C01923(callback));
                            break;
                        case 103:
                            EuiccConnector.this.mEuiccService.getEuiccProfileInfoList(slotIdFromCardId, new C01934(callback));
                            break;
                        case 104:
                            EuiccConnector.this.mEuiccService.getDefaultDownloadableSubscriptionList(slotIdFromCardId, ((GetDefaultListRequest) message.obj).mForceDeactivateSim, new C01945(callback, i2));
                            break;
                        case 105:
                            EuiccConnector.this.mEuiccService.getEuiccInfo(slotIdFromCardId, new C01956(callback));
                            break;
                        case 106:
                            EuiccConnector.this.mEuiccService.deleteSubscription(slotIdFromCardId, ((DeleteRequest) message.obj).mIccid, new C01967(callback));
                            break;
                        case 107:
                            SwitchRequest switchRequest = (SwitchRequest) message.obj;
                            EuiccConnector.this.mEuiccService.switchToSubscription(slotIdFromCardId, message.arg2, switchRequest.mIccid, switchRequest.mForceDeactivateSim, new C01978(callback), switchRequest.mUsePortIndex);
                            break;
                        case 108:
                            UpdateNicknameRequest updateNicknameRequest = (UpdateNicknameRequest) message.obj;
                            EuiccConnector.this.mEuiccService.updateSubscriptionNickname(slotIdFromCardId, updateNicknameRequest.mIccid, updateNicknameRequest.mNickname, new C01989(callback));
                            break;
                        case 109:
                            EuiccConnector.this.mEuiccService.eraseSubscriptions(slotIdFromCardId, new C018510(callback));
                            break;
                        case 110:
                            EuiccConnector.this.mEuiccService.retainSubscriptionsForFactoryReset(slotIdFromCardId, new C018712(callback));
                            break;
                        case 111:
                            EuiccConnector.this.mEuiccService.getOtaStatus(slotIdFromCardId, new C018813(callback));
                            break;
                        case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
                            EuiccConnector.this.mEuiccService.startOtaIfNecessary(slotIdFromCardId, new C018914(callback));
                            break;
                        case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR /* 113 */:
                            EuiccConnector.this.mEuiccService.eraseSubscriptionsWithOptions(slotIdFromCardId, message.arg2, new C018611(callback));
                            break;
                        case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                            EuiccConnector.this.mEuiccService.dump(new C019015(callback));
                            break;
                        default:
                            Log.wtf("EuiccConnector", "Unimplemented eUICC command: " + message.what);
                            callback.onEuiccServiceUnavailable();
                            EuiccConnector.this.onCommandEnd(callback);
                            return true;
                    }
                } catch (Exception e) {
                    Log.w("EuiccConnector", "Exception making binder call to EuiccService", e);
                    callback.onEuiccServiceUnavailable();
                    EuiccConnector.this.onCommandEnd(callback);
                }
                return true;
            } else {
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$1 */
        /* loaded from: classes.dex */
        public class C01841 extends IGetEidCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01841(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onSuccess(final String str) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01841.this.lambda$onSuccess$0(baseEuiccCommandCallback, str);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onSuccess$0(BaseEuiccCommandCallback baseEuiccCommandCallback, String str) {
                ((GetEidCommandCallback) baseEuiccCommandCallback).onGetEidComplete(str);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$2 */
        /* loaded from: classes.dex */
        public class C01912 extends IGetDownloadableSubscriptionMetadataCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;
            final /* synthetic */ int val$cardId;

            C01912(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                this.val$callback = baseEuiccCommandCallback;
                this.val$cardId = i;
            }

            public void onComplete(final GetDownloadableSubscriptionMetadataResult getDownloadableSubscriptionMetadataResult) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                final int i = this.val$cardId;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01912.this.lambda$onComplete$0(baseEuiccCommandCallback, i, getDownloadableSubscriptionMetadataResult);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i, GetDownloadableSubscriptionMetadataResult getDownloadableSubscriptionMetadataResult) {
                ((GetMetadataCommandCallback) baseEuiccCommandCallback).onGetMetadataComplete(i, getDownloadableSubscriptionMetadataResult);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$3 */
        /* loaded from: classes.dex */
        public class C01923 extends IDownloadSubscriptionCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01923(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final DownloadSubscriptionResult downloadSubscriptionResult) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$3$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01923.this.lambda$onComplete$0(baseEuiccCommandCallback, downloadSubscriptionResult);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, DownloadSubscriptionResult downloadSubscriptionResult) {
                ((DownloadCommandCallback) baseEuiccCommandCallback).onDownloadComplete(downloadSubscriptionResult);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$4 */
        /* loaded from: classes.dex */
        public class C01934 extends IGetEuiccProfileInfoListCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01934(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final GetEuiccProfileInfoListResult getEuiccProfileInfoListResult) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01934.this.lambda$onComplete$0(baseEuiccCommandCallback, getEuiccProfileInfoListResult);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, GetEuiccProfileInfoListResult getEuiccProfileInfoListResult) {
                ((GetEuiccProfileInfoListCommandCallback) baseEuiccCommandCallback).onListComplete(getEuiccProfileInfoListResult);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$5 */
        /* loaded from: classes.dex */
        public class C01945 extends IGetDefaultDownloadableSubscriptionListCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;
            final /* synthetic */ int val$cardId;

            C01945(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                this.val$callback = baseEuiccCommandCallback;
                this.val$cardId = i;
            }

            public void onComplete(final GetDefaultDownloadableSubscriptionListResult getDefaultDownloadableSubscriptionListResult) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                final int i = this.val$cardId;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01945.this.lambda$onComplete$0(baseEuiccCommandCallback, i, getDefaultDownloadableSubscriptionListResult);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i, GetDefaultDownloadableSubscriptionListResult getDefaultDownloadableSubscriptionListResult) {
                ((GetDefaultListCommandCallback) baseEuiccCommandCallback).onGetDefaultListComplete(i, getDefaultDownloadableSubscriptionListResult);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$6 */
        /* loaded from: classes.dex */
        public class C01956 extends IGetEuiccInfoCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01956(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onSuccess(final EuiccInfo euiccInfo) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01956.this.lambda$onSuccess$0(baseEuiccCommandCallback, euiccInfo);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onSuccess$0(BaseEuiccCommandCallback baseEuiccCommandCallback, EuiccInfo euiccInfo) {
                ((GetEuiccInfoCommandCallback) baseEuiccCommandCallback).onGetEuiccInfoComplete(euiccInfo);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$7 */
        /* loaded from: classes.dex */
        public class C01967 extends IDeleteSubscriptionCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01967(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$7$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01967.this.lambda$onComplete$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((DeleteCommandCallback) baseEuiccCommandCallback).onDeleteComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$8 */
        /* loaded from: classes.dex */
        public class C01978 extends ISwitchToSubscriptionCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01978(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$8$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01978.this.lambda$onComplete$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((SwitchCommandCallback) baseEuiccCommandCallback).onSwitchComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$9 */
        /* loaded from: classes.dex */
        public class C01989 extends IUpdateSubscriptionNicknameCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C01989(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$9$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C01989.this.lambda$onComplete$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((UpdateNicknameCommandCallback) baseEuiccCommandCallback).onUpdateNicknameComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$10 */
        /* loaded from: classes.dex */
        public class C018510 extends IEraseSubscriptionsCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C018510(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$10$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C018510.this.lambda$onComplete$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((EraseCommandCallback) baseEuiccCommandCallback).onEraseComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$11 */
        /* loaded from: classes.dex */
        public class C018611 extends IEraseSubscriptionsCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C018611(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$11$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C018611.this.lambda$onComplete$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((EraseCommandCallback) baseEuiccCommandCallback).onEraseComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$12 */
        /* loaded from: classes.dex */
        public class C018712 extends IRetainSubscriptionsForFactoryResetCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C018712(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$12$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C018712.this.lambda$onComplete$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((RetainSubscriptionsCommandCallback) baseEuiccCommandCallback).onRetainSubscriptionsComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$13 */
        /* loaded from: classes.dex */
        public class C018813 extends IGetOtaStatusCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C018813(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onSuccess(final int i) {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$13$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C018813.this.lambda$onSuccess$0(baseEuiccCommandCallback, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onSuccess$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((GetOtaStatusCommandCallback) baseEuiccCommandCallback).onGetOtaStatusComplete(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$14 */
        /* loaded from: classes.dex */
        public class C018914 extends IOtaStatusChangedCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C018914(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onOtaStatusChanged(final int i) throws RemoteException {
                if (i == 1) {
                    EuiccConnector euiccConnector = EuiccConnector.this;
                    final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                    euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$14$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            EuiccConnector.ConnectedState.C018914.lambda$onOtaStatusChanged$0(EuiccConnector.BaseEuiccCommandCallback.this, i);
                        }
                    });
                    return;
                }
                EuiccConnector euiccConnector2 = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback2 = this.val$callback;
                euiccConnector2.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$14$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C018914.this.lambda$onOtaStatusChanged$1(baseEuiccCommandCallback2, i);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public static /* synthetic */ void lambda$onOtaStatusChanged$0(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((OtaStatusChangedCallback) baseEuiccCommandCallback).onOtaStatusChanged(i);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onOtaStatusChanged$1(BaseEuiccCommandCallback baseEuiccCommandCallback, int i) {
                ((OtaStatusChangedCallback) baseEuiccCommandCallback).onOtaStatusChanged(i);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$15 */
        /* loaded from: classes.dex */
        public class C019015 extends IEuiccServiceDumpResultCallback.Stub {
            final /* synthetic */ BaseEuiccCommandCallback val$callback;

            C019015(BaseEuiccCommandCallback baseEuiccCommandCallback) {
                this.val$callback = baseEuiccCommandCallback;
            }

            public void onComplete(final String str) throws RemoteException {
                EuiccConnector euiccConnector = EuiccConnector.this;
                final BaseEuiccCommandCallback baseEuiccCommandCallback = this.val$callback;
                euiccConnector.sendMessage(6, new Runnable() { // from class: com.android.internal.telephony.euicc.EuiccConnector$ConnectedState$15$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        EuiccConnector.ConnectedState.C019015.this.lambda$onComplete$0(baseEuiccCommandCallback, str);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onComplete$0(BaseEuiccCommandCallback baseEuiccCommandCallback, String str) {
                ((DumpEuiccServiceCommandCallback) baseEuiccCommandCallback).onDumpEuiccServiceComplete(str);
                EuiccConnector.this.onCommandEnd(baseEuiccCommandCallback);
            }
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public void exit() {
            EuiccConnector.this.removeMessages(3);
            for (BaseEuiccCommandCallback baseEuiccCommandCallback : EuiccConnector.this.mActiveCommandCallbacks) {
                baseEuiccCommandCallback.onEuiccServiceUnavailable();
            }
            EuiccConnector.this.mActiveCommandCallbacks.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BaseEuiccCommandCallback getCallback(Message message) {
        switch (message.what) {
            case 100:
            case 103:
            case 105:
            case 109:
            case 110:
            case 111:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_APN_TYPE_CONFLICT /* 112 */:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INVALID_PCSCF_ADDR /* 113 */:
            case TelephonyProto$TelephonyEvent.RilSetupDataCallResponse.RilDataCallFailCause.PDP_FAIL_INTERNAL_CALL_PREEMPT_BY_HIGH_PRIO_APN /* 114 */:
                return (BaseEuiccCommandCallback) message.obj;
            case 101:
                return ((GetMetadataRequest) message.obj).mCallback;
            case CallFailCause.RECOVERY_ON_TIMER_EXPIRY /* 102 */:
                return ((DownloadRequest) message.obj).mCallback;
            case 104:
                return ((GetDefaultListRequest) message.obj).mCallback;
            case 106:
                return ((DeleteRequest) message.obj).mCallback;
            case 107:
                return ((SwitchRequest) message.obj).mCallback;
            case 108:
                return ((UpdateNicknameRequest) message.obj).mCallback;
            default:
                throw new IllegalArgumentException("Unsupported message: " + message.what);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSlotIdFromCardId(int i) {
        if (i != -1 && i != -2) {
            UiccSlotInfo[] uiccSlotsInfo = ((TelephonyManager) this.mContext.getSystemService("phone")).getUiccSlotsInfo();
            if (uiccSlotsInfo == null || uiccSlotsInfo.length == 0) {
                Log.e("EuiccConnector", "UiccSlotInfo is null or empty");
            } else {
                String convertToCardString = UiccController.getInstance().convertToCardString(i);
                for (int i2 = 0; i2 < uiccSlotsInfo.length; i2++) {
                    if (uiccSlotsInfo[i2] == null) {
                        AnomalyReporter.reportAnomaly(UUID.fromString("4195b83d-6cee-4999-a02f-d0b9f7079b9d"), "EuiccConnector: Found UiccSlotInfo Null object.");
                    }
                    UiccSlotInfo uiccSlotInfo = uiccSlotsInfo[i2];
                    if (IccUtils.compareIgnoreTrailingFs(convertToCardString, uiccSlotInfo != null ? uiccSlotInfo.getCardId() : null)) {
                        return i2;
                    }
                }
                Log.i("EuiccConnector", "No UiccSlotInfo found for cardId: " + i);
                return -1;
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCommandStart(BaseEuiccCommandCallback baseEuiccCommandCallback) {
        this.mActiveCommandCallbacks.add(baseEuiccCommandCallback);
        removeMessages(3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCommandEnd(BaseEuiccCommandCallback baseEuiccCommandCallback) {
        if (!this.mActiveCommandCallbacks.remove(baseEuiccCommandCallback)) {
            Log.wtf("EuiccConnector", "Callback already removed from mActiveCommandCallbacks");
        }
        if (this.mActiveCommandCallbacks.isEmpty()) {
            sendMessageDelayed(3, 60000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ServiceInfo findBestComponent() {
        return (ServiceInfo) findBestComponent(this.mPm);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean createBinding() {
        if (this.mSelectedComponent == null) {
            Log.wtf("EuiccConnector", "Attempting to create binding but no component is selected");
            return false;
        }
        Intent intent = new Intent("android.service.euicc.EuiccService");
        ServiceInfo serviceInfo = this.mSelectedComponent;
        intent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
        return this.mContext.bindService(intent, this, 67108865);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbind() {
        this.mEuiccService = null;
        this.mContext.unbindService(this);
    }

    private static ComponentInfo findBestComponent(PackageManager packageManager, List<ResolveInfo> list) {
        ComponentInfo componentInfo = null;
        if (list != null) {
            int i = NetworkStackConstants.NEIGHBOR_ADVERTISEMENT_FLAG_ROUTER;
            for (ResolveInfo resolveInfo : list) {
                if (isValidEuiccComponent(packageManager, resolveInfo) && resolveInfo.filter.getPriority() > i) {
                    i = resolveInfo.filter.getPriority();
                    componentInfo = TelephonyUtils.getComponentInfo(resolveInfo);
                }
            }
        }
        return componentInfo;
    }

    private static boolean isValidEuiccComponent(PackageManager packageManager, ResolveInfo resolveInfo) {
        String str;
        ComponentInfo componentInfo = TelephonyUtils.getComponentInfo(resolveInfo);
        String packageName = new ComponentName(componentInfo.packageName, componentInfo.name).getPackageName();
        if (packageManager.checkPermission("android.permission.WRITE_EMBEDDED_SUBSCRIPTIONS", packageName) != 0) {
            Log.wtf("EuiccConnector", "Package " + packageName + " does not declare WRITE_EMBEDDED_SUBSCRIPTIONS");
            return false;
        }
        if (componentInfo instanceof ServiceInfo) {
            str = ((ServiceInfo) componentInfo).permission;
        } else if (componentInfo instanceof ActivityInfo) {
            str = ((ActivityInfo) componentInfo).permission;
        } else {
            throw new IllegalArgumentException("Can only verify services/activities");
        }
        if (!TextUtils.equals(str, "android.permission.BIND_EUICC_SERVICE")) {
            Log.wtf("EuiccConnector", "Package " + packageName + " does not require the BIND_EUICC_SERVICE permission");
            return false;
        }
        IntentFilter intentFilter = resolveInfo.filter;
        if (intentFilter == null || intentFilter.getPriority() == 0) {
            Log.wtf("EuiccConnector", "Package " + packageName + " does not specify a priority");
            return false;
        }
        return true;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        sendMessage(4, IEuiccService.Stub.asInterface(iBinder));
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        sendMessage(5);
    }

    /* loaded from: classes.dex */
    private class EuiccPackageMonitor extends PackageChangeReceiver {
        private EuiccPackageMonitor() {
        }

        public void onPackageAdded(String str) {
            sendPackageChange(str, true);
        }

        public void onPackageRemoved(String str) {
            sendPackageChange(str, true);
        }

        public void onPackageUpdateFinished(String str) {
            sendPackageChange(str, true);
        }

        public void onPackageModified(String str) {
            sendPackageChange(str, false);
        }

        public void onHandleForceStop(String[] strArr, boolean z) {
            if (z) {
                for (String str : strArr) {
                    sendPackageChange(str, true);
                }
            }
        }

        private void sendPackageChange(String str, boolean z) {
            EuiccConnector euiccConnector = EuiccConnector.this;
            if (!z) {
                str = null;
            }
            euiccConnector.sendMessage(1, str);
        }
    }

    @Override // com.android.internal.telephony.StateMachine
    protected void unhandledMessage(Message message) {
        IState currentState = getCurrentState();
        StringBuilder sb = new StringBuilder();
        sb.append("Unhandled message ");
        sb.append(message.what);
        sb.append(" in state ");
        sb.append(currentState == null ? "null" : currentState.getName());
        Log.wtf("EuiccConnector", sb.toString());
        UUID fromString = UUID.fromString("0db20514-5fa1-4e62-a7b7-2acf5f92c957");
        AnomalyReporter.reportAnomaly(fromString, "EuiccConnector: Found unhandledMessage " + String.valueOf(message.what));
    }

    @Override // com.android.internal.telephony.StateMachine
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(fileDescriptor, printWriter, strArr);
        printWriter.println("mSelectedComponent=" + this.mSelectedComponent);
        printWriter.println("mEuiccService=" + this.mEuiccService);
        printWriter.println("mActiveCommandCount=" + this.mActiveCommandCallbacks.size());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSubscriptionInfoListForAllAccessibleEuiccs() {
        if (this.mTm.getCardIdForDefaultEuicc() == -1) {
            this.mSm.requestEmbeddedSubscriptionInfoListRefresh();
            return;
        }
        for (UiccCardInfo uiccCardInfo : this.mTm.getUiccCardsInfo()) {
            if (uiccCardInfo.isEuicc()) {
                this.mSm.requestEmbeddedSubscriptionInfoListRefresh(uiccCardInfo.getCardId());
            }
        }
    }
}
