package com.android.server.hdmi;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
@VisibleForTesting
/* loaded from: classes.dex */
public class HdmiCecAtomWriter {
    @VisibleForTesting
    protected static final int FEATURE_ABORT_OPCODE_UNKNOWN = 256;

    public void messageReported(HdmiCecMessage hdmiCecMessage, int i, int i2, int i3) {
        messageReportedBase(createMessageReportedGenericArgs(hdmiCecMessage, i, i3, i2), createMessageReportedSpecialArgs(hdmiCecMessage));
    }

    public void messageReported(HdmiCecMessage hdmiCecMessage, int i, int i2) {
        messageReported(hdmiCecMessage, i, i2, -1);
    }

    public final MessageReportedGenericArgs createMessageReportedGenericArgs(HdmiCecMessage hdmiCecMessage, int i, int i2, int i3) {
        return new MessageReportedGenericArgs(i3, i, hdmiCecMessage.getSource(), hdmiCecMessage.getDestination(), hdmiCecMessage.getOpcode(), i2 == -1 ? 0 : i2 + 10);
    }

    public final MessageReportedSpecialArgs createMessageReportedSpecialArgs(HdmiCecMessage hdmiCecMessage) {
        int opcode = hdmiCecMessage.getOpcode();
        if (opcode != 0) {
            if (opcode == 68) {
                return createUserControlPressedSpecialArgs(hdmiCecMessage);
            }
            return new MessageReportedSpecialArgs();
        }
        return createFeatureAbortSpecialArgs(hdmiCecMessage);
    }

    public final MessageReportedSpecialArgs createUserControlPressedSpecialArgs(HdmiCecMessage hdmiCecMessage) {
        MessageReportedSpecialArgs messageReportedSpecialArgs = new MessageReportedSpecialArgs();
        if (hdmiCecMessage.getParams().length > 0) {
            byte b = hdmiCecMessage.getParams()[0];
            if (b >= 30 && b <= 41) {
                messageReportedSpecialArgs.mUserControlPressedCommand = 2;
            } else {
                messageReportedSpecialArgs.mUserControlPressedCommand = b + 256;
            }
        }
        return messageReportedSpecialArgs;
    }

    public final MessageReportedSpecialArgs createFeatureAbortSpecialArgs(HdmiCecMessage hdmiCecMessage) {
        MessageReportedSpecialArgs messageReportedSpecialArgs = new MessageReportedSpecialArgs();
        if (hdmiCecMessage.getParams().length > 0) {
            messageReportedSpecialArgs.mFeatureAbortOpcode = hdmiCecMessage.getParams()[0] & 255;
            if (hdmiCecMessage.getParams().length > 1) {
                messageReportedSpecialArgs.mFeatureAbortReason = hdmiCecMessage.getParams()[1] + 10;
            }
        }
        return messageReportedSpecialArgs;
    }

    public final void messageReportedBase(MessageReportedGenericArgs messageReportedGenericArgs, MessageReportedSpecialArgs messageReportedSpecialArgs) {
        writeHdmiCecMessageReportedAtom(messageReportedGenericArgs.mUid, messageReportedGenericArgs.mDirection, messageReportedGenericArgs.mInitiatorLogicalAddress, messageReportedGenericArgs.mDestinationLogicalAddress, messageReportedGenericArgs.mOpcode, messageReportedGenericArgs.mSendMessageResult, messageReportedSpecialArgs.mUserControlPressedCommand, messageReportedSpecialArgs.mFeatureAbortOpcode, messageReportedSpecialArgs.mFeatureAbortReason);
    }

    @VisibleForTesting
    public void writeHdmiCecMessageReportedAtom(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        FrameworkStatsLog.write(310, i, i2, i3, i4, i5, i6, i7, i8, i9);
    }

    public void activeSourceChanged(int i, int i2, int i3) {
        FrameworkStatsLog.write(309, i, i2, i3);
    }

    /* loaded from: classes.dex */
    public class MessageReportedGenericArgs {
        public final int mDestinationLogicalAddress;
        public final int mDirection;
        public final int mInitiatorLogicalAddress;
        public final int mOpcode;
        public final int mSendMessageResult;
        public final int mUid;

        public MessageReportedGenericArgs(int i, int i2, int i3, int i4, int i5, int i6) {
            this.mUid = i;
            this.mDirection = i2;
            this.mInitiatorLogicalAddress = i3;
            this.mDestinationLogicalAddress = i4;
            this.mOpcode = i5;
            this.mSendMessageResult = i6;
        }
    }

    /* loaded from: classes.dex */
    public class MessageReportedSpecialArgs {
        public int mFeatureAbortOpcode;
        public int mFeatureAbortReason;
        public int mUserControlPressedCommand;

        public MessageReportedSpecialArgs() {
            this.mUserControlPressedCommand = 0;
            this.mFeatureAbortOpcode = 256;
            this.mFeatureAbortReason = 0;
        }
    }
}
