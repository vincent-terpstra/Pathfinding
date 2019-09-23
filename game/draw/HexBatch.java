package com.spacehex.game.draw;

public class HexBatch extends SimpleBatch {
	public static final float HexWidth = (float)Math.sqrt(3) / 1.5f;
	
	public static final float screenX(float x, float y){
		return (x + y *.5f) * HexWidth;
	}
	// screenY(float y) returns y
	public HexBatch(){
		super(0, 20, new SpriteArray("images"), "hex");
	}
	
	@Override
	public void draw(float[] img, int clr,
			float x, float y, float z){
		super.draw(img, clr,  screenX(x,y), y, z);
	}
	
	public void drawInt(int num, float x, float y){
		drawInt(num, -2, x, y, .25f, 1, 2);
	}
	
	public void draw(float x, float y){
		setScale(.05f);
		draw(null, 1 , x, y, 0);
	}
}
