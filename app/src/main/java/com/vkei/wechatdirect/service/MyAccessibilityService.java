package com.vkei.wechatdirect.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.vkei.wechatdirect.R;
import com.vkei.wechatdirect.application.MyApplication;
import com.vkei.wechatdirect.reference.Reference;
import com.vkei.wechatdirect.utils.AccessibilityHelper;

import java.util.List;

/**
 * vser.weichatscan.service
 *
 * @author Chris <br/>
 *         创建日期 2016.03.2016/3/29 <br/>
 *         功能:
 *         修改者，修改日期，修改内容.
 */
public class MyAccessibilityService extends AccessibilityService {
    private final static String TAG = "MyAccessibilityService";

    private static boolean mJumpSuccess = true;
    private static boolean mHasFind = false;

    private boolean mLoading = false;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        boolean flag = (MyApplication.sStartJump
                && Reference.WECHAT_MAIN_UI.equals(event.getClassName())) || mLoading;
        Log.i(TAG, "mLoading == " + mLoading + ", sStartJump == " + MyApplication.sStartJump
                + "," + flag);
        Log.i(TAG, "eventType " + event.getEventType() + ",openType " + MyApplication.sOpenType);
        if ((MyApplication.sStartJump && Reference.WECHAT_MAIN_UI.equals(event.getClassName()))
                || mLoading) {
            Log.i(TAG, "class name : " + event.getClassName().toString());
            if (MyApplication.sOpenType == Reference.RANK_ADD_MP) {
                List<AccessibilityNodeInfo> list = event.getSource().findAccessibilityNodeInfosByText(getString(R.string.wc_scan));
                getNodeInfos(event);
                if (!mHasFind || (list == null || list.size() < 1)) {
                    Log.i(TAG, "hasfind : " + mHasFind);
                    mLoading = true;
                    return;
                }
                Log.i(TAG, "hasfind : " + mHasFind + "," + list.size());
            } else {
                List<AccessibilityNodeInfo> list = getNodeInfos(event);
                if (list == null || list.size() < 1) {
                    mLoading = true;
                    return;
                }
                AccessibilityHelper.performClick(list.get(list.size() - 1));
            }
            mJumpSuccess = true;
            MyApplication.sStartJump = false;
            mLoading = false;
            jumpPage();
        } else if (!isFirstJump()
                && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (MyApplication.sOpenType == Reference.RANK_ADD_MP_SEARCH) {
                AccessibilityNodeInfo info = event.getSource().findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
                if (info == null) {
                    Log.i(TAG, "search");
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle arguments = new Bundle();
                        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "VKEI518");
                        info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                    } else {
                        info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                    }
                }
                return;
            }
            List<AccessibilityNodeInfo> list = getNodeInfos(event);
            if (list == null || list.size() < 1) {
                if (MyApplication.sOpenType == Reference.RANK_MOMENTS_SEND) {
                    mJumpSuccess = mHasFind;
                    mHasFind = false;
                } else {
                    mJumpSuccess = false;
                }/*else if (MyApplication.sOpenType == Reference.RANK_ADD_MP_INPUT) {
                    mJumpSuccess = mHasFind;
                    mHasFind = false;
                }*/
                jumpPage();
                return;
            }
            mJumpSuccess = true;
            jumpPage();
            AccessibilityHelper.performClick(list.get(list.size() - 1));
        }
    }

    /**
     * 判断是否是第一次跳转
     *
     * @return
     */
    private boolean isFirstJump() {
        return MyApplication.sStartJump || MyApplication.sOpenType == Reference.RANK_SCAN
                || MyApplication.sOpenType == Reference.RANK_MOMENTS
                || MyApplication.sOpenType == Reference.RANK_ADD_MP;
    }

    /**
     * 跳转界面
     */
    private static void jumpPage() {
        if (!mJumpSuccess) {
            MyApplication.init();
            return;
        }
        switch (MyApplication.sOpenType) {
            case Reference.RANK_SCAN://当前处于扫一扫界面
                MyApplication.init();
                break;
            case Reference.RANK_MOMENTS://当前出去朋友圈界面
                MyApplication.sOpenType = Reference.RANK_MOMENTS_SEND;
                break;
            case Reference.RANK_MOMENTS_SEND:
                MyApplication.sOpenType = Reference.RANK_MOMENTS_SEND_PIC;
                break;
            case Reference.RANK_MOMENTS_SEND_PIC:
                MyApplication.init();
                break;
            case Reference.RANK_ADD_MP:
                MyApplication.sOpenType = Reference.RANK_ADD_MP_FRIEND;
                break;
            case Reference.RANK_ADD_MP_FRIEND:
                MyApplication.sOpenType = Reference.RANK_ADD_MP_INPUT;
                break;
            case Reference.RANK_ADD_MP_INPUT:
                MyApplication.sOpenType = Reference.RANK_ADD_MP_SEARCH;
                break;
            case Reference.RANK_ADD_MP_SEARCH:
                MyApplication.init();
                break;
        }
    }

    /**
     * 查找目标控件信息
     *
     * @param event 辅助功能响应事件
     * @return
     */
    private List<AccessibilityNodeInfo> getNodeInfos(AccessibilityEvent event) {
        final AccessibilityNodeInfo info = event.getSource();
        switch (MyApplication.sOpenType) {
            case Reference.RANK_SCAN:
                return info.findAccessibilityNodeInfosByText(getString(R.string.wc_scan));
            case Reference.RANK_MOMENTS:
                return info.findAccessibilityNodeInfosByText(getString(R.string.wc_moments));
            case Reference.RANK_MOMENTS_SEND:
                recycleByDes(info, Reference.WECHAT_FIND_BY_DESC);
                return null;
            case Reference.RANK_MOMENTS_SEND_PIC:
                return info.findAccessibilityNodeInfosByText(getString(R.string.wc_releasepic));
            case Reference.RANK_ADD_MP:
                recycleByDes(info, Reference.WECHAT_FIND_BY_DESC);
                return null;
            case Reference.RANK_ADD_MP_FRIEND:
                return info.findAccessibilityNodeInfosByText(getString(R.string.wc_add_friend));
            case Reference.RANK_ADD_MP_INPUT:
                return info.findAccessibilityNodeInfosByText(getString(R.string.wc_add_mp_input));
//            case Reference.RANK_ADD_MP_SEARCH:
//                return info.findFocus();
        }
        return null;
    }

    /**
     * 通过文件描述循环所有子控件
     *
     * @param info      当前节点
     * @param matchFlag 控件的文字描述
     */
    public static void recycleByDes(AccessibilityNodeInfo info, String matchFlag) {
        if (info != null) {
            CharSequence desrc = info.getContentDescription();
            if (desrc != null && matchFlag.equals(desrc.toString().trim())) {
                mHasFind = true;
                AccessibilityHelper.performClick(info);
            } else {
                final int size = info.getChildCount();
                for (int i = 0; i < size; i++) {
                    AccessibilityNodeInfo childInfo = info.getChild(i);
                    if (childInfo != null) {
                        recycleByDes(childInfo, matchFlag);
                    }
                }
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(getApplicationContext(), "onInterrupt", Toast.LENGTH_SHORT).show();
    }
}
