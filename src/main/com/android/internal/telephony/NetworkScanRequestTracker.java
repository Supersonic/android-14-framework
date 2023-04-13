package com.android.internal.telephony;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.telephony.CellInfo;
import android.telephony.LocationAccessPolicy;
import android.telephony.NetworkScanRequest;
import android.telephony.RadioAccessSpecifier;
import android.telephony.SubscriptionInfo;
import android.util.Log;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.subscription.SubscriptionManagerService;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public final class NetworkScanRequestTracker {
    private final Handler mHandler = new Handler() { // from class: com.android.internal.telephony.NetworkScanRequestTracker.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            Log.d("ScanRequestTracker", "Received Event :" + message.what);
            switch (message.what) {
                case 1:
                    NetworkScanRequestTracker.this.mScheduler.doStartScan((NetworkScanRequestInfo) message.obj);
                    return;
                case 2:
                    NetworkScanRequestTracker.this.mScheduler.startScanDone((AsyncResult) message.obj);
                    return;
                case 3:
                    NetworkScanRequestTracker.this.mScheduler.receiveResult((AsyncResult) message.obj);
                    return;
                case 4:
                    NetworkScanRequestTracker.this.mScheduler.doStopScan(message.arg1);
                    return;
                case 5:
                    NetworkScanRequestTracker.this.mScheduler.stopScanDone((AsyncResult) message.obj);
                    return;
                case 6:
                    NetworkScanRequestTracker.this.mScheduler.doInterruptScan(message.arg1);
                    return;
                case 7:
                    NetworkScanRequestTracker.this.mScheduler.interruptScanDone((AsyncResult) message.obj);
                    return;
                case 8:
                case 9:
                    NetworkScanRequestTracker.this.mScheduler.deleteScanAndMayNotify((NetworkScanRequestInfo) ((AsyncResult) message.obj).userObj, 1, true);
                    return;
                default:
                    return;
            }
        }
    };
    private final AtomicInteger mNextNetworkScanRequestId = new AtomicInteger(1);
    private final NetworkScanRequestScheduler mScheduler = new NetworkScanRequestScheduler();

    /* JADX INFO: Access modifiers changed from: private */
    public void logEmptyResultOrException(AsyncResult asyncResult) {
        if (asyncResult.result == null) {
            Log.e("ScanRequestTracker", "NetworkScanResult: Empty result");
            return;
        }
        Log.e("ScanRequestTracker", "NetworkScanResult: Exception: " + asyncResult.exception);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValidScan(NetworkScanRequestInfo networkScanRequestInfo) {
        RadioAccessSpecifier[] specifiers;
        if (networkScanRequestInfo.mRequest == null || networkScanRequestInfo.mRequest.getSpecifiers() == null || networkScanRequestInfo.mRequest.getSpecifiers().length > 8) {
            return false;
        }
        for (RadioAccessSpecifier radioAccessSpecifier : networkScanRequestInfo.mRequest.getSpecifiers()) {
            if (radioAccessSpecifier.getRadioAccessNetwork() != 1 && radioAccessSpecifier.getRadioAccessNetwork() != 2 && radioAccessSpecifier.getRadioAccessNetwork() != 3 && radioAccessSpecifier.getRadioAccessNetwork() != 6) {
                return false;
            }
            if (radioAccessSpecifier.getBands() != null && radioAccessSpecifier.getBands().length > 8) {
                return false;
            }
            if (radioAccessSpecifier.getChannels() != null && radioAccessSpecifier.getChannels().length > 32) {
                return false;
            }
        }
        if (networkScanRequestInfo.mRequest.getSearchPeriodicity() >= 5 && networkScanRequestInfo.mRequest.getSearchPeriodicity() <= 300 && networkScanRequestInfo.mRequest.getMaxSearchTime() >= 60 && networkScanRequestInfo.mRequest.getMaxSearchTime() <= 3600 && networkScanRequestInfo.mRequest.getIncrementalResultsPeriodicity() >= 1 && networkScanRequestInfo.mRequest.getIncrementalResultsPeriodicity() <= 10 && networkScanRequestInfo.mRequest.getSearchPeriodicity() <= networkScanRequestInfo.mRequest.getMaxSearchTime() && networkScanRequestInfo.mRequest.getIncrementalResultsPeriodicity() <= networkScanRequestInfo.mRequest.getMaxSearchTime()) {
            return networkScanRequestInfo.mRequest.getPlmns() == null || networkScanRequestInfo.mRequest.getPlmns().size() <= 20;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean doesCellInfoCorrespondToKnownMccMnc(CellInfo cellInfo, Collection<String> collection) {
        return collection.contains(cellInfo.getCellIdentity().getMccString() + cellInfo.getCellIdentity().getMncString());
    }

    public static Set<String> getAllowedMccMncsForLocationRestrictedScan(Context context) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (PhoneFactory.isSubscriptionManagerServiceEnabled()) {
                return (Set) SubscriptionManagerService.getInstance().getAvailableSubscriptionInfoList(context.getOpPackageName(), context.getAttributionTag()).stream().flatMap(new Function() { // from class: com.android.internal.telephony.NetworkScanRequestTracker$$ExternalSyntheticLambda0
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        Stream allowableMccMncsFromSubscriptionInfo;
                        allowableMccMncsFromSubscriptionInfo = NetworkScanRequestTracker.getAllowableMccMncsFromSubscriptionInfo((SubscriptionInfo) obj);
                        return allowableMccMncsFromSubscriptionInfo;
                    }
                }).collect(Collectors.toSet());
            }
            return (Set) SubscriptionController.getInstance().getAvailableSubscriptionInfoList(context.getOpPackageName(), context.getAttributionTag()).stream().flatMap(new Function() { // from class: com.android.internal.telephony.NetworkScanRequestTracker$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Stream allowableMccMncsFromSubscriptionInfo;
                    allowableMccMncsFromSubscriptionInfo = NetworkScanRequestTracker.getAllowableMccMncsFromSubscriptionInfo((SubscriptionInfo) obj);
                    return allowableMccMncsFromSubscriptionInfo;
                }
            }).collect(Collectors.toSet());
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Stream<String> getAllowableMccMncsFromSubscriptionInfo(SubscriptionInfo subscriptionInfo) {
        Stream<String> flatMap = Stream.of((Object[]) new List[]{subscriptionInfo.getEhplmns(), subscriptionInfo.getHplmns()}).flatMap(new Function() { // from class: com.android.internal.telephony.NetworkScanRequestTracker$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((List) obj).stream();
            }
        });
        if (subscriptionInfo.getMccString() == null || subscriptionInfo.getMncString() == null) {
            return flatMap;
        }
        return Stream.concat(flatMap, Stream.of(subscriptionInfo.getMccString() + subscriptionInfo.getMncString()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyMessenger(NetworkScanRequestInfo networkScanRequestInfo, int i, int i2, List<CellInfo> list) {
        Messenger messenger = networkScanRequestInfo.mMessenger;
        Message obtain = Message.obtain();
        obtain.what = i;
        obtain.arg1 = i2;
        obtain.arg2 = networkScanRequestInfo.mScanId;
        if (list != null) {
            if (i == 4) {
                final Set<String> allowedMccMncsForLocationRestrictedScan = getAllowedMccMncsForLocationRestrictedScan(networkScanRequestInfo.mPhone.getContext());
                list = (List) list.stream().map(new Function() { // from class: com.android.internal.telephony.NetworkScanRequestTracker$$ExternalSyntheticLambda2
                    @Override // java.util.function.Function
                    public final Object apply(Object obj) {
                        return ((CellInfo) obj).sanitizeLocationInfo();
                    }
                }).filter(new Predicate() { // from class: com.android.internal.telephony.NetworkScanRequestTracker$$ExternalSyntheticLambda3
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean doesCellInfoCorrespondToKnownMccMnc;
                        doesCellInfoCorrespondToKnownMccMnc = NetworkScanRequestTracker.doesCellInfoCorrespondToKnownMccMnc((CellInfo) obj, allowedMccMncsForLocationRestrictedScan);
                        return doesCellInfoCorrespondToKnownMccMnc;
                    }
                }).collect(Collectors.toList());
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArray("scanResult", (CellInfo[]) list.toArray(new CellInfo[list.size()]));
            obtain.setData(bundle);
        } else {
            obtain.obj = null;
        }
        try {
            messenger.send(obtain);
        } catch (RemoteException e) {
            Log.e("ScanRequestTracker", "Exception in notifyMessenger: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class NetworkScanRequestInfo implements IBinder.DeathRecipient {
        private final IBinder mBinder;
        private final String mCallingPackage;
        private boolean mIsBinderDead = false;
        private final Messenger mMessenger;
        private final Phone mPhone;
        private final int mPid;
        private boolean mRenounceFineLocationAccess;
        private final NetworkScanRequest mRequest;
        private final int mScanId;
        private final int mUid;

        NetworkScanRequestInfo(NetworkScanRequest networkScanRequest, Messenger messenger, IBinder iBinder, int i, Phone phone, int i2, int i3, String str, boolean z) {
            this.mRequest = networkScanRequest;
            this.mMessenger = messenger;
            this.mBinder = iBinder;
            this.mScanId = i;
            this.mPhone = phone;
            this.mUid = i2;
            this.mPid = i3;
            this.mCallingPackage = str;
            this.mRenounceFineLocationAccess = z;
            try {
                iBinder.linkToDeath(this, 0);
            } catch (RemoteException unused) {
                binderDied();
            }
        }

        synchronized void setIsBinderDead(boolean z) {
            this.mIsBinderDead = z;
        }

        synchronized boolean getIsBinderDead() {
            return this.mIsBinderDead;
        }

        NetworkScanRequest getRequest() {
            return this.mRequest;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.e("ScanRequestTracker", "PhoneInterfaceManager NetworkScanRequestInfo binderDied(" + this.mRequest + ", " + this.mBinder + ")");
            setIsBinderDead(true);
            NetworkScanRequestTracker.this.interruptNetworkScan(this.mScanId);
        }
    }

    /* loaded from: classes.dex */
    private class NetworkScanRequestScheduler {
        private NetworkScanRequestInfo mLiveRequestInfo;
        private NetworkScanRequestInfo mPendingRequestInfo;

        private boolean cacheScan(NetworkScanRequestInfo networkScanRequestInfo) {
            return false;
        }

        private NetworkScanRequestScheduler() {
        }

        private int rilErrorToScanError(int i) {
            if (i != 0) {
                if (i == 1) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: RADIO_NOT_AVAILABLE");
                    return 1;
                } else if (i == 6) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: REQUEST_NOT_SUPPORTED");
                    return 4;
                } else if (i == 40) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: MODEM_ERR");
                    return 1;
                } else if (i == 44) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: INVALID_ARGUMENTS");
                    return 2;
                } else if (i == 54) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: OPERATION_NOT_ALLOWED");
                    return 1;
                } else if (i == 64) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: DEVICE_IN_USE");
                    return 3;
                } else if (i == 37) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: NO_MEMORY");
                    return 1;
                } else if (i == 38) {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: INTERNAL_ERR");
                    return 1;
                } else {
                    Log.e("ScanRequestTracker", "rilErrorToScanError: Unexpected RadioError " + i);
                    return 10000;
                }
            }
            return 0;
        }

        private int commandExceptionErrorToScanError(CommandException.Error error) {
            switch (C00512.$SwitchMap$com$android$internal$telephony$CommandException$Error[error.ordinal()]) {
                case 1:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: RADIO_NOT_AVAILABLE");
                    return 1;
                case 2:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: REQUEST_NOT_SUPPORTED");
                    return 4;
                case 3:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: NO_MEMORY");
                    return 1;
                case 4:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: INTERNAL_ERR");
                    return 1;
                case 5:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: MODEM_ERR");
                    return 1;
                case 6:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: OPERATION_NOT_ALLOWED");
                    return 1;
                case 7:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: INVALID_ARGUMENTS");
                    return 2;
                case 8:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: DEVICE_IN_USE");
                    return 3;
                default:
                    Log.e("ScanRequestTracker", "commandExceptionErrorToScanError: Unexpected CommandExceptionError " + error);
                    return 10000;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void doStartScan(NetworkScanRequestInfo networkScanRequestInfo) {
            if (networkScanRequestInfo == null) {
                Log.e("ScanRequestTracker", "CMD_START_NETWORK_SCAN: nsri is null");
            } else if (!NetworkScanRequestTracker.this.isValidScan(networkScanRequestInfo)) {
                NetworkScanRequestTracker.this.notifyMessenger(networkScanRequestInfo, 2, 2, null);
            } else if (networkScanRequestInfo.getIsBinderDead()) {
                Log.e("ScanRequestTracker", "CMD_START_NETWORK_SCAN: Binder has died");
            } else if (startNewScan(networkScanRequestInfo) || interruptLiveScan(networkScanRequestInfo) || cacheScan(networkScanRequestInfo)) {
            } else {
                NetworkScanRequestTracker.this.notifyMessenger(networkScanRequestInfo, 2, 3, null);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void startScanDone(AsyncResult asyncResult) {
            NetworkScanRequestInfo networkScanRequestInfo = (NetworkScanRequestInfo) asyncResult.userObj;
            if (networkScanRequestInfo == null) {
                Log.e("ScanRequestTracker", "EVENT_START_NETWORK_SCAN_DONE: nsri is null");
                return;
            }
            if (this.mLiveRequestInfo != null && networkScanRequestInfo.mScanId == this.mLiveRequestInfo.mScanId) {
                if (asyncResult.exception == null && asyncResult.result != null) {
                    networkScanRequestInfo.mPhone.mCi.registerForNetworkScanResult(NetworkScanRequestTracker.this.mHandler, 3, networkScanRequestInfo);
                } else {
                    NetworkScanRequestTracker.this.logEmptyResultOrException(asyncResult);
                    Throwable th = asyncResult.exception;
                    if (th != null) {
                        deleteScanAndMayNotify(networkScanRequestInfo, commandExceptionErrorToScanError(((CommandException) th).getCommandError()), true);
                    } else {
                        Log.wtf("ScanRequestTracker", "EVENT_START_NETWORK_SCAN_DONE: ar.exception can not be null!");
                    }
                }
                return;
            }
            Log.e("ScanRequestTracker", "EVENT_START_NETWORK_SCAN_DONE: nsri does not match mLiveRequestInfo");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void receiveResult(AsyncResult asyncResult) {
            Object obj;
            NetworkScanRequestInfo networkScanRequestInfo = (NetworkScanRequestInfo) asyncResult.userObj;
            if (networkScanRequestInfo == null) {
                Log.e("ScanRequestTracker", "EVENT_RECEIVE_NETWORK_SCAN_RESULT: nsri is null");
                return;
            }
            LocationAccessPolicy.LocationPermissionQuery build = new LocationAccessPolicy.LocationPermissionQuery.Builder().setCallingPackage(networkScanRequestInfo.mCallingPackage).setCallingPid(networkScanRequestInfo.mPid).setCallingUid(networkScanRequestInfo.mUid).setCallingFeatureId(networkScanRequestInfo.mPhone.getContext().getAttributionTag()).setMinSdkVersionForFine(29).setMinSdkVersionForCoarse(29).setMinSdkVersionForEnforcement(29).setMethod("NetworkScanTracker#onResult").build();
            if (asyncResult.exception == null && (obj = asyncResult.result) != null) {
                NetworkScanResult networkScanResult = (NetworkScanResult) obj;
                int i = !networkScanRequestInfo.mRenounceFineLocationAccess && LocationAccessPolicy.checkLocationPermission(networkScanRequestInfo.mPhone.getContext(), build) == LocationAccessPolicy.LocationPermissionResult.ALLOWED ? 1 : 4;
                int i2 = networkScanResult.scanError;
                if (i2 == 0) {
                    if (networkScanRequestInfo.mPhone.getServiceStateTracker() != null) {
                        networkScanRequestInfo.mPhone.getServiceStateTracker().updateOperatorNameForCellInfo(networkScanResult.networkInfos);
                    }
                    NetworkScanRequestTracker.this.notifyMessenger(networkScanRequestInfo, i, rilErrorToScanError(networkScanResult.scanError), networkScanResult.networkInfos);
                    if (networkScanResult.scanStatus == 2) {
                        deleteScanAndMayNotify(networkScanRequestInfo, 0, true);
                        networkScanRequestInfo.mPhone.mCi.unregisterForNetworkScanResult(NetworkScanRequestTracker.this.mHandler);
                        return;
                    }
                    return;
                }
                if (networkScanResult.networkInfos != null) {
                    NetworkScanRequestTracker.this.notifyMessenger(networkScanRequestInfo, i, rilErrorToScanError(i2), networkScanResult.networkInfos);
                }
                deleteScanAndMayNotify(networkScanRequestInfo, rilErrorToScanError(networkScanResult.scanError), true);
                networkScanRequestInfo.mPhone.mCi.unregisterForNetworkScanResult(NetworkScanRequestTracker.this.mHandler);
                return;
            }
            NetworkScanRequestTracker.this.logEmptyResultOrException(asyncResult);
            deleteScanAndMayNotify(networkScanRequestInfo, 10000, true);
            networkScanRequestInfo.mPhone.mCi.unregisterForNetworkScanResult(NetworkScanRequestTracker.this.mHandler);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void doStopScan(int i) {
            NetworkScanRequestInfo networkScanRequestInfo = this.mLiveRequestInfo;
            if (networkScanRequestInfo != null && i == networkScanRequestInfo.mScanId) {
                this.mLiveRequestInfo.mPhone.stopNetworkScan(NetworkScanRequestTracker.this.mHandler.obtainMessage(5, this.mLiveRequestInfo));
            } else {
                NetworkScanRequestInfo networkScanRequestInfo2 = this.mPendingRequestInfo;
                if (networkScanRequestInfo2 != null && i == networkScanRequestInfo2.mScanId) {
                    NetworkScanRequestTracker.this.notifyMessenger(this.mPendingRequestInfo, 3, 0, null);
                    this.mPendingRequestInfo = null;
                } else {
                    Log.e("ScanRequestTracker", "stopScan: scan " + i + " does not exist!");
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void stopScanDone(AsyncResult asyncResult) {
            NetworkScanRequestInfo networkScanRequestInfo = (NetworkScanRequestInfo) asyncResult.userObj;
            if (networkScanRequestInfo == null) {
                Log.e("ScanRequestTracker", "EVENT_STOP_NETWORK_SCAN_DONE: nsri is null");
                return;
            }
            if (asyncResult.exception == null && asyncResult.result != null) {
                deleteScanAndMayNotify(networkScanRequestInfo, 0, true);
            } else {
                NetworkScanRequestTracker.this.logEmptyResultOrException(asyncResult);
                Throwable th = asyncResult.exception;
                if (th != null) {
                    deleteScanAndMayNotify(networkScanRequestInfo, commandExceptionErrorToScanError(((CommandException) th).getCommandError()), true);
                } else {
                    Log.wtf("ScanRequestTracker", "EVENT_STOP_NETWORK_SCAN_DONE: ar.exception can not be null!");
                }
            }
            networkScanRequestInfo.mPhone.mCi.unregisterForNetworkScanResult(NetworkScanRequestTracker.this.mHandler);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void doInterruptScan(int i) {
            NetworkScanRequestInfo networkScanRequestInfo = this.mLiveRequestInfo;
            if (networkScanRequestInfo != null && i == networkScanRequestInfo.mScanId) {
                this.mLiveRequestInfo.mPhone.stopNetworkScan(NetworkScanRequestTracker.this.mHandler.obtainMessage(7, this.mLiveRequestInfo));
            } else {
                Log.e("ScanRequestTracker", "doInterruptScan: scan " + i + " does not exist!");
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void interruptScanDone(AsyncResult asyncResult) {
            NetworkScanRequestInfo networkScanRequestInfo = (NetworkScanRequestInfo) asyncResult.userObj;
            if (networkScanRequestInfo == null) {
                Log.e("ScanRequestTracker", "EVENT_INTERRUPT_NETWORK_SCAN_DONE: nsri is null");
                return;
            }
            networkScanRequestInfo.mPhone.mCi.unregisterForNetworkScanResult(NetworkScanRequestTracker.this.mHandler);
            deleteScanAndMayNotify(networkScanRequestInfo, 0, false);
        }

        private synchronized boolean interruptLiveScan(NetworkScanRequestInfo networkScanRequestInfo) {
            if (this.mLiveRequestInfo == null || this.mPendingRequestInfo != null || networkScanRequestInfo.mUid != 1000 || this.mLiveRequestInfo.mUid == 1000) {
                return false;
            }
            doInterruptScan(this.mLiveRequestInfo.mScanId);
            this.mPendingRequestInfo = networkScanRequestInfo;
            NetworkScanRequestTracker.this.notifyMessenger(this.mLiveRequestInfo, 2, 10002, null);
            return true;
        }

        private synchronized boolean startNewScan(NetworkScanRequestInfo networkScanRequestInfo) {
            if (this.mLiveRequestInfo == null) {
                this.mLiveRequestInfo = networkScanRequestInfo;
                networkScanRequestInfo.mPhone.startNetworkScan(networkScanRequestInfo.getRequest(), NetworkScanRequestTracker.this.mHandler.obtainMessage(2, networkScanRequestInfo));
                networkScanRequestInfo.mPhone.mCi.registerForModemReset(NetworkScanRequestTracker.this.mHandler, 8, networkScanRequestInfo);
                networkScanRequestInfo.mPhone.mCi.registerForNotAvailable(NetworkScanRequestTracker.this.mHandler, 9, networkScanRequestInfo);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void deleteScanAndMayNotify(NetworkScanRequestInfo networkScanRequestInfo, int i, boolean z) {
            if (this.mLiveRequestInfo != null && networkScanRequestInfo.mScanId == this.mLiveRequestInfo.mScanId) {
                if (z) {
                    if (i == 0) {
                        NetworkScanRequestTracker.this.notifyMessenger(networkScanRequestInfo, 3, i, null);
                    } else {
                        NetworkScanRequestTracker.this.notifyMessenger(networkScanRequestInfo, 2, i, null);
                    }
                }
                this.mLiveRequestInfo.mPhone.mCi.unregisterForModemReset(NetworkScanRequestTracker.this.mHandler);
                this.mLiveRequestInfo.mPhone.mCi.unregisterForNotAvailable(NetworkScanRequestTracker.this.mHandler);
                this.mLiveRequestInfo = null;
                NetworkScanRequestInfo networkScanRequestInfo2 = this.mPendingRequestInfo;
                if (networkScanRequestInfo2 != null) {
                    startNewScan(networkScanRequestInfo2);
                    this.mPendingRequestInfo = null;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.internal.telephony.NetworkScanRequestTracker$2 */
    /* loaded from: classes.dex */
    public static /* synthetic */ class C00512 {
        static final /* synthetic */ int[] $SwitchMap$com$android$internal$telephony$CommandException$Error;

        static {
            int[] iArr = new int[CommandException.Error.values().length];
            $SwitchMap$com$android$internal$telephony$CommandException$Error = iArr;
            try {
                iArr[CommandException.Error.RADIO_NOT_AVAILABLE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.REQUEST_NOT_SUPPORTED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.NO_MEMORY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INTERNAL_ERR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.MODEM_ERR.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.OPERATION_NOT_ALLOWED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.INVALID_ARGUMENTS.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$internal$telephony$CommandException$Error[CommandException.Error.DEVICE_IN_USE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void interruptNetworkScan(int i) {
        this.mHandler.obtainMessage(6, i, 0).sendToTarget();
    }

    public int startNetworkScan(boolean z, NetworkScanRequest networkScanRequest, Messenger messenger, IBinder iBinder, Phone phone, int i, int i2, String str) {
        int andIncrement = this.mNextNetworkScanRequestId.getAndIncrement();
        this.mHandler.obtainMessage(1, new NetworkScanRequestInfo(networkScanRequest, messenger, iBinder, andIncrement, phone, i, i2, str, z)).sendToTarget();
        return andIncrement;
    }

    public void stopNetworkScan(int i, int i2) {
        synchronized (this.mScheduler) {
            if ((this.mScheduler.mLiveRequestInfo != null && i == this.mScheduler.mLiveRequestInfo.mScanId && i2 == this.mScheduler.mLiveRequestInfo.mUid) || (this.mScheduler.mPendingRequestInfo != null && i == this.mScheduler.mPendingRequestInfo.mScanId && i2 == this.mScheduler.mPendingRequestInfo.mUid)) {
                this.mHandler.obtainMessage(4, i, 0).sendToTarget();
            } else {
                throw new IllegalArgumentException("Scan with id: " + i + " does not exist!");
            }
        }
    }
}
