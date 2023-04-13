package com.android.internal.telephony.util;

import android.p008os.IInterface;
import android.p008os.RemoteCallbackList;
import java.util.function.Consumer;
/* loaded from: classes3.dex */
public class RemoteCallbackListExt<E extends IInterface> extends RemoteCallbackList<E> {
    public void broadcastAction(Consumer<E> action) {
        int itemCount = beginBroadcast();
        for (int i = 0; i < itemCount; i++) {
            try {
                action.accept(getBroadcastItem(i));
            } finally {
                finishBroadcast();
            }
        }
    }
}
