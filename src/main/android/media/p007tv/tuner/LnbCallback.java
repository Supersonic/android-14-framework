package android.media.p007tv.tuner;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.LnbCallback */
/* loaded from: classes2.dex */
public interface LnbCallback {
    void onDiseqcMessage(byte[] bArr);

    void onEvent(int i);
}
