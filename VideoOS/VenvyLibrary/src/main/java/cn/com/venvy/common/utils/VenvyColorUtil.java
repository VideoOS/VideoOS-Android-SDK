package cn.com.venvy.common.utils;

import android.text.TextUtils;

public class VenvyColorUtil {
	private static final String COLOR_SYMBOL = "#";
	private static final String COLOR_RGBA_SYMBOL = "rgba";

	/***
	 * 处理颜色数据
	 * 
	 */
	public static String parseColor(String mColor) {
		if (!TextUtils.isEmpty(mColor)) {
			// RGBA
			if (mColor.contains(COLOR_RGBA_SYMBOL)) {
				String replaceColor = mColor.replaceAll("rgba", "")
						.replaceAll("\\(", "").replaceAll("\\)", "");
				// 分割,
				String[] mIntColor = replaceColor.split("\\,");
				if (mIntColor.length >= 4) {
					int mAlpha = (int) (Float.valueOf(mIntColor[3]) * 255);
					int mRed = Integer.valueOf(mIntColor[0]);
					int mGreen = Integer.valueOf(mIntColor[1]);
					int mBlue = Integer.valueOf(mIntColor[2]);
					String mRgbaColor = COLOR_SYMBOL
							+ Integer.toHexString(mAlpha)
							+ Integer.toHexString(mRed)
							+ Integer.toHexString(mGreen)
							+ Integer.toHexString(mBlue);
					return mRgbaColor;
				}
			} else {
				String mReplaceColor = mColor.replaceAll(COLOR_SYMBOL, "");
				return COLOR_SYMBOL + mReplaceColor;
			}
			// TODO
		}
		return null;
	}
}
