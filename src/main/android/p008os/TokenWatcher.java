package android.p008os;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.IBinder;
import android.util.Log;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
/* renamed from: android.os.TokenWatcher */
/* loaded from: classes3.dex */
public abstract class TokenWatcher {
    private Handler mHandler;
    private String mTag;
    private Runnable mNotificationTask = new Runnable() { // from class: android.os.TokenWatcher.1
        @Override // java.lang.Runnable
        public void run() {
            int value;
            synchronized (TokenWatcher.this.mTokens) {
                value = TokenWatcher.this.mNotificationQueue;
                TokenWatcher.this.mNotificationQueue = -1;
            }
            if (value == 1) {
                TokenWatcher.this.acquired();
            } else if (value == 0) {
                TokenWatcher.this.released();
            }
        }
    };
    private WeakHashMap<IBinder, Death> mTokens = new WeakHashMap<>();
    private int mNotificationQueue = -1;
    private volatile boolean mAcquired = false;

    public abstract void acquired();

    public abstract void released();

    public TokenWatcher(Handler h, String tag) {
        this.mHandler = h;
        this.mTag = tag != null ? tag : "TokenWatcher";
    }

    public void acquire(IBinder token, String tag) {
        synchronized (this.mTokens) {
            if (this.mTokens.containsKey(token)) {
                return;
            }
            int oldSize = this.mTokens.size();
            Death d = new Death(token, tag);
            try {
                token.linkToDeath(d, 0);
                this.mTokens.put(token, d);
                if (oldSize == 0 && !this.mAcquired) {
                    sendNotificationLocked(true);
                    this.mAcquired = true;
                }
            } catch (RemoteException e) {
            }
        }
    }

    public void cleanup(IBinder token, boolean unlink) {
        synchronized (this.mTokens) {
            Death d = this.mTokens.remove(token);
            if (unlink && d != null) {
                d.token.unlinkToDeath(d, 0);
                d.token = null;
            }
            if (this.mTokens.size() == 0 && this.mAcquired) {
                sendNotificationLocked(false);
                this.mAcquired = false;
            }
        }
    }

    public void release(IBinder token) {
        cleanup(token, true);
    }

    public boolean isAcquired() {
        boolean z;
        synchronized (this.mTokens) {
            z = this.mAcquired;
        }
        return z;
    }

    public void dump() {
        ArrayList<String> a = dumpInternal();
        Iterator<String> it = a.iterator();
        while (it.hasNext()) {
            String s = it.next();
            Log.m108i(this.mTag, s);
        }
    }

    public void dump(PrintWriter pw) {
        ArrayList<String> a = dumpInternal();
        Iterator<String> it = a.iterator();
        while (it.hasNext()) {
            String s = it.next();
            pw.println(s);
        }
    }

    private ArrayList<String> dumpInternal() {
        ArrayList<String> a = new ArrayList<>();
        synchronized (this.mTokens) {
            Set<IBinder> keys = this.mTokens.keySet();
            a.add("Token count: " + this.mTokens.size());
            int i = 0;
            for (IBinder b : keys) {
                a.add(NavigationBarInflaterView.SIZE_MOD_START + i + "] " + this.mTokens.get(b).tag + " - " + b);
                i++;
            }
        }
        return a;
    }

    private void sendNotificationLocked(boolean on) {
        int i = this.mNotificationQueue;
        if (i == -1) {
            this.mNotificationQueue = on ? 1 : 0;
            this.mHandler.post(this.mNotificationTask);
        } else if (i != on) {
            this.mNotificationQueue = -1;
            this.mHandler.removeCallbacks(this.mNotificationTask);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.TokenWatcher$Death */
    /* loaded from: classes3.dex */
    public class Death implements IBinder.DeathRecipient {
        String tag;
        IBinder token;

        Death(IBinder token, String tag) {
            this.token = token;
            this.tag = tag;
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            TokenWatcher.this.cleanup(this.token, false);
        }

        protected void finalize() throws Throwable {
            try {
                if (this.token != null) {
                    Log.m104w(TokenWatcher.this.mTag, "cleaning up leaked reference: " + this.tag);
                    TokenWatcher.this.release(this.token);
                }
            } finally {
                super.finalize();
            }
        }
    }
}
