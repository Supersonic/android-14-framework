package com.android.internal.policy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
/* loaded from: classes4.dex */
public class PhoneLayoutInflater extends LayoutInflater {
    private static final String[] sClassPrefixList = {"android.widget.", "android.webkit.", "android.app."};

    public PhoneLayoutInflater(Context context) {
        super(context);
    }

    protected PhoneLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.LayoutInflater
    public View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        String[] strArr;
        View view;
        for (String prefix : sClassPrefixList) {
            try {
                view = createView(name, prefix, attrs);
            } catch (ClassNotFoundException e) {
            }
            if (view != null) {
                return view;
            }
        }
        return super.onCreateView(name, attrs);
    }

    @Override // android.view.LayoutInflater
    public LayoutInflater cloneInContext(Context newContext) {
        return new PhoneLayoutInflater(this, newContext);
    }
}
