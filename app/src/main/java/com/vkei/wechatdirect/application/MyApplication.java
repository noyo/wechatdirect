package com.vkei.wechatdirect.application;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * vser.weichatscan.application
 *
 * @author Chris <br/>
 *         创建日期 2016.03.2016/3/29 <br/>
 *         功能:
 *         修改者，修改日期，修改内容.
 */
public class MyApplication extends Application {
    public static int sScanRank;//当前进入的微信界面层级， 0表示还未进入
    public static int sMomentsRank;//当前进入的微信界面层级， 0表示还未进入
    public static int sOpenType;//0-扫一扫  1-朋友圈 2-聊天界面
    public static Map<Integer, Integer> sDoubleClick;//强制双击
    public static boolean sStartJump;
    private static MyApplication instance;

    static {
        sStartJump = false;
        instance = new MyApplication();
        sDoubleClick = new HashMap<>();
        sDoubleClick.put(0, 0);
        sDoubleClick.put(1, 0);
        sDoubleClick.put(2, 0);
    }

    private MyApplication() {
    }

    public static MyApplication getInstance() {
        return instance;
    }


    public static void init() {
        sOpenType = 0;
        sScanRank = 0;
        sMomentsRank = 0;
        sStartJump = false;
        sDoubleClick.put(0, 0);
        sDoubleClick.put(1, 0);
        sDoubleClick.put(2, 0);
        Log.i(MyApplication.class.getSimpleName(), "init()");
    }
}
