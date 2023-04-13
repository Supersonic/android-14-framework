package com.android.internal.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Icon;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.internal.C4057R;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.util.ContrastColorUtil;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
/* loaded from: classes5.dex */
public class PeopleHelper {
    private static final float COLOR_SHIFT_AMOUNT = 60.0f;
    private static final Pattern IGNORABLE_CHAR_PATTERN = Pattern.compile("[\\p{C}\\p{Z}]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
    private int mAvatarSize;
    private Context mContext;
    private Paint mPaint = new Paint(1);
    private Paint mTextPaint = new Paint();

    public void init(Context context) {
        this.mContext = context;
        this.mAvatarSize = context.getResources().getDimensionPixelSize(C4057R.dimen.messaging_avatar_size);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mTextPaint.setAntiAlias(true);
    }

    public void animateViewForceHidden(final CachingIconView view, final boolean forceHidden) {
        boolean nowForceHidden = view.willBeForceHidden() || view.isForceHidden();
        if (forceHidden == nowForceHidden) {
            return;
        }
        view.animate().cancel();
        view.setWillBeForceHidden(forceHidden);
        view.animate().scaleX(forceHidden ? 0.5f : 1.0f).scaleY(forceHidden ? 0.5f : 1.0f).alpha(forceHidden ? 0.0f : 1.0f).setInterpolator(forceHidden ? MessagingPropertyAnimator.ALPHA_OUT : MessagingPropertyAnimator.ALPHA_IN).setDuration(160L);
        if (view.getVisibility() != 0) {
            view.setForceHidden(forceHidden);
        } else {
            view.animate().withEndAction(new Runnable() { // from class: com.android.internal.widget.PeopleHelper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CachingIconView.this.setForceHidden(forceHidden);
                }
            });
        }
        view.animate().start();
    }

    public Icon createAvatarSymbol(CharSequence name, String symbol, int layoutColor) {
        float f;
        float f2;
        if (symbol.isEmpty() || TextUtils.isDigitsOnly(symbol) || SPECIAL_CHAR_PATTERN.matcher(symbol).find()) {
            Icon avatarIcon = Icon.createWithResource(this.mContext, (int) C4057R.C4058drawable.messaging_user);
            avatarIcon.setTint(findColor(name, layoutColor));
            return avatarIcon;
        }
        int i = this.mAvatarSize;
        Bitmap bitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float radius = this.mAvatarSize / 2.0f;
        int color = findColor(name, layoutColor);
        this.mPaint.setColor(color);
        canvas.drawCircle(radius, radius, radius, this.mPaint);
        boolean needDarkText = ColorUtils.calculateLuminance(color) > 0.5d;
        this.mTextPaint.setColor(needDarkText ? -16777216 : -1);
        Paint paint = this.mTextPaint;
        if (symbol.length() == 1) {
            f = this.mAvatarSize;
            f2 = 0.5f;
        } else {
            f = this.mAvatarSize;
            f2 = 0.3f;
        }
        paint.setTextSize(f * f2);
        int yPos = (int) (radius - ((this.mTextPaint.descent() + this.mTextPaint.ascent()) / 2.0f));
        canvas.drawText(symbol, radius, yPos, this.mTextPaint);
        return Icon.createWithBitmap(bitmap);
    }

    private int findColor(CharSequence senderName, int layoutColor) {
        double luminance = ContrastColorUtil.calculateLuminance(layoutColor);
        float shift = ((Math.abs(senderName.hashCode()) % 5) / 4.0f) - 0.5f;
        return ContrastColorUtil.getShiftedColor(layoutColor, (int) (60.0f * ((float) (((float) (shift + Math.max(0.30000001192092896d - luminance, 0.0d))) - Math.max(0.30000001192092896d - (1.0d - luminance), 0.0d)))));
    }

    private String getPureName(CharSequence name) {
        return IGNORABLE_CHAR_PATTERN.matcher(name).replaceAll("");
    }

    public String findNamePrefix(CharSequence name, String fallback) {
        String pureName = getPureName(name);
        if (pureName.isEmpty()) {
            return fallback;
        }
        try {
            return new String(Character.toChars(pureName.codePointAt(0)));
        } catch (RuntimeException e) {
            return fallback;
        }
    }

    public String findNameSplit(CharSequence name) {
        String nameString = name instanceof String ? (String) name : name.toString();
        String[] split = nameString.trim().split("[ ]+");
        if (split.length > 1) {
            String first = findNamePrefix(split[0], null);
            String second = findNamePrefix(split[1], null);
            if (first != null && second != null) {
                return first + second;
            }
        }
        return findNamePrefix(name, "");
    }

    public Map<CharSequence, String> mapUniqueNamesToPrefix(List<MessagingGroup> groups) {
        String charPrefix;
        ArrayMap<CharSequence, String> uniqueNames = new ArrayMap<>();
        ArrayMap<String, CharSequence> uniqueCharacters = new ArrayMap<>();
        for (int i = 0; i < groups.size(); i++) {
            MessagingGroup group = groups.get(i);
            CharSequence senderName = group.getSenderName();
            if (group.needsGeneratedAvatar() && !TextUtils.isEmpty(senderName) && !uniqueNames.containsKey(senderName) && (charPrefix = findNamePrefix(senderName, null)) != null) {
                if (uniqueCharacters.containsKey(charPrefix)) {
                    CharSequence existingName = uniqueCharacters.get(charPrefix);
                    if (existingName != null) {
                        uniqueNames.put(existingName, findNameSplit(existingName));
                        uniqueCharacters.put(charPrefix, null);
                    }
                    uniqueNames.put(senderName, findNameSplit(senderName));
                } else {
                    uniqueNames.put(senderName, charPrefix);
                    uniqueCharacters.put(charPrefix, senderName);
                }
            }
        }
        return uniqueNames;
    }

    public void maybeHideFirstSenderName(List<MessagingGroup> groups, boolean isOneToOne, CharSequence conversationTitle) {
        for (int i = groups.size() - 1; i >= 0; i--) {
            MessagingGroup messagingGroup = groups.get(i);
            CharSequence messageSender = messagingGroup.getSenderName();
            boolean canHide = isOneToOne && TextUtils.equals(conversationTitle, messageSender);
            messagingGroup.setCanHideSenderIfFirst(canHide);
        }
    }
}
