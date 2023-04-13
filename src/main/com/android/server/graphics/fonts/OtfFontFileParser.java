package com.android.server.graphics.fonts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontFileUtil;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import com.android.server.graphics.fonts.UpdatableFontDir;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DirectByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.NioUtils;
import java.nio.channels.FileChannel;
/* loaded from: classes.dex */
public class OtfFontFileParser implements UpdatableFontDir.FontFileParser {
    @Override // com.android.server.graphics.fonts.UpdatableFontDir.FontFileParser
    public String getPostScriptName(File file) throws IOException {
        ByteBuffer mmap = mmap(file);
        try {
            return FontFileUtil.getPostScriptName(mmap, 0);
        } finally {
            unmap(mmap);
        }
    }

    @Override // com.android.server.graphics.fonts.UpdatableFontDir.FontFileParser
    public String buildFontFileName(File file) throws IOException {
        ByteBuffer mmap = mmap(file);
        try {
            String postScriptName = FontFileUtil.getPostScriptName(mmap, 0);
            int isPostScriptType1Font = FontFileUtil.isPostScriptType1Font(mmap, 0);
            int isCollectionFont = FontFileUtil.isCollectionFont(mmap);
            if (!TextUtils.isEmpty(postScriptName) && isPostScriptType1Font != -1 && isCollectionFont != -1) {
                String str = isCollectionFont == 1 ? isPostScriptType1Font == 1 ? ".otc" : ".ttc" : isPostScriptType1Font == 1 ? ".otf" : ".ttf";
                return postScriptName + str;
            }
            unmap(mmap);
            return null;
        } finally {
            unmap(mmap);
        }
    }

    @Override // com.android.server.graphics.fonts.UpdatableFontDir.FontFileParser
    public long getRevision(File file) throws IOException {
        ByteBuffer mmap = mmap(file);
        try {
            return FontFileUtil.getRevision(mmap, 0);
        } finally {
            unmap(mmap);
        }
    }

    @Override // com.android.server.graphics.fonts.UpdatableFontDir.FontFileParser
    public void tryToCreateTypeface(File file) throws Throwable {
        ByteBuffer mmap = mmap(file);
        try {
            Typeface build = new Typeface.CustomFallbackBuilder(new FontFamily.Builder(new Font.Builder(mmap).build()).build()).build();
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(24.0f);
            textPaint.setTypeface(build);
            StaticLayout build2 = StaticLayout.Builder.obtain("abcXYZ@- \u1fad6🇺🇸💏🏻👨🏼\u200d❤️\u200d💋\u200d👨🏿", 0, 34, textPaint, (int) Math.ceil(Layout.getDesiredWidth("abcXYZ@- \u1fad6🇺🇸💏🏻👨🏼\u200d❤️\u200d💋\u200d👨🏿", textPaint))).build();
            build2.draw(new Canvas(Bitmap.createBitmap(build2.getWidth(), build2.getHeight(), Bitmap.Config.ALPHA_8)));
        } finally {
            unmap(mmap);
        }
    }

    public static ByteBuffer mmap(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            FileChannel channel = fileInputStream.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
            fileInputStream.close();
            return map;
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static void unmap(ByteBuffer byteBuffer) {
        if (byteBuffer instanceof DirectByteBuffer) {
            NioUtils.freeDirectBuffer(byteBuffer);
        }
    }
}
