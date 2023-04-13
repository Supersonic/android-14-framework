package com.android.commands.requestsync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SyncRequest;
import android.os.Bundle;
import java.io.PrintStream;
import java.net.URISyntaxException;
/* loaded from: classes.dex */
public class RequestSync {
    private Account mAccount;
    private String mAccountName;
    private String mAccountType;
    private String[] mArgs;
    private String mAuthority;
    private String mCurArgData;
    private int mExemptionFlag = 0;
    private Bundle mExtras = new Bundle();
    private int mNextArg;
    private Operation mOperation;
    private int mPeriodicIntervalSeconds;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    /* JADX WARN: Unknown enum class pattern. Please report as an issue! */
    /* loaded from: classes.dex */
    public static abstract class Operation {
        public static final Operation REQUEST_SYNC = new C00001("REQUEST_SYNC", 0);
        public static final Operation ADD_PERIODIC_SYNC = new C00012("ADD_PERIODIC_SYNC", 1);
        public static final Operation REMOVE_PERIODIC_SYNC = new C00023("REMOVE_PERIODIC_SYNC", 2);
        private static final /* synthetic */ Operation[] $VALUES = $values();

        abstract void invoke(RequestSync requestSync);

        /* renamed from: com.android.commands.requestsync.RequestSync$Operation$1 */
        /* loaded from: classes.dex */
        enum C00001 extends Operation {
            private C00001(String str, int i) {
                super(str, i);
            }

            @Override // com.android.commands.requestsync.RequestSync.Operation
            void invoke(RequestSync caller) {
                int flag = caller.mExemptionFlag;
                caller.mExtras.putInt("v_exemption", flag);
                if (flag == 0) {
                    System.out.println("Making a sync request as a background app.\nNote: request may be throttled by App Standby.\nTo override this behavior and run a sync immediately, pass a -f or -F option (use -h for help).\n");
                }
                SyncRequest request = new SyncRequest.Builder().setSyncAdapter(caller.mAccount, caller.mAuthority).setExtras(caller.mExtras).syncOnce().build();
                ContentResolver.requestSync(request);
            }
        }

        private static /* synthetic */ Operation[] $values() {
            return new Operation[]{REQUEST_SYNC, ADD_PERIODIC_SYNC, REMOVE_PERIODIC_SYNC};
        }

        private Operation(String str, int i) {
        }

        public static Operation valueOf(String name) {
            return (Operation) Enum.valueOf(Operation.class, name);
        }

        public static Operation[] values() {
            return (Operation[]) $VALUES.clone();
        }

        /* renamed from: com.android.commands.requestsync.RequestSync$Operation$2 */
        /* loaded from: classes.dex */
        enum C00012 extends Operation {
            private C00012(String str, int i) {
                super(str, i);
            }

            @Override // com.android.commands.requestsync.RequestSync.Operation
            void invoke(RequestSync caller) {
                ContentResolver.addPeriodicSync(caller.mAccount, caller.mAuthority, caller.mExtras, caller.mPeriodicIntervalSeconds);
            }
        }

        /* renamed from: com.android.commands.requestsync.RequestSync$Operation$3 */
        /* loaded from: classes.dex */
        enum C00023 extends Operation {
            private C00023(String str, int i) {
                super(str, i);
            }

            @Override // com.android.commands.requestsync.RequestSync.Operation
            void invoke(RequestSync caller) {
                ContentResolver.removePeriodicSync(caller.mAccount, caller.mAuthority, caller.mExtras);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new RequestSync().run(args);
        } catch (IllegalArgumentException e) {
            showUsage();
            System.err.println("Error: " + e);
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private void run(String[] args) throws Exception {
        this.mArgs = args;
        this.mNextArg = 0;
        boolean ok = parseArgs();
        if (ok) {
            Account account = (this.mAccountName == null || this.mAccountType == null) ? null : new Account(this.mAccountName, this.mAccountType);
            System.out.printf("Requesting sync for: \n", new Object[0]);
            if (account == null) {
                System.out.printf("  Account: all\n", new Object[0]);
            } else {
                System.out.printf("  Account: %s (%s)\n", account.name, account.type);
            }
            PrintStream printStream = System.out;
            Object[] objArr = new Object[1];
            String str = this.mAuthority;
            if (str == null) {
                str = "All";
            }
            objArr[0] = str;
            printStream.printf("  Authority: %s\n", objArr);
            if (this.mExtras.size() > 0) {
                System.out.printf("  Extras:\n", new Object[0]);
                for (String key : this.mExtras.keySet()) {
                    System.out.printf("    %s: %s\n", key, this.mExtras.get(key));
                }
            }
            this.mAccount = account;
            this.mOperation.invoke(this);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean parseArgs() throws URISyntaxException {
        char c;
        this.mOperation = Operation.REQUEST_SYNC;
        String[] strArr = this.mArgs;
        if (strArr.length > 0) {
            String str = strArr[0];
            switch (str.hashCode()) {
                case -1439021497:
                    if (str.equals("add-periodic")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 810481092:
                    if (str.equals("remove-periodic")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    this.mNextArg++;
                    this.mOperation = Operation.ADD_PERIODIC_SYNC;
                    this.mPeriodicIntervalSeconds = Integer.parseInt(nextArgRequired());
                    break;
                case 1:
                    this.mNextArg++;
                    this.mOperation = Operation.REMOVE_PERIODIC_SYNC;
                    break;
            }
        }
        while (true) {
            String opt = nextOption();
            if (opt != null) {
                if (!opt.equals("-h") && !opt.equals("--help")) {
                    if (opt.equals("-n") || opt.equals("--account-name")) {
                        this.mAccountName = nextArgRequired();
                    } else if (opt.equals("-t") || opt.equals("--account-type")) {
                        this.mAccountType = nextArgRequired();
                    } else if (opt.equals("-a") || opt.equals("--authority")) {
                        this.mAuthority = nextArgRequired();
                    } else if (opt.equals("--is") || opt.equals("--ignore-settings")) {
                        this.mExtras.putBoolean("ignore_settings", true);
                    } else if (opt.equals("--ib") || opt.equals("--ignore-backoff")) {
                        this.mExtras.putBoolean("ignore_backoff", true);
                    } else if (opt.equals("--dd") || opt.equals("--discard-deletions")) {
                        this.mExtras.putBoolean("discard_deletions", true);
                    } else if (opt.equals("--nr") || opt.equals("--no-retry")) {
                        this.mExtras.putBoolean("do_not_retry", true);
                    } else if (opt.equals("--ex") || opt.equals("--expedited")) {
                        this.mExtras.putBoolean("expedited", true);
                    } else if (opt.equals("-i") || opt.equals("--initialize")) {
                        this.mExtras.putBoolean("initialize", true);
                    } else if (opt.equals("-m") || opt.equals("--manual")) {
                        this.mExtras.putBoolean("force", true);
                    } else if (opt.equals("--od") || opt.equals("--override-deletions")) {
                        this.mExtras.putBoolean("deletions_override", true);
                    } else if (opt.equals("-u") || opt.equals("--upload-only")) {
                        this.mExtras.putBoolean("upload", true);
                    } else if (opt.equals("--rc") || opt.equals("--require-charging")) {
                        this.mExtras.putBoolean("require_charging", true);
                    } else if (opt.equals("--ej") || opt.equals("--schedule-as-ej")) {
                        this.mExtras.putBoolean("schedule_as_expedited_job", true);
                    } else if (opt.equals("-e") || opt.equals("--es") || opt.equals("--extra-string")) {
                        String key = nextArgRequired();
                        String value = nextArgRequired();
                        this.mExtras.putString(key, value);
                    } else if (opt.equals("--esn") || opt.equals("--extra-string-null")) {
                        String key2 = nextArgRequired();
                        this.mExtras.putString(key2, null);
                    } else if (opt.equals("--ei") || opt.equals("--extra-int")) {
                        String key3 = nextArgRequired();
                        String value2 = nextArgRequired();
                        this.mExtras.putInt(key3, Integer.valueOf(value2).intValue());
                    } else if (opt.equals("--el") || opt.equals("--extra-long")) {
                        String key4 = nextArgRequired();
                        String value3 = nextArgRequired();
                        this.mExtras.putLong(key4, Long.parseLong(value3));
                    } else if (opt.equals("--ef") || opt.equals("--extra-float")) {
                        String key5 = nextArgRequired();
                        String value4 = nextArgRequired();
                        this.mExtras.putFloat(key5, (float) Long.parseLong(value4));
                    } else if (opt.equals("--ed") || opt.equals("--extra-double")) {
                        String key6 = nextArgRequired();
                        String value5 = nextArgRequired();
                        this.mExtras.putFloat(key6, (float) Long.parseLong(value5));
                    } else if (opt.equals("--ez") || opt.equals("--extra-bool")) {
                        String key7 = nextArgRequired();
                        String value6 = nextArgRequired();
                        this.mExtras.putBoolean(key7, Boolean.valueOf(value6).booleanValue());
                    } else if (opt.equals("-f") || opt.equals("--foreground")) {
                        this.mExemptionFlag = 1;
                    } else if (opt.equals("-F") || opt.equals("--top")) {
                        this.mExemptionFlag = 2;
                    } else {
                        System.err.println("Error: Unknown option: " + opt);
                        showUsage();
                        return false;
                    }
                }
            } else if (this.mNextArg < this.mArgs.length) {
                showUsage();
                return false;
            } else {
                return true;
            }
        }
        showUsage();
        return false;
    }

    private String nextOption() {
        if (this.mCurArgData != null) {
            String prev = this.mArgs[this.mNextArg - 1];
            throw new IllegalArgumentException("No argument expected after \"" + prev + "\"");
        }
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        if (arg.startsWith("-")) {
            this.mNextArg++;
            if (arg.equals("--")) {
                return null;
            }
            if (arg.length() > 1 && arg.charAt(1) != '-') {
                if (arg.length() > 2) {
                    this.mCurArgData = arg.substring(2);
                    return arg.substring(0, 2);
                }
                this.mCurArgData = null;
                return arg;
            }
            this.mCurArgData = null;
            return arg;
        }
        return null;
    }

    private String nextArg() {
        if (this.mCurArgData != null) {
            String arg = this.mCurArgData;
            this.mCurArgData = null;
            return arg;
        }
        int i = this.mNextArg;
        String[] strArr = this.mArgs;
        if (i < strArr.length) {
            this.mNextArg = i + 1;
            return strArr[i];
        }
        return null;
    }

    private String nextArgRequired() {
        String arg = nextArg();
        if (arg == null) {
            String prev = this.mArgs[this.mNextArg - 1];
            throw new IllegalArgumentException("Argument expected after \"" + prev + "\"");
        }
        return arg;
    }

    private static void showUsage() {
        System.err.println("Usage:\n\n  requestsync [options]\n    With no options, a sync will be requested for all account and all sync\n    authorities with no extras.\n    Basic options:\n       -h|--help: Display this message\n       -n|--account-name <ACCOUNT-NAME>\n       -t|--account-type <ACCOUNT-TYPE>\n       -a|--authority <AUTHORITY>\n    App-standby related options\n\n       -f|--foreground (defeat app-standby job throttling, but not battery saver)\n       -F|--top (defeat app-standby job throttling and battery saver)\n    ContentResolver extra options:\n      --is|--ignore-settings: Add SYNC_EXTRAS_IGNORE_SETTINGS\n      --ib|--ignore-backoff: Add SYNC_EXTRAS_IGNORE_BACKOFF\n      --dd|--discard-deletions: Add SYNC_EXTRAS_DISCARD_LOCAL_DELETIONS\n      --nr|--no-retry: Add SYNC_EXTRAS_DO_NOT_RETRY\n      --ex|--expedited: Add SYNC_EXTRAS_EXPEDITED\n      -i|--initialize: Add SYNC_EXTRAS_INITIALIZE\n      --m|--manual: Add SYNC_EXTRAS_MANUAL\n      --od|--override-deletions: Add SYNC_EXTRAS_OVERRIDE_TOO_MANY_DELETIONS\n      -u|--upload-only: Add SYNC_EXTRAS_UPLOAD\n      --rc|--require-charging: Add SYNC_EXTRAS_REQUIRE_CHARGING\n    Custom extra options:\n      -e|--es|--extra-string <KEY> <VALUE>\n      --esn|--extra-string-null <KEY>\n      --ei|--extra-int <KEY> <VALUE>\n      --el|--extra-long <KEY> <VALUE>\n      --ef|--extra-float <KEY> <VALUE>\n      --ed|--extra-double <KEY> <VALUE>\n      --ez|--extra-bool <KEY> <VALUE>\n\n  requestsync add-periodic INTERVAL-SECOND [options]\n  requestsync remove-periodic [options]\n    Mandatory options:\n      -n|--account-name <ACCOUNT-NAME>\n      -t|--account-type <ACCOUNT-TYPE>\n      -a|--authority <AUTHORITY>\n    Also takes the above extra options.\n");
    }
}
