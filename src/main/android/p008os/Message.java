package android.p008os;

import android.p008os.Parcelable;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
/* renamed from: android.os.Message */
/* loaded from: classes3.dex */
public final class Message implements Parcelable {
    static final int FLAGS_TO_CLEAR_ON_COPY_FROM = 1;
    static final int FLAG_ASYNCHRONOUS = 2;
    static final int FLAG_IN_USE = 1;
    private static final int MAX_POOL_SIZE = 50;
    public static final int UID_NONE = -1;
    private static Message sPool;
    public int arg1;
    public int arg2;
    Runnable callback;
    Bundle data;
    int flags;
    Message next;
    public Object obj;
    public Messenger replyTo;
    Handler target;
    public int what;
    public long when;
    public static final Object sPoolSync = new Object();
    private static int sPoolSize = 0;
    private static boolean gCheckRecycle = true;
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() { // from class: android.os.Message.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Message createFromParcel(Parcel source) {
            Message msg = Message.obtain();
            msg.readFromParcel(source);
            return msg;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
    public int sendingUid = -1;
    public int workSourceUid = -1;

    public static Message obtain() {
        synchronized (sPoolSync) {
            Message m = sPool;
            if (m != null) {
                sPool = m.next;
                m.next = null;
                m.flags = 0;
                sPoolSize--;
                return m;
            }
            return new Message();
        }
    }

    public static Message obtain(Message orig) {
        Message m = obtain();
        m.what = orig.what;
        m.arg1 = orig.arg1;
        m.arg2 = orig.arg2;
        m.obj = orig.obj;
        m.replyTo = orig.replyTo;
        m.sendingUid = orig.sendingUid;
        m.workSourceUid = orig.workSourceUid;
        if (orig.data != null) {
            m.data = new Bundle(orig.data);
        }
        m.target = orig.target;
        m.callback = orig.callback;
        return m;
    }

    public static Message obtain(Handler h) {
        Message m = obtain();
        m.target = h;
        return m;
    }

    public static Message obtain(Handler h, Runnable callback) {
        Message m = obtain();
        m.target = h;
        m.callback = callback;
        return m;
    }

    public static Message obtain(Handler h, int what) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        return m;
    }

    public static Message obtain(Handler h, int what, Object obj) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.obj = obj;
        return m;
    }

    public static Message obtain(Handler h, int what, int arg1, int arg2) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        return m;
    }

    public static Message obtain(Handler h, int what, int arg1, int arg2, Object obj) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.obj = obj;
        return m;
    }

    public static void updateCheckRecycle(int targetSdkVersion) {
        if (targetSdkVersion < 21) {
            gCheckRecycle = false;
        }
    }

    public void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This message cannot be recycled because it is still in use.");
            }
            return;
        }
        recycleUnchecked();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void recycleUnchecked() {
        this.flags = 1;
        this.what = 0;
        this.arg1 = 0;
        this.arg2 = 0;
        this.obj = null;
        this.replyTo = null;
        this.sendingUid = -1;
        this.workSourceUid = -1;
        this.when = 0L;
        this.target = null;
        this.callback = null;
        this.data = null;
        synchronized (sPoolSync) {
            int i = sPoolSize;
            if (i < 50) {
                this.next = sPool;
                sPool = this;
                sPoolSize = i + 1;
            }
        }
    }

    public void copyFrom(Message o) {
        this.flags = o.flags & (-2);
        this.what = o.what;
        this.arg1 = o.arg1;
        this.arg2 = o.arg2;
        this.obj = o.obj;
        this.replyTo = o.replyTo;
        this.sendingUid = o.sendingUid;
        this.workSourceUid = o.workSourceUid;
        Bundle bundle = o.data;
        if (bundle != null) {
            this.data = (Bundle) bundle.clone();
        } else {
            this.data = null;
        }
    }

    public long getWhen() {
        return this.when;
    }

    public void setTarget(Handler target) {
        this.target = target;
    }

    public Handler getTarget() {
        return this.target;
    }

    public Runnable getCallback() {
        return this.callback;
    }

    public Message setCallback(Runnable r) {
        this.callback = r;
        return this;
    }

    public Bundle getData() {
        if (this.data == null) {
            this.data = new Bundle();
        }
        return this.data;
    }

    public Bundle peekData() {
        return this.data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public Message setWhat(int what) {
        this.what = what;
        return this;
    }

    public void sendToTarget() {
        this.target.sendMessage(this);
    }

    public boolean isAsynchronous() {
        return (this.flags & 2) != 0;
    }

    public void setAsynchronous(boolean async) {
        if (async) {
            this.flags |= 2;
        } else {
            this.flags &= -3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInUse() {
        return (this.flags & 1) == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void markInUse() {
        this.flags |= 1;
    }

    public String toString() {
        return toString(SystemClock.uptimeMillis());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String toString(long now) {
        StringBuilder b = new StringBuilder();
        b.append("{ when=");
        TimeUtils.formatDuration(this.when - now, b);
        if (this.target != null) {
            if (this.callback != null) {
                b.append(" callback=");
                b.append(this.callback.getClass().getName());
            } else {
                b.append(" what=");
                b.append(this.what);
            }
            if (this.arg1 != 0) {
                b.append(" arg1=");
                b.append(this.arg1);
            }
            if (this.arg2 != 0) {
                b.append(" arg2=");
                b.append(this.arg2);
            }
            if (this.obj != null) {
                b.append(" obj=");
                b.append(this.obj);
            }
            b.append(" target=");
            b.append(this.target.getClass().getName());
        } else {
            b.append(" barrier=");
            b.append(this.arg1);
        }
        b.append(" }");
        return b.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long messageToken = proto.start(fieldId);
        proto.write(1112396529665L, this.when);
        if (this.target != null) {
            Runnable runnable = this.callback;
            if (runnable != null) {
                proto.write(1138166333442L, runnable.getClass().getName());
            } else {
                proto.write(1120986464259L, this.what);
            }
            int i = this.arg1;
            if (i != 0) {
                proto.write(1120986464260L, i);
            }
            int i2 = this.arg2;
            if (i2 != 0) {
                proto.write(1120986464261L, i2);
            }
            Object obj = this.obj;
            if (obj != null) {
                proto.write(1138166333446L, obj.toString());
            }
            proto.write(1138166333447L, this.target.getClass().getName());
        } else {
            proto.write(1120986464264L, this.arg1);
        }
        proto.end(messageToken);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.callback != null) {
            throw new RuntimeException("Can't marshal callbacks across processes.");
        }
        dest.writeInt(this.what);
        dest.writeInt(this.arg1);
        dest.writeInt(this.arg2);
        Object obj = this.obj;
        if (obj != null) {
            try {
                Parcelable p = (Parcelable) obj;
                dest.writeInt(1);
                dest.writeParcelable(p, flags);
            } catch (ClassCastException e) {
                throw new RuntimeException("Can't marshal non-Parcelable objects across processes.");
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeLong(this.when);
        dest.writeBundle(this.data);
        Messenger.writeMessengerOrNullToParcel(this.replyTo, dest);
        dest.writeInt(this.sendingUid);
        dest.writeInt(this.workSourceUid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readFromParcel(Parcel source) {
        this.what = source.readInt();
        this.arg1 = source.readInt();
        this.arg2 = source.readInt();
        if (source.readInt() != 0) {
            this.obj = source.readParcelable(getClass().getClassLoader(), Object.class);
        }
        this.when = source.readLong();
        this.data = source.readBundle();
        this.replyTo = Messenger.readMessengerOrNullFromParcel(source);
        this.sendingUid = source.readInt();
        this.workSourceUid = source.readInt();
    }
}
