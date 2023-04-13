package android.app;

import android.content.Context;
import android.p008os.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class GrammaticalInflectionManager {
    private static final Set<Integer> VALID_GENDER_VALUES = new HashSet(Arrays.asList(0, 1, 2, 3));
    private final Context mContext;
    private final IGrammaticalInflectionManager mService;

    public GrammaticalInflectionManager(Context context, IGrammaticalInflectionManager service) {
        this.mContext = context;
        this.mService = service;
    }

    public int getApplicationGrammaticalGender() {
        return this.mContext.getApplicationContext().getResources().getConfiguration().getGrammaticalGender();
    }

    public void setRequestedApplicationGrammaticalGender(int grammaticalGender) {
        if (!VALID_GENDER_VALUES.contains(Integer.valueOf(grammaticalGender))) {
            throw new IllegalArgumentException("Unknown grammatical gender");
        }
        try {
            this.mService.setRequestedApplicationGrammaticalGender(this.mContext.getPackageName(), this.mContext.getUserId(), grammaticalGender);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
