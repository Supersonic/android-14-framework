package com.android.server.people;

import android.os.CancellationSignal;
import android.service.appprediction.IPredictionService;
/* loaded from: classes2.dex */
public abstract class PeopleServiceInternal extends IPredictionService.Stub {
    public abstract byte[] getBackupPayload(int i);

    public abstract void pruneDataForUser(int i, CancellationSignal cancellationSignal);

    public abstract void restore(int i, byte[] bArr);
}
