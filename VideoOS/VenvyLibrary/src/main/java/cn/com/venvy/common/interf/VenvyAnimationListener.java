package cn.com.venvy.common.interf;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * This adapter class provides empty implementations of the methods from
 * {@link android.view.animation.Animation.AnimationListener}.
 * Any custom listener that cares only about a subset of the methods of this listener can
 * simply subclass this adapter class instead of implementing the interface directly.
 */
public abstract class VenvyAnimationListener implements AnimationListener{
	@Override
	public void onAnimationEnd(Animation animation) {
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

}
