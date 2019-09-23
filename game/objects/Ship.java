package com.spacehex.game.objects;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.map.HArray;
import com.spacehex.game.map.HWorld;
import com.spacehex.game.map.Path;

public class Ship extends MapObject implements HWorld.Update {
	public Ship(int x, int y, HWorld world) {
		super(MINER, x, y, max, 1);
	}
	
	//path set
	private int vector, dir = -1;
	private Path path = new Path();
	private static final byte speed = 4;
	private static final int max = 5;
	
	@Override
	public void update(HWorld world){
		//Travelling in a direction && passed the full distance
		if(dir != -1 && (vector += speed) > TILEDIST){
			vector -= TILEDIST;
			dir = path.nextDir();
			if(dir != -1 && !world.move(this,  dir)){
				dir = -1; //unable to move this direction
				vector = 0;
			}
		}
	}
	
	public final void moveTo(HexXY target, HWorld world){
		path = new HArray().calculatePath(world, this, target);
		//At rest && the path has a direction to travel
		if(dir == -1 && (dir = path.nextDir()) != -1)
			world.move(this,  dir);
	}
	
	//allows the ship to move
	@Override
	public void draw(HexBatch batch){
		if(dir!= -1){
			batch.draw(MINER, x, y, 1.0f);
			batch.draw(MINER, x - unitX(dir), y - unitY(dir), 1.0f);
			float dif = 1 - vector / (float)TILEDIST;
			super.draw(batch, x - dif * unitX(dir),
						      y - dif * unitY(dir));
		} else {
			super.draw(batch);
		}
		
		
		//draw the path
		path.draw( batch, this);
	}
}
