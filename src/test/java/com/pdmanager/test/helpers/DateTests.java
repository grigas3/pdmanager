package com.pdmanager.test.helpers;

import com.pdmanager.helpers.ISO8601DateFormat;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

/**
 * Created by george on 17/4/2017.
 */
@RunWith(RobolectricTestRunner.class)
public class DateTests {


    @Test
    public void test_ISO8601() {

        ISO8601DateFormat df = new ISO8601DateFormat();

        try {
            Date d = df.parse("2010-07-28T22:25:51Z");

            Assert.assertEquals(df.format(d),("2010-07-28T22:25:51Z"));

        }
        catch (Exception ex)
        {


        }
    }

    @Test
    public void test_millis() {

        ISO8601DateFormat df = new ISO8601DateFormat();

        try {
            Date d1 =new Date(System.currentTimeMillis());
            Date d2=new Date();

            Assert.assertEquals(df.format(d1),df.format(d2));

        }
        catch (Exception ex)
        {


        }
    }
}