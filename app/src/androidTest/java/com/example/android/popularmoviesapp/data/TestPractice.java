package com.example.android.popularmoviesapp.data;

import android.test.AndroidTestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David on 10/07/16.
 */
public class TestPractice extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testThatDemonstratesAssertions() throws Throwable {
        int a = getFive();
        int b = 3;
        int c = 5;
        int d = 10;

        assertThat(getFive()).isEqualTo(4);
        assertEquals("X should be equal", a, c);
        assertTrue("Y should be true", d > a);
        assertFalse("Z should be false", a == b);

        if (b > d) {
            fail("XX should never happen");
        }
    }

    private int getFive() {
        return 5;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
