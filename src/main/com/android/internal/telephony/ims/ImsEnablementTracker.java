package com.android.internal.telephony.ims;

import android.content.ComponentName;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.telephony.ims.aidl.IImsServiceController;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.State;
import com.android.internal.telephony.StateMachine;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class ImsEnablementTracker {
    @VisibleForTesting
    protected static final int COMMAND_DISABLING_DONE = 5;
    @VisibleForTesting
    protected static final int COMMAND_ENABLING_DONE = 4;
    @VisibleForTesting
    protected static final int COMMAND_RESETTING_DONE = 6;
    private static final Map<Integer, String> EVENT_DESCRIPTION;
    @VisibleForTesting
    protected static final int STATE_IMS_DEFAULT = 1;
    @VisibleForTesting
    protected static final int STATE_IMS_DISABLED = 4;
    @VisibleForTesting
    protected static final int STATE_IMS_DISABLING = 3;
    @VisibleForTesting
    protected static final int STATE_IMS_DISCONNECTED = 0;
    @VisibleForTesting
    protected static final int STATE_IMS_ENABLED = 2;
    @VisibleForTesting
    protected static final int STATE_IMS_ENABLING = 5;
    @VisibleForTesting
    protected static final int STATE_IMS_RESETTING = 6;
    private final ComponentName mComponentName;
    private IImsServiceController mIImsServiceController;
    private long mLastImsOperationTimeMs;
    protected final Object mLock;
    private final Looper mLooper;
    private final int mState;
    private final SparseArray<ImsEnablementTrackerStateMachine> mStateMachines;

    static {
        HashMap hashMap = new HashMap();
        EVENT_DESCRIPTION = hashMap;
        hashMap.put(0, "COMMAND_NONE_MSG");
        hashMap.put(1, "COMMAND_ENABLE_MSG");
        hashMap.put(2, "COMMAND_DISABLE_MSG");
        hashMap.put(3, "COMMAND_RESET_MSG");
        hashMap.put(4, "COMMAND_ENABLING_DONE");
        hashMap.put(5, "COMMAND_DISABLING_DONE");
        hashMap.put(6, "COMMAND_RESETTING_DONE");
        hashMap.put(7, "COMMAND_CONNECTED_MSG");
        hashMap.put(8, "COMMAND_DISCONNECTED_MSG");
        hashMap.put(9, "COMMAND_INVALID_SUBID_MSG");
    }

    /* loaded from: classes.dex */
    class ImsEnablementTrackerStateMachine extends StateMachine {
        @VisibleForTesting
        public final Default mDefault;
        @VisibleForTesting
        public final Disabled mDisabled;
        @VisibleForTesting
        public final Disabling mDisabling;
        @VisibleForTesting
        public final Disconnected mDisconnected;
        @VisibleForTesting
        public final Enabled mEnabled;
        @VisibleForTesting
        public final Enabling mEnabling;
        private final int mPhoneId;
        @VisibleForTesting
        public final Resetting mResetting;
        @VisibleForTesting
        public int mSlotId;
        @VisibleForTesting
        public int mSubId;

        ImsEnablementTrackerStateMachine(String str, Looper looper, int i, int i2) {
            super(str, looper);
            this.mPhoneId = i2;
            Default r4 = new Default();
            this.mDefault = r4;
            Enabled enabled = new Enabled();
            this.mEnabled = enabled;
            Disabling disabling = new Disabling();
            this.mDisabling = disabling;
            Disabled disabled = new Disabled();
            this.mDisabled = disabled;
            Enabling enabling = new Enabling();
            this.mEnabling = enabling;
            Resetting resetting = new Resetting();
            this.mResetting = resetting;
            Disconnected disconnected = new Disconnected();
            this.mDisconnected = disconnected;
            addState(r4);
            addState(enabled);
            addState(disabling);
            addState(disabled);
            addState(enabling);
            addState(resetting);
            addState(disconnected);
            setInitialState(getState(i));
        }

        public void clearAllMessage() {
            Log.d("ImsEnablementTracker", "clearAllMessage");
            removeMessages(1);
            removeMessages(2);
            removeMessages(3);
            removeMessages(4);
            removeMessages(5);
            removeMessages(6);
        }

        public void serviceBinderConnected() {
            clearAllMessage();
            sendMessage(7);
        }

        public void serviceBinderDisconnected() {
            clearAllMessage();
            sendMessage(8);
        }

        @VisibleForTesting
        public boolean isState(int i) {
            return i == this.mDefault.mStateNo ? getCurrentState() == this.mDefault : i == this.mEnabled.mStateNo ? getCurrentState() == this.mEnabled : i == this.mDisabling.mStateNo ? getCurrentState() == this.mDisabling : i == this.mDisabled.mStateNo ? getCurrentState() == this.mDisabled : i == this.mEnabling.mStateNo ? getCurrentState() == this.mEnabling : i == this.mResetting.mStateNo && getCurrentState() == this.mResetting;
        }

        private State getState(int i) {
            switch (i) {
                case 1:
                    return this.mDefault;
                case 2:
                    return this.mEnabled;
                case 3:
                    return this.mDisabling;
                case 4:
                    return this.mDisabled;
                case 5:
                    return this.mEnabling;
                case 6:
                    return this.mResetting;
                default:
                    return this.mDisconnected;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Default extends State {
            public int mStateNo = 1;

            Default() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Default state:enter");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Default state:exit");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Default state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                int i = message.what;
                if (i == 1) {
                    ImsEnablementTracker.this.sendEnableIms(message.arg1, message.arg2);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mEnabled);
                    return true;
                } else if (i == 2) {
                    ImsEnablementTracker.this.sendDisableIms(message.arg1, message.arg2);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mDisabled);
                    return true;
                } else if (i != 8) {
                    return false;
                } else {
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine3 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine3.transitionTo(imsEnablementTrackerStateMachine3.mDisconnected);
                    return true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Enabled extends State {
            public int mStateNo = 2;

            Enabled() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Enabled state:enter");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Enabled state:exit");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Enabled state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.mSlotId = message.arg1;
                imsEnablementTrackerStateMachine.mSubId = message.arg2;
                int i = message.what;
                if (i == 2) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mDisabling);
                    return true;
                } else if (i == 3) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mResetting);
                    return true;
                } else if (i == 8) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mDisconnected);
                    return true;
                } else if (i != 9) {
                    return false;
                } else {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mDefault);
                    return true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Disabling extends State {
            public int mStateNo = 3;

            Disabling() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Disabling state:enter");
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.sendMessageDelayed(5, imsEnablementTrackerStateMachine.mSlotId, imsEnablementTrackerStateMachine.mSubId, ImsEnablementTracker.this.getRemainThrottleTime());
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Disabling state:exit");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Disabling state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.mSlotId = message.arg1;
                imsEnablementTrackerStateMachine.mSubId = message.arg2;
                int i = message.what;
                if (i == 1) {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mEnabled);
                    return true;
                } else if (i == 3) {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine3 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine3.transitionTo(imsEnablementTrackerStateMachine3.mResetting);
                    return true;
                } else if (i == 5) {
                    imsEnablementTrackerStateMachine.removeMessages(2);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine4 = ImsEnablementTrackerStateMachine.this;
                    ImsEnablementTracker.this.sendDisableIms(imsEnablementTrackerStateMachine4.mSlotId, imsEnablementTrackerStateMachine4.mSubId);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine5 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine5.transitionTo(imsEnablementTrackerStateMachine5.mDisabled);
                    return true;
                } else if (i == 8) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mDisconnected);
                    return true;
                } else if (i != 9) {
                    return false;
                } else {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine6 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine6.transitionTo(imsEnablementTrackerStateMachine6.mDefault);
                    return true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Disabled extends State {
            public int mStateNo = 4;

            Disabled() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Disabled state:enter");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Disabled state:exit");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Disabled state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.mSlotId = message.arg1;
                imsEnablementTrackerStateMachine.mSubId = message.arg2;
                int i = message.what;
                if (i == 1) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mEnabling);
                    return true;
                } else if (i == 8) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mDisconnected);
                    return true;
                } else if (i != 9) {
                    return false;
                } else {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mDefault);
                    return true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Enabling extends State {
            public int mStateNo = 5;

            Enabling() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Enabling state:enter");
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.sendMessageDelayed(4, imsEnablementTrackerStateMachine.mSlotId, imsEnablementTrackerStateMachine.mSubId, ImsEnablementTracker.this.getRemainThrottleTime());
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Enabling state:exit");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Enabling state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.mSlotId = message.arg1;
                imsEnablementTrackerStateMachine.mSubId = message.arg2;
                int i = message.what;
                if (i == 2) {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mDisabled);
                    return true;
                } else if (i == 4) {
                    imsEnablementTrackerStateMachine.removeMessages(1);
                    ImsEnablementTracker.this.sendEnableIms(message.arg1, message.arg2);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine3 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine3.transitionTo(imsEnablementTrackerStateMachine3.mEnabled);
                    return true;
                } else if (i == 8) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mDisconnected);
                    return true;
                } else if (i != 9) {
                    return false;
                } else {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine4 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine4.transitionTo(imsEnablementTrackerStateMachine4.mDefault);
                    return true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Resetting extends State {
            public int mStateNo = 6;

            Resetting() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Resetting state:enter");
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.sendMessageDelayed(6, imsEnablementTrackerStateMachine.mSlotId, imsEnablementTrackerStateMachine.mSubId, ImsEnablementTracker.this.getRemainThrottleTime());
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Resetting state:exit");
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Resetting state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                imsEnablementTrackerStateMachine.mSlotId = message.arg1;
                imsEnablementTrackerStateMachine.mSubId = message.arg2;
                int i = message.what;
                if (i == 2) {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mDisabling);
                    return true;
                } else if (i == 6) {
                    imsEnablementTrackerStateMachine.removeMessages(3);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine3 = ImsEnablementTrackerStateMachine.this;
                    ImsEnablementTracker.this.sendDisableIms(imsEnablementTrackerStateMachine3.mSlotId, imsEnablementTrackerStateMachine3.mSubId);
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine4 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine4.transitionTo(imsEnablementTrackerStateMachine4.mEnabling);
                    return true;
                } else if (i == 8) {
                    imsEnablementTrackerStateMachine.transitionTo(imsEnablementTrackerStateMachine.mDisconnected);
                    return true;
                } else if (i != 9) {
                    return false;
                } else {
                    imsEnablementTrackerStateMachine.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine5 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine5.transitionTo(imsEnablementTrackerStateMachine5.mDefault);
                    return true;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class Disconnected extends State {
            public int mStateNo = 0;
            private int mLastMsg = 0;

            Disconnected() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void enter() {
                Log.d("ImsEnablementTracker", "Disconnected state:enter");
                ImsEnablementTrackerStateMachine.this.clearAllMessage();
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public void exit() {
                Log.d("ImsEnablementTracker", "Disconnected state:exit");
                this.mLastMsg = 0;
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                Log.d("ImsEnablementTracker", "[" + ImsEnablementTrackerStateMachine.this.mPhoneId + "]Disconnected state:processMessage. msg.what=" + ((String) ImsEnablementTracker.EVENT_DESCRIPTION.get(Integer.valueOf(message.what))) + ",component:" + ImsEnablementTracker.this.mComponentName);
                int i = message.what;
                if (i == 1 || i == 2 || i == 3) {
                    this.mLastMsg = i;
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine.mSlotId = message.arg1;
                    imsEnablementTrackerStateMachine.mSubId = message.arg2;
                    return true;
                } else if (i != 7) {
                    return false;
                } else {
                    ImsEnablementTrackerStateMachine.this.clearAllMessage();
                    ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine2 = ImsEnablementTrackerStateMachine.this;
                    imsEnablementTrackerStateMachine2.transitionTo(imsEnablementTrackerStateMachine2.mDefault);
                    int i2 = this.mLastMsg;
                    if (i2 != 0) {
                        ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine3 = ImsEnablementTrackerStateMachine.this;
                        imsEnablementTrackerStateMachine3.sendMessageDelayed(i2, imsEnablementTrackerStateMachine3.mSlotId, imsEnablementTrackerStateMachine3.mSubId, 0L);
                    }
                    return true;
                }
            }
        }
    }

    public ImsEnablementTracker(Looper looper, ComponentName componentName) {
        this.mLock = new Object();
        this.mLastImsOperationTimeMs = 0L;
        this.mIImsServiceController = null;
        this.mStateMachines = new SparseArray<>();
        this.mLooper = looper;
        this.mState = 0;
        this.mComponentName = componentName;
    }

    @VisibleForTesting
    public ImsEnablementTracker(Looper looper, IImsServiceController iImsServiceController, int i, int i2) {
        this.mLock = new Object();
        this.mLastImsOperationTimeMs = 0L;
        this.mIImsServiceController = iImsServiceController;
        this.mStateMachines = new SparseArray<>();
        this.mLooper = looper;
        this.mState = i;
        this.mComponentName = null;
        for (int i3 = 0; i3 < i2; i3++) {
            this.mStateMachines.put(i3, new ImsEnablementTrackerStateMachine("ImsEnablementTracker", this.mLooper, this.mState, i3));
        }
    }

    public void setNumOfSlots(int i) {
        int size = this.mStateMachines.size();
        Log.d("ImsEnablementTracker", "set the slots: old[" + size + "], new[" + i + "],component:" + this.mComponentName);
        if (i == size) {
            return;
        }
        if (size < i) {
            while (size < i) {
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = new ImsEnablementTrackerStateMachine("ImsEnablementTracker", this.mLooper, this.mState, size);
                imsEnablementTrackerStateMachine.start();
                this.mStateMachines.put(size, imsEnablementTrackerStateMachine);
                size++;
            }
        } else if (size <= i) {
        } else {
            while (true) {
                size--;
                if (size <= i - 1) {
                    return;
                }
                this.mStateMachines.remove(size);
                this.mStateMachines.get(size).quitNow();
            }
        }
    }

    @VisibleForTesting
    public void startStateMachineAsConnected(int i) {
        this.mStateMachines.get(i).start();
        this.mStateMachines.get(i).sendMessage(7);
    }

    @VisibleForTesting
    public Handler getHandler(int i) {
        return this.mStateMachines.get(i).getHandler();
    }

    @VisibleForTesting
    public boolean isState(int i, int i2) {
        return this.mStateMachines.get(i).isState(i2);
    }

    public void subIdChangedToInvalid(int i) {
        Log.d("ImsEnablementTracker", "[" + i + "] subId changed to invalid, component:" + this.mComponentName);
        ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = this.mStateMachines.get(i);
        if (imsEnablementTrackerStateMachine != null) {
            imsEnablementTrackerStateMachine.sendMessage(9, i);
        } else {
            Log.w("ImsEnablementTracker", "There is no state machine associated with this slotId.");
        }
    }

    public void enableIms(int i, int i2) {
        Log.d("ImsEnablementTracker", "[" + i + "][" + i2 + "]enableIms, component:" + this.mComponentName);
        ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = this.mStateMachines.get(i);
        if (imsEnablementTrackerStateMachine != null) {
            imsEnablementTrackerStateMachine.sendMessage(1, i, i2);
        } else {
            Log.w("ImsEnablementTracker", "There is no state machine associated with this slotId.");
        }
    }

    public void disableIms(int i, int i2) {
        Log.d("ImsEnablementTracker", "[" + i + "][" + i2 + "]disableIms, component:" + this.mComponentName);
        ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = this.mStateMachines.get(i);
        if (imsEnablementTrackerStateMachine != null) {
            imsEnablementTrackerStateMachine.sendMessage(2, i, i2);
        } else {
            Log.w("ImsEnablementTracker", "There is no state machine associated with this slotId.");
        }
    }

    public void resetIms(int i, int i2) {
        Log.d("ImsEnablementTracker", "[" + i + "][" + i2 + "]resetIms, component:" + this.mComponentName);
        ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = this.mStateMachines.get(i);
        if (imsEnablementTrackerStateMachine != null) {
            imsEnablementTrackerStateMachine.sendMessage(3, i, i2);
        } else {
            Log.w("ImsEnablementTracker", "There is no state machine associated with this slotId.");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setServiceController(IBinder iBinder) {
        synchronized (this.mLock) {
            this.mIImsServiceController = IImsServiceController.Stub.asInterface(iBinder);
            Log.d("ImsEnablementTracker", "setServiceController with Binder:" + this.mIImsServiceController + ", component:" + this.mComponentName);
            for (int i = 0; i < this.mStateMachines.size(); i++) {
                ImsEnablementTrackerStateMachine imsEnablementTrackerStateMachine = this.mStateMachines.get(i);
                if (imsEnablementTrackerStateMachine == null) {
                    Log.w("ImsEnablementTracker", "There is no state machine associated withthe slotId[" + i + "]");
                } else if (isServiceControllerAvailable()) {
                    imsEnablementTrackerStateMachine.serviceBinderConnected();
                } else {
                    imsEnablementTrackerStateMachine.serviceBinderDisconnected();
                }
            }
        }
    }

    @VisibleForTesting
    protected long getLastOperationTimeMillis() {
        return this.mLastImsOperationTimeMs;
    }

    @VisibleForTesting
    public long getRemainThrottleTime() {
        long currentTimeMillis = 1000 - (System.currentTimeMillis() - getLastOperationTimeMillis());
        Log.d("ImsEnablementTracker", "getRemainThrottleTime:" + currentTimeMillis);
        if (currentTimeMillis < 0) {
            return 0L;
        }
        return currentTimeMillis;
    }

    private boolean isServiceControllerAvailable() {
        if (this.mIImsServiceController != null) {
            return true;
        }
        Log.d("ImsEnablementTracker", "isServiceControllerAvailable : binder is not alive");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendEnableIms(int i, int i2) {
        try {
            synchronized (this.mLock) {
                if (isServiceControllerAvailable()) {
                    Log.d("ImsEnablementTracker", "[" + i + "][" + i2 + "]sendEnableIms,componentName[" + this.mComponentName + "]");
                    this.mIImsServiceController.enableIms(i, i2);
                    this.mLastImsOperationTimeMs = System.currentTimeMillis();
                }
            }
        } catch (RemoteException e) {
            Log.w("ImsEnablementTracker", "Couldn't enable IMS: " + e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendDisableIms(int i, int i2) {
        try {
            synchronized (this.mLock) {
                if (isServiceControllerAvailable()) {
                    Log.d("ImsEnablementTracker", "[" + i + "][" + i2 + "]sendDisableIms,componentName[" + this.mComponentName + "]");
                    this.mIImsServiceController.disableIms(i, i2);
                    this.mLastImsOperationTimeMs = System.currentTimeMillis();
                }
            }
        } catch (RemoteException e) {
            Log.w("ImsEnablementTracker", "Couldn't disable IMS: " + e.getMessage());
        }
    }
}
