package com.spacehex.game.map;

import java.util.Vector;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.objects.HexXY;
import com.spacehex.game.objects.MapObject;
import com.spacehex.game.objects.Section.MapRegion;
import com.spacehex.game.objects.Section.QuadTree;

public class HWorld {
	public HWorld(){
		//default quad for infinite map
		map = new QuadTree();
		last = map.findRegion(new HexXY(0,0));
		
		ships = new Vector<Update>(5);
		
		
	}
	
	void draw(HexBatch batch, boolean drawPath, int minY, int maxY, int x0, int x1, int x2, int x3){
		MapRegion region = findRegion(new HexXY(minY, x0));
		float dx0 = (x1 - x0)/(float)(maxY - minY);
		float dx1 = (x2 - x3)/(float)(maxY - minY);
		for(int y = minY; y <= maxY; y++ ){
			float dy = (float)(y - minY);
			int minx = (int)(x0 + dx0 * dy), maxX = (int)(x3 + dx1 * dy);
			for(int x = minx; x <= maxX; x++){
				HexXY loc = new HexXY(x,y);
				if(region.pointOutside(loc))
					region = findRegion(loc);
				MapObject obj = region.get(loc);
				if(obj != null)
					obj.draw(batch);
			}
		}
	}
	
	private QuadTree map;
	private Vector<Update> ships;
	
	void add(MapObject add){
		findRegion(add).add(add);
		
		if(add instanceof Update)
			ships.add((Update)add);
	}
	
	
	public interface Update{
		void update(HWorld world);
	}
	
	public void update() {
		for(Update ship : ships){
			ship.update(this);
		}
	}
	
	public boolean move(MapObject obj, int dir){
		return move(obj, new HexXY(obj).move(dir));
	}
	
	public boolean move(MapObject obj, HexXY next){
		MapRegion atNext = findRegion(next);
		if( atNext.isOpen(next)){
			findRegion(obj).remove(obj);
			   obj.set(next);
			atNext.add(obj);
			return true;
		}
		return false;
	}
	public boolean isOpen(HexXY hex){
		return findRegion(hex).isOpen(hex);
	}
	public MapObject getAt(HexXY hex){
		return findRegion(hex).get(hex);
	}
	public void remove(MapObject target){
		findRegion(target).remove(target);
		if(target instanceof Update)
			ships.remove(target);
	}
	
	// used to speed up finding region by remembering previous region
	private MapRegion last;
	
	private MapRegion findRegion(HexXY hex) {
		if(last.pointOutside(hex)){
			if(map.pointOutside(hex)){
				//map is not large enough expand to double the size
				this.map = new QuadTree(map, hex);
				return findRegion(hex);
			} else {
				return map.findRegion(hex);
			}
		} else {
			return last;
		}
	}
}
