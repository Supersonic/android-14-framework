package android.p008os;
/* renamed from: android.os.Broadcaster */
/* loaded from: classes3.dex */
public class Broadcaster {
    private Registration mReg;

    public void request(int senderWhat, Handler target, int targetWhat) {
        int n;
        synchronized (this) {
            Registration start = this.mReg;
            if (start == null) {
                Registration r = new Registration();
                r.senderWhat = senderWhat;
                r.targets = new Handler[1];
                r.targetWhats = new int[1];
                r.targets[0] = target;
                r.targetWhats[0] = targetWhat;
                this.mReg = r;
                r.next = r;
                r.prev = r;
            } else {
                Registration r2 = start;
                while (r2.senderWhat < senderWhat && (r2 = r2.next) != start) {
                }
                if (r2.senderWhat != senderWhat) {
                    Registration reg = new Registration();
                    reg.senderWhat = senderWhat;
                    reg.targets = new Handler[1];
                    reg.targetWhats = new int[1];
                    reg.next = r2;
                    reg.prev = r2.prev;
                    r2.prev.next = reg;
                    r2.prev = reg;
                    if (r2 == this.mReg && r2.senderWhat > reg.senderWhat) {
                        this.mReg = reg;
                    }
                    r2 = reg;
                    n = 0;
                } else {
                    n = r2.targets.length;
                    Handler[] oldTargets = r2.targets;
                    int[] oldWhats = r2.targetWhats;
                    for (int i = 0; i < n; i++) {
                        if (oldTargets[i] == target && oldWhats[i] == targetWhat) {
                            return;
                        }
                    }
                    int i2 = n + 1;
                    r2.targets = new Handler[i2];
                    System.arraycopy(oldTargets, 0, r2.targets, 0, n);
                    r2.targetWhats = new int[n + 1];
                    System.arraycopy(oldWhats, 0, r2.targetWhats, 0, n);
                }
                r2.targets[n] = target;
                r2.targetWhats[n] = targetWhat;
            }
        }
    }

    public void cancelRequest(int senderWhat, Handler target, int targetWhat) {
        synchronized (this) {
            Registration start = this.mReg;
            Registration r = start;
            if (r == null) {
                return;
            }
            while (r.senderWhat < senderWhat && (r = r.next) != start) {
            }
            if (r.senderWhat == senderWhat) {
                Handler[] targets = r.targets;
                int[] whats = r.targetWhats;
                int oldLen = targets.length;
                int i = 0;
                while (true) {
                    if (i >= oldLen) {
                        break;
                    } else if (targets[i] != target || whats[i] != targetWhat) {
                        i++;
                    } else {
                        r.targets = new Handler[oldLen - 1];
                        r.targetWhats = new int[oldLen - 1];
                        if (i > 0) {
                            System.arraycopy(targets, 0, r.targets, 0, i);
                            System.arraycopy(whats, 0, r.targetWhats, 0, i);
                        }
                        int remainingLen = (oldLen - i) - 1;
                        if (remainingLen != 0) {
                            System.arraycopy(targets, i + 1, r.targets, i, remainingLen);
                            System.arraycopy(whats, i + 1, r.targetWhats, i, remainingLen);
                        }
                    }
                }
            }
        }
    }

    public void dumpRegistrations() {
        synchronized (this) {
            Registration start = this.mReg;
            System.out.println("Broadcaster " + this + " {");
            if (start != null) {
                Registration r = start;
                do {
                    System.out.println("    senderWhat=" + r.senderWhat);
                    int n = r.targets.length;
                    for (int i = 0; i < n; i++) {
                        System.out.println("        [" + r.targetWhats[i] + "] " + r.targets[i]);
                    }
                    r = r.next;
                } while (r != start);
                System.out.println("}");
            } else {
                System.out.println("}");
            }
        }
    }

    public void broadcast(Message msg) {
        synchronized (this) {
            if (this.mReg == null) {
                return;
            }
            int senderWhat = msg.what;
            Registration start = this.mReg;
            Registration r = start;
            while (r.senderWhat < senderWhat && (r = r.next) != start) {
            }
            if (r.senderWhat == senderWhat) {
                Handler[] targets = r.targets;
                int[] whats = r.targetWhats;
                int n = targets.length;
                for (int i = 0; i < n; i++) {
                    Handler target = targets[i];
                    Message m = Message.obtain();
                    m.copyFrom(msg);
                    m.what = whats[i];
                    target.sendMessage(m);
                }
            }
        }
    }

    /* renamed from: android.os.Broadcaster$Registration */
    /* loaded from: classes3.dex */
    private class Registration {
        Registration next;
        Registration prev;
        int senderWhat;
        int[] targetWhats;
        Handler[] targets;

        private Registration() {
        }
    }
}
