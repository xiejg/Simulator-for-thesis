import java.util.ArrayList;
// get MCKP data from raw data
public class MCKPdata {
	RawData data;
	int numOfm;
	ArrayList<ArrayList<Integer>> price;
	ArrayList<ArrayList<Double>> weight;
	int C;
	int[][] itemarray;
	int numOfbags;
	static int x=1;
	static double para_c=2;
	
	MCKPdata(RawData data){
		this.data=data;
		numOfbags=data.numOfsource;
		numOfm=data.m;
		itemarray=new int[data.numOfsource][data.m];
		price=new ArrayList<ArrayList<Integer>>();
		weight=new ArrayList<ArrayList<Double>>();
		Gendata();
	}
	
	void Gendata(){
		double[][] gain=new double[data.numOfsource][data.m];
		
		for(int i=0;i<data.numOfsource;i++){
			price.add(new ArrayList<Integer>());
			weight.add(new ArrayList<Double>());
			for(int j=0;j<data.m;j++){
				gain[i][j]=Math.max(data.costBS[i][j]-data.cost[i][j], 0);
//				gain[i][j]=Math.max(data.costBS2[j]-data.cost[i][j], 0);
				int p=(int)(gain[i][j]*x);				
				price.get(i).add(p);
				int w=0;
				if(data.cost[i][j]<Double.MAX_VALUE)
					w=(int)(data.cost[i][j]*x);
					
				weight.get(i).add((double)w);
				
			}
		}// cal profit weight
		
		
		
		for(int i=0;i<data.numOfsource;i++){
			for(int j=0;j<data.m;j++){
				itemarray[i][j]=data.mik[i][j];
			}
		}// cal itemarray
		
		double c=0;
		for(int i=0;i<data.m;i++)
			for(int j=0;j<data.numOfdest;j++){
				if(data.request[i][j]==1){
					double x=data.dests[j].x;
					double y=data.dests[j].y;
					double dist=Math.sqrt(x*x+y*y);
					double power=-data.T0*Math.pow(dist, data.alpha)/Math.log(data.Ps);
					c=c+data.pow2db(power);
				}
			}
		C=(int)(c*x*para_c);
		
	}
}
