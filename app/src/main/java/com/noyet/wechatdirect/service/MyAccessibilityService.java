package com.noyet.wechatdirect.service;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.noyet.wechatdirect.R;
import com.noyet.wechatdirect.application.MyApplication;
import com.noyet.wechatdirect.reference.Reference;
import com.noyet.wechatdirect.utils.AccessibilityHelper;

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

    private AccessibilityNodeInfo descInfo = null;
    private boolean mHasFind = false;
    private boolean mJumpSuccess = true;
    private boolean mLoading = false;
    private boolean mSearching = false;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(TAG, "mLoading == " + mLoading + ", sStartJump == " + MyApplication.sStartJump);
        Log.i(TAG, "eventType " + event.getEventType() + ",openType " + MyApplication.sOpenType);

        if ((MyApplication.sStartJump && Reference.WECHAT_MAIN_UI.equals(event.getClassName()))
                || mLoading) {
            Log.i(TAG, "class name : " + event.getClassName().toString());
            startFirstJump(event);
        } else if (!isFirstJump()
                && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                || isSearching(event)) {
            startJump(event);
        }
    }

    /**
     * 进行第一次界面跳转
     *
     * @param event 辅助功能响应事件
     */
    private void startFirstJump(AccessibilityEvent event) {
        if (MyApplication.sOpenType == Reference.RANK_MP_SEARCH) {
            List<AccessibilityNodeInfo> list = null;
            if (event.getSource() != null) {
                list = event.getSource()
                        .findAccessibilityNodeInfosByText(getString(R.string.wc_scan));
            }
            getNodeInfos(event);
            if (!mHasFind || (list == null || list.size() < 1)) {
                Log.i(TAG, "hasfind : " + mHasFind + ", list: " + (list == null ? null : list.size()));
                mLoading = true;
                return;
            }
            mHasFind = false;
            Log.i(TAG, "hasfind : " + mHasFind + ", list: " + list.size());
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
    }

    /**
     * 进行界面跳转
     *
     * @param event 辅助功能响应事件
     */
    private void startJump(AccessibilityEvent event) {
        mSearching = false;
        if (MyApplication.sOpenType == Reference.RANK_MP_SEARCH_PASTE) {
            AccessibilityNodeInfo info = null;
            if (event.getSource() != null) {
                info = event.getSource()
                        .findFocus(AccessibilityNodeInfo.FOCUS_INPUT);
            }
            if (info == null) {
                Log.i(TAG, "search");
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(
                            AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE
                            , "VKEI518");
                    info.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                } else {
                    info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                }
            }
        }
        List<AccessibilityNodeInfo> list = getNodeInfos(event);
        if (list == null || list.size() < 1) {
            if (MyApplication.sOpenType == Reference.RANK_MP_SEARCH_PASTE) {
                mSearching = true;
                return;
            } else if (MyApplication.sOpenType == Reference.RANK_MOMENTS_SEND) {
                mJumpSuccess = mHasFind;
                mHasFind = false;
            } else {
                mJumpSuccess = false;
            }
            jumpPage();
            return;
        }
        mJumpSuccess = true;
        jumpPage();
        AccessibilityHelper.performClick(list.get(list.size() - 1));
    }

    /**
     * 是否正在查询公众号
     *
     * @return
     */
    private boolean isSearching(AccessibilityEvent event) {
        return MyApplication.sOpenType == Reference.RANK_MP_SEARCH_PASTE
                && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                && mSearching;
    }

    /**
     * 判断是否是第一次跳转
     *
     * @return
     */
    private boolean isFirstJump() {
        return MyApplication.sStartJump || MyApplication.sOpenType == Reference.RANK_SCAN
                || MyApplication.sOpenType == Reference.RANK_MOMENTS
                || MyApplication.sOpenType == Reference.RANK_MP_SEARCH;
    }

    /**
     * 跳转界面
     */
    private void jumpPage() {
        if (!mJumpSuccess) {
            Log.i(TAG, "jump error: openType == " + MyApplication.sOpenType);
            MyApplication.init();
            return;
        }
        switch (MyApplication.sOpenType) {
            case Reference.RANK_SCAN://当前处于扫一扫界面
                MyApplication.init();
                break;
            case Reference.RANK_MOMENTS://当前处于朋友圈界面
                MyApplication.sOpenType = Reference.RANK_MOMENTS_SEND;
                break;
            case Reference.RANK_MOMENTS_SEND:
                MyApplication.sOpenType = Reference.RANK_MOMENTS_SEND_PIC;
                break;
            case Reference.RANK_MOMENTS_SEND_PIC:
                MyApplication.init();
                break;
            case Reference.RANK_MP_SEARCH:
                MyApplication.sOpenType = Reference.RANK_MP_SEARCH_PASTE;
                break;
            case Reference.RANK_MP_SEARCH_PASTE:
                MyApplication.sOpenType = Reference.RANK_MP_SEARCH_RESULT;
                break;
            case Reference.RANK_MP_SEARCH_RESULT:
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

            case Reference.RANK_MP_SEARCH:
                recycleByDes(info, Reference.WECHAT_SEARCH_BY_DESC);
                return null;
            case Reference.RANK_MP_SEARCH_PASTE:
                return info.findAccessibilityNodeInfosByText(getString(R.string.wc_mp_search_text));
        }
        return null;
    }

    /**
     * 通过文件描述循环所有子控件
     *
     * @param info      当前节点
     * @param matchFlag 控件的文字描述
     */
    public void recycleByTxt(AccessibilityNodeInfo info, String matchFlag) {
        if (info != null) {
            CharSequence desrc = info.getText();
            if (desrc != null && matchFlag.equals(desrc.toString().trim())) {
                mHasFind = true;
                AccessibilityHelper.performClick(info);
            } else {
                final int size = info.getChildCount();
                for (int i = 0; i < size; i++) {
                    AccessibilityNodeInfo childInfo = info.getChild(i);
                    if (childInfo != null) {
                        recycleByTxt(childInfo, matchFlag);
                    }
                }
            }
        }
    }

    /**
     * 通过文件描述循环所有子控件
     *
     * @param info      当前节点
     * @param matchFlag 控件的文字描述
     */
    public void recycleByDes(AccessibilityNodeInfo info, String matchFlag) {
        if (info != null) {
            CharSequence desrc = info.getContentDescription();
            if (desrc != null && matchFlag.equals(desrc.toString().trim())) {
                mHasFind = true;
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                descInfo = info;
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
