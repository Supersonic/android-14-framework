package com.android.server.incident;

import android.os.Handler;
import android.os.IBinder;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class RequestQueue {
    public final Handler mHandler;
    public boolean mStarted;
    public ArrayList<Rec> mPending = new ArrayList<>();
    public final Runnable mWorker = new Runnable() { // from class: com.android.server.incident.RequestQueue.1
        @Override // java.lang.Runnable
        public void run() {
            ArrayList arrayList;
            synchronized (RequestQueue.this.mPending) {
                if (RequestQueue.this.mPending.size() > 0) {
                    arrayList = new ArrayList(RequestQueue.this.mPending);
                    RequestQueue.this.mPending.clear();
                } else {
                    arrayList = null;
                }
            }
            if (arrayList != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    ((Rec) arrayList.get(i)).runnable.run();
                }
            }
        }
    };

    /* loaded from: classes.dex */
    public class Rec {
        public final IBinder key;
        public final Runnable runnable;
        public final boolean value;

        public Rec(IBinder iBinder, boolean z, Runnable runnable) {
            this.key = iBinder;
            this.value = z;
            this.runnable = runnable;
        }
    }

    public RequestQueue(Handler handler) {
        this.mHandler = handler;
    }

    public void start() {
        synchronized (this.mPending) {
            if (!this.mStarted) {
                if (this.mPending.size() > 0) {
                    this.mHandler.post(this.mWorker);
                }
                this.mStarted = true;
            }
        }
    }

    public void enqueue(IBinder iBinder, boolean z, Runnable runnable) {
        boolean z2;
        synchronized (this.mPending) {
            if (!z) {
                try {
                    z2 = true;
                    for (int size = this.mPending.size() - 1; size >= 0; size--) {
                        Rec rec = this.mPending.get(size);
                        if (rec.key == iBinder && rec.value) {
                            this.mPending.remove(size);
                            break;
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            z2 = false;
            if (!z2) {
                this.mPending.add(new Rec(iBinder, z, runnable));
            }
            if (this.mStarted) {
                this.mHandler.post(this.mWorker);
            }
        }
    }
}
