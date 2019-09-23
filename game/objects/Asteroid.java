package com.spacehex.game.objects;

import com.spacehex.game.draw.HexBatch;

public class Asteroid extends MapObject {
	public Asteroid(int x, int y){
		super(ASTEROID, x, y, 0,0);
	}
	
	@Override
	public void draw(HexBatch batch){
		batch.draw(ASTEROID, x, y, 1.3f);
		batch.draw(-2, x, y, .6f);
	}
}
