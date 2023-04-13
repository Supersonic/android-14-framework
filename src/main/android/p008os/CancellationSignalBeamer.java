package android.p008os;

import android.p008os.CancellationSignal;
import android.p008os.IBinder;
import android.system.SystemCleaner;
import java.lang.ref.Cleaner;
import java.lang.ref.Reference;
import java.util.HashMap;
/* renamed from: android.os.CancellationSignalBeamer */
/* loaded from: classes3.dex */
public class CancellationSignalBeamer {
    static final Cleaner sCleaner = SystemCleaner.cleaner();

    /* renamed from: android.os.CancellationSignalBeamer$Sender */
    /* loaded from: classes3.dex */
    public static abstract class Sender {

        /* renamed from: android.os.CancellationSignalBeamer$Sender$CloseableToken */
        /* loaded from: classes3.dex */
        public interface CloseableToken extends IBinder, AutoCloseable {
            @Override // java.lang.AutoCloseable
            void close();
        }

        public abstract void onCancel(IBinder iBinder);

        public abstract void onForget(IBinder iBinder);

        public CloseableToken beam(CancellationSignal cs) {
            if (cs == null) {
                return null;
            }
            return new Token(cs);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: android.os.CancellationSignalBeamer$Sender$Token */
        /* loaded from: classes3.dex */
        public static class Token extends Binder implements CloseableToken, Runnable {
            private Preparer mPreparer;
            private final Sender mSender;

            private Token(Sender sender, CancellationSignal signal) {
                this.mSender = sender;
                this.mPreparer = new Preparer(sender, signal, this);
            }

            @Override // android.p008os.CancellationSignalBeamer.Sender.CloseableToken, java.lang.AutoCloseable
            public void close() {
                Preparer preparer = this.mPreparer;
                this.mPreparer = null;
                if (preparer != null) {
                    preparer.setup();
                }
            }

            @Override // java.lang.Runnable
            public void run() {
                this.mSender.onForget(this);
            }

            /* renamed from: android.os.CancellationSignalBeamer$Sender$Token$Preparer */
            /* loaded from: classes3.dex */
            private static class Preparer implements CancellationSignal.OnCancelListener {
                private final Sender mSender;
                private final CancellationSignal mSignal;
                private final Token mToken;

                private Preparer(Sender sender, CancellationSignal signal, Token token) {
                    this.mSender = sender;
                    this.mSignal = signal;
                    this.mToken = token;
                }

                void setup() {
                    CancellationSignalBeamer.sCleaner.register(this, this.mToken);
                    this.mSignal.setOnCancelListener(this);
                }

                @Override // android.p008os.CancellationSignal.OnCancelListener
                public void onCancel() {
                    try {
                        this.mSender.onCancel(this.mToken);
                    } finally {
                        Reference.reachabilityFence(this);
                    }
                }
            }
        }
    }

    /* renamed from: android.os.CancellationSignalBeamer$Receiver */
    /* loaded from: classes3.dex */
    public static class Receiver implements IBinder.DeathRecipient {
        private final boolean mCancelOnSenderDeath;
        private final HashMap<IBinder, CancellationSignal> mTokenMap = new HashMap<>();

        public Receiver(boolean cancelOnSenderDeath) {
            this.mCancelOnSenderDeath = cancelOnSenderDeath;
        }

        public CancellationSignal unbeam(IBinder token) {
            if (token == null) {
                return null;
            }
            synchronized (this) {
                CancellationSignal cs = this.mTokenMap.get(token);
                if (cs != null) {
                    return cs;
                }
                CancellationSignal cs2 = new CancellationSignal();
                this.mTokenMap.put(token, cs2);
                try {
                    token.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    dead(token);
                }
                return cs2;
            }
        }

        public void forget(IBinder token) {
            synchronized (this) {
                if (this.mTokenMap.remove(token) != null) {
                    token.unlinkToDeath(this, 0);
                }
            }
        }

        public void cancel(IBinder token) {
            synchronized (this) {
                CancellationSignal cs = this.mTokenMap.get(token);
                if (cs != null) {
                    forget(token);
                    cs.cancel();
                }
            }
        }

        private void dead(IBinder token) {
            if (this.mCancelOnSenderDeath) {
                cancel(token);
            } else {
                forget(token);
            }
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied(IBinder who) {
            dead(who);
        }

        @Override // android.p008os.IBinder.DeathRecipient
        public void binderDied() {
            throw new RuntimeException("unreachable");
        }
    }
}
