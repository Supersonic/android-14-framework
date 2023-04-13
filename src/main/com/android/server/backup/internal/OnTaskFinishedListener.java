package com.android.server.backup.internal;
/* loaded from: classes.dex */
public interface OnTaskFinishedListener {
    public static final OnTaskFinishedListener NOP = new OnTaskFinishedListener() { // from class: com.android.server.backup.internal.OnTaskFinishedListener$$ExternalSyntheticLambda0
        @Override // com.android.server.backup.internal.OnTaskFinishedListener
        public final void onFinished(String str) {
            OnTaskFinishedListener.lambda$static$0(str);
        }
    };

    static /* synthetic */ void lambda$static$0(String str) {
    }

    void onFinished(String str);
}
