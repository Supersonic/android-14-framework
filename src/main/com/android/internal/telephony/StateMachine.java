package com.android.internal.telephony;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.StateMachine;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class StateMachine {
    public static final boolean HANDLED = true;
    public static final boolean NOT_HANDLED = false;
    private String mName;
    private SmHandler mSmHandler;
    private HandlerThread mSmThread;

    protected String getLogRecString(Message message) {
        return PhoneConfigurationManager.SSSS;
    }

    protected String getWhatToString(int i) {
        return null;
    }

    protected void haltedProcessMessage(Message message) {
    }

    protected void onHalting() {
    }

    protected void onPostHandleMessage(Message message) {
    }

    protected void onPreHandleMessage(Message message) {
    }

    protected void onQuitting() {
    }

    protected boolean recordLogRec(Message message) {
        return true;
    }

    /* loaded from: classes.dex */
    public static class LogRec {
        private IState mDstState;
        private String mInfo;
        private IState mOrgState;
        private StateMachine mSm;
        private IState mState;
        private long mTime;
        private int mWhat;

        LogRec(StateMachine stateMachine, Message message, String str, IState iState, IState iState2, IState iState3) {
            update(stateMachine, message, str, iState, iState2, iState3);
        }

        public void update(StateMachine stateMachine, Message message, String str, IState iState, IState iState2, IState iState3) {
            this.mSm = stateMachine;
            this.mTime = System.currentTimeMillis();
            this.mWhat = message != null ? message.what : 0;
            this.mInfo = str;
            this.mState = iState;
            this.mOrgState = iState2;
            this.mDstState = iState3;
        }

        public long getTime() {
            return this.mTime;
        }

        public long getWhat() {
            return this.mWhat;
        }

        public String getInfo() {
            return this.mInfo;
        }

        public IState getState() {
            return this.mState;
        }

        public IState getDestState() {
            return this.mDstState;
        }

        public IState getOriginalState() {
            return this.mOrgState;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("time=");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(this.mTime);
            sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", calendar, calendar, calendar, calendar, calendar, calendar));
            sb.append(" processed=");
            IState iState = this.mState;
            sb.append(iState == null ? "<null>" : iState.getName());
            sb.append(" org=");
            IState iState2 = this.mOrgState;
            sb.append(iState2 == null ? "<null>" : iState2.getName());
            sb.append(" dest=");
            IState iState3 = this.mDstState;
            sb.append(iState3 != null ? iState3.getName() : "<null>");
            sb.append(" what=");
            StateMachine stateMachine = this.mSm;
            String whatToString = stateMachine != null ? stateMachine.getWhatToString(this.mWhat) : PhoneConfigurationManager.SSSS;
            if (TextUtils.isEmpty(whatToString)) {
                sb.append(this.mWhat);
                sb.append("(0x");
                sb.append(Integer.toHexString(this.mWhat));
                sb.append(")");
            } else {
                sb.append(whatToString);
            }
            if (!TextUtils.isEmpty(this.mInfo)) {
                sb.append(" ");
                sb.append(this.mInfo);
            }
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class LogRecords {
        private int mCount;
        private boolean mLogOnlyTransitions;
        private Vector<LogRec> mLogRecVector;
        private int mMaxSize;
        private int mOldestIndex;

        private LogRecords() {
            this.mLogRecVector = new Vector<>();
            this.mMaxSize = 20;
            this.mOldestIndex = 0;
            this.mCount = 0;
            this.mLogOnlyTransitions = false;
        }

        synchronized void setSize(int i) {
            this.mMaxSize = i;
            this.mOldestIndex = 0;
            this.mCount = 0;
            this.mLogRecVector.clear();
        }

        synchronized void setLogOnlyTransitions(boolean z) {
            this.mLogOnlyTransitions = z;
        }

        synchronized boolean logOnlyTransitions() {
            return this.mLogOnlyTransitions;
        }

        synchronized int size() {
            return this.mLogRecVector.size();
        }

        synchronized int count() {
            return this.mCount;
        }

        synchronized void cleanup() {
            this.mLogRecVector.clear();
        }

        synchronized LogRec get(int i) {
            int i2 = this.mOldestIndex + i;
            int i3 = this.mMaxSize;
            if (i2 >= i3) {
                i2 -= i3;
            }
            if (i2 >= size()) {
                return null;
            }
            return this.mLogRecVector.get(i2);
        }

        synchronized void add(StateMachine stateMachine, Message message, String str, IState iState, IState iState2, IState iState3) {
            this.mCount++;
            if (this.mLogRecVector.size() < this.mMaxSize) {
                this.mLogRecVector.add(new LogRec(stateMachine, message, str, iState, iState2, iState3));
            } else {
                LogRec logRec = this.mLogRecVector.get(this.mOldestIndex);
                int i = this.mOldestIndex + 1;
                this.mOldestIndex = i;
                if (i >= this.mMaxSize) {
                    this.mOldestIndex = 0;
                }
                logRec.update(stateMachine, message, str, iState, iState2, iState3);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SmHandler extends Handler {
        private static final Object mSmHandlerObj = new Object();
        private boolean mDbg;
        private ArrayList<Message> mDeferredMessages;
        private State mDestState;
        private HaltingState mHaltingState;
        private boolean mHasQuit;
        private State mInitialState;
        private boolean mIsConstructionCompleted;
        private LogRecords mLogRecords;
        private Message mMsg;
        private QuittingState mQuittingState;
        private StateMachine mSm;
        private HashMap<State, StateInfo> mStateInfo;
        private StateInfo[] mStateStack;
        private int mStateStackTopIndex;
        private StateInfo[] mTempStateStack;
        private int mTempStateStackCount;
        private boolean mTransitionInProgress;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class StateInfo {
            boolean active;
            StateInfo parentStateInfo;
            State state;

            private StateInfo() {
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("state=");
                sb.append(this.state.getName());
                sb.append(",active=");
                sb.append(this.active);
                sb.append(",parent=");
                StateInfo stateInfo = this.parentStateInfo;
                sb.append(stateInfo == null ? "null" : stateInfo.state.getName());
                return sb.toString();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class HaltingState extends State {
            private HaltingState() {
            }

            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                SmHandler.this.mSm.haltedProcessMessage(message);
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class QuittingState extends State {
            @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
            public boolean processMessage(Message message) {
                return false;
            }

            private QuittingState() {
            }
        }

        @Override // android.os.Handler
        public final void handleMessage(Message message) {
            State processMsg;
            int i;
            StateMachine stateMachine;
            int i2;
            int i3;
            if (this.mHasQuit) {
                return;
            }
            StateMachine stateMachine2 = this.mSm;
            if (stateMachine2 != null && (i3 = message.what) != -2 && i3 != -1) {
                stateMachine2.onPreHandleMessage(message);
            }
            if (this.mDbg) {
                StateMachine stateMachine3 = this.mSm;
                stateMachine3.log("handleMessage: E msg.what=" + message.what);
            }
            this.mMsg = message;
            boolean z = this.mIsConstructionCompleted;
            if (z || (i2 = message.what) == -1) {
                processMsg = processMsg(message);
            } else if (!z && i2 == -2 && message.obj == mSmHandlerObj) {
                this.mIsConstructionCompleted = true;
                invokeEnterMethods(0);
                processMsg = null;
            } else {
                throw new RuntimeException("StateMachine.handleMessage: The start method not called, received msg: " + message);
            }
            performTransitions(processMsg, message);
            if (this.mDbg && (stateMachine = this.mSm) != null) {
                stateMachine.log("handleMessage: X");
            }
            StateMachine stateMachine4 = this.mSm;
            if (stateMachine4 == null || (i = message.what) == -2 || i == -1) {
                return;
            }
            stateMachine4.onPostHandleMessage(message);
        }

        private void performTransitions(State state, Message message) {
            State state2 = this.mStateStack[this.mStateStackTopIndex].state;
            boolean z = this.mSm.recordLogRec(this.mMsg) && message.obj != mSmHandlerObj;
            if (this.mLogRecords.logOnlyTransitions()) {
                if (this.mDestState != null) {
                    LogRecords logRecords = this.mLogRecords;
                    StateMachine stateMachine = this.mSm;
                    Message message2 = this.mMsg;
                    logRecords.add(stateMachine, message2, stateMachine.getLogRecString(message2), state, state2, this.mDestState);
                }
            } else if (z) {
                LogRecords logRecords2 = this.mLogRecords;
                StateMachine stateMachine2 = this.mSm;
                Message message3 = this.mMsg;
                logRecords2.add(stateMachine2, message3, stateMachine2.getLogRecString(message3), state, state2, this.mDestState);
            }
            State state3 = this.mDestState;
            if (state3 != null) {
                while (true) {
                    if (this.mDbg) {
                        this.mSm.log("handleMessage: new destination call exit/enter");
                    }
                    StateInfo stateInfo = setupTempStateStackWithStatesToEnter(state3);
                    this.mTransitionInProgress = true;
                    invokeExitMethods(stateInfo);
                    invokeEnterMethods(moveTempStateStackToStateStack());
                    moveDeferredMessageAtFrontOfQueue();
                    State state4 = this.mDestState;
                    if (state3 == state4) {
                        break;
                    }
                    state3 = state4;
                }
                this.mDestState = null;
            }
            if (state3 != null) {
                if (state3 == this.mQuittingState) {
                    this.mSm.onQuitting();
                    cleanupAfterQuitting();
                } else if (state3 == this.mHaltingState) {
                    this.mSm.onHalting();
                }
            }
        }

        private final void cleanupAfterQuitting() {
            if (this.mSm.mSmThread != null) {
                getLooper().quit();
                this.mSm.mSmThread = null;
            }
            this.mSm.mSmHandler = null;
            this.mSm = null;
            this.mMsg = null;
            this.mLogRecords.cleanup();
            this.mStateStack = null;
            this.mTempStateStack = null;
            this.mStateInfo.clear();
            this.mInitialState = null;
            this.mDestState = null;
            this.mDeferredMessages.clear();
            this.mHasQuit = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void completeConstruction() {
            if (this.mDbg) {
                this.mSm.log("completeConstruction: E");
            }
            int i = 0;
            for (StateInfo stateInfo : this.mStateInfo.values()) {
                int i2 = 0;
                while (stateInfo != null) {
                    stateInfo = stateInfo.parentStateInfo;
                    i2++;
                }
                if (i < i2) {
                    i = i2;
                }
            }
            if (this.mDbg) {
                this.mSm.log("completeConstruction: maxDepth=" + i);
            }
            this.mStateStack = new StateInfo[i];
            this.mTempStateStack = new StateInfo[i];
            setupInitialStateStack();
            sendMessageAtFrontOfQueue(obtainMessage(-2, mSmHandlerObj));
            if (this.mDbg) {
                this.mSm.log("completeConstruction: X");
            }
        }

        private final State processMsg(Message message) {
            StateInfo stateInfo = this.mStateStack[this.mStateStackTopIndex];
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                stateMachine.log("processMsg: " + stateInfo.state.getName());
            }
            if (isQuit(message)) {
                transitionTo(this.mQuittingState);
            } else {
                while (true) {
                    if (stateInfo.state.processMessage(message)) {
                        break;
                    }
                    stateInfo = stateInfo.parentStateInfo;
                    if (stateInfo == null) {
                        this.mSm.unhandledMessage(message);
                        break;
                    } else if (this.mDbg) {
                        StateMachine stateMachine2 = this.mSm;
                        stateMachine2.log("processMsg: " + stateInfo.state.getName());
                    }
                }
            }
            if (stateInfo != null) {
                return stateInfo.state;
            }
            return null;
        }

        private final void invokeExitMethods(StateInfo stateInfo) {
            StateInfo stateInfo2;
            while (true) {
                int i = this.mStateStackTopIndex;
                if (i < 0 || (stateInfo2 = this.mStateStack[i]) == stateInfo) {
                    return;
                }
                State state = stateInfo2.state;
                if (this.mDbg) {
                    StateMachine stateMachine = this.mSm;
                    stateMachine.log("invokeExitMethods: " + state.getName());
                }
                state.exit();
                StateInfo[] stateInfoArr = this.mStateStack;
                int i2 = this.mStateStackTopIndex;
                stateInfoArr[i2].active = false;
                this.mStateStackTopIndex = i2 - 1;
            }
        }

        private final void invokeEnterMethods(int i) {
            int i2 = i;
            while (true) {
                int i3 = this.mStateStackTopIndex;
                if (i2 <= i3) {
                    if (i == i3) {
                        this.mTransitionInProgress = false;
                    }
                    if (this.mDbg) {
                        StateMachine stateMachine = this.mSm;
                        stateMachine.log("invokeEnterMethods: " + this.mStateStack[i2].state.getName());
                    }
                    this.mStateStack[i2].state.enter();
                    this.mStateStack[i2].active = true;
                    i2++;
                } else {
                    this.mTransitionInProgress = false;
                    return;
                }
            }
        }

        private final void moveDeferredMessageAtFrontOfQueue() {
            for (int size = this.mDeferredMessages.size() - 1; size >= 0; size--) {
                Message message = this.mDeferredMessages.get(size);
                if (this.mDbg) {
                    this.mSm.log("moveDeferredMessageAtFrontOfQueue; what=" + message.what);
                }
                sendMessageAtFrontOfQueue(message);
            }
            this.mDeferredMessages.clear();
        }

        private final int moveTempStateStackToStateStack() {
            int i = this.mStateStackTopIndex + 1;
            int i2 = i;
            for (int i3 = this.mTempStateStackCount - 1; i3 >= 0; i3--) {
                if (this.mDbg) {
                    this.mSm.log("moveTempStackToStateStack: i=" + i3 + ",j=" + i2);
                }
                this.mStateStack[i2] = this.mTempStateStack[i3];
                i2++;
            }
            this.mStateStackTopIndex = i2 - 1;
            if (this.mDbg) {
                this.mSm.log("moveTempStackToStateStack: X mStateStackTop=" + this.mStateStackTopIndex + ",startingIndex=" + i + ",Top=" + this.mStateStack[this.mStateStackTopIndex].state.getName());
            }
            return i;
        }

        private final StateInfo setupTempStateStackWithStatesToEnter(State state) {
            this.mTempStateStackCount = 0;
            StateInfo stateInfo = this.mStateInfo.get(state);
            do {
                StateInfo[] stateInfoArr = this.mTempStateStack;
                int i = this.mTempStateStackCount;
                this.mTempStateStackCount = i + 1;
                stateInfoArr[i] = stateInfo;
                stateInfo = stateInfo.parentStateInfo;
                if (stateInfo == null) {
                    break;
                }
            } while (!stateInfo.active);
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                stateMachine.log("setupTempStateStackWithStatesToEnter: X mTempStateStackCount=" + this.mTempStateStackCount + ",curStateInfo: " + stateInfo);
            }
            return stateInfo;
        }

        private final void setupInitialStateStack() {
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                stateMachine.log("setupInitialStateStack: E mInitialState=" + this.mInitialState.getName());
            }
            StateInfo stateInfo = this.mStateInfo.get(this.mInitialState);
            this.mTempStateStackCount = 0;
            while (stateInfo != null) {
                StateInfo[] stateInfoArr = this.mTempStateStack;
                int i = this.mTempStateStackCount;
                stateInfoArr[i] = stateInfo;
                stateInfo = stateInfo.parentStateInfo;
                this.mTempStateStackCount = i + 1;
            }
            this.mStateStackTopIndex = -1;
            moveTempStateStackToStateStack();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final Message getCurrentMessage() {
            return this.mMsg;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final IState getCurrentState() {
            return this.mStateStack[this.mStateStackTopIndex].state;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final StateInfo addState(State state, State state2) {
            StateInfo stateInfo;
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                StringBuilder sb = new StringBuilder();
                sb.append("addStateInternal: E state=");
                sb.append(state.getName());
                sb.append(",parent=");
                sb.append(state2 == null ? PhoneConfigurationManager.SSSS : state2.getName());
                stateMachine.log(sb.toString());
            }
            if (state2 != null) {
                stateInfo = this.mStateInfo.get(state2);
                if (stateInfo == null) {
                    stateInfo = addState(state2, null);
                }
            } else {
                stateInfo = null;
            }
            StateInfo stateInfo2 = this.mStateInfo.get(state);
            if (stateInfo2 == null) {
                stateInfo2 = new StateInfo();
                this.mStateInfo.put(state, stateInfo2);
            }
            StateInfo stateInfo3 = stateInfo2.parentStateInfo;
            if (stateInfo3 != null && stateInfo3 != stateInfo) {
                throw new RuntimeException("state already added");
            }
            stateInfo2.state = state;
            stateInfo2.parentStateInfo = stateInfo;
            stateInfo2.active = false;
            if (this.mDbg) {
                this.mSm.log("addStateInternal: X stateInfo: " + stateInfo2);
            }
            return stateInfo2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeState(State state) {
            final StateInfo stateInfo = this.mStateInfo.get(state);
            if (stateInfo == null || stateInfo.active || this.mStateInfo.values().stream().filter(new Predicate() { // from class: com.android.internal.telephony.StateMachine$SmHandler$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$removeState$0;
                    lambda$removeState$0 = StateMachine.SmHandler.lambda$removeState$0(StateMachine.SmHandler.StateInfo.this, (StateMachine.SmHandler.StateInfo) obj);
                    return lambda$removeState$0;
                }
            }).findAny().isPresent()) {
                return;
            }
            this.mStateInfo.remove(state);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$removeState$0(StateInfo stateInfo, StateInfo stateInfo2) {
            return stateInfo2.parentStateInfo == stateInfo;
        }

        private SmHandler(Looper looper, StateMachine stateMachine) {
            super(looper);
            this.mHasQuit = false;
            this.mDbg = false;
            this.mLogRecords = new LogRecords();
            this.mStateStackTopIndex = -1;
            this.mHaltingState = new HaltingState();
            this.mQuittingState = new QuittingState();
            this.mStateInfo = new HashMap<>();
            this.mTransitionInProgress = false;
            this.mDeferredMessages = new ArrayList<>();
            this.mSm = stateMachine;
            addState(this.mHaltingState, null);
            addState(this.mQuittingState, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void setInitialState(State state) {
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                stateMachine.log("setInitialState: initialState=" + state.getName());
            }
            this.mInitialState = state;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void transitionTo(IState iState) {
            if (this.mTransitionInProgress) {
                String str = this.mSm.mName;
                Log.wtf(str, "transitionTo called while transition already in progress to " + this.mDestState + ", new target state=" + iState);
            }
            this.mDestState = (State) iState;
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                stateMachine.log("transitionTo: destState=" + this.mDestState.getName());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void deferMessage(Message message) {
            if (this.mDbg) {
                StateMachine stateMachine = this.mSm;
                stateMachine.log("deferMessage: msg=" + message.what);
            }
            Message obtainMessage = obtainMessage();
            obtainMessage.copyFrom(message);
            this.mDeferredMessages.add(obtainMessage);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void quit() {
            if (this.mDbg) {
                this.mSm.log("quit:");
            }
            sendMessage(obtainMessage(-1, mSmHandlerObj));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void quitNow() {
            if (this.mDbg) {
                this.mSm.log("quitNow:");
            }
            sendMessageAtFrontOfQueue(obtainMessage(-1, mSmHandlerObj));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isQuit(Message message) {
            return message.what == -1 && message.obj == mSmHandlerObj;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isDbg() {
            return this.mDbg;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void setDbg(boolean z) {
            this.mDbg = z;
        }
    }

    private void initStateMachine(String str, Looper looper) {
        this.mName = str;
        this.mSmHandler = new SmHandler(looper, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StateMachine(String str) {
        HandlerThread handlerThread = new HandlerThread(str);
        this.mSmThread = handlerThread;
        handlerThread.start();
        initStateMachine(str, this.mSmThread.getLooper());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StateMachine(String str, Looper looper) {
        initStateMachine(str, looper);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public StateMachine(String str, Handler handler) {
        initStateMachine(str, handler.getLooper());
    }

    public final void addState(State state, State state2) {
        this.mSmHandler.addState(state, state2);
    }

    public final void addState(State state) {
        this.mSmHandler.addState(state, null);
    }

    public final void removeState(State state) {
        this.mSmHandler.removeState(state);
    }

    public final void setInitialState(State state) {
        this.mSmHandler.setInitialState(state);
    }

    public final Message getCurrentMessage() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return null;
        }
        return smHandler.getCurrentMessage();
    }

    public final IState getCurrentState() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return null;
        }
        return smHandler.getCurrentState();
    }

    public final void transitionTo(IState iState) {
        this.mSmHandler.transitionTo(iState);
    }

    public final void transitionToHaltingState() {
        SmHandler smHandler = this.mSmHandler;
        smHandler.transitionTo(smHandler.mHaltingState);
    }

    public final void deferMessage(Message message) {
        this.mSmHandler.deferMessage(message);
    }

    protected void unhandledMessage(Message message) {
        if (this.mSmHandler.mDbg) {
            loge(" - unhandledMessage: msg.what=" + message.what);
        }
    }

    public final String getName() {
        return this.mName;
    }

    public final void setLogRecSize(int i) {
        this.mSmHandler.mLogRecords.setSize(i);
    }

    public final void setLogOnlyTransitions(boolean z) {
        this.mSmHandler.mLogRecords.setLogOnlyTransitions(z);
    }

    public final int getLogRecSize() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return 0;
        }
        return smHandler.mLogRecords.size();
    }

    @VisibleForTesting
    public final int getLogRecMaxSize() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return 0;
        }
        return smHandler.mLogRecords.mMaxSize;
    }

    public final int getLogRecCount() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return 0;
        }
        return smHandler.mLogRecords.count();
    }

    public final LogRec getLogRec(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return null;
        }
        return smHandler.mLogRecords.get(i);
    }

    public final Collection<LogRec> copyLogRecs() {
        Vector vector = new Vector();
        SmHandler smHandler = this.mSmHandler;
        if (smHandler != null) {
            Iterator it = smHandler.mLogRecords.mLogRecVector.iterator();
            while (it.hasNext()) {
                vector.add((LogRec) it.next());
            }
        }
        return vector;
    }

    public void addLogRec(String str) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.mLogRecords.add(this, smHandler.getCurrentMessage(), str, smHandler.getCurrentState(), smHandler.mStateStack[smHandler.mStateStackTopIndex].state, smHandler.mDestState);
    }

    public final Handler getHandler() {
        return this.mSmHandler;
    }

    public final Message obtainMessage() {
        return Message.obtain(this.mSmHandler);
    }

    public final Message obtainMessage(int i) {
        return Message.obtain(this.mSmHandler, i);
    }

    public final Message obtainMessage(int i, Object obj) {
        return Message.obtain(this.mSmHandler, i, obj);
    }

    public final Message obtainMessage(int i, int i2) {
        return Message.obtain(this.mSmHandler, i, i2, 0);
    }

    public final Message obtainMessage(int i, int i2, int i3) {
        return Message.obtain(this.mSmHandler, i, i2, i3);
    }

    public final Message obtainMessage(int i, int i2, int i3, Object obj) {
        return Message.obtain(this.mSmHandler, i, i2, i3, obj);
    }

    public void sendMessage(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessage(obtainMessage(i));
    }

    public void sendMessage(int i, Object obj) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessage(obtainMessage(i, obj));
    }

    public void sendMessage(int i, int i2) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessage(obtainMessage(i, i2));
    }

    public void sendMessage(int i, int i2, int i3) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessage(obtainMessage(i, i2, i3));
    }

    public void sendMessage(int i, int i2, int i3, Object obj) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessage(obtainMessage(i, i2, i3, obj));
    }

    public void sendMessage(Message message) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessage(message);
    }

    public void sendMessageDelayed(int i, long j) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageDelayed(obtainMessage(i), j);
    }

    public void sendMessageDelayed(int i, Object obj, long j) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageDelayed(obtainMessage(i, obj), j);
    }

    public void sendMessageDelayed(int i, int i2, long j) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageDelayed(obtainMessage(i, i2), j);
    }

    public void sendMessageDelayed(int i, int i2, int i3, long j) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageDelayed(obtainMessage(i, i2, i3), j);
    }

    public void sendMessageDelayed(int i, int i2, int i3, Object obj, long j) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageDelayed(obtainMessage(i, i2, i3, obj), j);
    }

    public void sendMessageDelayed(Message message, long j) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageDelayed(message, j);
    }

    protected final void sendMessageAtFrontOfQueue(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageAtFrontOfQueue(obtainMessage(i));
    }

    protected final void sendMessageAtFrontOfQueue(int i, Object obj) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageAtFrontOfQueue(obtainMessage(i, obj));
    }

    protected final void sendMessageAtFrontOfQueue(int i, int i2) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageAtFrontOfQueue(obtainMessage(i, i2));
    }

    protected final void sendMessageAtFrontOfQueue(int i, int i2, int i3) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageAtFrontOfQueue(obtainMessage(i, i2, i3));
    }

    protected final void sendMessageAtFrontOfQueue(int i, int i2, int i3, Object obj) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageAtFrontOfQueue(obtainMessage(i, i2, i3, obj));
    }

    protected final void sendMessageAtFrontOfQueue(Message message) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.sendMessageAtFrontOfQueue(message);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void removeMessages(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.removeMessages(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void removeDeferredMessages(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        Iterator it = smHandler.mDeferredMessages.iterator();
        while (it.hasNext()) {
            if (((Message) it.next()).what == i) {
                it.remove();
            }
        }
    }

    protected final boolean hasDeferredMessages(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return false;
        }
        Iterator it = smHandler.mDeferredMessages.iterator();
        while (it.hasNext()) {
            if (((Message) it.next()).what == i) {
                return true;
            }
        }
        return false;
    }

    protected final boolean hasMessages(int i) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return false;
        }
        return smHandler.hasMessages(i);
    }

    protected final boolean isQuit(Message message) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return message.what == -1;
        }
        return smHandler.isQuit(message);
    }

    public final void quit() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.quit();
    }

    public final void quitNow() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.quitNow();
    }

    public boolean isDbg() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return false;
        }
        return smHandler.isDbg();
    }

    public void setDbg(boolean z) {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.setDbg(z);
    }

    public void start() {
        SmHandler smHandler = this.mSmHandler;
        if (smHandler == null) {
            return;
        }
        smHandler.completeConstruction();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(getName() + ":");
        printWriter.println(" total records=" + getLogRecCount());
        for (int i = 0; i < getLogRecSize(); i++) {
            printWriter.println(" rec[" + i + "]: " + getLogRec(i));
            printWriter.flush();
        }
        IState currentState = getCurrentState();
        StringBuilder sb = new StringBuilder();
        sb.append("curState=");
        sb.append(currentState == null ? "<QUIT>" : currentState.getName());
        printWriter.println(sb.toString());
    }

    public String toString() {
        String str;
        String str2 = "(null)";
        try {
            str = this.mName.toString();
            try {
                str2 = this.mSmHandler.getCurrentState().getName().toString();
            } catch (ArrayIndexOutOfBoundsException | NullPointerException unused) {
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException unused2) {
            str = "(null)";
        }
        return "name=" + str + " state=" + str2;
    }

    protected void logAndAddLogRec(String str) {
        addLogRec(str);
        log(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void log(String str) {
        Log.d(this.mName, str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logd(String str) {
        Log.d(this.mName, str);
    }

    protected void logv(String str) {
        Log.v(this.mName, str);
    }

    protected void logi(String str) {
        Log.i(this.mName, str);
    }

    protected void logw(String str) {
        Log.w(this.mName, str);
    }

    protected void loge(String str) {
        Log.e(this.mName, str);
    }

    protected void loge(String str, Throwable th) {
        Log.e(this.mName, str, th);
    }
}
