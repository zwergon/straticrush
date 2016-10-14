package org.apache.batik.test;

import org.junit.Test;

public class ConverterTest {
	
	@Test
	public  void testConversion()  {
    	
    	String in_filename = "/home/irsrvhome1/R11/lecomtje/Desktop/minibasin.svg";
    
    	//int separation_points = Integer.parseInt(args[2]);
    	int separation_points = 50;
    	//System.out.println("In:"+in_filename+", Out:"+ out_filename);
    	
    	Converter cv =  new Converter(separation_points);
    	cv.readPathSvg(in_filename);
    	//cv.writePolygonSvg(out_filename);
    	
    }

}
