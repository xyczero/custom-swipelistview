package com.xyczero.customswipelistview;

import android.content.Context;

/**
 * 
 * @author xyczero
 * 
 *         welcome to www.xyczero.com
 * 
 */
public class CustomSwipeUtils {
	/**
	 * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
	 */
	public static int convertDiptoPixel(Context context, final float dpValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		// 0.5f:防止当density小于1时,1dip转换为px时为0的情况。
		return (int) (dpValue * density + 0.5f);
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}
}
