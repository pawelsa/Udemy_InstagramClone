package com.example.pawel.udemy_instagramclone;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Pawel on 16.02.2018.
 */

public class SquareCropView extends RelativeLayout {
    public SquareCropView(@NonNull Context context) {
        super(context);
    }

    public SquareCropView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareCropView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(widthSize, widthSize);
    }
}
