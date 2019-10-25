package com.hon.sunny;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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

    @Test
    public void testRxJavaDispose() {
        Disposable disposable = Observable.just(1)
                .map(i -> {
                    Thread.sleep(1000);
                    return "hello world";
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                        System.out::println,
                        Throwable::printStackTrace
                );

        disposable.dispose();//直接dispose，事件将被取消
    }
}