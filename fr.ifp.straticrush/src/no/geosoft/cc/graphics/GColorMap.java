package no.geosoft.cc.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class GColorMap {
	
	static short default_palette[] = {
	    5,
	    0, 0, 0, 255,
	    64, 0, 255, 255,
	    128, 0, 255, 0,
	    192, 255, 255, 0,
	    255, 255, 0, 0
	};

	static short rainbow_palette[] = {
	    7,
	    0, 255, 0, 0,
	    42, 255, 255, 0,
	    85, 0, 255, 0,
	    128, 0, 255, 255,
	    170, 0, 0, 255,
	    212, 255, 0, 255,
	    255, 255, 255, 255
	};

	static short africa_palette[] = {
	    4,
	    0, 204, 178, 115,
	    85, 255, 255, 0,
	    170, 0, 255, 0,
	    255, 255, 0, 0
	};

	static short pastel_palette[] = {
	    9,
	    0, 251, 180, 174,
	    32, 179, 205, 227,
	    64, 204, 235, 197,
	    96, 222, 203, 228,
	    128, 254, 217, 166,
	    159, 255, 255, 204,
	    191, 229, 216, 189,
	    223, 253, 218, 236,
	    255, 242, 242, 242
	};
	
	enum Type {
		NAMED,
		RANDOM,
		INTERPOLATED
	};
	
	
	@SuppressWarnings("serial")
	static class Palette extends Vector<Short> {
		public Palette( short[] array ){
			super(array.length);
			for( short val : array ){
				add(val);
			}
		}
	}
	
	
	static Map<String, Palette> palettes;
	
	Type type;
	
	String name;
	
	double min;
	double max;
	double cmin;
	double cmax;
	Vector<Short> colormap;
	
	
	static {
		palettes = new HashMap<String, GColorMap.Palette>();
		palettes.put("Default", new Palette(default_palette) );
		palettes.put("Africa", new Palette(africa_palette) );
		palettes.put("Rainbow", new Palette(rainbow_palette) );
		palettes.put("Pastel", new Palette(pastel_palette) );
	}
	
	public GColorMap(){
		this.type = Type.NAMED;
		this.name = "Default";
		this.cmin = 0;
		this.cmax = 0;
		
		createFromName( this.name );
		setMinMax( 0, getNColors() );
	}
	
	public void initFrom( GColorMap cmap ){
		this.type = cmap.type;
		this.name = cmap.name;
		this.min = cmap.min;
		this.max = cmap.max;
		this.cmin = cmap.min;
		this.cmax = cmap.max;
		this.colormap = new Vector<Short>( cmap.colormap );
	}
	
	public int getNColors(){
		return colormap.size()/3;
	}
	
	public GColor getColor( double z ){
		int n_colors = getNColors();

	    if (z > cmax) z = cmax ;
	    else if (z < cmin ) z = cmin ;
	    int indice = (int)((double)( n_colors - 1. ) *(z - cmin)/(cmax-cmin));
	    
	    return getColor( indice );
	}
	
	public GColor getColor( int index ){
		return new GColor(
				colormap.get(3*index), 
				colormap.get(3*index+1),
				colormap.get(3*index+2) );
	}
	
	public void setCurrentMin( double min ){
		this.cmin = min;
	}
	
	public void setCurrentMax( double cmax ){
		this.cmax = max;
	}
	
	public void setMinMax( double min, double max ){
		this.min = this.cmin = min;
		this.max = this.cmax = max;
	}
	
	public void createFromName( String name ){
	    type = Type.NAMED;

	    if ( palettes.containsKey(name) ){

	        this.name = name;
	        colormap = new Vector<Short>();

	        Vector<Short> tab = palettes.get(name);

	        
	        int nSteps = tab.get(0);
	        
	        int iStep =0;
	        while( iStep < nSteps-1 ){
	            int iStart = tab.get(4*iStep+1);
	            int iEnd   = tab.get(4*iStep+5);;
	            GColor c1 = new GColor( tab.get(4*iStep+2), tab.get(4*iStep+3), tab.get(4*iStep+4) );
	            GColor c2 = new GColor( tab.get(4*iStep+6), tab.get(4*iStep+7), tab.get(4*iStep+8) );

	            createInterpolated( iStart, iEnd, c1, c2 );
	            iStep++;
	        }

	    }
	}
	
	public void createInterpolated( GColor c1, GColor c2 ){
		this.type = Type.INTERPOLATED;
		colormap = new Vector<Short>();
		
		createInterpolated(0, 255, c1, c2);
	}
	
	
	public void createRandomColormap()
	{
	    type = Type.RANDOM;
	    colormap = new Vector<Short>();

	    Random r = new Random();
		
		int n_colors = 255;
		for (int i=0; i< n_colors; i++)
		{
			GColor c = GColor.getHSBColor(
					r.nextFloat(), 
					(float) (0.1 + 0.2 * r.nextFloat()),
					(float) (0.3 + 0.5 * r.nextFloat()));
	        colormap.add((short)c.getRed());
	        colormap.add((short)c.getGreen());
	        colormap.add((short)c.getBlue());
		}

	}
	
	public void createDefaultColormap(){
		createFromName( "Default" );
		setMinMax( 0, getNColors() );
	}
	
	protected void createInterpolated( int iStart, int iEnd, GColor c1, GColor c2 )
	{
	    int nColors = iEnd - iStart + 1;

	    int r = c1.getRed() ;
	    int g = c1.getGreen();
	    int b = c1.getBlue();

	    double r_step = (c2.getRed()   - r) / (double)(nColors -1.0) ;
	    double g_step = (c2.getGreen() - g) / (double)(nColors -1.0) ;
	    double b_step = (c2.getBlue()  - b) / (double)(nColors -1.0) ;

	    for (int i=0; i<nColors; i++)
	    {
	        colormap.add((short)(r + r_step*i));
	        colormap.add((short)(g + g_step*i));
	        colormap.add((short)(b + b_step*i));
	    }

	}
	
	
	

}
