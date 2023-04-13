package com.android.server.policy;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public final class SingleKeyGestureDetector {
    public static final long MULTI_PRESS_TIMEOUT = ViewConfiguration.getMultiPressTimeout();
    public static long sDefaultLongPressTimeout;
    public static long sDefaultVeryLongPressTimeout;
    public int mKeyPressCounter;
    public boolean mBeganFromNonInteractive = false;
    public final ArrayList<SingleKeyRule> mRules = new ArrayList<>();
    public SingleKeyRule mActiveRule = null;
    public int mDownKeyCode = 0;
    public boolean mHandledByLongPress = false;
    public long mLastDownTime = 0;
    public final Handler mHandler = new KeyHandler();

    /* loaded from: classes2.dex */
    public static abstract class SingleKeyRule {
        public final int mKeyCode;

        public int getMaxMultiPressCount() {
            return 1;
        }

        public void onLongPress(long j) {
        }

        public void onMultiPress(long j, int i) {
        }

        public abstract void onPress(long j);

        public void onVeryLongPress(long j) {
        }

        public boolean supportLongPress() {
            return false;
        }

        public boolean supportVeryLongPress() {
            return false;
        }

        public SingleKeyRule(int i) {
            this.mKeyCode = i;
        }

        public final boolean shouldInterceptKey(int i) {
            return i == this.mKeyCode;
        }

        public long getLongPressTimeoutMs() {
            return SingleKeyGestureDetector.sDefaultLongPressTimeout;
        }

        public long getVeryLongPressTimeoutMs() {
            return SingleKeyGestureDetector.sDefaultVeryLongPressTimeout;
        }

        public String toString() {
            return "KeyCode=" + KeyEvent.keyCodeToString(this.mKeyCode) + ", LongPress=" + supportLongPress() + ", VeryLongPress=" + supportVeryLongPress() + ", MaxMultiPressCount=" + getMaxMultiPressCount();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return (obj instanceof SingleKeyRule) && this.mKeyCode == ((SingleKeyRule) obj).mKeyCode;
        }

        public int hashCode() {
            return this.mKeyCode;
        }
    }

    public static SingleKeyGestureDetector get(Context context) {
        SingleKeyGestureDetector singleKeyGestureDetector = new SingleKeyGestureDetector();
        sDefaultLongPressTimeout = context.getResources().getInteger(17694848);
        sDefaultVeryLongPressTimeout = context.getResources().getInteger(17694983);
        return singleKeyGestureDetector;
    }

    public void addRule(SingleKeyRule singleKeyRule) {
        if (this.mRules.contains(singleKeyRule)) {
            throw new IllegalArgumentException("Rule : " + singleKeyRule + " already exists.");
        }
        this.mRules.add(singleKeyRule);
    }

    public void interceptKey(KeyEvent keyEvent, boolean z) {
        if (keyEvent.getAction() == 0) {
            int i = this.mDownKeyCode;
            if (i == 0 || i != keyEvent.getKeyCode()) {
                this.mBeganFromNonInteractive = !z;
            }
            interceptKeyDown(keyEvent);
            return;
        }
        interceptKeyUp(keyEvent);
    }

    public final void interceptKeyDown(KeyEvent keyEvent) {
        SingleKeyRule singleKeyRule;
        int keyCode = keyEvent.getKeyCode();
        int i = this.mDownKeyCode;
        if (i == keyCode) {
            if (this.mActiveRule == null || (keyEvent.getFlags() & 128) == 0 || !this.mActiveRule.supportLongPress() || this.mHandledByLongPress) {
                return;
            }
            this.mHandledByLongPress = true;
            this.mHandler.removeMessages(0);
            this.mHandler.removeMessages(1);
            Message obtainMessage = this.mHandler.obtainMessage(0, keyCode, 0, this.mActiveRule);
            obtainMessage.setAsynchronous(true);
            this.mHandler.sendMessage(obtainMessage);
            return;
        }
        if (i != 0 || ((singleKeyRule = this.mActiveRule) != null && !singleKeyRule.shouldInterceptKey(keyCode))) {
            reset();
        }
        this.mDownKeyCode = keyCode;
        if (this.mActiveRule == null) {
            int size = this.mRules.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                }
                SingleKeyRule singleKeyRule2 = this.mRules.get(i2);
                if (singleKeyRule2.shouldInterceptKey(keyCode)) {
                    this.mActiveRule = singleKeyRule2;
                    break;
                }
                i2++;
            }
            this.mLastDownTime = 0L;
        }
        if (this.mActiveRule == null) {
            return;
        }
        long downTime = keyEvent.getDownTime() - this.mLastDownTime;
        this.mLastDownTime = keyEvent.getDownTime();
        if (downTime >= MULTI_PRESS_TIMEOUT) {
            this.mKeyPressCounter = 1;
        } else {
            this.mKeyPressCounter++;
        }
        if (this.mKeyPressCounter == 1) {
            if (this.mActiveRule.supportLongPress()) {
                Message obtainMessage2 = this.mHandler.obtainMessage(0, keyCode, 0, this.mActiveRule);
                obtainMessage2.setAsynchronous(true);
                this.mHandler.sendMessageDelayed(obtainMessage2, this.mActiveRule.getLongPressTimeoutMs());
            }
            if (this.mActiveRule.supportVeryLongPress()) {
                Message obtainMessage3 = this.mHandler.obtainMessage(1, keyCode, 0, this.mActiveRule);
                obtainMessage3.setAsynchronous(true);
                this.mHandler.sendMessageDelayed(obtainMessage3, this.mActiveRule.getVeryLongPressTimeoutMs());
                return;
            }
            return;
        }
        this.mHandler.removeMessages(0);
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        if (this.mActiveRule.getMaxMultiPressCount() <= 1 || this.mKeyPressCounter != this.mActiveRule.getMaxMultiPressCount()) {
            return;
        }
        Message obtainMessage4 = this.mHandler.obtainMessage(2, keyCode, this.mKeyPressCounter, this.mActiveRule);
        obtainMessage4.setAsynchronous(true);
        this.mHandler.sendMessage(obtainMessage4);
    }

    public final boolean interceptKeyUp(KeyEvent keyEvent) {
        this.mDownKeyCode = 0;
        if (this.mActiveRule == null) {
            return false;
        }
        if (!this.mHandledByLongPress) {
            long eventTime = keyEvent.getEventTime();
            if (eventTime < this.mLastDownTime + this.mActiveRule.getLongPressTimeoutMs()) {
                this.mHandler.removeMessages(0);
            } else {
                this.mHandledByLongPress = this.mActiveRule.supportLongPress();
            }
            if (eventTime < this.mLastDownTime + this.mActiveRule.getVeryLongPressTimeoutMs()) {
                this.mHandler.removeMessages(1);
            } else {
                this.mHandledByLongPress = this.mActiveRule.supportVeryLongPress();
            }
        }
        if (this.mHandledByLongPress) {
            this.mHandledByLongPress = false;
            this.mKeyPressCounter = 0;
            this.mActiveRule = null;
            return true;
        } else if (keyEvent.getKeyCode() == this.mActiveRule.mKeyCode) {
            if (this.mActiveRule.getMaxMultiPressCount() == 1) {
                Message obtainMessage = this.mHandler.obtainMessage(2, this.mActiveRule.mKeyCode, 1, this.mActiveRule);
                obtainMessage.setAsynchronous(true);
                this.mHandler.sendMessage(obtainMessage);
                this.mActiveRule = null;
                return true;
            }
            if (this.mKeyPressCounter < this.mActiveRule.getMaxMultiPressCount()) {
                Message obtainMessage2 = this.mHandler.obtainMessage(2, this.mActiveRule.mKeyCode, this.mKeyPressCounter, this.mActiveRule);
                obtainMessage2.setAsynchronous(true);
                this.mHandler.sendMessageDelayed(obtainMessage2, MULTI_PRESS_TIMEOUT);
            }
            return true;
        } else {
            reset();
            return false;
        }
    }

    public int getKeyPressCounter(int i) {
        SingleKeyRule singleKeyRule = this.mActiveRule;
        if (singleKeyRule == null || singleKeyRule.mKeyCode != i) {
            return 0;
        }
        return this.mKeyPressCounter;
    }

    public void reset() {
        if (this.mActiveRule != null) {
            if (this.mDownKeyCode != 0) {
                this.mHandler.removeMessages(0);
                this.mHandler.removeMessages(1);
            }
            if (this.mKeyPressCounter > 0) {
                this.mHandler.removeMessages(2);
                this.mKeyPressCounter = 0;
            }
            this.mActiveRule = null;
        }
        this.mHandledByLongPress = false;
        this.mDownKeyCode = 0;
    }

    public boolean isKeyIntercepted(int i) {
        SingleKeyRule singleKeyRule = this.mActiveRule;
        return singleKeyRule != null && singleKeyRule.shouldInterceptKey(i);
    }

    public boolean beganFromNonInteractive() {
        return this.mBeganFromNonInteractive;
    }

    public void dump(String str, PrintWriter printWriter) {
        printWriter.println(str + "SingleKey rules:");
        Iterator<SingleKeyRule> it = this.mRules.iterator();
        while (it.hasNext()) {
            printWriter.println(str + "  " + it.next());
        }
    }

    /* loaded from: classes2.dex */
    public class KeyHandler extends Handler {
        public KeyHandler() {
            super(Looper.getMainLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            SingleKeyRule singleKeyRule = (SingleKeyRule) message.obj;
            if (singleKeyRule == null) {
                Log.wtf("SingleKeyGesture", "No active rule.");
                return;
            }
            int i = message.arg2;
            int i2 = message.what;
            if (i2 == 0) {
                singleKeyRule.onLongPress(SingleKeyGestureDetector.this.mLastDownTime);
            } else if (i2 == 1) {
                singleKeyRule.onVeryLongPress(SingleKeyGestureDetector.this.mLastDownTime);
            } else if (i2 != 2) {
            } else {
                if (i == 1) {
                    singleKeyRule.onPress(SingleKeyGestureDetector.this.mLastDownTime);
                } else {
                    singleKeyRule.onMultiPress(SingleKeyGestureDetector.this.mLastDownTime, i);
                }
            }
        }
    }
}
