package com.android.server.vibrator;

import android.os.CombinedVibration;
import android.os.IBinder;
import android.os.VibrationAttributes;
import android.os.VibrationEffect;
import android.os.vibrator.PrebakedSegment;
import android.os.vibrator.PrimitiveSegment;
import android.os.vibrator.RampSegment;
import android.os.vibrator.StepSegment;
import android.os.vibrator.VibrationEffectSegment;
import android.util.proto.ProtoOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes2.dex */
public abstract class Vibration {
    public static final SimpleDateFormat DEBUG_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
    public static final AtomicInteger sNextVibrationId = new AtomicInteger(1);
    public final CallerInfo callerInfo;
    public final IBinder callerToken;

    /* renamed from: id */
    public final long f1160id;
    public final VibrationStats stats = new VibrationStats();

    public abstract boolean isRepeating();

    /* loaded from: classes2.dex */
    public enum Status {
        UNKNOWN(0),
        RUNNING(1),
        FINISHED(2),
        FINISHED_UNEXPECTED(3),
        FORWARDED_TO_INPUT_DEVICES(4),
        CANCELLED_BINDER_DIED(5),
        CANCELLED_BY_SCREEN_OFF(6),
        CANCELLED_BY_SETTINGS_UPDATE(7),
        CANCELLED_BY_USER(8),
        CANCELLED_BY_UNKNOWN_REASON(9),
        CANCELLED_SUPERSEDED(10),
        IGNORED_ERROR_APP_OPS(11),
        IGNORED_ERROR_CANCELLING(12),
        IGNORED_ERROR_SCHEDULING(13),
        IGNORED_ERROR_TOKEN(14),
        IGNORED_APP_OPS(15),
        IGNORED_BACKGROUND(16),
        IGNORED_UNKNOWN_VIBRATION(17),
        IGNORED_UNSUPPORTED(18),
        IGNORED_FOR_EXTERNAL(19),
        IGNORED_FOR_HIGHER_IMPORTANCE(20),
        IGNORED_FOR_ONGOING(21),
        IGNORED_FOR_POWER(22),
        IGNORED_FOR_RINGER_MODE(23),
        IGNORED_FOR_SETTINGS(24),
        IGNORED_SUPERSEDED(25),
        IGNORED_FROM_VIRTUAL_DEVICE(26);
        
        private final int mProtoEnumValue;

        Status(int i) {
            this.mProtoEnumValue = i;
        }

        public int getProtoEnumValue() {
            return this.mProtoEnumValue;
        }
    }

    public Vibration(IBinder iBinder, CallerInfo callerInfo) {
        Objects.requireNonNull(iBinder);
        Objects.requireNonNull(callerInfo);
        this.f1160id = sNextVibrationId.getAndIncrement();
        this.callerToken = iBinder;
        this.callerInfo = callerInfo;
    }

    /* loaded from: classes2.dex */
    public static final class CallerInfo {
        public final VibrationAttributes attrs;
        public final int displayId;
        public final String opPkg;
        public final String reason;
        public final int uid;

        public CallerInfo(VibrationAttributes vibrationAttributes, int i, int i2, String str, String str2) {
            Objects.requireNonNull(vibrationAttributes);
            this.attrs = vibrationAttributes;
            this.uid = i;
            this.displayId = i2;
            this.opPkg = str;
            this.reason = str2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof CallerInfo) {
                CallerInfo callerInfo = (CallerInfo) obj;
                return Objects.equals(this.attrs, callerInfo.attrs) && this.uid == callerInfo.uid && this.displayId == callerInfo.displayId && Objects.equals(this.opPkg, callerInfo.opPkg) && Objects.equals(this.reason, callerInfo.reason);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.attrs, Integer.valueOf(this.uid), Integer.valueOf(this.displayId), this.opPkg, this.reason);
        }

        public String toString() {
            return "CallerInfo{ attrs=" + this.attrs + ", uid=" + this.uid + ", displayId=" + this.displayId + ", opPkg=" + this.opPkg + ", reason=" + this.reason + '}';
        }
    }

    /* loaded from: classes2.dex */
    public static final class EndInfo {
        public final CallerInfo endedBy;
        public final Status status;

        public EndInfo(Status status) {
            this(status, null);
        }

        public EndInfo(Status status, CallerInfo callerInfo) {
            this.status = status;
            this.endedBy = callerInfo;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof EndInfo) {
                EndInfo endInfo = (EndInfo) obj;
                return Objects.equals(this.endedBy, endInfo.endedBy) && this.status == endInfo.status;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.status, this.endedBy);
        }

        public String toString() {
            return "EndInfo{status=" + this.status + ", endedBy=" + this.endedBy + '}';
        }
    }

    /* loaded from: classes2.dex */
    public static final class DebugInfo {
        public final CallerInfo mCallerInfo;
        public final long mCreateTime;
        public final long mDurationMs;
        public final CombinedVibration mEffect;
        public final long mEndTime;
        public final CombinedVibration mOriginalEffect;
        public final float mScale;
        public final long mStartTime;
        public final Status mStatus;

        public DebugInfo(Status status, VibrationStats vibrationStats, CombinedVibration combinedVibration, CombinedVibration combinedVibration2, float f, CallerInfo callerInfo) {
            Objects.requireNonNull(callerInfo);
            this.mCreateTime = vibrationStats.getCreateTimeDebug();
            this.mStartTime = vibrationStats.getStartTimeDebug();
            this.mEndTime = vibrationStats.getEndTimeDebug();
            this.mDurationMs = vibrationStats.getDurationDebug();
            this.mEffect = combinedVibration;
            this.mOriginalEffect = combinedVibration2;
            this.mScale = f;
            this.mCallerInfo = callerInfo;
            this.mStatus = status;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("createTime: ");
            sb.append(Vibration.DEBUG_DATE_FORMAT.format(new Date(this.mCreateTime)));
            sb.append(", startTime: ");
            sb.append(Vibration.DEBUG_DATE_FORMAT.format(new Date(this.mStartTime)));
            sb.append(", endTime: ");
            sb.append(this.mEndTime == 0 ? null : Vibration.DEBUG_DATE_FORMAT.format(new Date(this.mEndTime)));
            sb.append(", durationMs: ");
            sb.append(this.mDurationMs);
            sb.append(", status: ");
            sb.append(this.mStatus.name().toLowerCase());
            sb.append(", effect: ");
            sb.append(this.mEffect);
            sb.append(", originalEffect: ");
            sb.append(this.mOriginalEffect);
            sb.append(", scale: ");
            sb.append(String.format("%.2f", Float.valueOf(this.mScale)));
            sb.append(", callerInfo: ");
            sb.append(this.mCallerInfo);
            return sb.toString();
        }

        public void dumpProto(ProtoOutputStream protoOutputStream, long j) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, this.mStartTime);
            protoOutputStream.write(1112396529666L, this.mEndTime);
            protoOutputStream.write(1112396529671L, this.mDurationMs);
            protoOutputStream.write(1159641169928L, this.mStatus.ordinal());
            long start2 = protoOutputStream.start(1146756268037L);
            VibrationAttributes vibrationAttributes = this.mCallerInfo.attrs;
            protoOutputStream.write(1120986464257L, vibrationAttributes.getUsage());
            protoOutputStream.write(1120986464258L, vibrationAttributes.getAudioUsage());
            protoOutputStream.write(1120986464259L, vibrationAttributes.getFlags());
            protoOutputStream.end(start2);
            CombinedVibration combinedVibration = this.mEffect;
            if (combinedVibration != null) {
                dumpEffect(protoOutputStream, 1146756268035L, combinedVibration);
            }
            CombinedVibration combinedVibration2 = this.mOriginalEffect;
            if (combinedVibration2 != null) {
                dumpEffect(protoOutputStream, 1146756268036L, combinedVibration2);
            }
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, CombinedVibration combinedVibration) {
            dumpEffect(protoOutputStream, j, (CombinedVibration.Sequential) CombinedVibration.startSequential().addNext(combinedVibration).combine());
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, CombinedVibration.Sequential sequential) {
            long start = protoOutputStream.start(j);
            for (int i = 0; i < sequential.getEffects().size(); i++) {
                CombinedVibration combinedVibration = (CombinedVibration) sequential.getEffects().get(i);
                if (combinedVibration instanceof CombinedVibration.Mono) {
                    dumpEffect(protoOutputStream, 2246267895809L, (CombinedVibration.Mono) combinedVibration);
                } else if (combinedVibration instanceof CombinedVibration.Stereo) {
                    dumpEffect(protoOutputStream, 2246267895809L, (CombinedVibration.Stereo) combinedVibration);
                }
                protoOutputStream.write(2220498092034L, ((Integer) sequential.getDelays().get(i)).intValue());
            }
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, CombinedVibration.Mono mono) {
            long start = protoOutputStream.start(j);
            dumpEffect(protoOutputStream, 2246267895809L, mono.getEffect());
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, CombinedVibration.Stereo stereo) {
            long start = protoOutputStream.start(j);
            for (int i = 0; i < stereo.getEffects().size(); i++) {
                protoOutputStream.write(2220498092034L, stereo.getEffects().keyAt(i));
                dumpEffect(protoOutputStream, 2246267895809L, (VibrationEffect) stereo.getEffects().valueAt(i));
            }
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, VibrationEffect vibrationEffect) {
            long start = protoOutputStream.start(j);
            VibrationEffect.Composed composed = (VibrationEffect.Composed) vibrationEffect;
            for (VibrationEffectSegment vibrationEffectSegment : composed.getSegments()) {
                dumpEffect(protoOutputStream, 1146756268033L, vibrationEffectSegment);
            }
            protoOutputStream.write(1120986464258L, composed.getRepeatIndex());
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, VibrationEffectSegment vibrationEffectSegment) {
            long start = protoOutputStream.start(j);
            if (vibrationEffectSegment instanceof StepSegment) {
                dumpEffect(protoOutputStream, 1146756268035L, (StepSegment) vibrationEffectSegment);
            } else if (vibrationEffectSegment instanceof RampSegment) {
                dumpEffect(protoOutputStream, 1146756268036L, (RampSegment) vibrationEffectSegment);
            } else if (vibrationEffectSegment instanceof PrebakedSegment) {
                dumpEffect(protoOutputStream, 1146756268033L, (PrebakedSegment) vibrationEffectSegment);
            } else if (vibrationEffectSegment instanceof PrimitiveSegment) {
                dumpEffect(protoOutputStream, 1146756268034L, (PrimitiveSegment) vibrationEffectSegment);
            }
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, StepSegment stepSegment) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, stepSegment.getDuration());
            protoOutputStream.write(1108101562370L, stepSegment.getAmplitude());
            protoOutputStream.write(1108101562371L, stepSegment.getFrequencyHz());
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, RampSegment rampSegment) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, rampSegment.getDuration());
            protoOutputStream.write(1108101562370L, rampSegment.getStartAmplitude());
            protoOutputStream.write(1108101562371L, rampSegment.getEndAmplitude());
            protoOutputStream.write(1108101562372L, rampSegment.getStartFrequencyHz());
            protoOutputStream.write(1108101562373L, rampSegment.getEndFrequencyHz());
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, PrebakedSegment prebakedSegment) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, prebakedSegment.getEffectId());
            protoOutputStream.write(1120986464258L, prebakedSegment.getEffectStrength());
            protoOutputStream.write(1120986464259L, prebakedSegment.shouldFallback());
            protoOutputStream.end(start);
        }

        public final void dumpEffect(ProtoOutputStream protoOutputStream, long j, PrimitiveSegment primitiveSegment) {
            long start = protoOutputStream.start(j);
            protoOutputStream.write(1120986464257L, primitiveSegment.getPrimitiveId());
            protoOutputStream.write(1108101562370L, primitiveSegment.getScale());
            protoOutputStream.write(1120986464259L, primitiveSegment.getDelay());
            protoOutputStream.end(start);
        }
    }
}
