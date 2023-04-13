package android.p008os;

import android.p008os.IBinder;
import android.p008os.IInterface;
import android.telecom.TelecomManager;
import android.util.ArrayMap;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* renamed from: android.os.RemoteCallbackList */
/* loaded from: classes3.dex */
public class RemoteCallbackList<E extends IInterface> {
    private static final String TAG = "RemoteCallbackList";
    private Object[] mActiveBroadcast;
    private StringBuilder mRecentCallers;
    ArrayMap<IBinder, RemoteCallbackList<E>.Callback> mCallbacks = new ArrayMap<>();
    private int mBroadcastCount = -1;
    private boolean mKilled = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.RemoteCallbackList$Callback */
    /* loaded from: classes3.dex */
    public final class Callback implements IBinder.DeathRecipient {
        final E mCallback;
        final Object mCookie;

        Callback(E callback, Object cookie) {
            this.mCallback = callback;
            this.mCookie = cookie;
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (RemoteCallbackList.this.mCallbacks) {
                RemoteCallbackList.this.mCallbacks.remove(this.mCallback.asBinder());
            }
            RemoteCallbackList.this.onCallbackDied(this.mCallback, this.mCookie);
        }
    }

    public boolean register(E callback) {
        return register(callback, null);
    }

    public boolean register(E callback, Object cookie) {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return false;
            }
            logExcessiveCallbacks();
            IBinder binder = callback.asBinder();
            try {
                RemoteCallbackList<E>.Callback cb = new Callback(callback, cookie);
                unregister(callback);
                binder.linkToDeath(cb, 0);
                this.mCallbacks.put(binder, cb);
                return true;
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    /* JADX WARN: Type inference failed for: r3v0, types: [E extends android.os.IInterface, android.os.IInterface] */
    public boolean unregister(E callback) {
        synchronized (this.mCallbacks) {
            RemoteCallbackList<E>.Callback cb = this.mCallbacks.remove(callback.asBinder());
            if (cb != null) {
                cb.mCallback.asBinder().unlinkToDeath(cb, 0);
                return true;
            }
            return false;
        }
    }

    /* JADX WARN: Type inference failed for: r4v0, types: [E extends android.os.IInterface, android.os.IInterface] */
    public void kill() {
        synchronized (this.mCallbacks) {
            for (int cbi = this.mCallbacks.size() - 1; cbi >= 0; cbi--) {
                RemoteCallbackList<E>.Callback cb = this.mCallbacks.valueAt(cbi);
                cb.mCallback.asBinder().unlinkToDeath(cb, 0);
            }
            this.mCallbacks.clear();
            this.mKilled = true;
        }
    }

    public void onCallbackDied(E callback) {
    }

    public void onCallbackDied(E callback, Object cookie) {
        onCallbackDied(callback);
    }

    public int beginBroadcast() {
        synchronized (this.mCallbacks) {
            if (this.mBroadcastCount > 0) {
                throw new IllegalStateException("beginBroadcast() called while already in a broadcast");
            }
            int N = this.mCallbacks.size();
            this.mBroadcastCount = N;
            if (N <= 0) {
                return 0;
            }
            Object[] active = this.mActiveBroadcast;
            if (active == null || active.length < N) {
                Object[] objArr = new Object[N];
                active = objArr;
                this.mActiveBroadcast = objArr;
            }
            for (int i = 0; i < N; i++) {
                active[i] = this.mCallbacks.valueAt(i);
            }
            return N;
        }
    }

    public E getBroadcastItem(int index) {
        return ((Callback) this.mActiveBroadcast[index]).mCallback;
    }

    public Object getBroadcastCookie(int index) {
        return ((Callback) this.mActiveBroadcast[index]).mCookie;
    }

    public void finishBroadcast() {
        synchronized (this.mCallbacks) {
            int N = this.mBroadcastCount;
            if (N < 0) {
                throw new IllegalStateException("finishBroadcast() called outside of a broadcast");
            }
            Object[] active = this.mActiveBroadcast;
            if (active != null) {
                for (int i = 0; i < N; i++) {
                    active[i] = null;
                }
            }
            this.mBroadcastCount = -1;
        }
    }

    public void broadcast(Consumer<E> action) {
        int itemCount = beginBroadcast();
        for (int i = 0; i < itemCount; i++) {
            try {
                action.accept(getBroadcastItem(i));
            } finally {
                finishBroadcast();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <C> void broadcastForEachCookie(Consumer<C> action) {
        int itemCount = beginBroadcast();
        for (int i = 0; i < itemCount; i++) {
            try {
                action.accept(getBroadcastCookie(i));
            } finally {
                finishBroadcast();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <C> void broadcast(BiConsumer<E, C> action) {
        int itemCount = beginBroadcast();
        for (int i = 0; i < itemCount; i++) {
            try {
                action.accept(getBroadcastItem(i), getBroadcastCookie(i));
            } finally {
                finishBroadcast();
            }
        }
    }

    public int getRegisteredCallbackCount() {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return 0;
            }
            return this.mCallbacks.size();
        }
    }

    public E getRegisteredCallbackItem(int index) {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return null;
            }
            return (E) this.mCallbacks.valueAt(index).mCallback;
        }
    }

    public Object getRegisteredCallbackCookie(int index) {
        synchronized (this.mCallbacks) {
            if (this.mKilled) {
                return null;
            }
            return this.mCallbacks.valueAt(index).mCookie;
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        synchronized (this.mCallbacks) {
            pw.print(prefix);
            pw.print("callbacks: ");
            pw.println(this.mCallbacks.size());
            pw.print(prefix);
            pw.print("killed: ");
            pw.println(this.mKilled);
            pw.print(prefix);
            pw.print("broadcasts count: ");
            pw.println(this.mBroadcastCount);
        }
    }

    private void logExcessiveCallbacks() {
        StringBuilder sb;
        long size = this.mCallbacks.size();
        if (size >= TelecomManager.VERY_SHORT_CALL_TIME_MS) {
            if (size == TelecomManager.VERY_SHORT_CALL_TIME_MS && this.mRecentCallers == null) {
                this.mRecentCallers = new StringBuilder();
            }
            if (this.mRecentCallers != null && sb.length() < 1000) {
                this.mRecentCallers.append(Debug.getCallers(5));
                this.mRecentCallers.append('\n');
                if (this.mRecentCallers.length() >= 1000) {
                    Slog.wtf(TAG, "More than 3000 remote callbacks registered. Recent callers:\n" + this.mRecentCallers.toString());
                    this.mRecentCallers = null;
                }
            }
        }
    }
}
