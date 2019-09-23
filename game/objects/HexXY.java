package com.spacehex.game.objects;

public class HexXY {
	public HexXY(){
		this(0, 0);
	}
	
	public HexXY(HexXY hex){
		set(hex);
	}
	
	public HexXY(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	protected int x, y;
	
	//Set functions
	public HexXY set(HexXY hex){
		this.x = hex.x;
		this.y = hex.y;
		return this;
	}
	
	
	//Move function
	public HexXY move(HexXY hex){
		return move(hex.x, hex.y);
	}
	
	public HexXY move(int dir){
		return move(unitX(dir), unitY(dir));
	}
	
	public HexXY move(int x, int y){
		this.x += x;
		this.y += y;
		return this;
	}
	
	public boolean isAt(int x, int y){
		return this.x == x && this.y == y;
	}
	
	public boolean isAt(HexXY hex){
		return isAt(hex.x, hex.y);
	}
	
	public int dist(HexXY hex){
		return dist(hex.x, hex.y);
	}
	
	public int dist(int x, int y){
		int dx = this.x - x;
		int dy = this.y - y;
		return Math.abs(dx) + Math.abs(dy) + Math.abs(dx + dy);
	}
	
	//Hex coordinates on the map
	public final int x(){ return x; }
	public final int y(){ return y; }
	
	//Unit vectors for each direction on the HexMap
	//	input range [0-6]
	//  output unit vectors starting at [1,0] (right) and rotating clockwise
	private static final int[] array = {1, 1, 0,-1,-1, 0, 1, 1};
	public  static final int unitX(int dir){ return array[dir]; }
	public  static final int unitY(int dir){ return array[dir + 2];}
	
	public final int direction(HexXY dir){
		int dx = x - dir.x(),
			dy = y - dir.y();
		if(dx == 0){ 		return dy == -1 ? 5 : 2; } 
		else if(dy == 0){	return dx == -1 ? 0 : 3; }
		return dx == -1 ? 1 : 4;
	}
}
