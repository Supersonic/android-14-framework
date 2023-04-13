package com.android.internal.widget;

import android.app.PendingIntent;
import android.app.RemoteLockscreenValidationResult;
import android.app.StartLockscreenValidationRequest;
import android.app.trust.IStrongAuthTracker;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.security.keystore.recovery.KeyChainProtectionParams;
import android.security.keystore.recovery.KeyChainSnapshot;
import android.security.keystore.recovery.RecoveryCertPath;
import android.security.keystore.recovery.WrappedApplicationKey;
import com.android.internal.widget.ICheckCredentialProgressCallback;
import com.android.internal.widget.IWeakEscrowTokenActivatedListener;
import com.android.internal.widget.IWeakEscrowTokenRemovedListener;
import java.util.List;
import java.util.Map;
/* loaded from: classes5.dex */
public interface ILockSettings extends IInterface {
    long addWeakEscrowToken(byte[] bArr, int i, IWeakEscrowTokenActivatedListener iWeakEscrowTokenActivatedListener) throws RemoteException;

    VerifyCredentialResponse checkCredential(LockscreenCredential lockscreenCredential, int i, ICheckCredentialProgressCallback iCheckCredentialProgressCallback) throws RemoteException;

    void closeSession(String str) throws RemoteException;

    String generateKey(String str) throws RemoteException;

    String generateKeyWithMetadata(String str, byte[] bArr) throws RemoteException;

    boolean getBoolean(String str, boolean z, int i) throws RemoteException;

    int getCredentialType(int i) throws RemoteException;

    byte[] getHashFactor(LockscreenCredential lockscreenCredential, int i) throws RemoteException;

    String getKey(String str) throws RemoteException;

    KeyChainSnapshot getKeyChainSnapshot() throws RemoteException;

    long getLong(String str, long j, int i) throws RemoteException;

    int[] getRecoverySecretTypes() throws RemoteException;

    Map getRecoveryStatus() throws RemoteException;

    boolean getSeparateProfileChallengeEnabled(int i) throws RemoteException;

    String getString(String str, String str2, int i) throws RemoteException;

    int getStrongAuthForUser(int i) throws RemoteException;

    boolean hasPendingEscrowToken(int i) throws RemoteException;

    boolean hasSecureLockScreen() throws RemoteException;

    String importKey(String str, byte[] bArr) throws RemoteException;

    String importKeyWithMetadata(String str, byte[] bArr, byte[] bArr2) throws RemoteException;

    void initRecoveryServiceWithSigFile(String str, byte[] bArr, byte[] bArr2) throws RemoteException;

    boolean isWeakEscrowTokenActive(long j, int i) throws RemoteException;

    boolean isWeakEscrowTokenValid(long j, byte[] bArr, int i) throws RemoteException;

    Map recoverKeyChainSnapshot(String str, byte[] bArr, List<WrappedApplicationKey> list) throws RemoteException;

    void registerStrongAuthTracker(IStrongAuthTracker iStrongAuthTracker) throws RemoteException;

    boolean registerWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener iWeakEscrowTokenRemovedListener) throws RemoteException;

    void removeCachedUnifiedChallenge(int i) throws RemoteException;

    void removeGatekeeperPasswordHandle(long j) throws RemoteException;

    void removeKey(String str) throws RemoteException;

    boolean removeWeakEscrowToken(long j, int i) throws RemoteException;

    void reportSuccessfulBiometricUnlock(boolean z, int i) throws RemoteException;

    void requireStrongAuth(int i, int i2) throws RemoteException;

    void resetKeyStore(int i) throws RemoteException;

    void scheduleNonStrongBiometricIdleTimeout(int i) throws RemoteException;

    void setBoolean(String str, boolean z, int i) throws RemoteException;

    boolean setLockCredential(LockscreenCredential lockscreenCredential, LockscreenCredential lockscreenCredential2, int i) throws RemoteException;

    void setLong(String str, long j, int i) throws RemoteException;

    void setRecoverySecretTypes(int[] iArr) throws RemoteException;

    void setRecoveryStatus(String str, int i) throws RemoteException;

    void setSeparateProfileChallengeEnabled(int i, boolean z, LockscreenCredential lockscreenCredential) throws RemoteException;

    void setServerParams(byte[] bArr) throws RemoteException;

    void setSnapshotCreatedPendingIntent(PendingIntent pendingIntent) throws RemoteException;

    void setString(String str, String str2, int i) throws RemoteException;

    byte[] startRecoverySessionWithCertPath(String str, String str2, RecoveryCertPath recoveryCertPath, byte[] bArr, byte[] bArr2, List<KeyChainProtectionParams> list) throws RemoteException;

    StartLockscreenValidationRequest startRemoteLockscreenValidation() throws RemoteException;

    void systemReady() throws RemoteException;

    boolean tryUnlockWithCachedUnifiedChallenge(int i) throws RemoteException;

    void unregisterStrongAuthTracker(IStrongAuthTracker iStrongAuthTracker) throws RemoteException;

    boolean unregisterWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener iWeakEscrowTokenRemovedListener) throws RemoteException;

    void userPresent(int i) throws RemoteException;

    RemoteLockscreenValidationResult validateRemoteLockscreen(byte[] bArr) throws RemoteException;

    VerifyCredentialResponse verifyCredential(LockscreenCredential lockscreenCredential, int i, int i2) throws RemoteException;

    VerifyCredentialResponse verifyGatekeeperPasswordHandle(long j, long j2, int i) throws RemoteException;

    VerifyCredentialResponse verifyTiedProfileChallenge(LockscreenCredential lockscreenCredential, int i, int i2) throws RemoteException;

    /* loaded from: classes5.dex */
    public static class Default implements ILockSettings {
        @Override // com.android.internal.widget.ILockSettings
        public void setBoolean(String key, boolean value, int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setLong(String key, long value, int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setString(String key, String value, int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean getBoolean(String key, boolean defaultValue, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public long getLong(String key, long defaultValue, int userId) throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.widget.ILockSettings
        public String getString(String key, String defaultValue, int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean setLockCredential(LockscreenCredential credential, LockscreenCredential savedCredential, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void resetKeyStore(int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public VerifyCredentialResponse checkCredential(LockscreenCredential credential, int userId, ICheckCredentialProgressCallback progressCallback) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public VerifyCredentialResponse verifyCredential(LockscreenCredential credential, int userId, int flags) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public VerifyCredentialResponse verifyTiedProfileChallenge(LockscreenCredential credential, int userId, int flags) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public VerifyCredentialResponse verifyGatekeeperPasswordHandle(long gatekeeperPasswordHandle, long challenge, int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void removeGatekeeperPasswordHandle(long gatekeeperPasswordHandle) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public int getCredentialType(int userId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.widget.ILockSettings
        public byte[] getHashFactor(LockscreenCredential currentCredential, int userId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setSeparateProfileChallengeEnabled(int userId, boolean enabled, LockscreenCredential managedUserPassword) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean getSeparateProfileChallengeEnabled(int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void registerStrongAuthTracker(IStrongAuthTracker tracker) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void unregisterStrongAuthTracker(IStrongAuthTracker tracker) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void requireStrongAuth(int strongAuthReason, int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void reportSuccessfulBiometricUnlock(boolean isStrongBiometric, int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void scheduleNonStrongBiometricIdleTimeout(int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void systemReady() throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void userPresent(int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public int getStrongAuthForUser(int userId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean hasPendingEscrowToken(int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void initRecoveryServiceWithSigFile(String rootCertificateAlias, byte[] recoveryServiceCertFile, byte[] recoveryServiceSigFile) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public KeyChainSnapshot getKeyChainSnapshot() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public String generateKey(String alias) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public String generateKeyWithMetadata(String alias, byte[] metadata) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public String importKey(String alias, byte[] keyBytes) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public String importKeyWithMetadata(String alias, byte[] keyBytes, byte[] metadata) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public String getKey(String alias) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void removeKey(String alias) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setSnapshotCreatedPendingIntent(PendingIntent intent) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setServerParams(byte[] serverParams) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setRecoveryStatus(String alias, int status) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public Map getRecoveryStatus() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void setRecoverySecretTypes(int[] secretTypes) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public int[] getRecoverySecretTypes() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public byte[] startRecoverySessionWithCertPath(String sessionId, String rootCertificateAlias, RecoveryCertPath verifierCertPath, byte[] vaultParams, byte[] vaultChallenge, List<KeyChainProtectionParams> secrets) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public Map recoverKeyChainSnapshot(String sessionId, byte[] recoveryKeyBlob, List<WrappedApplicationKey> applicationKeys) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void closeSession(String sessionId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public StartLockscreenValidationRequest startRemoteLockscreenValidation() throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public RemoteLockscreenValidationResult validateRemoteLockscreen(byte[] encryptedCredential) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean hasSecureLockScreen() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean tryUnlockWithCachedUnifiedChallenge(int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public void removeCachedUnifiedChallenge(int userId) throws RemoteException {
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean registerWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener listener) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean unregisterWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener listener) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public long addWeakEscrowToken(byte[] token, int userId, IWeakEscrowTokenActivatedListener callback) throws RemoteException {
            return 0L;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean removeWeakEscrowToken(long handle, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean isWeakEscrowTokenActive(long handle, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.widget.ILockSettings
        public boolean isWeakEscrowTokenValid(long handle, byte[] token, int userId) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Stub extends Binder implements ILockSettings {
        public static final String DESCRIPTOR = "com.android.internal.widget.ILockSettings";
        static final int TRANSACTION_addWeakEscrowToken = 51;
        static final int TRANSACTION_checkCredential = 9;
        static final int TRANSACTION_closeSession = 43;
        static final int TRANSACTION_generateKey = 29;
        static final int TRANSACTION_generateKeyWithMetadata = 30;
        static final int TRANSACTION_getBoolean = 4;
        static final int TRANSACTION_getCredentialType = 14;
        static final int TRANSACTION_getHashFactor = 15;
        static final int TRANSACTION_getKey = 33;
        static final int TRANSACTION_getKeyChainSnapshot = 28;
        static final int TRANSACTION_getLong = 5;
        static final int TRANSACTION_getRecoverySecretTypes = 40;
        static final int TRANSACTION_getRecoveryStatus = 38;
        static final int TRANSACTION_getSeparateProfileChallengeEnabled = 17;
        static final int TRANSACTION_getString = 6;
        static final int TRANSACTION_getStrongAuthForUser = 25;
        static final int TRANSACTION_hasPendingEscrowToken = 26;
        static final int TRANSACTION_hasSecureLockScreen = 46;
        static final int TRANSACTION_importKey = 31;
        static final int TRANSACTION_importKeyWithMetadata = 32;
        static final int TRANSACTION_initRecoveryServiceWithSigFile = 27;
        static final int TRANSACTION_isWeakEscrowTokenActive = 53;
        static final int TRANSACTION_isWeakEscrowTokenValid = 54;
        static final int TRANSACTION_recoverKeyChainSnapshot = 42;
        static final int TRANSACTION_registerStrongAuthTracker = 18;
        static final int TRANSACTION_registerWeakEscrowTokenRemovedListener = 49;
        static final int TRANSACTION_removeCachedUnifiedChallenge = 48;
        static final int TRANSACTION_removeGatekeeperPasswordHandle = 13;
        static final int TRANSACTION_removeKey = 34;
        static final int TRANSACTION_removeWeakEscrowToken = 52;
        static final int TRANSACTION_reportSuccessfulBiometricUnlock = 21;
        static final int TRANSACTION_requireStrongAuth = 20;
        static final int TRANSACTION_resetKeyStore = 8;
        static final int TRANSACTION_scheduleNonStrongBiometricIdleTimeout = 22;
        static final int TRANSACTION_setBoolean = 1;
        static final int TRANSACTION_setLockCredential = 7;
        static final int TRANSACTION_setLong = 2;
        static final int TRANSACTION_setRecoverySecretTypes = 39;
        static final int TRANSACTION_setRecoveryStatus = 37;
        static final int TRANSACTION_setSeparateProfileChallengeEnabled = 16;
        static final int TRANSACTION_setServerParams = 36;
        static final int TRANSACTION_setSnapshotCreatedPendingIntent = 35;
        static final int TRANSACTION_setString = 3;
        static final int TRANSACTION_startRecoverySessionWithCertPath = 41;
        static final int TRANSACTION_startRemoteLockscreenValidation = 44;
        static final int TRANSACTION_systemReady = 23;
        static final int TRANSACTION_tryUnlockWithCachedUnifiedChallenge = 47;
        static final int TRANSACTION_unregisterStrongAuthTracker = 19;
        static final int TRANSACTION_unregisterWeakEscrowTokenRemovedListener = 50;
        static final int TRANSACTION_userPresent = 24;
        static final int TRANSACTION_validateRemoteLockscreen = 45;
        static final int TRANSACTION_verifyCredential = 10;
        static final int TRANSACTION_verifyGatekeeperPasswordHandle = 12;
        static final int TRANSACTION_verifyTiedProfileChallenge = 11;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static ILockSettings asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ILockSettings)) {
                return (ILockSettings) iin;
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
                    return "setBoolean";
                case 2:
                    return "setLong";
                case 3:
                    return "setString";
                case 4:
                    return "getBoolean";
                case 5:
                    return "getLong";
                case 6:
                    return "getString";
                case 7:
                    return "setLockCredential";
                case 8:
                    return "resetKeyStore";
                case 9:
                    return "checkCredential";
                case 10:
                    return "verifyCredential";
                case 11:
                    return "verifyTiedProfileChallenge";
                case 12:
                    return "verifyGatekeeperPasswordHandle";
                case 13:
                    return "removeGatekeeperPasswordHandle";
                case 14:
                    return "getCredentialType";
                case 15:
                    return "getHashFactor";
                case 16:
                    return "setSeparateProfileChallengeEnabled";
                case 17:
                    return "getSeparateProfileChallengeEnabled";
                case 18:
                    return "registerStrongAuthTracker";
                case 19:
                    return "unregisterStrongAuthTracker";
                case 20:
                    return "requireStrongAuth";
                case 21:
                    return "reportSuccessfulBiometricUnlock";
                case 22:
                    return "scheduleNonStrongBiometricIdleTimeout";
                case 23:
                    return "systemReady";
                case 24:
                    return "userPresent";
                case 25:
                    return "getStrongAuthForUser";
                case 26:
                    return "hasPendingEscrowToken";
                case 27:
                    return "initRecoveryServiceWithSigFile";
                case 28:
                    return "getKeyChainSnapshot";
                case 29:
                    return "generateKey";
                case 30:
                    return "generateKeyWithMetadata";
                case 31:
                    return "importKey";
                case 32:
                    return "importKeyWithMetadata";
                case 33:
                    return "getKey";
                case 34:
                    return "removeKey";
                case 35:
                    return "setSnapshotCreatedPendingIntent";
                case 36:
                    return "setServerParams";
                case 37:
                    return "setRecoveryStatus";
                case 38:
                    return "getRecoveryStatus";
                case 39:
                    return "setRecoverySecretTypes";
                case 40:
                    return "getRecoverySecretTypes";
                case 41:
                    return "startRecoverySessionWithCertPath";
                case 42:
                    return "recoverKeyChainSnapshot";
                case 43:
                    return "closeSession";
                case 44:
                    return "startRemoteLockscreenValidation";
                case 45:
                    return "validateRemoteLockscreen";
                case 46:
                    return "hasSecureLockScreen";
                case 47:
                    return "tryUnlockWithCachedUnifiedChallenge";
                case 48:
                    return "removeCachedUnifiedChallenge";
                case 49:
                    return "registerWeakEscrowTokenRemovedListener";
                case 50:
                    return "unregisterWeakEscrowTokenRemovedListener";
                case 51:
                    return "addWeakEscrowToken";
                case 52:
                    return "removeWeakEscrowToken";
                case 53:
                    return "isWeakEscrowTokenActive";
                case 54:
                    return "isWeakEscrowTokenValid";
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
                            String _arg0 = data.readString();
                            boolean _arg1 = data.readBoolean();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setBoolean(_arg0, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            long _arg12 = data.readLong();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            setLong(_arg02, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            setString(_arg03, _arg13, _arg23);
                            reply.writeNoException();
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            boolean _arg14 = data.readBoolean();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result = getBoolean(_arg04, _arg14, _arg24);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            long _arg15 = data.readLong();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            long _result2 = getLong(_arg05, _arg15, _arg25);
                            reply.writeNoException();
                            reply.writeLong(_result2);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg16 = data.readString();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            String _result3 = getString(_arg06, _arg16, _arg26);
                            reply.writeNoException();
                            reply.writeString(_result3);
                            break;
                        case 7:
                            LockscreenCredential _arg07 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            LockscreenCredential _arg17 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = setLockCredential(_arg07, _arg17, _arg27);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            resetKeyStore(_arg08);
                            reply.writeNoException();
                            break;
                        case 9:
                            LockscreenCredential _arg09 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            int _arg18 = data.readInt();
                            ICheckCredentialProgressCallback _arg28 = ICheckCredentialProgressCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            VerifyCredentialResponse _result5 = checkCredential(_arg09, _arg18, _arg28);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 10:
                            LockscreenCredential _arg010 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            int _arg19 = data.readInt();
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            VerifyCredentialResponse _result6 = verifyCredential(_arg010, _arg19, _arg29);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 11:
                            LockscreenCredential _arg011 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            int _arg110 = data.readInt();
                            int _arg210 = data.readInt();
                            data.enforceNoDataAvail();
                            VerifyCredentialResponse _result7 = verifyTiedProfileChallenge(_arg011, _arg110, _arg210);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 12:
                            long _arg012 = data.readLong();
                            long _arg111 = data.readLong();
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            VerifyCredentialResponse _result8 = verifyGatekeeperPasswordHandle(_arg012, _arg111, _arg211);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 13:
                            long _arg013 = data.readLong();
                            data.enforceNoDataAvail();
                            removeGatekeeperPasswordHandle(_arg013);
                            reply.writeNoException();
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result9 = getCredentialType(_arg014);
                            reply.writeNoException();
                            reply.writeInt(_result9);
                            break;
                        case 15:
                            LockscreenCredential _arg015 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            byte[] _result10 = getHashFactor(_arg015, _arg112);
                            reply.writeNoException();
                            reply.writeByteArray(_result10);
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            boolean _arg113 = data.readBoolean();
                            LockscreenCredential _arg212 = (LockscreenCredential) data.readTypedObject(LockscreenCredential.CREATOR);
                            data.enforceNoDataAvail();
                            setSeparateProfileChallengeEnabled(_arg016, _arg113, _arg212);
                            reply.writeNoException();
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result11 = getSeparateProfileChallengeEnabled(_arg017);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 18:
                            IStrongAuthTracker _arg018 = IStrongAuthTracker.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerStrongAuthTracker(_arg018);
                            reply.writeNoException();
                            break;
                        case 19:
                            IStrongAuthTracker _arg019 = IStrongAuthTracker.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterStrongAuthTracker(_arg019);
                            reply.writeNoException();
                            break;
                        case 20:
                            int _arg020 = data.readInt();
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            requireStrongAuth(_arg020, _arg114);
                            reply.writeNoException();
                            break;
                        case 21:
                            boolean _arg021 = data.readBoolean();
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            reportSuccessfulBiometricUnlock(_arg021, _arg115);
                            reply.writeNoException();
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            data.enforceNoDataAvail();
                            scheduleNonStrongBiometricIdleTimeout(_arg022);
                            reply.writeNoException();
                            break;
                        case 23:
                            systemReady();
                            reply.writeNoException();
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            data.enforceNoDataAvail();
                            userPresent(_arg023);
                            reply.writeNoException();
                            break;
                        case 25:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result12 = getStrongAuthForUser(_arg024);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            break;
                        case 26:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result13 = hasPendingEscrowToken(_arg025);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 27:
                            String _arg026 = data.readString();
                            byte[] _arg116 = data.createByteArray();
                            byte[] _arg213 = data.createByteArray();
                            data.enforceNoDataAvail();
                            initRecoveryServiceWithSigFile(_arg026, _arg116, _arg213);
                            reply.writeNoException();
                            break;
                        case 28:
                            KeyChainSnapshot _result14 = getKeyChainSnapshot();
                            reply.writeNoException();
                            reply.writeTypedObject(_result14, 1);
                            break;
                        case 29:
                            String _arg027 = data.readString();
                            data.enforceNoDataAvail();
                            String _result15 = generateKey(_arg027);
                            reply.writeNoException();
                            reply.writeString(_result15);
                            break;
                        case 30:
                            String _arg028 = data.readString();
                            byte[] _arg117 = data.createByteArray();
                            data.enforceNoDataAvail();
                            String _result16 = generateKeyWithMetadata(_arg028, _arg117);
                            reply.writeNoException();
                            reply.writeString(_result16);
                            break;
                        case 31:
                            String _arg029 = data.readString();
                            byte[] _arg118 = data.createByteArray();
                            data.enforceNoDataAvail();
                            String _result17 = importKey(_arg029, _arg118);
                            reply.writeNoException();
                            reply.writeString(_result17);
                            break;
                        case 32:
                            String _arg030 = data.readString();
                            byte[] _arg119 = data.createByteArray();
                            byte[] _arg214 = data.createByteArray();
                            data.enforceNoDataAvail();
                            String _result18 = importKeyWithMetadata(_arg030, _arg119, _arg214);
                            reply.writeNoException();
                            reply.writeString(_result18);
                            break;
                        case 33:
                            String _arg031 = data.readString();
                            data.enforceNoDataAvail();
                            String _result19 = getKey(_arg031);
                            reply.writeNoException();
                            reply.writeString(_result19);
                            break;
                        case 34:
                            String _arg032 = data.readString();
                            data.enforceNoDataAvail();
                            removeKey(_arg032);
                            reply.writeNoException();
                            break;
                        case 35:
                            PendingIntent _arg033 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            setSnapshotCreatedPendingIntent(_arg033);
                            reply.writeNoException();
                            break;
                        case 36:
                            byte[] _arg034 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setServerParams(_arg034);
                            reply.writeNoException();
                            break;
                        case 37:
                            String _arg035 = data.readString();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            setRecoveryStatus(_arg035, _arg120);
                            reply.writeNoException();
                            break;
                        case 38:
                            Map _result20 = getRecoveryStatus();
                            reply.writeNoException();
                            reply.writeMap(_result20);
                            break;
                        case 39:
                            int[] _arg036 = data.createIntArray();
                            data.enforceNoDataAvail();
                            setRecoverySecretTypes(_arg036);
                            reply.writeNoException();
                            break;
                        case 40:
                            int[] _result21 = getRecoverySecretTypes();
                            reply.writeNoException();
                            reply.writeIntArray(_result21);
                            break;
                        case 41:
                            String _arg037 = data.readString();
                            String _arg121 = data.readString();
                            RecoveryCertPath _arg215 = (RecoveryCertPath) data.readTypedObject(RecoveryCertPath.CREATOR);
                            byte[] _arg3 = data.createByteArray();
                            byte[] _arg4 = data.createByteArray();
                            List<KeyChainProtectionParams> _arg5 = data.createTypedArrayList(KeyChainProtectionParams.CREATOR);
                            data.enforceNoDataAvail();
                            byte[] _result22 = startRecoverySessionWithCertPath(_arg037, _arg121, _arg215, _arg3, _arg4, _arg5);
                            reply.writeNoException();
                            reply.writeByteArray(_result22);
                            break;
                        case 42:
                            String _arg038 = data.readString();
                            byte[] _arg122 = data.createByteArray();
                            List<WrappedApplicationKey> _arg216 = data.createTypedArrayList(WrappedApplicationKey.CREATOR);
                            data.enforceNoDataAvail();
                            Map _result23 = recoverKeyChainSnapshot(_arg038, _arg122, _arg216);
                            reply.writeNoException();
                            reply.writeMap(_result23);
                            break;
                        case 43:
                            String _arg039 = data.readString();
                            data.enforceNoDataAvail();
                            closeSession(_arg039);
                            reply.writeNoException();
                            break;
                        case 44:
                            StartLockscreenValidationRequest _result24 = startRemoteLockscreenValidation();
                            reply.writeNoException();
                            reply.writeTypedObject(_result24, 1);
                            break;
                        case 45:
                            byte[] _arg040 = data.createByteArray();
                            data.enforceNoDataAvail();
                            RemoteLockscreenValidationResult _result25 = validateRemoteLockscreen(_arg040);
                            reply.writeNoException();
                            reply.writeTypedObject(_result25, 1);
                            break;
                        case 46:
                            boolean _result26 = hasSecureLockScreen();
                            reply.writeNoException();
                            reply.writeBoolean(_result26);
                            break;
                        case 47:
                            int _arg041 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result27 = tryUnlockWithCachedUnifiedChallenge(_arg041);
                            reply.writeNoException();
                            reply.writeBoolean(_result27);
                            break;
                        case 48:
                            int _arg042 = data.readInt();
                            data.enforceNoDataAvail();
                            removeCachedUnifiedChallenge(_arg042);
                            reply.writeNoException();
                            break;
                        case 49:
                            IWeakEscrowTokenRemovedListener _arg043 = IWeakEscrowTokenRemovedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result28 = registerWeakEscrowTokenRemovedListener(_arg043);
                            reply.writeNoException();
                            reply.writeBoolean(_result28);
                            break;
                        case 50:
                            IWeakEscrowTokenRemovedListener _arg044 = IWeakEscrowTokenRemovedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result29 = unregisterWeakEscrowTokenRemovedListener(_arg044);
                            reply.writeNoException();
                            reply.writeBoolean(_result29);
                            break;
                        case 51:
                            byte[] _arg045 = data.createByteArray();
                            int _arg123 = data.readInt();
                            IWeakEscrowTokenActivatedListener _arg217 = IWeakEscrowTokenActivatedListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            long _result30 = addWeakEscrowToken(_arg045, _arg123, _arg217);
                            reply.writeNoException();
                            reply.writeLong(_result30);
                            break;
                        case 52:
                            long _arg046 = data.readLong();
                            int _arg124 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result31 = removeWeakEscrowToken(_arg046, _arg124);
                            reply.writeNoException();
                            reply.writeBoolean(_result31);
                            break;
                        case 53:
                            long _arg047 = data.readLong();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = isWeakEscrowTokenActive(_arg047, _arg125);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            break;
                        case 54:
                            long _arg048 = data.readLong();
                            byte[] _arg126 = data.createByteArray();
                            int _arg218 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result33 = isWeakEscrowTokenValid(_arg048, _arg126, _arg218);
                            reply.writeNoException();
                            reply.writeBoolean(_result33);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes5.dex */
        public static class Proxy implements ILockSettings {
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

            @Override // com.android.internal.widget.ILockSettings
            public void setBoolean(String key, boolean value, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeBoolean(value);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setLong(String key, long value, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeLong(value);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setString(String key, String value, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(value);
                    _data.writeInt(userId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean getBoolean(String key, boolean defaultValue, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeBoolean(defaultValue);
                    _data.writeInt(userId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public long getLong(String key, long defaultValue, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeLong(defaultValue);
                    _data.writeInt(userId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public String getString(String key, String defaultValue, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(defaultValue);
                    _data.writeInt(userId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean setLockCredential(LockscreenCredential credential, LockscreenCredential savedCredential, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(credential, 0);
                    _data.writeTypedObject(savedCredential, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void resetKeyStore(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public VerifyCredentialResponse checkCredential(LockscreenCredential credential, int userId, ICheckCredentialProgressCallback progressCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(credential, 0);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(progressCallback);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    VerifyCredentialResponse _result = (VerifyCredentialResponse) _reply.readTypedObject(VerifyCredentialResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public VerifyCredentialResponse verifyCredential(LockscreenCredential credential, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(credential, 0);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    VerifyCredentialResponse _result = (VerifyCredentialResponse) _reply.readTypedObject(VerifyCredentialResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public VerifyCredentialResponse verifyTiedProfileChallenge(LockscreenCredential credential, int userId, int flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(credential, 0);
                    _data.writeInt(userId);
                    _data.writeInt(flags);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    VerifyCredentialResponse _result = (VerifyCredentialResponse) _reply.readTypedObject(VerifyCredentialResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public VerifyCredentialResponse verifyGatekeeperPasswordHandle(long gatekeeperPasswordHandle, long challenge, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(gatekeeperPasswordHandle);
                    _data.writeLong(challenge);
                    _data.writeInt(userId);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    VerifyCredentialResponse _result = (VerifyCredentialResponse) _reply.readTypedObject(VerifyCredentialResponse.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void removeGatekeeperPasswordHandle(long gatekeeperPasswordHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(gatekeeperPasswordHandle);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public int getCredentialType(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public byte[] getHashFactor(LockscreenCredential currentCredential, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(currentCredential, 0);
                    _data.writeInt(userId);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setSeparateProfileChallengeEnabled(int userId, boolean enabled, LockscreenCredential managedUserPassword) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    _data.writeBoolean(enabled);
                    _data.writeTypedObject(managedUserPassword, 0);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean getSeparateProfileChallengeEnabled(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void registerStrongAuthTracker(IStrongAuthTracker tracker) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(tracker);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void unregisterStrongAuthTracker(IStrongAuthTracker tracker) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(tracker);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void requireStrongAuth(int strongAuthReason, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strongAuthReason);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void reportSuccessfulBiometricUnlock(boolean isStrongBiometric, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(isStrongBiometric);
                    _data.writeInt(userId);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void scheduleNonStrongBiometricIdleTimeout(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void systemReady() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void userPresent(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public int getStrongAuthForUser(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean hasPendingEscrowToken(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void initRecoveryServiceWithSigFile(String rootCertificateAlias, byte[] recoveryServiceCertFile, byte[] recoveryServiceSigFile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(rootCertificateAlias);
                    _data.writeByteArray(recoveryServiceCertFile);
                    _data.writeByteArray(recoveryServiceSigFile);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public KeyChainSnapshot getKeyChainSnapshot() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    KeyChainSnapshot _result = (KeyChainSnapshot) _reply.readTypedObject(KeyChainSnapshot.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public String generateKey(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public String generateKeyWithMetadata(String alias, byte[] metadata) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    _data.writeByteArray(metadata);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public String importKey(String alias, byte[] keyBytes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    _data.writeByteArray(keyBytes);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public String importKeyWithMetadata(String alias, byte[] keyBytes, byte[] metadata) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    _data.writeByteArray(keyBytes);
                    _data.writeByteArray(metadata);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public String getKey(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void removeKey(String alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setSnapshotCreatedPendingIntent(PendingIntent intent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setServerParams(byte[] serverParams) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(serverParams);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setRecoveryStatus(String alias, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(alias);
                    _data.writeInt(status);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public Map getRecoveryStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void setRecoverySecretTypes(int[] secretTypes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(secretTypes);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public int[] getRecoverySecretTypes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(40, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public byte[] startRecoverySessionWithCertPath(String sessionId, String rootCertificateAlias, RecoveryCertPath verifierCertPath, byte[] vaultParams, byte[] vaultChallenge, List<KeyChainProtectionParams> secrets) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeString(rootCertificateAlias);
                    _data.writeTypedObject(verifierCertPath, 0);
                    _data.writeByteArray(vaultParams);
                    _data.writeByteArray(vaultChallenge);
                    _data.writeTypedList(secrets, 0);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                    byte[] _result = _reply.createByteArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public Map recoverKeyChainSnapshot(String sessionId, byte[] recoveryKeyBlob, List<WrappedApplicationKey> applicationKeys) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sessionId);
                    _data.writeByteArray(recoveryKeyBlob);
                    _data.writeTypedList(applicationKeys, 0);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void closeSession(String sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(sessionId);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public StartLockscreenValidationRequest startRemoteLockscreenValidation() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    StartLockscreenValidationRequest _result = (StartLockscreenValidationRequest) _reply.readTypedObject(StartLockscreenValidationRequest.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public RemoteLockscreenValidationResult validateRemoteLockscreen(byte[] encryptedCredential) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(encryptedCredential);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    RemoteLockscreenValidationResult _result = (RemoteLockscreenValidationResult) _reply.readTypedObject(RemoteLockscreenValidationResult.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean hasSecureLockScreen() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean tryUnlockWithCachedUnifiedChallenge(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public void removeCachedUnifiedChallenge(int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(userId);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean registerWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean unregisterWeakEscrowTokenRemovedListener(IWeakEscrowTokenRemovedListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public long addWeakEscrowToken(byte[] token, int userId, IWeakEscrowTokenActivatedListener callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeByteArray(token);
                    _data.writeInt(userId);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(51, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean removeWeakEscrowToken(long handle, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeInt(userId);
                    this.mRemote.transact(52, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean isWeakEscrowTokenActive(long handle, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeInt(userId);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.widget.ILockSettings
            public boolean isWeakEscrowTokenValid(long handle, byte[] token, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(handle);
                    _data.writeByteArray(token);
                    _data.writeInt(userId);
                    this.mRemote.transact(54, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 53;
        }
    }
}
