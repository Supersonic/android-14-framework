package com.android.internal.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.p008os.Bundle;
/* loaded from: classes4.dex */
public class MediaRouteControllerDialogFragment extends DialogFragment {
    public MediaRouteControllerDialogFragment() {
        setCancelable(true);
    }

    public MediaRouteControllerDialog onCreateControllerDialog(Context context, Bundle savedInstanceState) {
        return new MediaRouteControllerDialog(context, getTheme());
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return onCreateControllerDialog(getContext(), savedInstanceState);
    }
}
