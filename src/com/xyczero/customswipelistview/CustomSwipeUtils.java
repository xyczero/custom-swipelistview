/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, xyczero <xiayuncheng1991@gmail.com>
 *  
 *  	http://www.xyczero.com/
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    CustomSwipeUtils.java
 *  @brief   Custom Swipe Tool Set
 *  
 *  @version 1.0     
 *  @author  xyczero
 *  @date    2015/01/12    
 */
package com.xyczero.customswipelistview;

import android.content.Context;

/**
 * Common utils is designed for CustomSwipeListview
 * 
 * @author xyczero
 * 
 */
public class CustomSwipeUtils {

	/**
	 * convert dp(dip) to pixel
	 * 
	 * @param context
	 *            The context of the Activity
	 * @param dpValue
	 *            The value of dp(dip)
	 * @return
	 */
	public static int convertDptoPx(Context context, final float dpValue) {
		final float density = context.getResources().getDisplayMetrics().density;
		// +0.5f is designed to prevent that one dip convert zero pixel when the
		// machine's density is less than one;
		return (int) (dpValue * density + 0.5f);
	}

	/**
	 * 
	 * @param context
	 *            The context of the Activity
	 * @return get the width of the screen in pixel
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}
}
