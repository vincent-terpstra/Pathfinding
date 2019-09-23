package com.spacehex.game.map;

import com.spacehex.game.draw.HexBatch;
import com.spacehex.game.objects.HexXY;
/**
 * @author Vincent Terpstra
 * Jun 15, 2018
 * 
 * List of Hex objects ordered by lowest weight
 */
public class HexList {
	public static class Node extends HexXY {
		Node(HexXY start, int dir, HexXY end, int dist){
			super(start);
			move(dir);
			this.weight = (dEnd = dist(end)) + (dStart = dist);
			this.dir = dir;
		}
		public Node(){weight = dir = dStart = dEnd = 0;}
		final int 
			dEnd, 
			dStart, 
			dir;
		int weight;
		Node node = null;
	}
	
	HexList(){
		list = new Node();
	}
	
	Node list;
	void insert(final Node node){
		Node current = list;
		//find where to insert the node
		while(current.node != null && current.node.weight < node.weight) {
			current = current.node;
		}
		//add to the linked list
		node.node = current.node;
		current.node = node;
	}
	
	Node pop(){
		Node tmp = list.node;
		if(tmp != null){
			list.node = tmp.node;
			tmp.node = null;
		}
		return tmp;
	}
	
	void draw(HexBatch batch, int clr){
		Node current = list;
		while(current.node != null){
			current = current.node;
			batch.draw( clr, current.x(), current.y(), .5f);
			batch.drawInt(current.weight, current.x(), current.y());
		}
	}
	
	void insertByDistance(Node node){
		Node current = list;
		while(current.node != null && node.dStart < current.node.dStart){
			current = current.node;
		}
		node.node = current.node;
		current.node = node;
	}
	
	boolean contains(Node hex){
		Node current = list;
		while(current.node != null){
			if(current.node.isAt(hex)){
				//remove node and go with the one that is "closer"
				if(hex.weight <= current.node.weight){
					current.node = current.node.node;
					return false;
				} else {
					return true;
				}
			}
			current = current.node;
		}
		return false;
	}
}
