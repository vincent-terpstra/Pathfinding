package com.spacehex.game.map;

import com.spacehex.game.objects.HexXY;
import com.spacehex.game.objects.Ship;

public class HArray {
	
	static final int half= 20, width = half * 2 + 1;
	private Data[][] nodes = new Data[width][];
	private Node 
		list = null;

	private final class Data {
		Data(int weight, int dir){
			this.weight = weight;
			this.dir = dir;
		}
		final int 
			weight,
			dir;
	}
	
	private final class Node extends HexXY {
		private Node(HexXY start, int dir, HexXY end, int dist){
			super(start);
			move(dir);
			this.weight = (dEnd = 16 * dist(end)) + (dStart = dist + 32);
			this.dir = dir;
		}
		final int 
			dEnd, 
			dStart, 
			dir,
			weight;
		Node node = null;
	}
	
	private int x,y;
	
	public Path calculatePath(HWorld world, Ship start, HexXY finish){
		//check if next to or at the finish
		if(start.dist(finish) <=2 ){
			return new Path();
		}
		x = half - start.x();
		y = half - start.y();
		//shift finish and start to match grid
		HexXY center = new HexXY(half, half);
			  finish = new HexXY(finish).move(x, y);
		//add ~6 nodes right next to the start
		for(int i = 0; i < 6; i++){
			insert( new Node(center, i, finish, 0), world);
		}
		
		while(list != null){
			Node node = list;
			list = list.node;
			//add 3 nodes in the direction of the left/right then straight
			for(int i = 0; i < 3; i++){
				if(center.dist(node) + 1 == width){
					//reached the edge of our bounds
					return genPath(start, node);
				}
				
				int[] dir = {1, 5, 0};
				Node next = new Node(node, (node.dir + dir[i]) % 6, finish, (i == 2 ? 0 : 1) + node.dStart);
				if(next.dEnd == 0){ 
					//found the end node generate a path
					return genPath(start, next);
				}
				final int nx = next.x(), 
						  ny = next.y();
				if (nodes[nx] == null || nodes[nx][ny] == null || nodes[nx][ny].weight >= next.weight){
					insert(next, world);
				}
			}
		}
		return new Path();
	}
	/*
	public void draw(HexBatch batch){
		for(int w = 0; w < width; w++){
			for(int h = 0; h < width; h++){
				int cx = w - x,
					cy = h - y;
				if(nodes[w] != null && nodes[w][h] != null){
					batch.draw( 5, cx,cy, .5f);
					batch.drawInt(nodes[w][h].weight, cx, cy);
				} else {
					batch.draw(-1, cx, cy, .1f);
				}
			}
		}
	}
	*/
	private void insert(Node node, HWorld world){
		final int 
			nx = node.x(),
			ny = node.y(),
			weight = world.isOpen(new HexXY(nx -x, ny - y)) ? node.weight : 0;
		//label blocked terrain at 0
		if(nodes[nx] == null) {	
			nodes[nx] = new Data[width];
		}
		nodes[nx][ny] = new Data(weight, node.dir);
		
		if(weight == 0) return; //Blocked terrain
		
		//add the node to the priority queue
		if(list == null || list.weight >= node.weight){
			node.node = list;
			list = node;
		} else {
			Node current = list;
			while(current.node != null && current.node.weight < node.weight) {
				//keep moving down the linked list till where the node should be inserted
				current = current.node; 
			}
			//add to the linked list
			node.node    = current.node;
			current.node = node;
		}
		Node current = list;
		while(current != null){
			current = current.node;
		}
	}
	
	private Path genPath(Ship ship, Node end){
		int[] path = new int[40];
		int idx = 0;
		int lastdir = end.dir;
		int dir = (lastdir + 3) % 6;
		end.move(dir);
		int len = 1;
		while(!end.isAt(half, half) && idx < 38){
			int ndir = nodes[end.x()][end.y()].dir;
			if(ndir != lastdir){
				path[idx++] = lastdir;
				path[idx++] = len;
				len = 1;
				lastdir = ndir;
				dir = (lastdir + 3) % 6;
			} else {
				len++;
			}
			end.move(dir);
		}
		
		path[idx++] = lastdir;
		path[idx++] = len;
		
		return new Path(path, idx);
	}
}
