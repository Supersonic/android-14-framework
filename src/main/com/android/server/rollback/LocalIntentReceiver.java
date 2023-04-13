package com.android.server.rollback;

import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class LocalIntentReceiver {
    public final Consumer<Intent> mConsumer;
    public IIntentSender.Stub mLocalSender = new IIntentSender.Stub() { // from class: com.android.server.rollback.LocalIntentReceiver.1
        public void send(int i, Intent intent, String str, IBinder iBinder, IIntentReceiver iIntentReceiver, String str2, Bundle bundle) {
            LocalIntentReceiver.this.mConsumer.accept(intent);
        }
    };

    public LocalIntentReceiver(Consumer<Intent> consumer) {
        this.mConsumer = consumer;
    }

    public IntentSender getIntentSender() {
        return new IntentSender(this.mLocalSender);
    }
}
