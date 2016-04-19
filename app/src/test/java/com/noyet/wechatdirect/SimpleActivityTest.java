package com.noyet.wechatdirect;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.*;

/**
 * com.noyet.wechatdirect
 *
 * @author Chris <br/>
 *         创建日期 2016.04.2016/4/13 <br/>
 *         功能:
 *         修改者，修改日期，修改内容.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SimpleActivityTest {
    @Test
    public void testActivity() {
        MainActivity sampleActivity = Robolectric.setupActivity(MainActivity.class);
        assertNotNull(sampleActivity);
        assertEquals(sampleActivity.getTitle(), "SimpleActivity");
    }
}
