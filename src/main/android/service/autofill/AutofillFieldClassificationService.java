package android.service.autofill;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteCallback;
import android.p008os.RemoteException;
import android.service.autofill.IAutofillFieldClassificationService;
import android.util.Log;
import android.view.autofill.AutofillValue;
import com.android.internal.util.function.NonaConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
@SystemApi
/* loaded from: classes3.dex */
public abstract class AutofillFieldClassificationService extends Service {
    public static final String EXTRA_SCORES = "scores";
    public static final String REQUIRED_ALGORITHM_CREDIT_CARD = "CREDIT_CARD";
    public static final String REQUIRED_ALGORITHM_EDIT_DISTANCE = "EDIT_DISTANCE";
    public static final String REQUIRED_ALGORITHM_EXACT_MATCH = "EXACT_MATCH";
    public static final String SERVICE_INTERFACE = "android.service.autofill.AutofillFieldClassificationService";
    public static final String SERVICE_META_DATA_KEY_AVAILABLE_ALGORITHMS = "android.autofill.field_classification.available_algorithms";
    public static final String SERVICE_META_DATA_KEY_DEFAULT_ALGORITHM = "android.autofill.field_classification.default_algorithm";
    private static final String TAG = "AutofillFieldClassificationService";
    private final Handler mHandler = new Handler(Looper.getMainLooper(), null, true);
    private AutofillFieldClassificationServiceWrapper mWrapper;

    /* JADX INFO: Access modifiers changed from: private */
    public void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) {
        Bundle data = new Bundle();
        float[][] scores = onCalculateScores(actualValues, Arrays.asList(userDataValues), Arrays.asList(categoryIds), defaultAlgorithm, defaultArgs, algorithms, args);
        if (scores != null) {
            data.putParcelable(EXTRA_SCORES, new Scores(scores));
        }
        callback.sendResult(data);
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.mWrapper = new AutofillFieldClassificationServiceWrapper();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mWrapper;
    }

    @SystemApi
    @Deprecated
    public float[][] onGetScores(String algorithm, Bundle algorithmOptions, List<AutofillValue> actualValues, List<String> userDataValues) {
        Log.m110e(TAG, "service implementation (" + getClass() + " does not implement onGetScores()");
        return null;
    }

    @SystemApi
    public float[][] onCalculateScores(List<AutofillValue> actualValues, List<String> userDataValues, List<String> categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) {
        Log.m110e(TAG, "service implementation (" + getClass() + " does not implement onCalculateScore()");
        return null;
    }

    /* loaded from: classes3.dex */
    private final class AutofillFieldClassificationServiceWrapper extends IAutofillFieldClassificationService.Stub {
        private AutofillFieldClassificationServiceWrapper() {
        }

        @Override // android.service.autofill.IAutofillFieldClassificationService
        public void calculateScores(RemoteCallback callback, List<AutofillValue> actualValues, String[] userDataValues, String[] categoryIds, String defaultAlgorithm, Bundle defaultArgs, Map algorithms, Map args) throws RemoteException {
            AutofillFieldClassificationService.this.mHandler.sendMessage(PooledLambda.obtainMessage(new NonaConsumer() { // from class: android.service.autofill.AutofillFieldClassificationService$AutofillFieldClassificationServiceWrapper$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.function.NonaConsumer
                public final void accept(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6, Object obj7, Object obj8, Object obj9) {
                    ((AutofillFieldClassificationService) obj).calculateScores((RemoteCallback) obj2, (List) obj3, (String[]) obj4, (String[]) obj5, (String) obj6, (Bundle) obj7, (Map) obj8, (Map) obj9);
                }
            }, AutofillFieldClassificationService.this, callback, actualValues, userDataValues, categoryIds, defaultAlgorithm, defaultArgs, algorithms, args));
        }
    }

    /* loaded from: classes3.dex */
    public static final class Scores implements Parcelable {
        public static final Parcelable.Creator<Scores> CREATOR = new Parcelable.Creator<Scores>() { // from class: android.service.autofill.AutofillFieldClassificationService.Scores.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Scores createFromParcel(Parcel parcel) {
                return new Scores(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Scores[] newArray(int size) {
                return new Scores[size];
            }
        };
        public final float[][] scores;

        private Scores(Parcel parcel) {
            int size1 = parcel.readInt();
            int size2 = parcel.readInt();
            this.scores = (float[][]) Array.newInstance(Float.TYPE, size1, size2);
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    this.scores[i][j] = parcel.readFloat();
                }
            }
        }

        private Scores(float[][] scores) {
            this.scores = scores;
        }

        public String toString() {
            float[][] fArr = this.scores;
            int size1 = fArr.length;
            int size2 = size1 > 0 ? fArr[0].length : 0;
            StringBuilder builder = new StringBuilder("Scores [").append(size1).append("x").append(size2).append("] ");
            for (int i = 0; i < size1; i++) {
                builder.append(i).append(": ").append(Arrays.toString(this.scores[i])).append(' ');
            }
            return builder.toString();
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel parcel, int flags) {
            float[][] fArr = this.scores;
            int size1 = fArr.length;
            int size2 = fArr[0].length;
            parcel.writeInt(size1);
            parcel.writeInt(size2);
            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    parcel.writeFloat(this.scores[i][j]);
                }
            }
        }
    }
}
