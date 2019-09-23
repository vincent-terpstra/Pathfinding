package com.spacehex.game.objects;

import java.util.HashMap;

public abstract class Section {
	public static final int DIM = 32;
	protected final int x, y;
	protected Section(int x, int y){
		this.x = x;
		this.y = y;
	}
	public abstract MapRegion findRegion(HexXY hex);
	public boolean pointOutside(HexXY hex){
		return pointOutside(hex.x - x, hex.y - y);
	}
	abstract boolean pointOutside(int tx, int ty);
	
	public static class QuadTree extends Section {
		private final Section[] maps;
		private final int radius;
		public QuadTree(){
			this(0, 0, 0, 0, 0, DIM);
		}
		public QuadTree(QuadTree map, HexXY hex){
			this(map.x, map.y, hex.x, hex.y, map.radius,  2 * map.radius);
			maps[idx(map.x, map.y)] = map;
		}
		private QuadTree(int mapx, int mapy, int tx, int ty, int shift, int radius){
			super(shift(mapx, tx, shift), shift(mapy, ty, shift));
			this.radius = radius;
			this.maps = new Section[4];
		}
		
		//changes the quadrant during creation
		private static final int shift(int a, int b, int rad){
			if(b < a) rad *= -1;
			return a + rad;
		}
		@Override
		boolean pointOutside(int tx, int ty){
			return tx < - radius || tx >= radius || ty < -radius || ty >= radius;
		}
		
		@Override
		public MapRegion findRegion(HexXY hex) {
			int tx = hex.x, ty = hex.y;
			int idx = idx(tx, ty);
			if(maps[idx] == null){
				maps[idx] = (radius == Section.DIM) ? 
					new MapRegion(x - ((tx < x) ? radius : 0), 
								  y - ((ty < y) ? radius : 0)) : 
					new QuadTree(x, y, tx, ty, radius / 2, radius / 2);
			} 
			return maps[idx].findRegion(hex);
		}
	
		private final int idx(int tx, int ty){
			int idx = tx >= x  ? 0 : 2;
			if( ty >= y ) idx++;
			return idx;
		}
	}
	public class MapRegion extends Section {
		public MapRegion(int x, int y){
			super(x,y);
			objs = new HashMap<Integer, MapObject>(5);
			for(int i = 0; i < DIM; i++){
				for(int j  = DIM - 1; j >= 0; j--){
					if(Math.random() < .35f)
						add (new Asteroid(i + x, j + y));
				}
			}
		}
		
		HashMap <Integer, MapObject> objs;
		
		public final  boolean pointOutside(int bx, int by){
			return bx < 0 || bx >= DIM || by < 0 || by >= DIM;
		}
		public final MapRegion findRegion(HexXY hex){
			return this;
		}
		private final int idx(final HexXY hex){
			return (hex.x - x) * DIM + hex.y - y;
		}
		
		public final boolean isOpen(final HexXY hex){
			return get(hex) == null;
		}
		public final boolean addAt(HexXY loc, MapObject add){
			int idx = idx(loc);
			if(objs.get(idx) == null){
				objs.put(idx, add);
				return true;
			}
			return false;
		}
		public final MapObject get(final HexXY hex){
			return objs.get(idx(hex));
		}
		
		public final void remove(final HexXY hex){
			objs.remove(idx(hex));
		}
		
		public final void add(final MapObject add){
			objs.put(idx(add), add);
		}
	}
}
