package com.android.internal.org.bouncycastle.crypto.params;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import com.android.internal.midi.MidiConstants;
/* loaded from: classes4.dex */
public class DESParameters extends KeyParameter {
    public static final int DES_KEY_LENGTH = 8;
    private static byte[] DES_weak_keys = {1, 1, 1, 1, 1, 1, 1, 1, 31, 31, 31, 31, AcquiredInfo.POWER_PRESS, AcquiredInfo.POWER_PRESS, AcquiredInfo.POWER_PRESS, AcquiredInfo.POWER_PRESS, MidiConstants.STATUS_PITCH_BEND, MidiConstants.STATUS_PITCH_BEND, MidiConstants.STATUS_PITCH_BEND, MidiConstants.STATUS_PITCH_BEND, MidiConstants.STATUS_MIDI_TIME_CODE, MidiConstants.STATUS_MIDI_TIME_CODE, MidiConstants.STATUS_MIDI_TIME_CODE, MidiConstants.STATUS_MIDI_TIME_CODE, -2, -2, -2, -2, -2, -2, -2, -2, 1, -2, 1, -2, 1, -2, 1, -2, 31, MidiConstants.STATUS_PITCH_BEND, 31, MidiConstants.STATUS_PITCH_BEND, AcquiredInfo.POWER_PRESS, MidiConstants.STATUS_MIDI_TIME_CODE, AcquiredInfo.POWER_PRESS, MidiConstants.STATUS_MIDI_TIME_CODE, 1, MidiConstants.STATUS_PITCH_BEND, 1, MidiConstants.STATUS_PITCH_BEND, 1, MidiConstants.STATUS_MIDI_TIME_CODE, 1, MidiConstants.STATUS_MIDI_TIME_CODE, 31, -2, 31, -2, AcquiredInfo.POWER_PRESS, -2, AcquiredInfo.POWER_PRESS, -2, 1, 31, 1, 31, 1, AcquiredInfo.POWER_PRESS, 1, AcquiredInfo.POWER_PRESS, MidiConstants.STATUS_PITCH_BEND, -2, MidiConstants.STATUS_PITCH_BEND, -2, MidiConstants.STATUS_MIDI_TIME_CODE, -2, MidiConstants.STATUS_MIDI_TIME_CODE, -2, -2, 1, -2, 1, -2, 1, -2, 1, MidiConstants.STATUS_PITCH_BEND, 31, MidiConstants.STATUS_PITCH_BEND, 31, MidiConstants.STATUS_MIDI_TIME_CODE, AcquiredInfo.POWER_PRESS, MidiConstants.STATUS_MIDI_TIME_CODE, AcquiredInfo.POWER_PRESS, MidiConstants.STATUS_PITCH_BEND, 1, MidiConstants.STATUS_PITCH_BEND, 1, MidiConstants.STATUS_MIDI_TIME_CODE, 1, MidiConstants.STATUS_MIDI_TIME_CODE, 1, -2, 31, -2, 31, -2, AcquiredInfo.POWER_PRESS, -2, AcquiredInfo.POWER_PRESS, 31, 1, 31, 1, AcquiredInfo.POWER_PRESS, 1, AcquiredInfo.POWER_PRESS, 1, -2, MidiConstants.STATUS_PITCH_BEND, -2, MidiConstants.STATUS_PITCH_BEND, -2, MidiConstants.STATUS_MIDI_TIME_CODE, -2, MidiConstants.STATUS_MIDI_TIME_CODE};
    private static final int N_DES_WEAK_KEYS = 16;

    public DESParameters(byte[] key) {
        super(key);
        if (isWeakKey(key, 0)) {
            throw new IllegalArgumentException("attempt to create weak DES key");
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x001c, code lost:
        r0 = r0 + 1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static boolean isWeakKey(byte[] key, int offset) {
        if (key.length - offset < 8) {
            throw new IllegalArgumentException("key material too short.");
        }
        int i = 0;
        while (i < 16) {
            for (int j = 0; j < 8; j++) {
                if (key[j + offset] != DES_weak_keys[(i * 8) + j]) {
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public static void setOddParity(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            bytes[i] = (byte) ((b & 254) | (((((((((b >> 1) ^ (b >> 2)) ^ (b >> 3)) ^ (b >> 4)) ^ (b >> 5)) ^ (b >> 6)) ^ (b >> 7)) ^ 1) & 1));
        }
    }
}
