package com.android.server.p030am;
/* renamed from: com.android.server.am.AppErrorsProto */
/* loaded from: classes5.dex */
public final class AppErrorsProto {
    public static final long BAD_PROCESSES = 2246267895811L;
    public static final long NOW_UPTIME_MS = 1112396529665L;
    public static final long PROCESS_CRASH_TIMES = 2246267895810L;

    /* renamed from: com.android.server.am.AppErrorsProto$ProcessCrashTime */
    /* loaded from: classes5.dex */
    public final class ProcessCrashTime {
        public static final long ENTRIES = 2246267895810L;
        public static final long PROCESS_NAME = 1138166333441L;

        public ProcessCrashTime() {
        }

        /* renamed from: com.android.server.am.AppErrorsProto$ProcessCrashTime$Entry */
        /* loaded from: classes5.dex */
        public final class Entry {
            public static final long LAST_CRASHED_AT_MS = 1112396529666L;
            public static final long UID = 1120986464257L;

            public Entry() {
            }
        }
    }

    /* renamed from: com.android.server.am.AppErrorsProto$BadProcess */
    /* loaded from: classes5.dex */
    public final class BadProcess {
        public static final long ENTRIES = 2246267895810L;
        public static final long PROCESS_NAME = 1138166333441L;

        public BadProcess() {
        }

        /* renamed from: com.android.server.am.AppErrorsProto$BadProcess$Entry */
        /* loaded from: classes5.dex */
        public final class Entry {
            public static final long CRASHED_AT_MS = 1112396529666L;
            public static final long LONG_MSG = 1138166333444L;
            public static final long SHORT_MSG = 1138166333443L;
            public static final long STACK = 1138166333445L;
            public static final long UID = 1120986464257L;

            public Entry() {
            }
        }
    }
}
