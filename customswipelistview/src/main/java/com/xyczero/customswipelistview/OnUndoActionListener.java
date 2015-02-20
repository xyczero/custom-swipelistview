/*
 *  COPYRIGHT NOTICE  
 *  Copyright (C) 2015, xyczero <xiayuncheng1991@gmail.com>
 *  
 *  	http://www.xyczero.com/
 *   
 *  @license under the Apache License, Version 2.0 
 *
 *  @file    OnUndoActionListener.java
 *  @brief   Custom Swipe Undo Action Listener Interface
 *  
 *  @version 1.0     
 *  @author  xyczero
 *  @date    2015/01/12    
 */
package com.xyczero.customswipelistview;

/**
 * That is the bridge between a {@link com.xyczero.customswipelistview.CustomSwipeBaseAdapter} and a
 * {@link com.xyczero.customswipelistview.CustomSwipeUndoDialog} that listens revocation event.
 * 
 * @author xyczero
 * 
 */
public interface OnUndoActionListener {

	/**
	 * Revocation event to be canceled.
	 */
	public void noExecuteUndoAction();

	/**
	 * Revocation event to be executed.
	 */
	public void executeUndoAction();
}
