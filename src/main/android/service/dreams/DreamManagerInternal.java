package android.service.dreams;
/* loaded from: classes3.dex */
public abstract class DreamManagerInternal {

    /* loaded from: classes3.dex */
    public interface DreamManagerStateListener {
        void onKeepDreamingWhenUndockedChanged(boolean z);
    }

    public abstract boolean canStartDreaming(boolean z);

    public abstract boolean isDreaming();

    public abstract void registerDreamManagerStateListener(DreamManagerStateListener dreamManagerStateListener);

    public abstract void requestDream();

    public abstract void startDream(boolean z, String str);

    public abstract void stopDream(boolean z, String str);

    public abstract void unregisterDreamManagerStateListener(DreamManagerStateListener dreamManagerStateListener);
}
