package com.android.internal.util;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Looper;
import android.p008os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.util.StateMachine;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.function.Predicate;
/* loaded from: classes3.dex */
public class StateMachine {
    public static final boolean HANDLED = true;
    public static final boolean NOT_HANDLED = false;
    private static final int SM_INIT_CMD = -2;
    private static final int SM_QUIT_CMD = -1;
    private String mName;
    private SmHandler mSmHandler;
    private HandlerThread mSmThread;

    /* loaded from: classes3.dex */
    public static class LogRec {
        private IState mDstState;
        private String mInfo;
        private IState mOrgState;
        private StateMachine mSm;
        private IState mState;
        private long mTime;
        private int mWhat;

        LogRec(StateMachine sm, Message msg, String info, IState state, IState orgState, IState transToState) {
            update(sm, msg, info, state, orgState, transToState);
        }

        public void update(StateMachine sm, Message msg, String info, IState state, IState orgState, IState dstState) {
            this.mSm = sm;
            this.mTime = System.currentTimeMillis();
            this.mWhat = msg != null ? msg.what : 0;
            this.mInfo = info;
            this.mState = state;
            this.mOrgState = orgState;
            this.mDstState = dstState;
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
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(this.mTime);
            sb.append(String.format("%tm-%td %tH:%tM:%tS.%tL", c, c, c, c, c, c));
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
            String what = stateMachine != null ? stateMachine.getWhatToString(this.mWhat) : "";
            if (TextUtils.isEmpty(what)) {
                sb.append(this.mWhat);
                sb.append("(0x");
                sb.append(Integer.toHexString(this.mWhat));
                sb.append(NavigationBarInflaterView.KEY_CODE_END);
            } else {
                sb.append(what);
            }
            if (!TextUtils.isEmpty(this.mInfo)) {
                sb.append(" ");
                sb.append(this.mInfo);
            }
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class LogRecords {
        private static final int DEFAULT_SIZE = 20;
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

        synchronized void setSize(int maxSize) {
            this.mMaxSize = maxSize;
            this.mOldestIndex = 0;
            this.mCount = 0;
            this.mLogRecVector.clear();
        }

        synchronized void setLogOnlyTransitions(boolean enable) {
            this.mLogOnlyTransitions = enable;
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

        synchronized LogRec get(int index) {
            int nextIndex = this.mOldestIndex + index;
            int i = this.mMaxSize;
            if (nextIndex >= i) {
                nextIndex -= i;
            }
            if (nextIndex >= size()) {
                return null;
            }
            return this.mLogRecVector.get(nextIndex);
        }

        synchronized void add(StateMachine sm, Message msg, String messageInfo, IState state, IState orgState, IState transToState) {
            this.mCount++;
            if (this.mLogRecVector.size() < this.mMaxSize) {
                this.mLogRecVector.add(new LogRec(sm, msg, messageInfo, state, orgState, transToState));
            } else {
                LogRec pmi = this.mLogRecVector.get(this.mOldestIndex);
                int i = this.mOldestIndex + 1;
                this.mOldestIndex = i;
                if (i >= this.mMaxSize) {
                    this.mOldestIndex = 0;
                }
                pmi.update(sm, msg, messageInfo, state, orgState, transToState);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
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
        /* loaded from: classes3.dex */
        public class StateInfo {
            boolean active;
            StateInfo parentStateInfo;
            State state;

            private StateInfo() {
            }

            public String toString() {
                StringBuilder append = new StringBuilder().append("state=").append(this.state.getName()).append(",active=").append(this.active).append(",parent=");
                StateInfo stateInfo = this.parentStateInfo;
                return append.append(stateInfo == null ? "null" : stateInfo.state.getName()).toString();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class HaltingState extends State {
            private HaltingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                SmHandler.this.mSm.haltedProcessMessage(msg);
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public class QuittingState extends State {
            private QuittingState() {
            }

            @Override // com.android.internal.util.State, com.android.internal.util.IState
            public boolean processMessage(Message msg) {
                return false;
            }
        }

        @Override // android.p008os.Handler
        public final void handleMessage(Message msg) {
            StateMachine stateMachine;
            if (!this.mHasQuit) {
                if (this.mSm != null && msg.what != -2 && msg.what != -1) {
                    this.mSm.onPreHandleMessage(msg);
                }
                if (this.mDbg) {
                    this.mSm.log("handleMessage: E msg.what=" + msg.what);
                }
                this.mMsg = msg;
                State msgProcessedState = null;
                if (this.mIsConstructionCompleted || msg.what == -1) {
                    msgProcessedState = processMsg(msg);
                } else if (!this.mIsConstructionCompleted && this.mMsg.what == -2 && this.mMsg.obj == mSmHandlerObj) {
                    this.mIsConstructionCompleted = true;
                    invokeEnterMethods(0);
                } else {
                    throw new RuntimeException("StateMachine.handleMessage: The start method not called, received msg: " + msg);
                }
                performTransitions(msgProcessedState, msg);
                if (this.mDbg && (stateMachine = this.mSm) != null) {
                    stateMachine.log("handleMessage: X");
                }
                if (this.mSm != null && msg.what != -2 && msg.what != -1) {
                    this.mSm.onPostHandleMessage(msg);
                }
            }
        }

        private void performTransitions(State msgProcessedState, Message msg) {
            State orgState = this.mStateStack[this.mStateStackTopIndex].state;
            boolean recordLogMsg = this.mSm.recordLogRec(this.mMsg) && msg.obj != mSmHandlerObj;
            if (this.mLogRecords.logOnlyTransitions()) {
                if (this.mDestState != null) {
                    LogRecords logRecords = this.mLogRecords;
                    StateMachine stateMachine = this.mSm;
                    Message message = this.mMsg;
                    logRecords.add(stateMachine, message, stateMachine.getLogRecString(message), msgProcessedState, orgState, this.mDestState);
                }
            } else if (recordLogMsg) {
                LogRecords logRecords2 = this.mLogRecords;
                StateMachine stateMachine2 = this.mSm;
                Message message2 = this.mMsg;
                logRecords2.add(stateMachine2, message2, stateMachine2.getLogRecString(message2), msgProcessedState, orgState, this.mDestState);
            }
            State destState = this.mDestState;
            if (destState != null) {
                while (true) {
                    if (this.mDbg) {
                        this.mSm.log("handleMessage: new destination call exit/enter");
                    }
                    StateInfo commonStateInfo = setupTempStateStackWithStatesToEnter(destState);
                    this.mTransitionInProgress = true;
                    invokeExitMethods(commonStateInfo);
                    int stateStackEnteringIndex = moveTempStateStackToStateStack();
                    invokeEnterMethods(stateStackEnteringIndex);
                    moveDeferredMessageAtFrontOfQueue();
                    if (destState == this.mDestState) {
                        break;
                    }
                    destState = this.mDestState;
                }
                this.mDestState = null;
            }
            if (destState != null) {
                if (destState == this.mQuittingState) {
                    this.mSm.onQuitting();
                    cleanupAfterQuitting();
                } else if (destState == this.mHaltingState) {
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
            int maxDepth = 0;
            for (StateInfo si : this.mStateInfo.values()) {
                int depth = 0;
                StateInfo i = si;
                while (i != null) {
                    i = i.parentStateInfo;
                    depth++;
                }
                if (maxDepth < depth) {
                    maxDepth = depth;
                }
            }
            if (this.mDbg) {
                this.mSm.log("completeConstruction: maxDepth=" + maxDepth);
            }
            this.mStateStack = new StateInfo[maxDepth];
            this.mTempStateStack = new StateInfo[maxDepth];
            setupInitialStateStack();
            sendMessageAtFrontOfQueue(obtainMessage(-2, mSmHandlerObj));
            if (this.mDbg) {
                this.mSm.log("completeConstruction: X");
            }
        }

        private final State processMsg(Message msg) {
            StateInfo curStateInfo = this.mStateStack[this.mStateStackTopIndex];
            if (this.mDbg) {
                this.mSm.log("processMsg: " + curStateInfo.state.getName());
            }
            if (isQuit(msg)) {
                transitionTo(this.mQuittingState);
            } else {
                while (true) {
                    if (curStateInfo.state.processMessage(msg)) {
                        break;
                    }
                    curStateInfo = curStateInfo.parentStateInfo;
                    if (curStateInfo == null) {
                        this.mSm.unhandledMessage(msg);
                        break;
                    } else if (this.mDbg) {
                        this.mSm.log("processMsg: " + curStateInfo.state.getName());
                    }
                }
            }
            if (curStateInfo != null) {
                return curStateInfo.state;
            }
            return null;
        }

        private final void invokeExitMethods(StateInfo commonStateInfo) {
            StateInfo stateInfo;
            while (true) {
                int i = this.mStateStackTopIndex;
                if (i >= 0 && (stateInfo = this.mStateStack[i]) != commonStateInfo) {
                    State curState = stateInfo.state;
                    if (this.mDbg) {
                        this.mSm.log("invokeExitMethods: " + curState.getName());
                    }
                    curState.exit();
                    this.mStateStack[this.mStateStackTopIndex].active = false;
                    this.mStateStackTopIndex--;
                } else {
                    return;
                }
            }
        }

        private final void invokeEnterMethods(int stateStackEnteringIndex) {
            int i = stateStackEnteringIndex;
            while (true) {
                int i2 = this.mStateStackTopIndex;
                if (i <= i2) {
                    if (stateStackEnteringIndex == i2) {
                        this.mTransitionInProgress = false;
                    }
                    if (this.mDbg) {
                        this.mSm.log("invokeEnterMethods: " + this.mStateStack[i].state.getName());
                    }
                    this.mStateStack[i].state.enter();
                    this.mStateStack[i].active = true;
                    i++;
                } else {
                    this.mTransitionInProgress = false;
                    return;
                }
            }
        }

        private final void moveDeferredMessageAtFrontOfQueue() {
            for (int i = this.mDeferredMessages.size() - 1; i >= 0; i--) {
                Message curMsg = this.mDeferredMessages.get(i);
                if (this.mDbg) {
                    this.mSm.log("moveDeferredMessageAtFrontOfQueue; what=" + curMsg.what);
                }
                sendMessageAtFrontOfQueue(curMsg);
            }
            this.mDeferredMessages.clear();
        }

        private final int moveTempStateStackToStateStack() {
            int startingIndex = this.mStateStackTopIndex + 1;
            int j = startingIndex;
            for (int i = this.mTempStateStackCount - 1; i >= 0; i--) {
                if (this.mDbg) {
                    this.mSm.log("moveTempStackToStateStack: i=" + i + ",j=" + j);
                }
                this.mStateStack[j] = this.mTempStateStack[i];
                j++;
            }
            this.mStateStackTopIndex = j - 1;
            if (this.mDbg) {
                this.mSm.log("moveTempStackToStateStack: X mStateStackTop=" + this.mStateStackTopIndex + ",startingIndex=" + startingIndex + ",Top=" + this.mStateStack[this.mStateStackTopIndex].state.getName());
            }
            return startingIndex;
        }

        private final StateInfo setupTempStateStackWithStatesToEnter(State destState) {
            this.mTempStateStackCount = 0;
            StateInfo curStateInfo = this.mStateInfo.get(destState);
            do {
                StateInfo[] stateInfoArr = this.mTempStateStack;
                int i = this.mTempStateStackCount;
                this.mTempStateStackCount = i + 1;
                stateInfoArr[i] = curStateInfo;
                curStateInfo = curStateInfo.parentStateInfo;
                if (curStateInfo == null) {
                    break;
                }
            } while (!curStateInfo.active);
            if (this.mDbg) {
                this.mSm.log("setupTempStateStackWithStatesToEnter: X mTempStateStackCount=" + this.mTempStateStackCount + ",curStateInfo: " + curStateInfo);
            }
            return curStateInfo;
        }

        private final void setupInitialStateStack() {
            if (this.mDbg) {
                this.mSm.log("setupInitialStateStack: E mInitialState=" + this.mInitialState.getName());
            }
            StateInfo curStateInfo = this.mStateInfo.get(this.mInitialState);
            int i = 0;
            while (true) {
                this.mTempStateStackCount = i;
                if (curStateInfo != null) {
                    this.mTempStateStack[this.mTempStateStackCount] = curStateInfo;
                    curStateInfo = curStateInfo.parentStateInfo;
                    i = this.mTempStateStackCount + 1;
                } else {
                    this.mStateStackTopIndex = -1;
                    moveTempStateStackToStateStack();
                    return;
                }
            }
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
        public final StateInfo addState(State state, State parent) {
            if (this.mDbg) {
                this.mSm.log("addStateInternal: E state=" + state.getName() + ",parent=" + (parent == null ? "" : parent.getName()));
            }
            StateInfo parentStateInfo = null;
            if (parent != null) {
                StateInfo parentStateInfo2 = this.mStateInfo.get(parent);
                parentStateInfo = parentStateInfo2;
                if (parentStateInfo == null) {
                    parentStateInfo = addState(parent, null);
                }
            }
            StateInfo stateInfo = this.mStateInfo.get(state);
            if (stateInfo == null) {
                stateInfo = new StateInfo();
                this.mStateInfo.put(state, stateInfo);
            }
            if (stateInfo.parentStateInfo != null && stateInfo.parentStateInfo != parentStateInfo) {
                throw new RuntimeException("state already added");
            }
            stateInfo.state = state;
            stateInfo.parentStateInfo = parentStateInfo;
            stateInfo.active = false;
            if (this.mDbg) {
                this.mSm.log("addStateInternal: X stateInfo: " + stateInfo);
            }
            return stateInfo;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeState(State state) {
            final StateInfo stateInfo = this.mStateInfo.get(state);
            if (stateInfo == null || stateInfo.active) {
                return;
            }
            boolean isParent = this.mStateInfo.values().stream().filter(new Predicate() { // from class: com.android.internal.util.StateMachine$SmHandler$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return StateMachine.SmHandler.lambda$removeState$0(StateMachine.SmHandler.StateInfo.this, (StateMachine.SmHandler.StateInfo) obj);
                }
            }).findAny().isPresent();
            if (isParent) {
                return;
            }
            this.mStateInfo.remove(state);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ boolean lambda$removeState$0(StateInfo stateInfo, StateInfo si) {
            return si.parentStateInfo == stateInfo;
        }

        private SmHandler(Looper looper, StateMachine sm) {
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
            this.mSm = sm;
            addState(this.mHaltingState, null);
            addState(this.mQuittingState, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void setInitialState(State initialState) {
            if (this.mDbg) {
                this.mSm.log("setInitialState: initialState=" + initialState.getName());
            }
            this.mInitialState = initialState;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void transitionTo(IState destState) {
            if (this.mTransitionInProgress) {
                Log.wtf(this.mSm.mName, "transitionTo called while transition already in progress to " + this.mDestState + ", new target state=" + destState);
            }
            this.mDestState = (State) destState;
            if (this.mDbg) {
                this.mSm.log("transitionTo: destState=" + this.mDestState.getName());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void deferMessage(Message msg) {
            if (this.mDbg) {
                this.mSm.log("deferMessage: msg=" + msg.what);
            }
            Message newMsg = obtainMessage();
            newMsg.copyFrom(msg);
            this.mDeferredMessages.add(newMsg);
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
        public final boolean isQuit(Message msg) {
            return msg.what == -1 && msg.obj == mSmHandlerObj;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isDbg() {
            return this.mDbg;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final void setDbg(boolean dbg) {
            this.mDbg = dbg;
        }
    }

    private void initStateMachine(String name, Looper looper) {
        this.mName = name;
        this.mSmHandler = new SmHandler(looper, this);
    }

    protected StateMachine(String name) {
        HandlerThread handlerThread = new HandlerThread(name);
        this.mSmThread = handlerThread;
        handlerThread.start();
        Looper looper = this.mSmThread.getLooper();
        initStateMachine(name, looper);
    }

    protected StateMachine(String name, Looper looper) {
        initStateMachine(name, looper);
    }

    protected StateMachine(String name, Handler handler) {
        initStateMachine(name, handler.getLooper());
    }

    protected void onPreHandleMessage(Message msg) {
    }

    protected void onPostHandleMessage(Message msg) {
    }

    public final void addState(State state, State parent) {
        this.mSmHandler.addState(state, parent);
    }

    public final void addState(State state) {
        this.mSmHandler.addState(state, null);
    }

    public final void removeState(State state) {
        this.mSmHandler.removeState(state);
    }

    public final void setInitialState(State initialState) {
        this.mSmHandler.setInitialState(initialState);
    }

    public final Message getCurrentMessage() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return null;
        }
        return smh.getCurrentMessage();
    }

    public final IState getCurrentState() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return null;
        }
        return smh.getCurrentState();
    }

    public final void transitionTo(IState destState) {
        this.mSmHandler.transitionTo(destState);
    }

    public final void transitionToHaltingState() {
        SmHandler smHandler = this.mSmHandler;
        smHandler.transitionTo(smHandler.mHaltingState);
    }

    public final void deferMessage(Message msg) {
        this.mSmHandler.deferMessage(msg);
    }

    protected void unhandledMessage(Message msg) {
        if (this.mSmHandler.mDbg) {
            loge(" - unhandledMessage: msg.what=" + msg.what);
        }
    }

    protected void haltedProcessMessage(Message msg) {
    }

    protected void onHalting() {
    }

    protected void onQuitting() {
    }

    public final String getName() {
        return this.mName;
    }

    public final void setLogRecSize(int maxSize) {
        this.mSmHandler.mLogRecords.setSize(maxSize);
    }

    public final void setLogOnlyTransitions(boolean enable) {
        this.mSmHandler.mLogRecords.setLogOnlyTransitions(enable);
    }

    public final int getLogRecSize() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return 0;
        }
        return smh.mLogRecords.size();
    }

    public final int getLogRecMaxSize() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return 0;
        }
        return smh.mLogRecords.mMaxSize;
    }

    public final int getLogRecCount() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return 0;
        }
        return smh.mLogRecords.count();
    }

    public final LogRec getLogRec(int index) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return null;
        }
        return smh.mLogRecords.get(index);
    }

    public final Collection<LogRec> copyLogRecs() {
        Vector<LogRec> vlr = new Vector<>();
        SmHandler smh = this.mSmHandler;
        if (smh != null) {
            Iterator it = smh.mLogRecords.mLogRecVector.iterator();
            while (it.hasNext()) {
                LogRec lr = (LogRec) it.next();
                vlr.add(lr);
            }
        }
        return vlr;
    }

    public void addLogRec(String string) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.mLogRecords.add(this, smh.getCurrentMessage(), string, smh.getCurrentState(), smh.mStateStack[smh.mStateStackTopIndex].state, smh.mDestState);
    }

    protected boolean recordLogRec(Message msg) {
        return true;
    }

    protected String getLogRecString(Message msg) {
        return "";
    }

    protected String getWhatToString(int what) {
        return null;
    }

    public final Handler getHandler() {
        return this.mSmHandler;
    }

    public final Message obtainMessage() {
        return Message.obtain(this.mSmHandler);
    }

    public final Message obtainMessage(int what) {
        return Message.obtain(this.mSmHandler, what);
    }

    public final Message obtainMessage(int what, Object obj) {
        return Message.obtain(this.mSmHandler, what, obj);
    }

    public final Message obtainMessage(int what, int arg1) {
        return Message.obtain(this.mSmHandler, what, arg1, 0);
    }

    public final Message obtainMessage(int what, int arg1, int arg2) {
        return Message.obtain(this.mSmHandler, what, arg1, arg2);
    }

    public final Message obtainMessage(int what, int arg1, int arg2, Object obj) {
        return Message.obtain(this.mSmHandler, what, arg1, arg2, obj);
    }

    public void sendMessage(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what));
    }

    public void sendMessage(int what, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, obj));
    }

    public void sendMessage(int what, int arg1) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, arg1));
    }

    public void sendMessage(int what, int arg1, int arg2) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, arg1, arg2));
    }

    public void sendMessage(int what, int arg1, int arg2, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(obtainMessage(what, arg1, arg2, obj));
    }

    public void sendMessage(Message msg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessage(msg);
    }

    public void sendMessageDelayed(int what, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what), delayMillis);
    }

    public void sendMessageDelayed(int what, Object obj, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, obj), delayMillis);
    }

    public void sendMessageDelayed(int what, int arg1, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, arg1), delayMillis);
    }

    public void sendMessageDelayed(int what, int arg1, int arg2, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, arg1, arg2), delayMillis);
    }

    public void sendMessageDelayed(int what, int arg1, int arg2, Object obj, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(obtainMessage(what, arg1, arg2, obj), delayMillis);
    }

    public void sendMessageDelayed(Message msg, long delayMillis) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageDelayed(msg, delayMillis);
    }

    protected final void sendMessageAtFrontOfQueue(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what));
    }

    protected final void sendMessageAtFrontOfQueue(int what, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, obj));
    }

    protected final void sendMessageAtFrontOfQueue(int what, int arg1) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1));
    }

    protected final void sendMessageAtFrontOfQueue(int what, int arg1, int arg2) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1, arg2));
    }

    protected final void sendMessageAtFrontOfQueue(int what, int arg1, int arg2, Object obj) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(obtainMessage(what, arg1, arg2, obj));
    }

    protected final void sendMessageAtFrontOfQueue(Message msg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.sendMessageAtFrontOfQueue(msg);
    }

    protected final void removeMessages(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.removeMessages(what);
    }

    protected final void removeDeferredMessages(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        Iterator<Message> iterator = smh.mDeferredMessages.iterator();
        while (iterator.hasNext()) {
            Message msg = iterator.next();
            if (msg.what == what) {
                iterator.remove();
            }
        }
    }

    protected final boolean hasDeferredMessages(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return false;
        }
        Iterator<Message> iterator = smh.mDeferredMessages.iterator();
        while (iterator.hasNext()) {
            Message msg = iterator.next();
            if (msg.what == what) {
                return true;
            }
        }
        return false;
    }

    protected final boolean hasMessages(int what) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return false;
        }
        return smh.hasMessages(what);
    }

    protected final boolean isQuit(Message msg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return msg.what == -1;
        }
        return smh.isQuit(msg);
    }

    public final void quit() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.quit();
    }

    public final void quitNow() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.quitNow();
    }

    public boolean isDbg() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return false;
        }
        return smh.isDbg();
    }

    public void setDbg(boolean dbg) {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.setDbg(dbg);
    }

    public void start() {
        SmHandler smh = this.mSmHandler;
        if (smh == null) {
            return;
        }
        smh.completeConstruction();
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println(getName() + ":");
        pw.println(" total records=" + getLogRecCount());
        for (int i = 0; i < getLogRecSize(); i++) {
            pw.println(" rec[" + i + "]: " + getLogRec(i));
            pw.flush();
        }
        IState curState = getCurrentState();
        pw.println("curState=" + (curState == null ? "<QUIT>" : curState.getName()));
    }

    public String toString() {
        String name = "(null)";
        String state = "(null)";
        try {
            name = this.mName.toString();
            state = this.mSmHandler.getCurrentState().getName().toString();
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
        }
        return "name=" + name + " state=" + state;
    }

    protected void logAndAddLogRec(String s) {
        addLogRec(s);
        log(s);
    }

    protected void log(String s) {
        Log.m112d(this.mName, s);
    }

    protected void logd(String s) {
        Log.m112d(this.mName, s);
    }

    protected void logv(String s) {
        Log.m106v(this.mName, s);
    }

    protected void logi(String s) {
        Log.m108i(this.mName, s);
    }

    protected void logw(String s) {
        Log.m104w(this.mName, s);
    }

    protected void loge(String s) {
        Log.m110e(this.mName, s);
    }

    protected void loge(String s, Throwable e) {
        Log.m109e(this.mName, s, e);
    }
}
