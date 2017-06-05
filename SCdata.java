import java.util.ArrayList;
// set cover data
public class SCdata {
	RawData data;
	int numOfsource;
	int m;
	Request[] r;
	ArrayList<Set> sets;			
	ArrayList<ArrayList<Double>> weight;
	
	SCdata(RawData data){
		this.data=data;
		m=data.m;
		this.numOfsource=data.numOfsource;
		sets=new ArrayList<Set>();
		weight=new ArrayList<ArrayList<Double>>();
		
		genData();
	}
	
	void genData(){
		int num=0;
		for(int i=0;i<data.m;i++){
			for(int j=0;j<data.numOfdest;j++){
				if(data.request[i][j]==1)
					num++;
			}
		}// count requests
		r=new Request[num];
		int index=0;
		for(int i=0;i<data.m;i++){
			for(int j=0;j<data.numOfdest;j++){
				if(data.request[i][j]==1)
				{
					r[index]=new Request(j,i);
					index++;
				}
			}
		}// get requests
		
		for(int i=0;i<data.numOfsource;i++){
			
			weight.add(new ArrayList<Double>());
			for(int j=0;j<data.m;j++){
				
				weight.get(i).add(data.cost[i][j]+1);
				
			}
		}//get weight for si
		
		/*weight.add(new ArrayList<Double>());
		for(int i=0;i<data.m;i++){
			for(int j=0;j<data.source)
		}*/
		
		for(int i=0;i<data.numOfsource;i++){
			for(int j=0;j<data.m;j++){
				if(data.mik[i][j]==1){
					int[] d=data.findDests(data.request, j, data.dist, i);
					if(d.length>0)
						sets.add(new Set(i,j,d,false,data.cost[i][j]));
						//sets.add(new Set(i,j,d,true));
				}
			}
		}// get sets for D2D
		
		for(int i=0;i<data.m;i++){
			int[] d=findBSdests(i);
			double w=0.0;
			for(int j=0;j<d.length;j++){
				int device=d[j];
				double x=data.dests[device].x;
				double y=data.dests[device].y;
				double dis=Math.sqrt(x*x+y*y);
				double power=-data.T0*Math.pow(dis, data.alpha)/Math.log(data.Ps);
				w=w+power;
			}
			sets.add(new Set(data.numOfsource,i,d,true,w));
		}// get sets for BS
	}
	
	int[] findBSdests(int file){
		int[] result;
		int num=0;
		for(int i=0;i<data.numOfdest;i++){
			if(data.request[file][i]==1)
				num++;
		}
		result=new int[num];
		
		int index=0;
		for(int i=0;i<data.numOfdest;i++){
			if(data.request[file][i]==1){
				result[index]=i;
				index++;
			}
		}
		return result;
	}
}
