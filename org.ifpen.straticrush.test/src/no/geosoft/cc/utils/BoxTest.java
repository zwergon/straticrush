package no.geosoft.cc.utils;

import junit.framework.Assert;
import no.geosoft.cc.utils.Box;
import no.geosoft.cc.utils.Rect;

import org.junit.Test;

public class BoxTest {

	@Test
	public void isInsideTest() {
		Box box=new Box(0,0,10,10);
		
		Assert.assertTrue(box.isInside(1, 1));
		Assert.assertTrue(box.isInside(0, 1));
		Assert.assertTrue(box.isInside(0, 0));
		Assert.assertFalse(box.isInside(-1, 1));
	}
	
	@Test
	public void isInsideOfTest() {
		Box box=new Box(0,0,10,10);
		Box box2=new Box(1,1,9,9);
		Box box3=new Box(-1,-1,9,9);
		Box box4=new Box(-10,-10,-1,-1);
		
		Assert.assertTrue(box2.isInsideOf(box));
		Assert.assertFalse(box3.isInsideOf(box));
		Assert.assertFalse(box4.isInsideOf(box));
		Assert.assertTrue(box.isInsideOf(box));
	}

	@Test
	public void isOverlappingBoxTest() {
		Box box=new Box(0,0,10,10);
		Box box2=new Box(1,1,9,9);
		Box box3=new Box(-1,-1,9,9);
		Box box4=new Box(-10,-10,-1,-1);
		Box box5=new Box(-10,-10,0,0);
		Box box6=new Box(-10,-10,10,0);
		
		Assert.assertTrue(box2.isOverlapping(box));
		Assert.assertTrue(box3.isOverlapping(box));
		Assert.assertFalse(box4.isOverlapping(box));
		Assert.assertFalse(box5.isOverlapping(box));
		Assert.assertFalse(box6.isOverlapping(box));
		Assert.assertTrue(box.isOverlapping(box));
	}
	
	@Test
	public void isOverlappingRecTest() {
		Box box=new Box(0,0,10,10);
		Rect rectangle=new Rect(0,0,10,10);
		Rect rectangle2=new Rect(1,1,9,9);
		Rect rectangle3=new Rect(-1,-1,9,9);
		Rect rectangle4=new Rect(-10,-10,-1,-1);
		Rect rectangle5=new Rect(-10,-10,0,0);
		Rect rectangle6=new Rect(-10,-10,10,0);
		
		Assert.assertTrue(box.isOverlapping(rectangle2));
		Assert.assertTrue(box.isOverlapping(rectangle3));
		Assert.assertFalse(box.isOverlapping(rectangle4));
		Assert.assertFalse(box.isOverlapping(rectangle5));
		Assert.assertFalse(box.isOverlapping(rectangle6));
		Assert.assertTrue(box.isOverlapping(rectangle));
	}
	
	@Test
	public void offsetTest() {
		Box box=new Box(0,0,10,10);
		box.offset(1, 0);
		
		Assert.assertEquals(box.x1, 1);
		Assert.assertEquals(box.x2, 11);
		Assert.assertEquals(box.y1, 0);
		Assert.assertEquals(box.y2, 10);
		
		box.set(0,0,10,10);
		box.offset(-1, -9);

		Assert.assertEquals(box.x1, -1);
		Assert.assertEquals(box.x2, 9);
		Assert.assertEquals(box.y1, -9);
		Assert.assertEquals(box.y2, 1);
		
	}
	
}
