package android.p008os;

import android.util.ArraySet;
import java.util.concurrent.LinkedBlockingQueue;
/* renamed from: android.os.TestLooperManager */
/* loaded from: classes3.dex */
public class TestLooperManager {
    private static final ArraySet<Looper> sHeldLoopers = new ArraySet<>();
    private final LinkedBlockingQueue<MessageExecution> mExecuteQueue = new LinkedBlockingQueue<>();
    private final Looper mLooper;
    private boolean mLooperBlocked;
    private final MessageQueue mQueue;
    private boolean mReleased;

    public TestLooperManager(Looper looper) {
        ArraySet<Looper> arraySet = sHeldLoopers;
        synchronized (arraySet) {
            if (arraySet.contains(looper)) {
                throw new RuntimeException("TestLooperManager already held for this looper");
            }
            arraySet.add(looper);
        }
        this.mLooper = looper;
        this.mQueue = looper.getQueue();
        new Handler(looper).post(new LooperHolder());
    }

    public MessageQueue getMessageQueue() {
        checkReleased();
        return this.mQueue;
    }

    @Deprecated
    public MessageQueue getQueue() {
        return getMessageQueue();
    }

    public Message next() {
        while (!this.mLooperBlocked) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        checkReleased();
        return this.mQueue.next();
    }

    public void release() {
        ArraySet<Looper> arraySet = sHeldLoopers;
        synchronized (arraySet) {
            arraySet.remove(this.mLooper);
        }
        checkReleased();
        this.mReleased = true;
        this.mExecuteQueue.add(new MessageExecution());
    }

    public void execute(Message message) {
        checkReleased();
        if (Looper.myLooper() == this.mLooper) {
            message.target.dispatchMessage(message);
            return;
        }
        MessageExecution execution = new MessageExecution();
        execution.f326m = message;
        synchronized (execution) {
            this.mExecuteQueue.add(execution);
            try {
                execution.wait();
            } catch (InterruptedException e) {
            }
            if (execution.response != null) {
                throw new RuntimeException(execution.response);
            }
        }
    }

    public void recycle(Message msg) {
        checkReleased();
        msg.recycleUnchecked();
    }

    public boolean hasMessages(Handler h, Object object, int what) {
        checkReleased();
        return this.mQueue.hasMessages(h, what, object);
    }

    public boolean hasMessages(Handler h, Object object, Runnable r) {
        checkReleased();
        return this.mQueue.hasMessages(h, r, object);
    }

    private void checkReleased() {
        if (this.mReleased) {
            throw new RuntimeException("release() has already be called");
        }
    }

    /* renamed from: android.os.TestLooperManager$LooperHolder */
    /* loaded from: classes3.dex */
    private class LooperHolder implements Runnable {
        private LooperHolder() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (TestLooperManager.this) {
                TestLooperManager.this.mLooperBlocked = true;
                TestLooperManager.this.notify();
            }
            while (!TestLooperManager.this.mReleased) {
                try {
                    MessageExecution take = (MessageExecution) TestLooperManager.this.mExecuteQueue.take();
                    if (take.f326m != null) {
                        processMessage(take);
                    }
                } catch (InterruptedException e) {
                }
            }
            synchronized (TestLooperManager.this) {
                TestLooperManager.this.mLooperBlocked = false;
            }
        }

        private void processMessage(MessageExecution mex) {
            synchronized (mex) {
                try {
                    mex.f326m.target.dispatchMessage(mex.f326m);
                    mex.response = null;
                    mex.notifyAll();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.TestLooperManager$MessageExecution */
    /* loaded from: classes3.dex */
    public static class MessageExecution {

        /* renamed from: m */
        private Message f326m;
        private Throwable response;

        private MessageExecution() {
        }
    }
}
