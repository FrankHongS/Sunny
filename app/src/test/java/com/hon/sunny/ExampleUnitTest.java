package com.hon.sunny;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void test01() {
        List<String> l1 = new ArrayList<>();
        l1.add("a");

        List<String> l2 = l1;
        l2.clear();

        assertSame(l1, l2);
    }
}