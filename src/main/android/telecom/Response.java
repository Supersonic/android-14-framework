package android.telecom;
/* loaded from: classes3.dex */
public interface Response<IN, OUT> {
    void onError(IN in, int i, String str);

    void onResult(IN in, OUT... outArr);
}
