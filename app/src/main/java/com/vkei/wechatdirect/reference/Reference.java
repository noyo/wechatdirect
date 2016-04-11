package com.vkei.wechatdirect.reference;

import java.util.HashMap;
import java.util.Map;

/**
 * com.vkei.openwechat.reference
 *
 * @author Chris <br/>
 *         创建日期 2016.03.2016/3/30 <br/>
 *         功能:
 *         修改者，修改日期，修改内容.
 */
public class Reference {
    public final static String WECHAT_MAIN_UI;
    public final static String WECHAT_SCAN;
    public final static String WECHAT_MOMENTS;
    public final static String WECHAT_FIND_BY_DESC;
    public final static String WECHAT_SEARCH_BY_DESC;
    public final static String WECHAT_PACKAGE_NAME;
    public final static String PACKAGE_NAME;

    public final static int RANK_SCAN = 0;//扫一扫
    public final static int RANK_MOMENTS = 1;//朋友圈
    public final static int RANK_MOMENTS_SEND = 2;//朋友圈发布按钮
    public final static int RANK_MOMENTS_SEND_PIC = 3;//朋友圈发布照片
    public final static int RANK_MOMENTS_SEND_VIDEO = 4;//朋友圈发布小视频
//    public final static int RANK_ADD_MP = 100;//一建关注公众号
//    public final static int RANK_ADD_MP_FRIEND = 101;//一建关注公众号->点击朋友按钮
//    public final static int RANK_ADD_MP_INPUT = 102;//一建关注公众号->点击朋友按钮->点击输入
//    public final static int RANK_ADD_MP_SEARCH = 103;//一建关注公众号->点击朋友按钮->点击输入->搜索
    public final static int RANK_MP_SEARCH = 110;//一建关注公众号: 首页搜索按钮
    public final static int RANK_MP_SEARCH_PASTE = 111;//一建关注公众号: 首页搜索按钮->粘贴
    public final static int RANK_MP_SEARCH_RESULT = 112;//一建关注公众号: 首页搜索按钮->粘贴->搜索结果

    static {
        WECHAT_MAIN_UI = "com.tencent.mm.ui.LauncherUI";
        PACKAGE_NAME = "com.vkei.wechatdirect";
        WECHAT_PACKAGE_NAME = "com.tencent.mm";
        WECHAT_SCAN = "wc_scan";
        WECHAT_MOMENTS = "wc_moments";
        WECHAT_FIND_BY_DESC = "更多功能按钮";
        WECHAT_SEARCH_BY_DESC = "搜索";
    }
}
