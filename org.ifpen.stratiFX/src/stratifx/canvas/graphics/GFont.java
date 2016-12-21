package stratifx.canvas.graphics;

public  class GFont extends Object {
	
	public static final int NORMAL    = 1 << 0;
	public static final int BOLD      = 1 << 1;
	public static final int ITALIC    = 1 << 2;
	public static final int UNDERLINE = 1 << 3;
	
	private String fontName;
	private int fontSize;
	private int fontStyle;
	
	public GFont(){
		fontName="Arial";
		fontSize=14;
		fontStyle = NORMAL;	
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}
	
	public boolean isBold(){
		return (( fontStyle & BOLD ) == BOLD );
	}
	
	public boolean isItalic(){
		return (( fontStyle & ITALIC ) == ITALIC );
	}

	public int getFontStyle() {
		return fontStyle;
	}

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	
	public int getSize(){
		return fontSize;
	}
	
	@Override
	protected  GFont clone() {
		GFont font = new GFont();
		font.fontName = fontName;
		font.fontSize = fontSize;
		font.fontStyle = fontStyle;
		
		return font;
	}
}
