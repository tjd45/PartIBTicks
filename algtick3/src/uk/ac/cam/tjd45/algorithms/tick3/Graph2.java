package uk.ac.cam.tjd45.algorithms.tick3;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import uk.ac.cam.rkh23.Algorithms.Tick3.*;

public class Graph2 extends GraphBase {

	public Graph2(URL url) throws IOException {
		super(url);
	}

	public Graph2(String file) throws IOException {
		super(file);
	}

	public Graph2(int adj[][]) {
		super(adj);
	}

	@Override
	public List<Integer> getFewestEdgesPath(int src, int target) throws TargetUnreachable {
		List<Integer> result = new ArrayList<Integer>();
		int[] predecessor = new int[getNumVertices()];
		int[] distance = new int[getNumVertices()];
		int[] colour = new int[getNumVertices()]; // white = 0; grey = 1; black = 2;
		boolean[] reachable = new boolean[getNumVertices()];
		Arrays.fill(distance, -1); // infinity denoted by -1
		Arrays.fill(predecessor, -1); // no predecessor denoted by -1
		Arrays.fill(reachable, false); // initialise all vertices to unreachable

		// Queue Q = new Queue();
		LinkedList<Integer> Q = new LinkedList<Integer>();
		
		reachable[src] = true;
		distance[src] = 0;
		colour[src]++;
		Q.addLast(src);

		while (!Q.isEmpty()) {
			int u = Q.removeFirst();
			assert (colour[u] == 1);
			for (int v = 0; v < getNumVertices(); v++) {
				try {
					if (getWeight(u, v) != 0) {
						if (colour[v] == 0) {
							reachable[v] = true;
							colour[v] = 1;
							distance[v] = distance[u] + 1;
							predecessor[v] = u;
							Q.addLast(v);
						}
					}
				} catch (InvalidEdgeException e) {
					continue;
				}
			}
			colour[u] = 2;
		}

		List<Integer> l = new ArrayList<Integer>();
		int temp = target;
		l.add(target);
		if (reachable[target] == false) {
			throw new TargetUnreachable();
		} else {
			while (temp != src) {
				l.add(predecessor[temp]);
				temp = predecessor[temp];
			}
		}

		for (int i = l.size() - 1; i > -1; i--) {
			result.add(l.remove(i));
		}
		return result;
	}

	public static boolean contains(List<int[]> edges, int[] edge) {
		boolean result = false;
		for (int[] edge1 : edges) {
			if (edge1[0] == edge[0] && edge1[1] == edge[1]) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public MaxFlowNetwork getMaxFlow(int s, int t) {
		
		// make a copy of initial mAdj with capacity for each original edge
		int capacity[][] = new int[getNumVertices()][getNumVertices()];
		for (int u = 0; u < getNumVertices(); u++) {
			for (int v = 0; v < getNumVertices(); v++) {
				if (mAdj[u][v] != 0) {
					capacity[u][v] = mAdj[u][v];
				}
			}
		}
		
		// flow set to 0
		int[][] flow = new int[getNumVertices()][getNumVertices()];
		
		int pCf;
		
		int maxFlow = 0;
		
		List<Integer> path;
		
		try {
			path = getFewestEdgesPath(s, t);
		} catch (TargetUnreachable e) {
			return new MaxFlowNetwork(0, new Graph(mAdj));
		}
		
		List<int[]> edges = new ArrayList<int[]>();

		// List of edges (u,v) in E
		for (int u = 0; u < getNumVertices(); u++) {
			for (int v = 0; v < getNumVertices(); v++) {
				if (mAdj[u][v] != 0) {
					int[] edge = new int[2];
					edge[0] = u;
					edge[1] = v;
					edges.add(edge);
				}
			}
		}

		while (!path.isEmpty()) {

			// cf(p)= min(cf(u,v) : (u,v) is in path)
			List<Integer> pCFs = new ArrayList<Integer>();
			for (int i = 0; i < path.size() - 1; i++) {
				
				// store edge (u,v)
				int[] edge = new int[2];
				int u = path.get(i);
				int v = path.get(i + 1);
				edge[0] = u;
				edge[1] = v;
				
				// store edge (v,u)
				int[] revEdge = new int[2];
				revEdge[0] = v;
				revEdge[1] = u;
				
				// if (u,v) is in E, residual flow = capacity - current flow
				// else if (v,u) is in E, residual flow = current flow
				// else residual flow = 0
				if (contains(edges,edge)) {
					int cF = capacity[u][v] - flow[u][v];
					pCFs.add(cF);
				} else if (contains(edges,revEdge)){
					int cF = flow[v][u];
					pCFs.add(cF);
				} else {
					int cF = 0;
					pCFs.add(cF);
				}
			}
			
			// find path residual flow = min(residual flow for all edges)
			int min = pCFs.get(0);
			for (int cF : pCFs) {
				if (cF < min)
					min = cF;
			}
			pCf = min;
			
			for (int i = 0; i < path.size() - 1; i++) {
				int u = path.get(i);
				int v = path.get(i + 1);

				// update residual graph i.e. mAdj for all edges in p
				mAdj[u][v] = capacity[u][v] - flow[u][v];
				mAdj[v][u] += flow[u][v];
				// update flow for all edges in p
				flow[u][v] = flow[u][v] + pCf;
			}
			
			maxFlow += pCf;
			
			// update path to target
			// if target is now unreachable, path becomes an empty List - while loop can then be broken
			try {
				path = getFewestEdgesPath(s, t);
			} catch (TargetUnreachable e) {
				path = new ArrayList<Integer>();
			}
		}
		
		Graph gF = new Graph(mAdj);

		return new MaxFlowNetwork(maxFlow, gF);

	}

	public static void main(String[] args) throws IOException, TargetUnreachable {
		URL url = new URL("http://www.cl.cam.ac.uk/teaching/1516/Algorithms/ticks/maxflow_test.05.in");
		Graph g = new Graph(url);
		System.out.println(g.getMaxFlow(0, 10).getFlow());
	}

}
