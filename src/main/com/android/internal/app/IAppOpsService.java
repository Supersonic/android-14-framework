package com.android.internal.app;

import android.app.AppOpsManager;
import android.app.AsyncNotedAppOp;
import android.app.RuntimeAppOpAccessMessage;
import android.app.SyncNotedAppOp;
import android.content.AttributionSource;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.PackageTagsList;
import android.p008os.Parcel;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import com.android.internal.app.IAppOpsActiveCallback;
import com.android.internal.app.IAppOpsAsyncNotedCallback;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsNotedCallback;
import com.android.internal.app.IAppOpsStartedCallback;
import java.util.List;
/* loaded from: classes4.dex */
public interface IAppOpsService extends IInterface {
    void addHistoricalOps(AppOpsManager.HistoricalOps historicalOps) throws RemoteException;

    int checkAudioOperation(int i, int i2, int i3, String str) throws RemoteException;

    int checkOperation(int i, int i2, String str) throws RemoteException;

    int checkOperationRaw(int i, int i2, String str, String str2) throws RemoteException;

    int checkPackage(int i, String str) throws RemoteException;

    void clearHistory() throws RemoteException;

    void collectNoteOpCallsForValidation(String str, int i, String str2, long j) throws RemoteException;

    RuntimeAppOpAccessMessage collectRuntimeAppOpAccessMessage() throws RemoteException;

    List<AsyncNotedAppOp> extractAsyncOps(String str) throws RemoteException;

    void finishOperation(IBinder iBinder, int i, int i2, String str, String str2) throws RemoteException;

    void finishProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z) throws RemoteException;

    void getHistoricalOps(int i, String str, String str2, List<String> list, int i2, int i3, long j, long j2, int i4, RemoteCallback remoteCallback) throws RemoteException;

    void getHistoricalOpsFromDiskRaw(int i, String str, String str2, List<String> list, int i2, int i3, long j, long j2, int i4, RemoteCallback remoteCallback) throws RemoteException;

    List<AppOpsManager.PackageOps> getOpsForPackage(int i, String str, int[] iArr) throws RemoteException;

    List<AppOpsManager.PackageOps> getPackagesForOps(int[] iArr) throws RemoteException;

    List<AppOpsManager.PackageOps> getUidOps(int i, int[] iArr) throws RemoteException;

    boolean isOperationActive(int i, int i2, String str) throws RemoteException;

    boolean isProxying(int i, String str, String str2, int i2, String str3) throws RemoteException;

    SyncNotedAppOp noteOperation(int i, int i2, String str, String str2, boolean z, String str3, boolean z2) throws RemoteException;

    SyncNotedAppOp noteProxyOperation(int i, AttributionSource attributionSource, boolean z, String str, boolean z2, boolean z3) throws RemoteException;

    void offsetHistory(long j) throws RemoteException;

    int permissionToOpCode(String str) throws RemoteException;

    void rebootHistory(long j) throws RemoteException;

    void reloadNonHistoricalState() throws RemoteException;

    void removeUser(int i) throws RemoteException;

    MessageSamplingConfig reportRuntimeAppOpAccessMessageAndGetConfig(String str, SyncNotedAppOp syncNotedAppOp, String str2) throws RemoteException;

    void resetAllModes(int i, String str) throws RemoteException;

    void resetHistoryParameters() throws RemoteException;

    void resetPackageOpsNoHistory(String str) throws RemoteException;

    void setAudioRestriction(int i, int i2, int i3, int i4, String[] strArr) throws RemoteException;

    void setCameraAudioRestriction(int i) throws RemoteException;

    void setHistoryParameters(int i, long j, int i2) throws RemoteException;

    void setMode(int i, int i2, String str, int i3) throws RemoteException;

    void setUidMode(int i, int i2, int i3) throws RemoteException;

    void setUserRestriction(int i, boolean z, IBinder iBinder, int i2, PackageTagsList packageTagsList) throws RemoteException;

    void setUserRestrictions(Bundle bundle, IBinder iBinder, int i) throws RemoteException;

    boolean shouldCollectNotes(int i) throws RemoteException;

    SyncNotedAppOp startOperation(IBinder iBinder, int i, int i2, String str, String str2, boolean z, boolean z2, String str3, boolean z3, int i3, int i4) throws RemoteException;

    SyncNotedAppOp startProxyOperation(IBinder iBinder, int i, AttributionSource attributionSource, boolean z, boolean z2, String str, boolean z3, boolean z4, int i2, int i3, int i4) throws RemoteException;

    void startWatchingActive(int[] iArr, IAppOpsActiveCallback iAppOpsActiveCallback) throws RemoteException;

    void startWatchingAsyncNoted(String str, IAppOpsAsyncNotedCallback iAppOpsAsyncNotedCallback) throws RemoteException;

    void startWatchingMode(int i, String str, IAppOpsCallback iAppOpsCallback) throws RemoteException;

    void startWatchingModeWithFlags(int i, String str, int i2, IAppOpsCallback iAppOpsCallback) throws RemoteException;

    void startWatchingNoted(int[] iArr, IAppOpsNotedCallback iAppOpsNotedCallback) throws RemoteException;

    void startWatchingStarted(int[] iArr, IAppOpsStartedCallback iAppOpsStartedCallback) throws RemoteException;

    void stopWatchingActive(IAppOpsActiveCallback iAppOpsActiveCallback) throws RemoteException;

    void stopWatchingAsyncNoted(String str, IAppOpsAsyncNotedCallback iAppOpsAsyncNotedCallback) throws RemoteException;

    void stopWatchingMode(IAppOpsCallback iAppOpsCallback) throws RemoteException;

    void stopWatchingNoted(IAppOpsNotedCallback iAppOpsNotedCallback) throws RemoteException;

    void stopWatchingStarted(IAppOpsStartedCallback iAppOpsStartedCallback) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAppOpsService {
        @Override // com.android.internal.app.IAppOpsService
        public int checkOperation(int code, int uid, String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IAppOpsService
        public SyncNotedAppOp noteOperation(int code, int uid, String packageName, String attributionTag, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public SyncNotedAppOp startOperation(IBinder clientId, int code, int uid, String packageName, String attributionTag, boolean startIfModeDefault, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage, int attributionFlags, int attributionChainId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void finishOperation(IBinder clientId, int code, int uid, String packageName, String attributionTag) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void startWatchingMode(int op, String packageName, IAppOpsCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void stopWatchingMode(IAppOpsCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public int permissionToOpCode(String permission) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IAppOpsService
        public int checkAudioOperation(int code, int usage, int uid, String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IAppOpsService
        public boolean shouldCollectNotes(int opCode) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setCameraAudioRestriction(int mode) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public SyncNotedAppOp noteProxyOperation(int code, AttributionSource attributionSource, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage, boolean skipProxyOperation) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public SyncNotedAppOp startProxyOperation(IBinder clientId, int code, AttributionSource attributionSource, boolean startIfModeDefault, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage, boolean skipProxyOperation, int proxyAttributionFlags, int proxiedAttributionFlags, int attributionChainId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void finishProxyOperation(IBinder clientId, int code, AttributionSource attributionSource, boolean skipProxyOperation) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public int checkPackage(int uid, String packageName) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IAppOpsService
        public RuntimeAppOpAccessMessage collectRuntimeAppOpAccessMessage() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public MessageSamplingConfig reportRuntimeAppOpAccessMessageAndGetConfig(String packageName, SyncNotedAppOp appOp, String message) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public List<AppOpsManager.PackageOps> getPackagesForOps(int[] ops) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public List<AppOpsManager.PackageOps> getOpsForPackage(int uid, String packageName, int[] ops) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void getHistoricalOps(int uid, String packageName, String attributionTag, List<String> ops, int historyFlags, int filter, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void getHistoricalOpsFromDiskRaw(int uid, String packageName, String attributionTag, List<String> ops, int historyFlags, int filter, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void offsetHistory(long duration) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setHistoryParameters(int mode, long baseSnapshotInterval, int compressionStep) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void addHistoricalOps(AppOpsManager.HistoricalOps ops) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void resetHistoryParameters() throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void resetPackageOpsNoHistory(String packageName) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void clearHistory() throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void rebootHistory(long offlineDurationMillis) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public List<AppOpsManager.PackageOps> getUidOps(int uid, int[] ops) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setUidMode(int code, int uid, int mode) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setMode(int code, int uid, String packageName, int mode) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void resetAllModes(int reqUserId, String reqPackageName) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setAudioRestriction(int code, int usage, int uid, int mode, String[] exceptionPackages) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setUserRestrictions(Bundle restrictions, IBinder token, int userHandle) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void setUserRestriction(int code, boolean restricted, IBinder token, int userHandle, PackageTagsList excludedPackageTags) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void removeUser(int userHandle) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void startWatchingActive(int[] ops, IAppOpsActiveCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void stopWatchingActive(IAppOpsActiveCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public boolean isOperationActive(int code, int uid, String packageName) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.app.IAppOpsService
        public boolean isProxying(int op, String proxyPackageName, String proxyAttributionTag, int proxiedUid, String proxiedPackageName) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void startWatchingStarted(int[] ops, IAppOpsStartedCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void stopWatchingStarted(IAppOpsStartedCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void startWatchingModeWithFlags(int op, String packageName, int flags, IAppOpsCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void startWatchingNoted(int[] ops, IAppOpsNotedCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void stopWatchingNoted(IAppOpsNotedCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void startWatchingAsyncNoted(String packageName, IAppOpsAsyncNotedCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void stopWatchingAsyncNoted(String packageName, IAppOpsAsyncNotedCallback callback) throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public List<AsyncNotedAppOp> extractAsyncOps(String packageName) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.app.IAppOpsService
        public int checkOperationRaw(int code, int uid, String packageName, String attributionTag) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.app.IAppOpsService
        public void reloadNonHistoricalState() throws RemoteException {
        }

        @Override // com.android.internal.app.IAppOpsService
        public void collectNoteOpCallsForValidation(String stackTrace, int op, String packageName, long version) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAppOpsService {
        public static final String DESCRIPTOR = "com.android.internal.app.IAppOpsService";
        static final int TRANSACTION_addHistoricalOps = 23;
        static final int TRANSACTION_checkAudioOperation = 8;
        static final int TRANSACTION_checkOperation = 1;
        static final int TRANSACTION_checkOperationRaw = 48;
        static final int TRANSACTION_checkPackage = 14;
        static final int TRANSACTION_clearHistory = 26;
        static final int TRANSACTION_collectNoteOpCallsForValidation = 50;
        static final int TRANSACTION_collectRuntimeAppOpAccessMessage = 15;
        static final int TRANSACTION_extractAsyncOps = 47;
        static final int TRANSACTION_finishOperation = 4;
        static final int TRANSACTION_finishProxyOperation = 13;
        static final int TRANSACTION_getHistoricalOps = 19;
        static final int TRANSACTION_getHistoricalOpsFromDiskRaw = 20;
        static final int TRANSACTION_getOpsForPackage = 18;
        static final int TRANSACTION_getPackagesForOps = 17;
        static final int TRANSACTION_getUidOps = 28;
        static final int TRANSACTION_isOperationActive = 38;
        static final int TRANSACTION_isProxying = 39;
        static final int TRANSACTION_noteOperation = 2;
        static final int TRANSACTION_noteProxyOperation = 11;
        static final int TRANSACTION_offsetHistory = 21;
        static final int TRANSACTION_permissionToOpCode = 7;
        static final int TRANSACTION_rebootHistory = 27;
        static final int TRANSACTION_reloadNonHistoricalState = 49;
        static final int TRANSACTION_removeUser = 35;
        static final int TRANSACTION_reportRuntimeAppOpAccessMessageAndGetConfig = 16;
        static final int TRANSACTION_resetAllModes = 31;
        static final int TRANSACTION_resetHistoryParameters = 24;
        static final int TRANSACTION_resetPackageOpsNoHistory = 25;
        static final int TRANSACTION_setAudioRestriction = 32;
        static final int TRANSACTION_setCameraAudioRestriction = 10;
        static final int TRANSACTION_setHistoryParameters = 22;
        static final int TRANSACTION_setMode = 30;
        static final int TRANSACTION_setUidMode = 29;
        static final int TRANSACTION_setUserRestriction = 34;
        static final int TRANSACTION_setUserRestrictions = 33;
        static final int TRANSACTION_shouldCollectNotes = 9;
        static final int TRANSACTION_startOperation = 3;
        static final int TRANSACTION_startProxyOperation = 12;
        static final int TRANSACTION_startWatchingActive = 36;
        static final int TRANSACTION_startWatchingAsyncNoted = 45;
        static final int TRANSACTION_startWatchingMode = 5;
        static final int TRANSACTION_startWatchingModeWithFlags = 42;
        static final int TRANSACTION_startWatchingNoted = 43;
        static final int TRANSACTION_startWatchingStarted = 40;
        static final int TRANSACTION_stopWatchingActive = 37;
        static final int TRANSACTION_stopWatchingAsyncNoted = 46;
        static final int TRANSACTION_stopWatchingMode = 6;
        static final int TRANSACTION_stopWatchingNoted = 44;
        static final int TRANSACTION_stopWatchingStarted = 41;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppOpsService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAppOpsService)) {
                return (IAppOpsService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "checkOperation";
                case 2:
                    return "noteOperation";
                case 3:
                    return "startOperation";
                case 4:
                    return "finishOperation";
                case 5:
                    return "startWatchingMode";
                case 6:
                    return "stopWatchingMode";
                case 7:
                    return "permissionToOpCode";
                case 8:
                    return "checkAudioOperation";
                case 9:
                    return "shouldCollectNotes";
                case 10:
                    return "setCameraAudioRestriction";
                case 11:
                    return "noteProxyOperation";
                case 12:
                    return "startProxyOperation";
                case 13:
                    return "finishProxyOperation";
                case 14:
                    return "checkPackage";
                case 15:
                    return "collectRuntimeAppOpAccessMessage";
                case 16:
                    return "reportRuntimeAppOpAccessMessageAndGetConfig";
                case 17:
                    return "getPackagesForOps";
                case 18:
                    return "getOpsForPackage";
                case 19:
                    return "getHistoricalOps";
                case 20:
                    return "getHistoricalOpsFromDiskRaw";
                case 21:
                    return "offsetHistory";
                case 22:
                    return "setHistoryParameters";
                case 23:
                    return "addHistoricalOps";
                case 24:
                    return "resetHistoryParameters";
                case 25:
                    return "resetPackageOpsNoHistory";
                case 26:
                    return "clearHistory";
                case 27:
                    return "rebootHistory";
                case 28:
                    return "getUidOps";
                case 29:
                    return "setUidMode";
                case 30:
                    return "setMode";
                case 31:
                    return "resetAllModes";
                case 32:
                    return "setAudioRestriction";
                case 33:
                    return "setUserRestrictions";
                case 34:
                    return "setUserRestriction";
                case 35:
                    return "removeUser";
                case 36:
                    return "startWatchingActive";
                case 37:
                    return "stopWatchingActive";
                case 38:
                    return "isOperationActive";
                case 39:
                    return "isProxying";
                case 40:
                    return "startWatchingStarted";
                case 41:
                    return "stopWatchingStarted";
                case 42:
                    return "startWatchingModeWithFlags";
                case 43:
                    return "startWatchingNoted";
                case 44:
                    return "stopWatchingNoted";
                case 45:
                    return "startWatchingAsyncNoted";
                case 46:
                    return "stopWatchingAsyncNoted";
                case 47:
                    return "extractAsyncOps";
                case 48:
                    return "checkOperationRaw";
                case 49:
                    return "reloadNonHistoricalState";
                case 50:
                    return "collectNoteOpCallsForValidation";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            int _result = checkOperation(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            String _arg22 = data.readString();
                            String _arg3 = data.readString();
                            boolean _arg4 = data.readBoolean();
                            String _arg5 = data.readString();
                            boolean _arg6 = data.readBoolean();
                            data.enforceNoDataAvail();
                            SyncNotedAppOp _result2 = noteOperation(_arg02, _arg12, _arg22, _arg3, _arg4, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            return true;
                        case 3:
                            IBinder _arg03 = data.readStrongBinder();
                            int _arg13 = data.readInt();
                            int _arg23 = data.readInt();
                            String _arg32 = data.readString();
                            String _arg42 = data.readString();
                            boolean _arg52 = data.readBoolean();
                            boolean _arg62 = data.readBoolean();
                            String _arg7 = data.readString();
                            boolean _arg8 = data.readBoolean();
                            int _arg9 = data.readInt();
                            int _arg10 = data.readInt();
                            data.enforceNoDataAvail();
                            SyncNotedAppOp _result3 = startOperation(_arg03, _arg13, _arg23, _arg32, _arg42, _arg52, _arg62, _arg7, _arg8, _arg9, _arg10);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            return true;
                        case 4:
                            IBinder _arg04 = data.readStrongBinder();
                            int _arg14 = data.readInt();
                            int _arg24 = data.readInt();
                            String _arg33 = data.readString();
                            String _arg43 = data.readString();
                            data.enforceNoDataAvail();
                            finishOperation(_arg04, _arg14, _arg24, _arg33, _arg43);
                            reply.writeNoException();
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            String _arg15 = data.readString();
                            IAppOpsCallback _arg25 = IAppOpsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startWatchingMode(_arg05, _arg15, _arg25);
                            reply.writeNoException();
                            return true;
                        case 6:
                            IAppOpsCallback _arg06 = IAppOpsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopWatchingMode(_arg06);
                            reply.writeNoException();
                            return true;
                        case 7:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            int _result4 = permissionToOpCode(_arg07);
                            reply.writeNoException();
                            reply.writeInt(_result4);
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg16 = data.readInt();
                            int _arg26 = data.readInt();
                            String _arg34 = data.readString();
                            data.enforceNoDataAvail();
                            int _result5 = checkAudioOperation(_arg08, _arg16, _arg26, _arg34);
                            reply.writeNoException();
                            reply.writeInt(_result5);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result6 = shouldCollectNotes(_arg09);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            return true;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            setCameraAudioRestriction(_arg010);
                            reply.writeNoException();
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            AttributionSource _arg17 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            boolean _arg27 = data.readBoolean();
                            String _arg35 = data.readString();
                            boolean _arg44 = data.readBoolean();
                            boolean _arg53 = data.readBoolean();
                            data.enforceNoDataAvail();
                            SyncNotedAppOp _result7 = noteProxyOperation(_arg011, _arg17, _arg27, _arg35, _arg44, _arg53);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            return true;
                        case 12:
                            IBinder _arg012 = data.readStrongBinder();
                            int _arg18 = data.readInt();
                            AttributionSource _arg28 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            boolean _arg36 = data.readBoolean();
                            boolean _arg45 = data.readBoolean();
                            String _arg54 = data.readString();
                            boolean _arg63 = data.readBoolean();
                            boolean _arg72 = data.readBoolean();
                            int _arg82 = data.readInt();
                            int _arg92 = data.readInt();
                            int _arg102 = data.readInt();
                            data.enforceNoDataAvail();
                            SyncNotedAppOp _result8 = startProxyOperation(_arg012, _arg18, _arg28, _arg36, _arg45, _arg54, _arg63, _arg72, _arg82, _arg92, _arg102);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            return true;
                        case 13:
                            IBinder _arg013 = data.readStrongBinder();
                            int _arg19 = data.readInt();
                            AttributionSource _arg29 = (AttributionSource) data.readTypedObject(AttributionSource.CREATOR);
                            boolean _arg37 = data.readBoolean();
                            data.enforceNoDataAvail();
                            finishProxyOperation(_arg013, _arg19, _arg29, _arg37);
                            reply.writeNoException();
                            return true;
                        case 14:
                            int _arg014 = data.readInt();
                            String _arg110 = data.readString();
                            data.enforceNoDataAvail();
                            int _result9 = checkPackage(_arg014, _arg110);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            return true;
                        case 15:
                            RuntimeAppOpAccessMessage _result10 = collectRuntimeAppOpAccessMessage();
                            reply.writeNoException();
                            reply.writeTypedObject(_result10, 1);
                            return true;
                        case 16:
                            String _arg015 = data.readString();
                            SyncNotedAppOp _arg111 = (SyncNotedAppOp) data.readTypedObject(SyncNotedAppOp.CREATOR);
                            String _arg210 = data.readString();
                            data.enforceNoDataAvail();
                            MessageSamplingConfig _result11 = reportRuntimeAppOpAccessMessageAndGetConfig(_arg015, _arg111, _arg210);
                            reply.writeNoException();
                            reply.writeTypedObject(_result11, 1);
                            return true;
                        case 17:
                            int[] _arg016 = data.createIntArray();
                            data.enforceNoDataAvail();
                            List<AppOpsManager.PackageOps> _result12 = getPackagesForOps(_arg016);
                            reply.writeNoException();
                            reply.writeTypedList(_result12, 1);
                            return true;
                        case 18:
                            int _arg017 = data.readInt();
                            String _arg112 = data.readString();
                            int[] _arg211 = data.createIntArray();
                            data.enforceNoDataAvail();
                            List<AppOpsManager.PackageOps> _result13 = getOpsForPackage(_arg017, _arg112, _arg211);
                            reply.writeNoException();
                            reply.writeTypedList(_result13, 1);
                            return true;
                        case 19:
                            int _arg018 = data.readInt();
                            String _arg113 = data.readString();
                            String _arg212 = data.readString();
                            List<String> _arg38 = data.createStringArrayList();
                            int _arg46 = data.readInt();
                            int _arg55 = data.readInt();
                            long _arg64 = data.readLong();
                            long _arg73 = data.readLong();
                            int _arg83 = data.readInt();
                            RemoteCallback _arg93 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getHistoricalOps(_arg018, _arg113, _arg212, _arg38, _arg46, _arg55, _arg64, _arg73, _arg83, _arg93);
                            reply.writeNoException();
                            return true;
                        case 20:
                            int _arg019 = data.readInt();
                            String _arg114 = data.readString();
                            String _arg213 = data.readString();
                            List<String> _arg39 = data.createStringArrayList();
                            int _arg47 = data.readInt();
                            int _arg56 = data.readInt();
                            long _arg65 = data.readLong();
                            long _arg74 = data.readLong();
                            int _arg84 = data.readInt();
                            RemoteCallback _arg94 = (RemoteCallback) data.readTypedObject(RemoteCallback.CREATOR);
                            data.enforceNoDataAvail();
                            getHistoricalOpsFromDiskRaw(_arg019, _arg114, _arg213, _arg39, _arg47, _arg56, _arg65, _arg74, _arg84, _arg94);
                            reply.writeNoException();
                            return true;
                        case 21:
                            long _arg020 = data.readLong();
                            data.enforceNoDataAvail();
                            offsetHistory(_arg020);
                            reply.writeNoException();
                            return true;
                        case 22:
                            int _arg021 = data.readInt();
                            long _arg115 = data.readLong();
                            int _arg214 = data.readInt();
                            data.enforceNoDataAvail();
                            setHistoryParameters(_arg021, _arg115, _arg214);
                            reply.writeNoException();
                            return true;
                        case 23:
                            AppOpsManager.HistoricalOps _arg022 = (AppOpsManager.HistoricalOps) data.readTypedObject(AppOpsManager.HistoricalOps.CREATOR);
                            data.enforceNoDataAvail();
                            addHistoricalOps(_arg022);
                            reply.writeNoException();
                            return true;
                        case 24:
                            resetHistoryParameters();
                            reply.writeNoException();
                            return true;
                        case 25:
                            String _arg023 = data.readString();
                            data.enforceNoDataAvail();
                            resetPackageOpsNoHistory(_arg023);
                            reply.writeNoException();
                            return true;
                        case 26:
                            clearHistory();
                            reply.writeNoException();
                            return true;
                        case 27:
                            long _arg024 = data.readLong();
                            data.enforceNoDataAvail();
                            rebootHistory(_arg024);
                            reply.writeNoException();
                            return true;
                        case 28:
                            int _arg025 = data.readInt();
                            int[] _arg116 = data.createIntArray();
                            data.enforceNoDataAvail();
                            List<AppOpsManager.PackageOps> _result14 = getUidOps(_arg025, _arg116);
                            reply.writeNoException();
                            reply.writeTypedList(_result14, 1);
                            return true;
                        case 29:
                            int _arg026 = data.readInt();
                            int _arg117 = data.readInt();
                            int _arg215 = data.readInt();
                            data.enforceNoDataAvail();
                            setUidMode(_arg026, _arg117, _arg215);
                            reply.writeNoException();
                            return true;
                        case 30:
                            int _arg027 = data.readInt();
                            int _arg118 = data.readInt();
                            String _arg216 = data.readString();
                            int _arg310 = data.readInt();
                            data.enforceNoDataAvail();
                            setMode(_arg027, _arg118, _arg216, _arg310);
                            reply.writeNoException();
                            return true;
                        case 31:
                            int _arg028 = data.readInt();
                            String _arg119 = data.readString();
                            data.enforceNoDataAvail();
                            resetAllModes(_arg028, _arg119);
                            reply.writeNoException();
                            return true;
                        case 32:
                            int _arg029 = data.readInt();
                            int _arg120 = data.readInt();
                            int _arg217 = data.readInt();
                            int _arg311 = data.readInt();
                            String[] _arg48 = data.createStringArray();
                            data.enforceNoDataAvail();
                            setAudioRestriction(_arg029, _arg120, _arg217, _arg311, _arg48);
                            reply.writeNoException();
                            return true;
                        case 33:
                            Bundle _arg030 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IBinder _arg121 = data.readStrongBinder();
                            int _arg218 = data.readInt();
                            data.enforceNoDataAvail();
                            setUserRestrictions(_arg030, _arg121, _arg218);
                            reply.writeNoException();
                            return true;
                        case 34:
                            int _arg031 = data.readInt();
                            boolean _arg122 = data.readBoolean();
                            IBinder _arg219 = data.readStrongBinder();
                            int _arg312 = data.readInt();
                            PackageTagsList _arg49 = (PackageTagsList) data.readTypedObject(PackageTagsList.CREATOR);
                            data.enforceNoDataAvail();
                            setUserRestriction(_arg031, _arg122, _arg219, _arg312, _arg49);
                            reply.writeNoException();
                            return true;
                        case 35:
                            int _arg032 = data.readInt();
                            data.enforceNoDataAvail();
                            removeUser(_arg032);
                            reply.writeNoException();
                            return true;
                        case 36:
                            int[] _arg033 = data.createIntArray();
                            IAppOpsActiveCallback _arg123 = IAppOpsActiveCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startWatchingActive(_arg033, _arg123);
                            reply.writeNoException();
                            return true;
                        case 37:
                            IAppOpsActiveCallback _arg034 = IAppOpsActiveCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopWatchingActive(_arg034);
                            reply.writeNoException();
                            return true;
                        case 38:
                            int _arg035 = data.readInt();
                            int _arg124 = data.readInt();
                            String _arg220 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result15 = isOperationActive(_arg035, _arg124, _arg220);
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            return true;
                        case 39:
                            int _arg036 = data.readInt();
                            String _arg125 = data.readString();
                            String _arg221 = data.readString();
                            int _arg313 = data.readInt();
                            String _arg410 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result16 = isProxying(_arg036, _arg125, _arg221, _arg313, _arg410);
                            reply.writeNoException();
                            reply.writeBoolean(_result16);
                            return true;
                        case 40:
                            int[] _arg037 = data.createIntArray();
                            IAppOpsStartedCallback _arg126 = IAppOpsStartedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startWatchingStarted(_arg037, _arg126);
                            reply.writeNoException();
                            return true;
                        case 41:
                            IAppOpsStartedCallback _arg038 = IAppOpsStartedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopWatchingStarted(_arg038);
                            reply.writeNoException();
                            return true;
                        case 42:
                            int _arg039 = data.readInt();
                            String _arg127 = data.readString();
                            int _arg222 = data.readInt();
                            IAppOpsCallback _arg314 = IAppOpsCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startWatchingModeWithFlags(_arg039, _arg127, _arg222, _arg314);
                            reply.writeNoException();
                            return true;
                        case 43:
                            int[] _arg040 = data.createIntArray();
                            IAppOpsNotedCallback _arg128 = IAppOpsNotedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startWatchingNoted(_arg040, _arg128);
                            reply.writeNoException();
                            return true;
                        case 44:
                            IAppOpsNotedCallback _arg041 = IAppOpsNotedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopWatchingNoted(_arg041);
                            reply.writeNoException();
                            return true;
                        case 45:
                            String _arg042 = data.readString();
                            IAppOpsAsyncNotedCallback _arg129 = IAppOpsAsyncNotedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            startWatchingAsyncNoted(_arg042, _arg129);
                            reply.writeNoException();
                            return true;
                        case 46:
                            String _arg043 = data.readString();
                            IAppOpsAsyncNotedCallback _arg130 = IAppOpsAsyncNotedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            stopWatchingAsyncNoted(_arg043, _arg130);
                            reply.writeNoException();
                            return true;
                        case 47:
                            String _arg044 = data.readString();
                            data.enforceNoDataAvail();
                            List<AsyncNotedAppOp> _result17 = extractAsyncOps(_arg044);
                            reply.writeNoException();
                            reply.writeTypedList(_result17, 1);
                            return true;
                        case 48:
                            int _arg045 = data.readInt();
                            int _arg131 = data.readInt();
                            String _arg223 = data.readString();
                            String _arg315 = data.readString();
                            data.enforceNoDataAvail();
                            int _result18 = checkOperationRaw(_arg045, _arg131, _arg223, _arg315);
                            reply.writeNoException();
                            reply.writeInt(_result18);
                            return true;
                        case 49:
                            reloadNonHistoricalState();
                            reply.writeNoException();
                            return true;
                        case 50:
                            String _arg046 = data.readString();
                            int _arg132 = data.readInt();
                            String _arg224 = data.readString();
                            long _arg316 = data.readLong();
                            data.enforceNoDataAvail();
                            collectNoteOpCallsForValidation(_arg046, _arg132, _arg224, _arg316);
                            reply.writeNoException();
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAppOpsService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.app.IAppOpsService
            public int checkOperation(int code, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public SyncNotedAppOp noteOperation(int code, int uid, String packageName, String attributionTag, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    _data.writeBoolean(shouldCollectAsyncNotedOp);
                    _data.writeString(message);
                    _data.writeBoolean(shouldCollectMessage);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    SyncNotedAppOp _result = (SyncNotedAppOp) _reply.readTypedObject(SyncNotedAppOp.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public SyncNotedAppOp startOperation(IBinder clientId, int code, int uid, String packageName, String attributionTag, boolean startIfModeDefault, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage, int attributionFlags, int attributionChainId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientId);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeInt(code);
                    try {
                        _data.writeInt(uid);
                        try {
                            _data.writeString(packageName);
                            try {
                                _data.writeString(attributionTag);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeBoolean(startIfModeDefault);
                        try {
                            _data.writeBoolean(shouldCollectAsyncNotedOp);
                            try {
                                _data.writeString(message);
                                try {
                                    _data.writeBoolean(shouldCollectMessage);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(attributionFlags);
                            try {
                                _data.writeInt(attributionChainId);
                                try {
                                    this.mRemote.transact(3, _data, _reply, 0);
                                    _reply.readException();
                                    SyncNotedAppOp _result = (SyncNotedAppOp) _reply.readTypedObject(SyncNotedAppOp.CREATOR);
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void finishOperation(IBinder clientId, int code, int uid, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientId);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingMode(int op, String packageName, IAppOpsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void stopWatchingMode(IAppOpsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public int permissionToOpCode(String permission) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(permission);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public int checkAudioOperation(int code, int usage, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(usage);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public boolean shouldCollectNotes(int opCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(opCode);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setCameraAudioRestriction(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public SyncNotedAppOp noteProxyOperation(int code, AttributionSource attributionSource, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage, boolean skipProxyOperation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeBoolean(shouldCollectAsyncNotedOp);
                    _data.writeString(message);
                    _data.writeBoolean(shouldCollectMessage);
                    _data.writeBoolean(skipProxyOperation);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    SyncNotedAppOp _result = (SyncNotedAppOp) _reply.readTypedObject(SyncNotedAppOp.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public SyncNotedAppOp startProxyOperation(IBinder clientId, int code, AttributionSource attributionSource, boolean startIfModeDefault, boolean shouldCollectAsyncNotedOp, String message, boolean shouldCollectMessage, boolean skipProxyOperation, int proxyAttributionFlags, int proxiedAttributionFlags, int attributionChainId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientId);
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    _data.writeInt(code);
                    try {
                        _data.writeTypedObject(attributionSource, 0);
                        try {
                            _data.writeBoolean(startIfModeDefault);
                            try {
                                _data.writeBoolean(shouldCollectAsyncNotedOp);
                            } catch (Throwable th2) {
                                th = th2;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th4) {
                        th = th4;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeString(message);
                        try {
                            _data.writeBoolean(shouldCollectMessage);
                            try {
                                _data.writeBoolean(skipProxyOperation);
                                try {
                                    _data.writeInt(proxyAttributionFlags);
                                } catch (Throwable th5) {
                                    th = th5;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th6) {
                                th = th6;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th7) {
                            th = th7;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                        try {
                            _data.writeInt(proxiedAttributionFlags);
                            try {
                                _data.writeInt(attributionChainId);
                                try {
                                    this.mRemote.transact(12, _data, _reply, 0);
                                    _reply.readException();
                                    SyncNotedAppOp _result = (SyncNotedAppOp) _reply.readTypedObject(SyncNotedAppOp.CREATOR);
                                    _reply.recycle();
                                    _data.recycle();
                                    return _result;
                                } catch (Throwable th8) {
                                    th = th8;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th9) {
                                th = th9;
                            }
                        } catch (Throwable th10) {
                            th = th10;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    _reply.recycle();
                    _data.recycle();
                    throw th;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void finishProxyOperation(IBinder clientId, int code, AttributionSource attributionSource, boolean skipProxyOperation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(clientId);
                    _data.writeInt(code);
                    _data.writeTypedObject(attributionSource, 0);
                    _data.writeBoolean(skipProxyOperation);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public int checkPackage(int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public RuntimeAppOpAccessMessage collectRuntimeAppOpAccessMessage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    RuntimeAppOpAccessMessage _result = (RuntimeAppOpAccessMessage) _reply.readTypedObject(RuntimeAppOpAccessMessage.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public MessageSamplingConfig reportRuntimeAppOpAccessMessageAndGetConfig(String packageName, SyncNotedAppOp appOp, String message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(appOp, 0);
                    _data.writeString(message);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                    MessageSamplingConfig _result = (MessageSamplingConfig) _reply.readTypedObject(MessageSamplingConfig.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public List<AppOpsManager.PackageOps> getPackagesForOps(int[] ops) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(ops);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    List<AppOpsManager.PackageOps> _result = _reply.createTypedArrayList(AppOpsManager.PackageOps.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public List<AppOpsManager.PackageOps> getOpsForPackage(int uid, String packageName, int[] ops) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeIntArray(ops);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    List<AppOpsManager.PackageOps> _result = _reply.createTypedArrayList(AppOpsManager.PackageOps.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void getHistoricalOps(int uid, String packageName, String attributionTag, List<String> ops, int historyFlags, int filter, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    try {
                        _data.writeString(attributionTag);
                        try {
                            _data.writeStringList(ops);
                            try {
                                _data.writeInt(historyFlags);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(filter);
                        try {
                            _data.writeLong(beginTimeMillis);
                            try {
                                _data.writeLong(endTimeMillis);
                                try {
                                    _data.writeInt(flags);
                                    try {
                                        _data.writeTypedObject(callback, 0);
                                        try {
                                            this.mRemote.transact(19, _data, _reply, 0);
                                            _reply.readException();
                                            _reply.recycle();
                                            _data.recycle();
                                        } catch (Throwable th4) {
                                            th = th4;
                                            _reply.recycle();
                                            _data.recycle();
                                            throw th;
                                        }
                                    } catch (Throwable th5) {
                                        th = th5;
                                    }
                                } catch (Throwable th6) {
                                    th = th6;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th9) {
                        th = th9;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th10) {
                    th = th10;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void getHistoricalOpsFromDiskRaw(int uid, String packageName, String attributionTag, List<String> ops, int historyFlags, int filter, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    try {
                        _data.writeString(attributionTag);
                        try {
                            _data.writeStringList(ops);
                            try {
                                _data.writeInt(historyFlags);
                            } catch (Throwable th) {
                                th = th;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                    try {
                        _data.writeInt(filter);
                        try {
                            _data.writeLong(beginTimeMillis);
                            try {
                                _data.writeLong(endTimeMillis);
                                try {
                                    _data.writeInt(flags);
                                    try {
                                        _data.writeTypedObject(callback, 0);
                                        try {
                                            this.mRemote.transact(20, _data, _reply, 0);
                                            _reply.readException();
                                            _reply.recycle();
                                            _data.recycle();
                                        } catch (Throwable th4) {
                                            th = th4;
                                            _reply.recycle();
                                            _data.recycle();
                                            throw th;
                                        }
                                    } catch (Throwable th5) {
                                        th = th5;
                                    }
                                } catch (Throwable th6) {
                                    th = th6;
                                    _reply.recycle();
                                    _data.recycle();
                                    throw th;
                                }
                            } catch (Throwable th7) {
                                th = th7;
                                _reply.recycle();
                                _data.recycle();
                                throw th;
                            }
                        } catch (Throwable th8) {
                            th = th8;
                            _reply.recycle();
                            _data.recycle();
                            throw th;
                        }
                    } catch (Throwable th9) {
                        th = th9;
                        _reply.recycle();
                        _data.recycle();
                        throw th;
                    }
                } catch (Throwable th10) {
                    th = th10;
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void offsetHistory(long duration) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(duration);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setHistoryParameters(int mode, long baseSnapshotInterval, int compressionStep) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeLong(baseSnapshotInterval);
                    _data.writeInt(compressionStep);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void addHistoricalOps(AppOpsManager.HistoricalOps ops) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ops, 0);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void resetHistoryParameters() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void resetPackageOpsNoHistory(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void clearHistory() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void rebootHistory(long offlineDurationMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(offlineDurationMillis);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public List<AppOpsManager.PackageOps> getUidOps(int uid, int[] ops) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(uid);
                    _data.writeIntArray(ops);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    List<AppOpsManager.PackageOps> _result = _reply.createTypedArrayList(AppOpsManager.PackageOps.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setUidMode(int code, int uid, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeInt(mode);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setMode(int code, int uid, String packageName, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeInt(mode);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void resetAllModes(int reqUserId, String reqPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(reqUserId);
                    _data.writeString(reqPackageName);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setAudioRestriction(int code, int usage, int uid, int mode, String[] exceptionPackages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(usage);
                    _data.writeInt(uid);
                    _data.writeInt(mode);
                    _data.writeStringArray(exceptionPackages);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setUserRestrictions(Bundle restrictions, IBinder token, int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(restrictions, 0);
                    _data.writeStrongBinder(token);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void setUserRestriction(int code, boolean restricted, IBinder token, int userHandle, PackageTagsList excludedPackageTags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeBoolean(restricted);
                    _data.writeStrongBinder(token);
                    _data.writeInt(userHandle);
                    _data.writeTypedObject(excludedPackageTags, 0);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void removeUser(int userHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userHandle);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingActive(int[] ops, IAppOpsActiveCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(ops);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void stopWatchingActive(IAppOpsActiveCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public boolean isOperationActive(int code, int uid, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public boolean isProxying(int op, String proxyPackageName, String proxyAttributionTag, int proxiedUid, String proxiedPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeString(proxyPackageName);
                    _data.writeString(proxyAttributionTag);
                    _data.writeInt(proxiedUid);
                    _data.writeString(proxiedPackageName);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingStarted(int[] ops, IAppOpsStartedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(ops);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void stopWatchingStarted(IAppOpsStartedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingModeWithFlags(int op, String packageName, int flags, IAppOpsCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(op);
                    _data.writeString(packageName);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingNoted(int[] ops, IAppOpsNotedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(ops);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void stopWatchingNoted(IAppOpsNotedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void startWatchingAsyncNoted(String packageName, IAppOpsAsyncNotedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void stopWatchingAsyncNoted(String packageName, IAppOpsAsyncNotedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public List<AsyncNotedAppOp> extractAsyncOps(String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    List<AsyncNotedAppOp> _result = _reply.createTypedArrayList(AsyncNotedAppOp.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public int checkOperationRaw(int code, int uid, String packageName, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(code);
                    _data.writeInt(uid);
                    _data.writeString(packageName);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void reloadNonHistoricalState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.app.IAppOpsService
            public void collectNoteOpCallsForValidation(String stackTrace, int op, String packageName, long version) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(stackTrace);
                    _data.writeInt(op);
                    _data.writeString(packageName);
                    _data.writeLong(version);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 49;
        }
    }
}
