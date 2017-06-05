import java.util.*;
//Set cover algorithm
public class SetCover {
	int numOfsource;
	int m;
	Request[] r;
	double W;// result of price and weight
	ArrayList<ArrayList<Double>> weight;
	ArrayList<Set> sets;
	
	ArrayList<ArrayList<Integer>> result;
	int[][] xik;
	SetCover(int numOfsource,int m,Request[] r,
			ArrayList<Set> sets,			
			ArrayList<ArrayList<Double>> weight
			){
		this.r=r;
		this.numOfsource=numOfsource;
		this.m=m;
		this.sets=sets;
		this.weight=weight;
		this.W=0;
		this.result=new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<=numOfsource;i++){
			result.add(new ArrayList<Integer>());
		}
		this.xik=new int[numOfsource+1][m];
	}
	
	int getMin(ArrayList<ArrayList<Double>> weight,
			ArrayList<Set> sets){
		int index=-1;
		double min=Double.MAX_VALUE;
		
		for(int i=0;i<sets.size();i++){
			
			Set s=sets.get(i);
			if(s.choose==true)continue;
			int[] des=s.destinations;
			
			double w=s.weight;
			w=w/des.length;
			if(w<min){
				min=w;
				index=i;
			}
		}
		
		return index;
	}
	
	void greedy(ArrayList<ArrayList<Double>> weight,Request[] r,
			ArrayList<Set> sets,ArrayList<ArrayList<Integer>> result){
		while(!done(r)){
			int index=getMin(weight,sets);
			if(index<0)break;
			int source=sets.get(index).source;
			int message=sets.get(index).message;
			sets.get(index).choose=true;
			result.get(source).add(message);
			if(source<numOfsource)W=W+sets.get(index).weight;
			int[] d=sets.get(index).destinations;
			for(int i=0;i<d.length;i++){
				for(int j=0;j<r.length;j++){
					if(r[j].desti==d[i] && message== r[j].message){
						if(r[j].done==false){
							r[j].done=true;
							r[j].source=sets.get(index).source;
						}
						
					}
				}
			}
		}// while
	}
	
	void run(){
		greedy(weight,r,sets,result);
		for(int i=0;i<=numOfsource;i++){
			for(int j=0;j<result.get(i).size();j++){
				xik[i][result.get(i).get(j)]=1;
			}
		}
	}
	
	boolean done(Request[] r){
		
		for(int i=0;i<r.length;i++)
			if(r[i].done==false)return false;
		
		return true;
	}
}
