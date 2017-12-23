package uk.ac.cam.tjd45.algorithms.tick1;

import uk.ac.cam.rkh23.Algorithms.Tick1.EmptyHeapException;
import uk.ac.cam.rkh23.Algorithms.Tick1.MaxCharHeapInterface;

public class MaxCharHeap implements MaxCharHeapInterface {

	private char[] data = new char[0];
	private static int x;
	
	public void heapify(int end, int root){
		if (root*2 + 1 > end)
			return;
		if (root*2 + 2 > end){
			if  (data[root]< data[(2 * root)+1]){
				char temp = data[root];
				data[root] = data[(2 * root) + 1];
				data[(2 * root) + 1] = temp;
				heapify(end,(2 * root) + 1);
			}
		} else{
			if ((data[root] >= data[(root*2) + 1])&&(data[root]>= data[(root*2)+2])){
				return;
			} else{
				if (data[(2 * root)+1] < data[(2 * root)+2]){
					char temp = data[root];
					data[root] = data[(2 * root) + 2];
					data[(2 * root) + 2] = temp;
					heapify(end,(2 * root) + 2);
				}else{
					char temp = data[root];
					data[root] = data[(2 * root) + 1];
					data[(2 * root) + 1] = temp;
					heapify(end,(2 * root) + 1);
				}
			}
		}
	}
	
	@Override
	public char getMax() throws EmptyHeapException {
		if (this.getLength() == 0) {
			throw new EmptyHeapException();
		}
		char max = data[0];
		data[0] = data[data.length - 1];

		char[] smallerarray = new char[data.length - 1];

		for (int j = 0; j < data.length - 1; j++) {
			smallerarray[j] = data[j];
		}

		data = smallerarray;

		boolean heaped = false;
		int current = 0;

		while (!heaped) {
			if (2 * current > data.length - 2) {
				heaped = true;
			} else {
				if (2 * current > data.length - 3) {
					if (data[current] >= data[(current * 2) + 1]) {
						heaped = true;
					} else {
						char temp = data[current];
						data[current] = data[(2 * current) + 1];
						data[(2 * current) + 1] = temp;
						current = (2 * current) + 1;
					}
				} else {
					if ((data[current] >= data[(2 * current) + 1]) && (data[current] >= data[(2 * current) + 2])) {
						heaped = true;
					} else {
						if (data[(2 * current) + 1] > data[(2 * current) + 2]) {
							char temp = data[current];
							data[current] = data[(2 * current) + 1];
							data[(2 * current) + 1] = temp;
							current = (2 * current) + 1;
						} else {
							char temp = data[current];
							data[current] = data[(2 * current) + 2];
							data[(2 * current) + 2] = temp;
							current = (2 * current) + 2;
						}
					}
				}
			}

		}

		return max;
	}

	@Override
	public void insert(char i) {

		char k = Character.toLowerCase(i);

		char[] biggerarray = new char[data.length + 1];

		for (int j = 0; j < data.length; j++)
			biggerarray[j] = data[j];

		data = biggerarray;
		data[data.length - 1] = k;

		boolean larger = true;
		int current = data.length - 1;
		while (larger) {
			if (current == 0) {
				larger = false;
			} else {
				if (((current - 1) / 2) == (int) ((current - 1) / 2)) {
					int check = (current - 1) / 2;
					if (data[current] > data[check]) {
						char temp = data[check];
						data[check] = data[current];
						data[current] = temp;
						current = check;
					} else
						larger = false;
				} else {
					if (((current - 2) / 2) == (int) ((current - 2) / 2)) {
						int check = (current - 2) / 2;
						if (data[current] > data[check]) {
							char temp = data[check];
							data[check] = data[current];
							data[current] = temp;
							current = check;
						} else
							larger = false;
					}
				}
			}
		}

	}

	@Override
	public int getLength() {
		return data.length;
	}

	public MaxCharHeap(String s) {
		s = s.toLowerCase();
		data = s.toCharArray();
		
		for (int i = ((data.length-1)/2); i > -1; i--){
			heapify(data.length-1,i);
		}
		
	}
	
	public static void main(String args[]){
		MaxCharHeap alex = new MaxCharHeap("12332");
		
	}

}
