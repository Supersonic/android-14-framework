package android.service.voice;

import android.p008os.Bundle;
import android.p008os.IBinder;
/* loaded from: classes3.dex */
public abstract class VoiceInteractionManagerInternal {
    public abstract HotwordDetectionServiceIdentity getHotwordDetectionServiceIdentity();

    public abstract String getVoiceInteractorPackageName(IBinder iBinder);

    public abstract boolean hasActiveSession(String str);

    public abstract void onPreCreatedUserConversion(int i);

    public abstract void startLocalVoiceInteraction(IBinder iBinder, String str, Bundle bundle);

    public abstract void stopLocalVoiceInteraction(IBinder iBinder);

    public abstract boolean supportsLocalVoiceInteraction();

    /* loaded from: classes3.dex */
    public static class HotwordDetectionServiceIdentity {
        private final int mIsolatedUid;
        private final int mOwnerUid;

        public HotwordDetectionServiceIdentity(int isolatedUid, int ownerUid) {
            this.mIsolatedUid = isolatedUid;
            this.mOwnerUid = ownerUid;
        }

        public int getIsolatedUid() {
            return this.mIsolatedUid;
        }

        public int getOwnerUid() {
            return this.mOwnerUid;
        }
    }
}
