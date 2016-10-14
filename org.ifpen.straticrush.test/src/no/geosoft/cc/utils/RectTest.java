package no.geosoft.cc.utils;

import junit.framework.Assert;
import no.geosoft.cc.utils.Rect;

import org.junit.Test;

import fr.ifp.kronosflow.geometry.RectD;

public class RectTest {

    @Test
    public void recTest() {
        Rect rectangle = new Rect();
        rectangle.set(0, 0, 10, 10);
        Assert.assertEquals(5, rectangle.getCenterX(), 0);
        Assert.assertEquals(5, rectangle.getCenterY(), 0);

        rectangle.expand(2, 1);

        Assert.assertEquals(-2, rectangle.x, 0);
        Assert.assertEquals(-1, rectangle.y, 0);
        Assert.assertEquals(12, rectangle.height, 0);
        Assert.assertEquals(14, rectangle.width, 0);

        Assert.assertEquals(5, rectangle.getCenterX(), 0);
        Assert.assertEquals(5, rectangle.getCenterY(), 0);

    }

    @Test
    public void rectDCenterAndOffsetTest() {
        RectD rectangle = new RectD();
        rectangle.set(0, 0, 10, 10);
        Assert.assertEquals(5, rectangle.centerX(), 0);
        Assert.assertEquals(5, rectangle.centerY(), 0);

        rectangle.offset(2, 1);

        Assert.assertEquals(2, rectangle.left, 0);
        Assert.assertEquals(11, rectangle.bottom, 0);
        Assert.assertEquals(12, rectangle.right, 0);
        Assert.assertEquals(1, rectangle.top, 0);

        Assert.assertEquals(7, rectangle.centerX(), 0);
        Assert.assertEquals(6, rectangle.centerY(), 0);
    }

    @Test
    public void rectDInsetTest() {
        RectD rectangle = new RectD();
        rectangle.set(0, 0, 10, 10);

        rectangle.inset(2, 1);

        Assert.assertEquals(2, rectangle.left, 0);
        Assert.assertEquals(9, rectangle.bottom, 0);
        Assert.assertEquals(8, rectangle.right, 0);
        Assert.assertEquals(1, rectangle.top, 0);

        rectangle.inset(-2, -1);

        Assert.assertEquals(0, rectangle.left, 0);
        Assert.assertEquals(10, rectangle.bottom, 0);
        Assert.assertEquals(10, rectangle.right, 0);
        Assert.assertEquals(0, rectangle.top, 0);
    }

    @Test
    public void rectDContainsTest() {
        RectD rectangle = new RectD();
        rectangle.set(0, 0, 10, 10);

        RectD rectangle2 = new RectD();
        rectangle2.set(5, 5, 9, 9);

        Assert.assertTrue(rectangle.contains(rectangle2));
        Assert.assertFalse(rectangle2.contains(rectangle));
        Assert.assertTrue(rectangle.contains(5, 5, 9, 9));
        Assert.assertFalse(rectangle2.contains(0, 0, 10, 10));
    }

    @Test
    public void rectDIntersectTest() {
        RectD rectangle = new RectD();
        rectangle.set(0, 0, 10, 10);

        RectD rectangle2 = new RectD();
        rectangle2.set(5, 5, 9, 9);

        Assert.assertTrue(rectangle.intersect(rectangle2));
        rectangle.set(0, 0, 10, 10);
        Assert.assertTrue(rectangle2.intersect(rectangle));
        Assert.assertTrue(rectangle.intersect(5, 5, 9, 9));
        rectangle2.set(5, 5, 9, 9);
        Assert.assertTrue(rectangle2.intersect(0, 0, 10, 10));

        rectangle.set(0, 0, 10, 10);
        rectangle2.set(-10, -10, -0.1, 5);
        Assert.assertFalse(rectangle.intersect(rectangle2));
        rectangle.set(0, 0, 10, 10);
        Assert.assertFalse(rectangle.intersect(-10, -10, -0.1, 5));

        rectangle.set(0, 0, 10, 10);
        rectangle2.set(-10, -10, 1, 5);
        Assert.assertTrue(rectangle.intersect(rectangle2));
        rectangle.set(0, 0, 10, 10);
        Assert.assertTrue(rectangle.intersect(-10, -10, 1, 5));

    }

    @Test
    public void rectDUnionTest() {
        RectD rectangle = new RectD();
        rectangle.set(0, 0, 10, 10);

        RectD rectangle2 = new RectD();
        rectangle2.set(5, 5, 9, 9);

        rectangle.union(rectangle2);
        Assert.assertEquals(0, rectangle.left, 0);
        Assert.assertEquals(10, rectangle.bottom, 0);
        Assert.assertEquals(10, rectangle.right, 0);
        Assert.assertEquals(0, rectangle.top, 0);

        rectangle2.set(2, 2, 15, 15);
        rectangle.union(rectangle2);
        Assert.assertEquals(0, rectangle.left, 0);
        Assert.assertEquals(15, rectangle.bottom, 0);
        Assert.assertEquals(15, rectangle.right, 0);
        Assert.assertEquals(0, rectangle.top, 0);

        rectangle2.set(-1, -3, 10, 10);
        rectangle.union(rectangle2);
        Assert.assertEquals(-1, rectangle.left, 0);
        Assert.assertEquals(15, rectangle.bottom, 0);
        Assert.assertEquals(15, rectangle.right, 0);
        Assert.assertEquals(-3, rectangle.top, 0);
    }

}
