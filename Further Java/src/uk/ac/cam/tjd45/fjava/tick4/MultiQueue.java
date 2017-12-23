package uk.ac.cam.tjd45.fjava.tick4;

import java.util.LinkedHashSet;
import java.util.Set;

public class MultiQueue<T> {

	private Set<MessageQueue<T>> outputs = new LinkedHashSet<MessageQueue<T>>();//TODO

	public synchronized void register(MessageQueue<T> q) { 
		outputs.add(q);
	}

	public synchronized void deregister(MessageQueue<T> q) {
		outputs.remove(q);
	}

	public synchronized void put(T message) {

		for(MessageQueue<T> temp: outputs){
			temp.put(message);
		}

	}
}