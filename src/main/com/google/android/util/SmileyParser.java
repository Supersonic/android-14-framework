package com.google.android.util;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import com.google.android.util.AbstractMessageParser;
import java.util.ArrayList;
/* loaded from: classes5.dex */
public class SmileyParser extends AbstractMessageParser {
    private SmileyResources mRes;

    public SmileyParser(String text, SmileyResources res) {
        super(text, true, false, false, false, false, false);
        this.mRes = res;
    }

    @Override // com.google.android.util.AbstractMessageParser
    protected AbstractMessageParser.Resources getResources() {
        return this.mRes;
    }

    public CharSequence getSpannableString(Context context) {
        int resid;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (getPartCount() == 0) {
            return "";
        }
        AbstractMessageParser.Part part = getPart(0);
        ArrayList<AbstractMessageParser.Token> tokens = part.getTokens();
        int len = tokens.size();
        for (int i = 0; i < len; i++) {
            AbstractMessageParser.Token token = tokens.get(i);
            int start = builder.length();
            builder.append((CharSequence) token.getRawText());
            if (token.getType() == AbstractMessageParser.Token.Type.SMILEY && (resid = this.mRes.getSmileyRes(token.getRawText())) != -1) {
                builder.setSpan(new ImageSpan(context, resid), start, builder.length(), 33);
            }
        }
        return builder;
    }
}
