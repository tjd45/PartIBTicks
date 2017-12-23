package uk.ac.cam.tjd45.algorithms.tick3;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import uk.ac.cam.rkh23.Algorithms.Tick3.GraphBase;
import uk.ac.cam.rkh23.Algorithms.Tick3.MaxFlowNetwork;
import uk.ac.cam.rkh23.Algorithms.Tick3.TargetUnreachable;

public class Graph extends GraphBase{

	public Graph(int[][] adj) {
		super(adj);	
	}
	
	public Graph(URL url) throws IOException{
		super(url);
	}
	
	public Graph(String file) throws IOException{
		super(file);
	}

	@Override
	public List<Integer> getFewestEdgesPath(int src, int target) throws TargetUnreachable {
		int NumVertices = getNumVertices();
		List<Integer> ShortestPath = new ArrayList<Integer>();
		
		int[] predecessor = new int[NumVertices];
		int[] distance = new int[NumVertices];
		int[] colour = new int[NumVertices];
		boolean[] reachable = new boolean[NumVertices];
		
		for (int i = 0;i<NumVertices;i++){
			predecessor[i] = -1; //Represent no current predecessor
			distance[i] = -1; //Represent infinity
			colour[i] = 0; //0 = White, 1 = Grey, 2 = Black
			reachable[i] = false;
		}
		
		distance[src] = 0;
		colour[src] = 1;
		
		Queue<Integer> q = new LinkedList<Integer>();
		
		q.add(src);
		
		while (!q.isEmpty()){
			int u = q.remove();
			for (int i = 0;i<NumVertices;i++){
				if (this.mAdj[u][i] != 0){
					if (colour[i] == 0){
						colour[i]++;
						reachable[i] = true;
						distance[i] = distance[u]+1;
						predecessor[i] = u;
						q.add(i);
					}
				}
			}
			colour[u]=2;
		}
		
		if (reachable[target]){
			int length = distance[target];
			int current = target;
			for (int i = 0;i<length;i++){
				ShortestPath.add(0, current);
				current = predecessor[current];
				
			}
			ShortestPath.add(0,current);
			return ShortestPath;
		}else
			throw new TargetUnreachable();
		
	}

	@Override
	public MaxFlowNetwork getMaxFlow(int s, int t) {
		int NumVertices = getNumVertices();
		
		//creating arrays for capacity and flows
		int capacity[][] = new int[NumVertices][NumVertices];
		
		for (int i = 0;i<NumVertices;i++){
			for (int j = 0;j<NumVertices;j++){
				capacity[i][j]=this.mAdj[i][j];
			}
		}
		
		int flow[][] = new int[NumVertices][NumVertices];
		int minResFlow;
		int maxFlow = 0;
		
		List<Integer> currentPath;
		
		//if the destination is unreachable then max flow is 0
		try {
			currentPath = getFewestEdgesPath(s,t);
		} catch (TargetUnreachable e) {
			return new MaxFlowNetwork(0, new Graph(mAdj));
		}
		
		//new 2d array for the edges in the graph
		ArrayList<int[]> Edge = new ArrayList<int[]>();
		
		for (int i = 0; i<NumVertices; i++)
			for (int j = 0; j<NumVertices; j++){
				if (mAdj[i][j] != 0){
					int e[] = new int[2];
					e[0] = i;
					e[1] = j;
					Edge.add(e);
				}
			}
		
		while (!currentPath.isEmpty()){
		
			ArrayList<Integer> ResFlows = new ArrayList<Integer>();
			
			for(int i = 0;i < currentPath.size() -1; i++){
				int e[] = new int[2];
				e[0] = currentPath.get(i);
				e[1] = currentPath.get(i+1);
				
				int rete[] = new int[2];
				rete[0] = e[1];
				rete[1] = e[0];
				
				boolean checker = false;
				boolean forward = false;
				
				//check to see if the forward edge e is in Edge, and then if the return edge rete is in it
				for (int[] current : Edge){
					if ((current[0]==e[0])&&(current[1]==e[1])){
						checker = true;
						forward = true;
					}else
						if ((current[0]==e[1])&&(current[1]==e[0]))
							checker = true;
				}
				
				
				//if forward edge then residual flow is capacity - flow, if rete then residual flow is just the flow
				if (checker) {
					if (forward) {
						ResFlows.add(capacity[e[0]][e[1]]-flow[e[0]][e[1]]);
					}else{
						ResFlows.add(flow[e[1]][e[0]]);
					}
				}else{
					ResFlows.add(0);
				}
				
			}
			
			//minResFlow = Collections.min(ResFlows);
			if (ResFlows.isEmpty()){
				System.out.println("got you");
			}
			minResFlow = ResFlows.get(0);
			for (int current : ResFlows){
				if (current < minResFlow)
					minResFlow = current;
			}
			
			for (int i = 0; i < currentPath.size()-1;i++){
				int v1 = currentPath.get(i);
				int v2 = currentPath.get(i+1);
				
				//update the residual graph for all edges in the current path
				mAdj[v1][v2] = capacity[v1][v2] - flow[v1][v2];
				mAdj[v2][v1] = mAdj[v2][v1] + flow[v1][v2];
				
				//update the flows
				flow[v1][v2] = flow[v1][v2] + minResFlow;
			}
			
			maxFlow = maxFlow + minResFlow;
			
			//create new current path, if target is now unreachable then exit 
			try{
				currentPath = getFewestEdgesPath(s,t);
			} catch (TargetUnreachable e){
				return new MaxFlowNetwork(maxFlow,new Graph(mAdj));
			}
			
		
		}
		
		return new MaxFlowNetwork(maxFlow, new Graph(mAdj));

	}

}
