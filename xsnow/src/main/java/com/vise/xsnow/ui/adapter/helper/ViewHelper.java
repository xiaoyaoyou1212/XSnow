package com.vise.xsnow.ui.adapter.helper;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.Adapter;

import com.vise.xsnow.ui.adapter.BaseViewHolder;

public interface ViewHelper<T extends BaseViewHolder> {
    /**
     * 设置textView文本内容
     *
     * @param viewId viewId
     * @param value  文本内容
     * @return viewHolder viewHolder viewHolder
     */
    T setText(int viewId, String value);

    /**
     * 设置imgView的图片,通过Id设置
     *
     * @param viewId   viewId
     * @param imgResId 图片Id
     * @return viewHolder viewHolder
     */
    T setImageResource(int viewId, int imgResId);

    /**
     * 设置背景颜色
     *
     * @param viewId viewId
     * @param color  颜色数值
     * @return viewHolder viewHolder
     */
    T setBackgroundColor(int viewId, int color);

    /**
     * 设置背景颜色
     *
     * @param viewId   viewId
     * @param colorRes 颜色Id
     * @return viewHolder
     */
    T setBackgroundColorRes(int viewId, int colorRes);

    /**
     * 设置textView文本颜色
     *
     * @param viewId viewId
     * @param color  颜色数值
     * @return viewHolder
     */
    T setTextColor(int viewId, int color);

    /**
     * 设置textView文本颜色
     *
     * @param viewId   viewId
     * @param colorRes 颜色Id
     * @return viewHolder
     */
    T setTextColorRes(int viewId, int colorRes);

    /**
     * 设置img的Drawable
     *
     * @param viewId   viewId
     * @param drawable drawable
     * @return viewHolder
     */
    T setImageDrawable(int viewId, Drawable drawable);

    /**
     * 设置img的Drawable
     *
     * @param viewId      viewId
     * @param drawableRes drawableId
     * @return viewHolder
     */
    T setImageDrawableRes(int viewId, int drawableRes);


    /**
     * 设置img图片路径
     *
     * @param viewId viewId
     * @param imgUrl 图片路径
     * @return viewHolder
     */
    T setImageUrl(int viewId, String imgUrl);

    /**
     * 设置img图片Bitmap
     *
     * @param viewId    viewId
     * @param imgBitmap imgBitmap
     * @return viewHolder
     */
    T setImageBitmap(int viewId, Bitmap imgBitmap);

    /**
     * 设置控件是否隐藏
     *
     * @param viewId  viewId
     * @param visible visible
     * @return viewHolder
     */
    T setVisible(int viewId, boolean visible);

    /**
     * 设置控件的tag
     *
     * @param viewId viewId
     * @param tag    tag
     * @return viewHolder
     */
    T setTag(int viewId, Object tag);

    /**
     * 设置控件tag
     *
     * @param viewId viewId
     * @param key    tag的key
     * @param tag    tag
     * @return viewHolder
     */
    T setTag(int viewId, int key, Object tag);

    /**
     * 设置Checkable控件的选择情况
     *
     * @param viewId  viewId
     * @param checked 选择
     * @return viewHolder
     */
    T setChecked(int viewId, boolean checked);

    /**
     * 设置absListView的Adapter
     *
     * @param viewId  viewId
     * @param adapter adapter
     * @return viewHolder
     */
    T setAdapter(int viewId, Adapter adapter);

    /**
     * 设置控件透明效果
     *
     * @param viewId viewId
     * @param value  透明值
     * @return viewHolder
     */
    T setAlpha(int viewId, float value);

    /**
     * 设置TextView字体
     *
     * @param viewId   viewId
     * @param typeface typeface
     * @return viewHolder
     */
    T setTypeface(int viewId, Typeface typeface);

    /**
     * 设置多个TextView字体
     *
     * @param typeface typeface
     * @param viewIds  viewId组合
     * @return viewHolder
     */
    T setTypeface(Typeface typeface, int... viewIds);

    /**
     * 设置ProgressBar控件进度
     *
     * @param viewId   viewId
     * @param progress progress
     * @return viewHolder
     */
    T setProgress(int viewId, int progress);

    /**
     * 设置ProgressBar控件进度
     *
     * @param viewId   viewId
     * @param progress progress
     * @param max      max
     * @return viewHolder
     */
    T setProgress(int viewId, int progress, int max);

    /**
     * 设置ProgressBar控件最大进度值
     *
     * @param viewId viewId
     * @param max    max
     * @return viewHolder
     */
    T setMax(int viewId, int max);
}
