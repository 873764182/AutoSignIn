package com.pixel.asi;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testWeekday(){
        int weekDay = AppUtil.getWeek();
        System.out.print("星期: " + weekDay);
    }

    @Test
    public void testHour(){
        int hour = AppUtil.getHour();
        System.out.println("当前" + hour + "点");
    }

    @Test
    public void testMinute(){
        int minute = AppUtil.getMinute();
        System.out.println("当前" + minute + "分");
    }

}