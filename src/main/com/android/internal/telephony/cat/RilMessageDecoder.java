package com.android.internal.telephony.cat;

import android.compat.annotation.UnsupportedAppUsage;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.State;
import com.android.internal.telephony.StateMachine;
import com.android.internal.telephony.uicc.IccFileHandler;
import com.android.internal.telephony.uicc.IccUtils;
/* loaded from: classes.dex */
public class RilMessageDecoder extends StateMachine {
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private static RilMessageDecoder[] mInstance;
    private static int mSimCount;
    private Handler mCaller;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private CommandParamsFactory mCmdParamsFactory;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    private RilMessage mCurrentRilMessage;
    private StateCmdParamsReady mStateCmdParamsReady;
    @UnsupportedAppUsage
    private StateStart mStateStart;

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public static synchronized RilMessageDecoder getInstance(Handler handler, IccFileHandler iccFileHandler, Context context, int i) {
        synchronized (RilMessageDecoder.class) {
            if (mInstance == null) {
                int supportedModemCount = TelephonyManager.getDefault().getSupportedModemCount();
                mSimCount = supportedModemCount;
                mInstance = new RilMessageDecoder[supportedModemCount];
                for (int i2 = 0; i2 < mSimCount; i2++) {
                    mInstance[i2] = null;
                }
            }
            if (i != -1 && i < mSimCount) {
                RilMessageDecoder[] rilMessageDecoderArr = mInstance;
                if (rilMessageDecoderArr[i] == null) {
                    rilMessageDecoderArr[i] = new RilMessageDecoder(handler, iccFileHandler, context);
                }
                return mInstance[i];
            }
            CatLog.m4d("RilMessageDecoder", "invaild slot id: " + i);
            return null;
        }
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void sendStartDecodingMessageParams(RilMessage rilMessage) {
        Message obtainMessage = obtainMessage(1);
        obtainMessage.obj = rilMessage;
        sendMessage(obtainMessage);
    }

    public void sendMsgParamsDecoded(ResultCode resultCode, CommandParams commandParams) {
        Message obtainMessage = obtainMessage(2);
        obtainMessage.arg1 = resultCode.value();
        obtainMessage.obj = commandParams;
        sendMessage(obtainMessage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void sendCmdForExecution(RilMessage rilMessage) {
        this.mCaller.obtainMessage(10, new RilMessage(rilMessage)).sendToTarget();
    }

    private RilMessageDecoder(Handler handler, IccFileHandler iccFileHandler, Context context) {
        super("RilMessageDecoder");
        this.mCmdParamsFactory = null;
        this.mCurrentRilMessage = null;
        this.mCaller = null;
        this.mStateStart = new StateStart();
        this.mStateCmdParamsReady = new StateCmdParamsReady();
        addState(this.mStateStart);
        addState(this.mStateCmdParamsReady);
        setInitialState(this.mStateStart);
        this.mCaller = handler;
        this.mCmdParamsFactory = CommandParamsFactory.getInstance(this, iccFileHandler, context);
    }

    private RilMessageDecoder() {
        super("RilMessageDecoder");
        this.mCmdParamsFactory = null;
        this.mCurrentRilMessage = null;
        this.mCaller = null;
        this.mStateStart = new StateStart();
        this.mStateCmdParamsReady = new StateCmdParamsReady();
    }

    /* loaded from: classes.dex */
    private class StateStart extends State {
        private StateStart() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (message.what == 1) {
                if (RilMessageDecoder.this.decodeMessageParams((RilMessage) message.obj)) {
                    RilMessageDecoder rilMessageDecoder = RilMessageDecoder.this;
                    rilMessageDecoder.transitionTo(rilMessageDecoder.mStateCmdParamsReady);
                }
            } else {
                CatLog.m5d(this, "StateStart unexpected expecting START=1 got " + message.what);
            }
            return true;
        }
    }

    /* loaded from: classes.dex */
    private class StateCmdParamsReady extends State {
        private StateCmdParamsReady() {
        }

        @Override // com.android.internal.telephony.State, com.android.internal.telephony.IState
        public boolean processMessage(Message message) {
            if (message.what == 2) {
                RilMessageDecoder.this.mCurrentRilMessage.mResCode = ResultCode.fromInt(message.arg1);
                RilMessageDecoder.this.mCurrentRilMessage.mData = message.obj;
                RilMessageDecoder rilMessageDecoder = RilMessageDecoder.this;
                rilMessageDecoder.sendCmdForExecution(rilMessageDecoder.mCurrentRilMessage);
                RilMessageDecoder rilMessageDecoder2 = RilMessageDecoder.this;
                rilMessageDecoder2.transitionTo(rilMessageDecoder2.mStateStart);
                return true;
            }
            CatLog.m5d(this, "StateCmdParamsReady expecting CMD_PARAMS_READY=2 got " + message.what);
            RilMessageDecoder.this.deferMessage(message);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:11:0x0012, code lost:
        if (r0 != 5) goto L11;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean decodeMessageParams(RilMessage rilMessage) {
        this.mCurrentRilMessage = rilMessage;
        int i = rilMessage.mId;
        if (i != 1) {
            if (i != 2 && i != 3) {
                if (i != 4) {
                }
            }
            try {
                try {
                    this.mCmdParamsFactory.make(BerTlv.decode(IccUtils.hexStringToBytes((String) rilMessage.mData)));
                    return true;
                } catch (ResultException e) {
                    CatLog.m5d(this, "decodeMessageParams: caught ResultException e=" + e);
                    this.mCurrentRilMessage.mResCode = e.result();
                    sendCmdForExecution(this.mCurrentRilMessage);
                }
            } catch (Exception unused) {
                CatLog.m5d(this, "decodeMessageParams dropping zombie messages");
            }
        }
        rilMessage.mResCode = ResultCode.OK;
        sendCmdForExecution(rilMessage);
        return false;
    }

    public void dispose() {
        quitNow();
        this.mStateStart = null;
        this.mStateCmdParamsReady = null;
        this.mCmdParamsFactory.dispose();
        this.mCmdParamsFactory = null;
        this.mCurrentRilMessage = null;
        this.mCaller = null;
        mInstance = null;
    }
}
