package android.content.p001pm.parsing.result;
/* renamed from: android.content.pm.parsing.result.ParseResult */
/* loaded from: classes.dex */
public interface ParseResult<ResultType> {
    int getErrorCode();

    String getErrorMessage();

    Exception getException();

    ResultType getResult();

    boolean isError();

    boolean isSuccess();
}
