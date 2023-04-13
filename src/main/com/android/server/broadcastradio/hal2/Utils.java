package com.android.server.broadcastradio.hal2;

import android.os.RemoteException;
/* loaded from: classes.dex */
public class Utils {

    /* loaded from: classes.dex */
    public interface FuncThrowingRemoteException<T> {
        T exec() throws RemoteException;
    }

    /* loaded from: classes.dex */
    public interface VoidFuncThrowingRemoteException {
        void exec() throws RemoteException;
    }

    public static FrequencyBand getBand(int i) {
        if (i < 30) {
            return FrequencyBand.UNKNOWN;
        }
        if (i < 500) {
            return FrequencyBand.AM_LW;
        }
        if (i < 1705) {
            return FrequencyBand.AM_MW;
        }
        if (i < 30000) {
            return FrequencyBand.AM_SW;
        }
        if (i < 60000) {
            return FrequencyBand.UNKNOWN;
        }
        if (i < 110000) {
            return FrequencyBand.FM;
        }
        return FrequencyBand.UNKNOWN;
    }

    public static <T> T maybeRethrow(FuncThrowingRemoteException<T> funcThrowingRemoteException) {
        try {
            return funcThrowingRemoteException.exec();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return null;
        }
    }

    public static void maybeRethrow(VoidFuncThrowingRemoteException voidFuncThrowingRemoteException) {
        try {
            voidFuncThrowingRemoteException.exec();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }
}
