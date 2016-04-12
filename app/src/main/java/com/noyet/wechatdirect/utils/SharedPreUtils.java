package com.noyet.wechatdirect.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * com.vkei.openwechat.utils
 *
 * @author Chris <br/>
 *         创建日期 2016.03.2016/3/30 <br/>
 *         功能:
 *         修改者，修改日期，修改内容.
 */
public class SharedPreUtils {
    private static SharedPreferences preferences;
    private static final String SPNAME = "wechat";

    public static void updatePreferences(Context context, String key, int value) {
        preferences = context.getSharedPreferences(SPNAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getPreferences(Context context, String key) {
        preferences = context.getSharedPreferences(SPNAME, 0);
        return preferences == null ? -1 : preferences.getInt(key, -1);
    }
}
