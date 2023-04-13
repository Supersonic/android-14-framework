package android.app;

import android.compat.Compatibility;
import android.p008os.Process;
import com.android.internal.compat.ChangeReporter;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class AppCompatCallbacks implements Compatibility.BehaviorChangeDelegate {
    private final ChangeReporter mChangeReporter;
    private final long[] mDisabledChanges;

    public static void install(long[] disabledChanges) {
        Compatibility.setBehaviorChangeDelegate(new AppCompatCallbacks(disabledChanges));
    }

    private AppCompatCallbacks(long[] disabledChanges) {
        long[] copyOf = Arrays.copyOf(disabledChanges, disabledChanges.length);
        this.mDisabledChanges = copyOf;
        Arrays.sort(copyOf);
        this.mChangeReporter = new ChangeReporter(1);
    }

    public void onChangeReported(long changeId) {
        reportChange(changeId, 3);
    }

    public boolean isChangeEnabled(long changeId) {
        if (Arrays.binarySearch(this.mDisabledChanges, changeId) < 0) {
            reportChange(changeId, 1);
            return true;
        }
        reportChange(changeId, 2);
        return false;
    }

    private void reportChange(long changeId, int state) {
        int uid = Process.myUid();
        this.mChangeReporter.reportChange(uid, changeId, state);
    }
}
