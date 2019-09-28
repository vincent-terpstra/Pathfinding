package com.spacehex.game.map;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.objects.HexXY;

public class Path {
	private final int[] nodes;
	private int turns;
	
	public Path(){
		this.nodes = null;
	}
	
	public Path(int dir){
		this( new int[]{dir, 1});
	}
	
	public Path(int[] nodes){
		this.nodes = nodes;
		this.turns = nodes.length;
	}
	
	public int nextDir(){
		if(nodes != null && turns >= 2){
			if(--nodes[turns-1] == -1){
				turns -= 2;
				return nextDir();
			}
			
			return nodes[turns-2];
		}
		return -1;
	}
	
	
	public void draw( HexBatch batch, HexXY ship){
		if(nodes != null){
			HexXY start = new HexXY().set(ship);
			for(int i = turns; i > 0;){
				int dist = nodes[--i];
				int dir  = nodes[--i];
				for(int d = 0; d < dist; d++){
					start.move(dir);
					batch.draw(3, start.x(), start.y(), .4f);	
				}
			}
		}
	}
}
