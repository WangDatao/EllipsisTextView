package com.easy.lindatao.ellipsistextview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.easy.lindatao.ellipsistextview.R;

import java.util.ArrayList;

/**
 * Created by lindatao on 2016/7/10.
 */
public class EllipsisTextView extends View {

    private static final String TAG = "EllipsisTextView";

    private TextPaint mTextPaint;

    private Paint mBitmapPaint;// 用于背景和图片的画笔

    private String mText = "";

    private String mEllipsis;

    private Paint.FontMetrics mFontMetrics; // 字体属性

    private int mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()); // 文字大小

    private int mTextColor = Color.BLACK;// 文字颜色

    private float mPerLineHeight = 0; // 每行文字的高度,等于mFontMetrics.bottom - mFontMeticics.top ,控件高度就是这个值得倍数

    private Context mContext;

    private int mMaxLine = 1;// 最大行数

    private int mLineSpace = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()); // 行间距，具体数值

    private int mDrawableMarginLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());

    private ArrayList<Drawable> mDrawables = new ArrayList<>();

    private int mViewWidth;

    private float mDrawTextX;

    private float mDrawTextY;

    public EllipsisTextView(Context context)
    {
        super(context);
        init(context, null);
    }

    public EllipsisTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public EllipsisTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        mContext = context;
        // 获取自定义属性
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.EllipsisTextView);
        int count = typedArray.getIndexCount();
        for (int i = 0; i < count; i++)
        {
            int attr = typedArray.getIndex(i);
            switch (attr)
            {
                case R.styleable.EllipsisTextView_textSize:
                    mTextSize = typedArray.getDimensionPixelSize(attr, mTextSize);
                    break;
                case R.styleable.EllipsisTextView_textColor:
                    mTextColor = typedArray.getColor(attr, mTextColor);
                    break;
                case R.styleable.EllipsisTextView_maxLine:
                    mMaxLine = typedArray.getInt(attr, mMaxLine);
                    break;
                case R.styleable.EllipsisTextView_lineSpace:
                    mLineSpace = typedArray.getDimensionPixelSize(attr, mLineSpace);
                    break;
                case R.styleable.EllipsisTextView_drawableMarginLeft:
                    mDrawableMarginLeft = typedArray.getDimensionPixelSize(attr,mDrawableMarginLeft);
                    break;

            }
        }
        typedArray.recycle();

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mFontMetrics = mTextPaint.getFontMetrics();
        mPerLineHeight = mFontMetrics.bottom - mFontMetrics.top;

        mBitmapPaint = new Paint();
        mBitmapPaint.setColor(Color.TRANSPARENT);

    }

    public void setText(String text)
    {

        if (!TextUtils.isEmpty(text))
        {
            mText = text;
        }
        else
        {
            mText = " ";
        }
        requestLayout();
        invalidate();
    }

    public void append(int drawableId)
    {
        Drawable drawable = mContext.getResources().getDrawable(drawableId);
        mDrawables.add(drawable);
        requestLayout();
        invalidate();
    }

    public void append(Drawable drawable)
    {
        mDrawables.add(drawable);
        requestLayout();
        invalidate();
    }

    // 未考虑图片高度大于文字的情况
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);

        // 获取省略后的文字
        mEllipsis = (String) ellipsis(mText, mMaxLine);

        int height = 0;
        float totalWidth = 0;
        float ellipsisTextWidth = mTextPaint.measureText((String) mEllipsis);


        totalWidth += ellipsisTextWidth;
        for (int j = 0; j < mDrawables.size(); j++)
        {
            totalWidth += mDrawableMarginLeft + mDrawables.get(j).getIntrinsicWidth();
        }
        int lineCount = (int) Math.ceil(totalWidth / mViewWidth); // 行数

        for (int i = 0; i < lineCount; i++)
        {
            if (i == 0) // 第一行
            {
                height += mPerLineHeight;
            }
            else
            {
                height += mPerLineHeight + mLineSpace;
            }
        }

        setMeasuredDimension(mViewWidth, height);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // 画背景
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBitmapPaint);
        mBitmapPaint.setColor(Color.WHITE);

        // 画文字
        if (mEllipsis == null)
        {
            return;
        }

        int currentLine = 0;

        // 每行已绘的宽度
        mDrawTextX = 0;
        // 文字y
        mDrawTextY = Math.abs(mFontMetrics.top);

        currentLine = drawText(canvas, mEllipsis, currentLine);

        // 画图片

        float left = mDrawTextX;

        float top = 0;

        for (int j = 0; j < mDrawables.size(); j++)
        {

            Drawable drawable = mDrawables.get(j);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            int drawableIntrinsicWidth = drawable.getIntrinsicWidth();
            if (mViewWidth - left > drawableIntrinsicWidth)
            {
                left += mDrawableMarginLeft;
            }
            else
            {
                currentLine++;
                left = 0;
            }

            top = mPerLineHeight / 2 - drawable.getIntrinsicHeight() / 2 + (mPerLineHeight + mLineSpace) * currentLine;

            canvas.drawBitmap(bitmap, left, top, mBitmapPaint);

            left += drawableIntrinsicWidth;

        }
    }

    private int drawText(Canvas canvas, String drawStr, int currentLine)
    {
        char[] chars = drawStr.toCharArray();
        float charWidth;


        for (int i = 0; i < chars.length; i++)
        {
            charWidth = mTextPaint.measureText(chars, i, 1);

            if (mViewWidth - mDrawTextX >= charWidth)
            {
                canvas.drawText(chars, i, 1, mDrawTextX, mDrawTextY, mTextPaint);
                mDrawTextX += charWidth;
            }
            else
            {
                currentLine++;
                mDrawTextX = 0;
                mDrawTextY += mPerLineHeight + mLineSpace;

                //每次换行,都重新进行ellipsis,减少换行空白导致的误差
                String substring;

                substring = drawStr.substring(i, drawStr.length());

                CharSequence ellipsis = ellipsis(substring, mMaxLine - currentLine);

                currentLine = drawText(canvas, (String) ellipsis, currentLine);
                break;
            }
        }
        return currentLine;
    }

    private CharSequence ellipsis(CharSequence charSequence, int lineNum)
    {

        float availableWidth = mViewWidth * lineNum;
        for (int i = 0; i < mDrawables.size(); i++)
        {
            availableWidth = availableWidth - mDrawableMarginLeft - mDrawables.get(i).getIntrinsicWidth();
        }
        return TextUtils.ellipsize(charSequence, mTextPaint, availableWidth, TextUtils.TruncateAt.END);
    }

    /**
     * listView中使用时，先清除存储的drawales
     */
    public void clearDrawable()
    {
        mDrawables.clear();
    }
}
