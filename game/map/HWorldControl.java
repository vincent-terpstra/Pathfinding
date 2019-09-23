package com.spacehex.game.map;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.objects.HexXY;
import com.spacehex.game.objects.MapObject;
import com.spacehex.game.objects.Ship;

public class HWorldControl {
	final HWorld world;
	public HWorldControl(HWorld world){
		this.world = world;
		this.ship = new Ship(5,8, world);
		world.add(ship);
	}
	
	public void draw(HexBatch batch){
		batch.setShift(mapX, mapY);
		final float height = HexBatch.Height(), width = HexBatch.Width();
		int minY = -(int)mapY;
		int maxY = (int)(-mapY + height);
		
		world.draw(batch, !dragged, minY - 1, maxY + 1, 
			(int)calcX(0,	 minY) - 2, 
			(int)calcX(0,	 maxY) - 2,
			(int)calcX(width, maxY) + 2,
			(int)calcX(width, minY) + 2);
			
		if(active != null){
			batch.draw( active.type, active.x(), active.y(), 1.5f);
			batch.draw(-2, active.x(), active.y(), 1.4f);
			active.draw(batch);
			if(dragged)
				batch.setShift(dragx, dragy);
			batch.draw( active.type, active.x(), active.y(), 1.5f);
			batch.draw(-2, active.x(), active.y(), 1.4f);
			active.draw(batch);
		}
	}
	
	private float calcX(float x, float y){
		return ((x - mapX) / HexBatch.HexWidth - y * .5f);
	}
	
	private HexXY lock(float hx, float hy){
		hy -= mapY - .666666f;
		int y = (int)Math.floor(hy);
		hy = 3 * (hy - y);
		
		hx = calcX(hx, y) + .5f;
		int x = (int)Math.floor(hx);
		hx = 2 * (hx - x) - 1;
		
		//check edge cases
		if(-hy > hx){ y --; } else
		if( hy < hx){ y --; x++;};
		return new HexXY(x,y);
	}
	
	private MapObject active;
	private final Ship ship;
	
	private boolean dragged = false;
	
	private float dragx, dragy;
	private float mapX, mapY;
	
	//Touch controls for world
	//
	public final void touchDown(float tx, float ty, int button){
		HexXY touch = lock(tx, ty);
		if(button == 0){ //grabbing object
			MapObject selection = world.getAt(touch);
			if(selection != null){
				active = selection;
				dragged = true;
				dragx = mapX;
				dragy = mapY;
				if(selection.isAt(ship)){
					ship.moveTo(ship, world); //reset path
				}
			}
		} else { //button == 1 move command ship
			ship.moveTo(touch, world);
		}
	}
	
	public final void touchUp(){
		if(active != null && dragged){
			world.remove(active);
			active.set(lock(HexBatch.screenX(active.x(), active.y()) + dragx, active.y() + dragy));
			world.add(active);	
		}
		dragged = false;
	}
	
	public final void touchDragged(float dx, float dy){
		if(dragged){
			this.dragx += dx;
			this.dragy += dy;
		} else {
			mapX += dx;
			mapY += dy;
		}
	}
}
