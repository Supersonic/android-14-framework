package com.android.server.locksettings;

import android.hardware.rebootescrow.IRebootEscrow;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ServiceSpecificException;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.util.NoSuchElementException;
import javax.crypto.SecretKey;
/* loaded from: classes2.dex */
public class RebootEscrowProviderHalImpl implements RebootEscrowProviderInterface {
    public final Injector mInjector;

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public int getType() {
        return 0;
    }

    /* loaded from: classes2.dex */
    public static class Injector {
        public IRebootEscrow getRebootEscrow() {
            try {
                return IRebootEscrow.Stub.asInterface(ServiceManager.getService("android.hardware.rebootescrow.IRebootEscrow/default"));
            } catch (NoSuchElementException unused) {
                Slog.i("RebootEscrowProviderHal", "Device doesn't implement RebootEscrow HAL");
                return null;
            }
        }
    }

    public RebootEscrowProviderHalImpl() {
        this.mInjector = new Injector();
    }

    @VisibleForTesting
    public RebootEscrowProviderHalImpl(Injector injector) {
        this.mInjector = injector;
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public boolean hasRebootEscrowSupport() {
        return this.mInjector.getRebootEscrow() != null;
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public RebootEscrowKey getAndClearRebootEscrowKey(SecretKey secretKey) {
        IRebootEscrow rebootEscrow = this.mInjector.getRebootEscrow();
        if (rebootEscrow == null) {
            Slog.w("RebootEscrowProviderHal", "Had reboot escrow data for users, but RebootEscrow HAL is unavailable");
            return null;
        }
        try {
            byte[] retrieveKey = rebootEscrow.retrieveKey();
            if (retrieveKey == null) {
                Slog.w("RebootEscrowProviderHal", "Had reboot escrow data for users, but could not retrieve key");
                return null;
            } else if (retrieveKey.length != 32) {
                Slog.e("RebootEscrowProviderHal", "IRebootEscrow returned key of incorrect size " + retrieveKey.length);
                return null;
            } else {
                int i = 0;
                for (byte b : retrieveKey) {
                    i |= b;
                }
                if (i == 0) {
                    Slog.w("RebootEscrowProviderHal", "IRebootEscrow returned an all-zeroes key");
                    return null;
                }
                rebootEscrow.storeKey(new byte[32]);
                return RebootEscrowKey.fromKeyBytes(retrieveKey);
            }
        } catch (ServiceSpecificException e) {
            Slog.w("RebootEscrowProviderHal", "Got service-specific exception: " + e.errorCode);
            return null;
        } catch (RemoteException unused) {
            Slog.w("RebootEscrowProviderHal", "Could not retrieve escrow data");
            return null;
        }
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public void clearRebootEscrowKey() {
        IRebootEscrow rebootEscrow = this.mInjector.getRebootEscrow();
        if (rebootEscrow == null) {
            return;
        }
        try {
            rebootEscrow.storeKey(new byte[32]);
        } catch (RemoteException | ServiceSpecificException unused) {
            Slog.w("RebootEscrowProviderHal", "Could not call RebootEscrow HAL to shred key");
        }
    }

    @Override // com.android.server.locksettings.RebootEscrowProviderInterface
    public boolean storeRebootEscrowKey(RebootEscrowKey rebootEscrowKey, SecretKey secretKey) {
        IRebootEscrow rebootEscrow = this.mInjector.getRebootEscrow();
        if (rebootEscrow == null) {
            Slog.w("RebootEscrowProviderHal", "Escrow marked as ready, but RebootEscrow HAL is unavailable");
            return false;
        }
        try {
            rebootEscrow.storeKey(rebootEscrowKey.getKeyBytes());
            Slog.i("RebootEscrowProviderHal", "Reboot escrow key stored with RebootEscrow HAL");
            return true;
        } catch (RemoteException | ServiceSpecificException e) {
            Slog.e("RebootEscrowProviderHal", "Failed escrow secret to RebootEscrow HAL", e);
            return false;
        }
    }
}
