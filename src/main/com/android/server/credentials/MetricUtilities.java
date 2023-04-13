package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.credentials.metrics.ApiName;
import com.android.server.credentials.metrics.ApiStatus;
import com.android.server.credentials.metrics.CandidateBrowsingPhaseMetric;
import com.android.server.credentials.metrics.CandidatePhaseMetric;
import com.android.server.credentials.metrics.ChosenProviderFinalPhaseMetric;
import com.android.server.credentials.metrics.InitialPhaseMetric;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
/* loaded from: classes.dex */
public class MetricUtilities {
    public static final int[] DEFAULT_REPEATED_INT_32 = new int[0];

    public static int getPackageUid(Context context, ComponentName componentName) {
        try {
            return context.getPackageManager().getApplicationInfo(componentName.getPackageName(), PackageManager.ApplicationInfoFlags.of(0L)).uid;
        } catch (Throwable unused) {
            Log.i("MetricUtilities", "Couldn't find required uid");
            return -1;
        }
    }

    public static void logApiCalled(ChosenProviderFinalPhaseMetric chosenProviderFinalPhaseMetric, List<CandidateBrowsingPhaseMetric> list, int i, int i2) {
        try {
            int size = list.size();
            int[] iArr = new int[size];
            int[] iArr2 = new int[size];
            int i3 = 0;
            for (CandidateBrowsingPhaseMetric candidateBrowsingPhaseMetric : list) {
                iArr[i3] = candidateBrowsingPhaseMetric.getEntryEnum();
                iArr2[i3] = candidateBrowsingPhaseMetric.getProviderUid();
                i3++;
            }
            FrameworkStatsLog.write((int) FrameworkStatsLog.CREDENTIAL_MANAGER_FINAL_PHASE, chosenProviderFinalPhaseMetric.getSessionId(), i2, chosenProviderFinalPhaseMetric.isUiReturned(), chosenProviderFinalPhaseMetric.getChosenUid(), chosenProviderFinalPhaseMetric.getTimestampFromReferenceStartMicroseconds(chosenProviderFinalPhaseMetric.getQueryStartTimeNanoseconds()), chosenProviderFinalPhaseMetric.getTimestampFromReferenceStartMicroseconds(chosenProviderFinalPhaseMetric.getQueryEndTimeNanoseconds()), chosenProviderFinalPhaseMetric.getTimestampFromReferenceStartMicroseconds(chosenProviderFinalPhaseMetric.getUiCallStartTimeNanoseconds()), chosenProviderFinalPhaseMetric.getTimestampFromReferenceStartMicroseconds(chosenProviderFinalPhaseMetric.getUiCallEndTimeNanoseconds()), chosenProviderFinalPhaseMetric.getTimestampFromReferenceStartMicroseconds(chosenProviderFinalPhaseMetric.getFinalFinishTimeNanoseconds()), chosenProviderFinalPhaseMetric.getChosenProviderStatus(), chosenProviderFinalPhaseMetric.isHasException(), chosenProviderFinalPhaseMetric.getAvailableEntries().stream().mapToInt(new ToIntFunction() { // from class: com.android.server.credentials.MetricUtilities$$ExternalSyntheticLambda0
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    int intValue;
                    intValue = ((Integer) obj).intValue();
                    return intValue;
                }
            }).toArray(), chosenProviderFinalPhaseMetric.getActionEntryCount(), chosenProviderFinalPhaseMetric.getCredentialEntryCount(), chosenProviderFinalPhaseMetric.getCredentialEntryTypeCount(), chosenProviderFinalPhaseMetric.getRemoteEntryCount(), chosenProviderFinalPhaseMetric.getAuthenticationEntryCount(), iArr, iArr2, i);
        } catch (Exception e) {
            Log.w("MetricUtilities", "Unexpected error during metric logging: " + e);
        }
    }

    public static void logApiCalled(Map<String, ProviderSession> map, int i) {
        try {
            Collection<ProviderSession> values = map.values();
            int size = values.size();
            int[] iArr = new int[size];
            int[] iArr2 = new int[size];
            int[] iArr3 = new int[size];
            int[] iArr4 = new int[size];
            boolean[] zArr = new boolean[size];
            int[] iArr5 = new int[size];
            int[] iArr6 = new int[size];
            int[] iArr7 = new int[size];
            int[] iArr8 = new int[size];
            int[] iArr9 = new int[size];
            int[] iArr10 = new int[size];
            Iterator<ProviderSession> it = values.iterator();
            int i2 = 0;
            boolean z = false;
            int i3 = -1;
            while (it.hasNext()) {
                CandidatePhaseMetric candidatePhaseMetric = it.next().mCandidatePhasePerProviderMetric;
                Iterator<ProviderSession> it2 = it;
                if (i3 == -1) {
                    i3 = candidatePhaseMetric.getSessionId();
                }
                if (!z) {
                    z = candidatePhaseMetric.isQueryReturned();
                }
                iArr[i2] = candidatePhaseMetric.getCandidateUid();
                int[] iArr11 = iArr10;
                iArr2[i2] = candidatePhaseMetric.getTimestampFromReferenceStartMicroseconds(candidatePhaseMetric.getStartQueryTimeNanoseconds());
                iArr3[i2] = candidatePhaseMetric.getTimestampFromReferenceStartMicroseconds(candidatePhaseMetric.getQueryFinishTimeNanoseconds());
                iArr4[i2] = candidatePhaseMetric.getProviderQueryStatus();
                zArr[i2] = candidatePhaseMetric.isHasException();
                iArr5[i2] = candidatePhaseMetric.getNumEntriesTotal();
                iArr6[i2] = candidatePhaseMetric.getCredentialEntryCount();
                iArr7[i2] = candidatePhaseMetric.getCredentialEntryTypeCount();
                iArr8[i2] = candidatePhaseMetric.getActionEntryCount();
                iArr9[i2] = candidatePhaseMetric.getAuthenticationEntryCount();
                iArr11[i2] = candidatePhaseMetric.getRemoteEntryCount();
                i2++;
                it = it2;
                iArr10 = iArr11;
            }
            FrameworkStatsLog.write((int) FrameworkStatsLog.CREDENTIAL_MANAGER_CANDIDATE_PHASE, i3, i, z, iArr, iArr2, iArr3, iArr4, zArr, iArr5, iArr8, iArr6, iArr7, iArr10, iArr9);
        } catch (Exception e) {
            Log.w("MetricUtilities", "Unexpected error during metric logging: " + e);
        }
    }

    public static void logApiCalled(ApiName apiName, ApiStatus apiStatus, int i) {
        try {
            int metricCode = apiName.getMetricCode();
            int metricCode2 = apiStatus.getMetricCode();
            int[] iArr = DEFAULT_REPEATED_INT_32;
            FrameworkStatsLog.write((int) FrameworkStatsLog.CREDENTIAL_MANAGER_API_CALLED, metricCode, i, metricCode2, iArr, iArr, iArr, -1, -1, -1, -1);
        } catch (Exception e) {
            Log.w("MetricUtilities", "Unexpected error during metric logging: " + e);
        }
    }

    public static void logApiCalled(InitialPhaseMetric initialPhaseMetric, int i) {
        try {
            FrameworkStatsLog.write((int) FrameworkStatsLog.CREDENTIAL_MANAGER_INIT_PHASE, initialPhaseMetric.getApiName(), initialPhaseMetric.getCallerUid(), initialPhaseMetric.getSessionId(), i, initialPhaseMetric.getCredentialServiceStartedTimeNanoseconds(), initialPhaseMetric.getCountRequestClassType());
        } catch (Exception e) {
            Log.w("MetricUtilities", "Unexpected error during metric logging: " + e);
        }
    }
}
