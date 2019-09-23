package com.spacehex.game.objects;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.map.HWorld;

public  class MapObject extends HexXY {
	static final int TILEDIST = 128;
	static byte TYPE = 0;
	public static final byte HEX = TYPE++, MINER = TYPE++, ASTEROID = TYPE++;
	
	protected MapObject(byte type, int x, int y, int space, int stack){
		super(x, y);
		this.type = type;
	}
	
	public final int type;
	protected byte iSpace;
	
	public void draw(HexBatch batch){
		draw(batch, x, y);
	}
	
	protected void draw(HexBatch batch, float x, float y){
		batch.draw(type, x, y, 1.3f);
		batch.draw(-2,   x, y, 1.1f);
	}
	
	public void target(HexXY obj, int type){};
}
