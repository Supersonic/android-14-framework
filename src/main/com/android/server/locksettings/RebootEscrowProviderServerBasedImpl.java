package com.android.server.locksettings;

import android.content.Context;
import android.os.RemoteException;
import android.provider.DeviceConfig;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.locksettings.ResumeOnRebootServiceProvider;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class RebootEscrowProviderServerBasedImpl implements RebootEscrowProviderInterface {
    public final Injector mInjector;
    public byte[] mServerBlob;
    public final LockSettingsStorage mStorage;

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public int getType() {
        return 1;
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public ResumeOnRebootServiceProvider.ResumeOnRebootServiceConnection mServiceConnection;

        public Injector(Context context) {
            this.mServiceConnection = null;
            ResumeOnRebootServiceProvider.ResumeOnRebootServiceConnection serviceConnection = new ResumeOnRebootServiceProvider(context).getServiceConnection();
            this.mServiceConnection = serviceConnection;
            if (serviceConnection == null) {
                Slog.e("RebootEscrowProviderServerBased", "Failed to resolve resume on reboot server service.");
            }
        }

        public final ResumeOnRebootServiceProvider.ResumeOnRebootServiceConnection getServiceConnection() {
            return this.mServiceConnection;
        }

        public long getServiceTimeoutInSeconds() {
            return DeviceConfig.getLong("ota", "server_based_service_timeout_in_seconds", 10L);
        }

        public long getServerBlobLifetimeInMillis() {
            return DeviceConfig.getLong("ota", "server_based_server_blob_lifetime_in_millis", 600000L);
        }
    }

    public RebootEscrowProviderServerBasedImpl(Context context, LockSettingsStorage lockSettingsStorage) {
        this(lockSettingsStorage, new Injector(context));
    }

    @VisibleForTesting
    public RebootEscrowProviderServerBasedImpl(LockSettingsStorage lockSettingsStorage, Injector injector) {
        this.mStorage = lockSettingsStorage;
        this.mInjector = injector;
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public boolean hasRebootEscrowSupport() {
        return this.mInjector.getServiceConnection() != null;
    }

    public final byte[] unwrapServerBlob(byte[] bArr, SecretKey secretKey) throws TimeoutException, RemoteException, IOException {
        ResumeOnRebootServiceProvider.ResumeOnRebootServiceConnection serviceConnection = this.mInjector.getServiceConnection();
        if (serviceConnection == null) {
            Slog.w("RebootEscrowProviderServerBased", "Had reboot escrow data for users, but resume on reboot server service is unavailable");
            return null;
        }
        byte[] decrypt = AesEncryptionUtil.decrypt(secretKey, bArr);
        if (decrypt == null) {
            Slog.w("RebootEscrowProviderServerBased", "Decrypted server blob should not be null");
            return null;
        }
        serviceConnection.bindToService(this.mInjector.getServiceTimeoutInSeconds());
        byte[] unwrap = serviceConnection.unwrap(decrypt, this.mInjector.getServiceTimeoutInSeconds());
        serviceConnection.unbindService();
        return unwrap;
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public RebootEscrowKey getAndClearRebootEscrowKey(SecretKey secretKey) throws IOException {
        if (this.mServerBlob == null) {
            this.mServerBlob = this.mStorage.readRebootEscrowServerBlob();
        }
        this.mStorage.removeRebootEscrowServerBlob();
        if (this.mServerBlob == null) {
            Slog.w("RebootEscrowProviderServerBased", "Failed to read reboot escrow server blob from storage");
            return null;
        } else if (secretKey == null) {
            Slog.w("RebootEscrowProviderServerBased", "Failed to decrypt the escrow key; decryption key from keystore is null.");
            return null;
        } else {
            Slog.i("RebootEscrowProviderServerBased", "Loaded reboot escrow server blob from storage");
            try {
                byte[] unwrapServerBlob = unwrapServerBlob(this.mServerBlob, secretKey);
                if (unwrapServerBlob == null) {
                    Slog.w("RebootEscrowProviderServerBased", "Decrypted reboot escrow key bytes should not be null");
                    return null;
                } else if (unwrapServerBlob.length != 32) {
                    Slog.e("RebootEscrowProviderServerBased", "Decrypted reboot escrow key has incorrect size " + unwrapServerBlob.length);
                    return null;
                } else {
                    return RebootEscrowKey.fromKeyBytes(unwrapServerBlob);
                }
            } catch (RemoteException | TimeoutException e) {
                Slog.w("RebootEscrowProviderServerBased", "Failed to decrypt the server blob ", e);
                return null;
            }
        }
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public void clearRebootEscrowKey() {
        this.mStorage.removeRebootEscrowServerBlob();
    }

    public final byte[] wrapEscrowKey(byte[] bArr, SecretKey secretKey) throws TimeoutException, RemoteException, IOException {
        ResumeOnRebootServiceProvider.ResumeOnRebootServiceConnection serviceConnection = this.mInjector.getServiceConnection();
        if (serviceConnection == null) {
            Slog.w("RebootEscrowProviderServerBased", "Failed to encrypt the reboot escrow key: resume on reboot server service is unavailable");
            return null;
        }
        serviceConnection.bindToService(this.mInjector.getServiceTimeoutInSeconds());
        byte[] wrapBlob = serviceConnection.wrapBlob(bArr, this.mInjector.getServerBlobLifetimeInMillis(), this.mInjector.getServiceTimeoutInSeconds());
        serviceConnection.unbindService();
        if (wrapBlob == null) {
            Slog.w("RebootEscrowProviderServerBased", "Server encrypted reboot escrow key cannot be null");
            return null;
        }
        return AesEncryptionUtil.encrypt(secretKey, wrapBlob);
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public boolean storeRebootEscrowKey(RebootEscrowKey rebootEscrowKey, SecretKey secretKey) {
        this.mStorage.removeRebootEscrowServerBlob();
        try {
            byte[] wrapEscrowKey = wrapEscrowKey(rebootEscrowKey.getKeyBytes(), secretKey);
            if (wrapEscrowKey == null) {
                Slog.w("RebootEscrowProviderServerBased", "Failed to encrypt the reboot escrow key");
                return false;
            }
            this.mStorage.writeRebootEscrowServerBlob(wrapEscrowKey);
            Slog.i("RebootEscrowProviderServerBased", "Reboot escrow key encrypted and stored.");
            return true;
        } catch (RemoteException | IOException | TimeoutException e) {
            Slog.w("RebootEscrowProviderServerBased", "Failed to encrypt the reboot escrow key ", e);
            return false;
        }
    }
}
