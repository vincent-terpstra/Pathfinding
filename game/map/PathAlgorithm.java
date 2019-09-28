package com.spacehex.game.map;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.objects.HexXY;

/**
 * @author Vincent
 * @date Sept 27 2019
 */
public class PathAlgorithm {
	static class Node extends HexXY {
		Node node;
		final int dir;
		final float weight, cost;
		Node(HexXY start, int dir, HexXY end, float cost){
			super(start);
			move(dir);
			this.dir = dir;
			this.weight = dist(end) + cost;
			this.cost = cost;
		}
		
		Node insertWeight(Node insert){
			if(insert.weight < weight){
				insert.node = this;
				return insert;
			}
			node = insert(node, insert);
			return this;
		}
	}
	
	static final Node insert(Node node, Node insert){
		return node != null ? node.insertWeight(insert) : insert;
	}
	/*
	 * DRAW DETAILS 
	 */
	static Node open;
	static int[][] closed;
	
	static int dx, dy;
	/**/
	static final int HALF = 20;
	static final int WIDTH = HALF * 2 + 1;
	/**
	 * A* algorithm to find a path on a Hexogonal grid using directions
	 * @param world
	 * @param start
	 * @param end
	 * @return the calculated path
	 */
	
	public static Path calculatePath(HWorld world, HexXY start, HexXY end){
		int dist = start.dist(end);
		if(dist == 0){
			return new Path();
		} else if(dist == 1){
			return new Path(start.direction(end));
		}
		
		start = start.clone().move(-HALF, - HALF);
		end.move(-start.x(), -start.y());
		
		HexXY first = new HexXY(HALF, HALF);
		/*
		 * DRAW DETAILS
		 */
		//create open list
			open = null;
		//create closed list
			closed = new int[WIDTH][WIDTH];
			
			dx = start.x();
			dy = start.y();
		/**/
		//add all 6 adjacent elements to list
		for(int i = 0; i < 6; i++){
			Node tmp = new Node( first, i, end, 1);
			if(world.isOpen(tmp.clone().move(start)))
				open = insert(open, tmp);
			//Remember spot
			closed[tmp.x()][tmp.y()] = tmp.dir + 3;
		}
		//while there are open paths
		/**/
		while(open != null ){
			//pop the "closest" open path
			Node next = open;
			open = open.node;
			
			//GENERATE neighbours of closest (using direction of previous node)
			for(int i = 0; i < 3; i++){
				int[] prior = {0, 1, -1}; //Prioritize straight first
				
				Node tmp = new Node(next, (next.dir + prior[i] + 6) % 6, end,
						//Heuristic each move costs 1 + .125f on turn (reduce turning)
						next.cost + 1 + .125f * Math.abs(prior[i]));
					
				//IF neighbour is at end OR outside of closed grid
				if(end.dist(tmp) == 0 || tmp.dist(HALF, HALF) > HALF){
					//Create a path
					return new Path(path(closed, tmp, tmp.dir + 3 , 2));
				}
				//IF we have seen that spot ignore
				if(closed[tmp.x()][tmp.y()] == 0){
					//if the spot is open add to pathfinding
					if(world.isOpen(tmp.clone().move(start)))
						open = insert(open, tmp);
					//remember spot
					closed[tmp.x()][tmp.y()] = tmp.dir + 3;
				}
			}
		}
		/**/
		return new Path();
	}
	
	/**
	 * Recursive function to create a Path from the array of seen locations
	 * @param closed
	 * @param last
	 * @param dir
	 * @param depth
	 * @return array for suggested
	 */
	
	static int[] path(int[][] closed, Node last, final int dir, final int depth){
		int dir2 = dir;
		int len = 0;
		while(dir2 == dir){
			last.move(dir % 6);
			dir2 = closed[last.x()][last.y()];
			len++;
		}
		int[] array = dir2 == 0 ? new int[depth] :
			path(closed, last, dir2, depth + 2);
		
		array[depth - 2] = (dir + 3) % 6;
		array[depth - 1] = len;
		
		return array;
	}
	
	public static void draw(HexBatch batch){
		int x = dx;
		if(closed != null)
		for(int[] list : closed){
			int y = dy;
			for(int n : list){
				if( n != 0){
					batch.draw(7, x, y, .8f);
				//	batch.drawInt(n, -2, x, y, .4f, 1, 2);
				}
				y++;
			}
			x++;
		}
		Node draw = open;
		while(draw != null){
			float _x = draw.x() + dx;
			float _y = draw.y() + dy;
			batch.draw(5, _x, _y, .8f);
		//	batch.drawInt(draw.dir, -2, _x, _y, .4f, 1, 2);
			draw = draw.node;
		}
	}
}
