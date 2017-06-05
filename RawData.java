import java.util.*;
// Raw data
public class RawData {
	int numOfsource;
	int numOfdest;
	int m;
	Source[] sources;
	Dest[] dests;
	
	double T0,alpha,R,L,Ps;
	double zipfc,zipfr;
	
	double bigM=Double.POSITIVE_INFINITY;
	
	int[][] request;
	int[][] t;
	int[][] mik;
	double[][] dist;
	double[][] cost;
	double[][] costBS;
	double[] costBS2;
	
	
	
	RawData(Source[] sources,Dest[] dests,
			double T0,double alpha,double R,
			double L,double Ps,int m, double zipfc,
			double zipfr){
		this.sources=sources;
		this.dests=dests;
		this.numOfdest=dests.length;
		this.numOfsource=sources.length;
		
		this.T0=T0;
		this.alpha=alpha;
		this.R=R;
		this.L=L;
		this.Ps=Ps;
		this.m=m;
		this.zipfc=zipfc;
		this.zipfr=zipfr;
	}
	
	RawData(Source[] sources,Dest[] dests,int[][] request,
			double T0,double alpha,double R,
			double L,double Ps,int m, double zipfc,
			double zipfr){
		this.sources=sources;
		this.dests=dests;
		this.numOfdest=dests.length;
		this.numOfsource=sources.length;
		this.request=request;
		this.T0=T0;
		this.alpha=alpha;
		this.R=R;
		this.L=L;
		this.Ps=Ps;
		this.m=m;
		this.zipfc=zipfc;
		this.zipfr=zipfr;
		
		this.dist=calDist();
		this.mik=cal_mi();
		this.t=cal_t(this.dist);
		this.cost=calCost(this.request,this.dist);
		this.costBS=calBSCost(this.request,this.dist);
	}
	
	int[][] genRequest(){
		int[][] result=new int[m][numOfdest];
		
		for(int i=0;i<numOfdest;i++){
			int j=0;
			int num=(int) (Math.random()*2);
			while(j<num){
				int file=Distribution.zipf(m, zipfr);
				if(result[file][i]==0){
					result[file][i]=1;
					j++;
				}
			}
		}
		
		return result;
	}
	
	int[][] cal_t(double[][] dist){
		int[][] result=new int[numOfsource][numOfdest];
		
		for(int i=0;i<numOfsource;i++){
			for(int j=0;j<numOfdest;j++){
				if(dist[i][j]<=L){
					result[i][j]=1;
				}
			}
		}
		
		return result;
	}
	
	int[][] cal_mi(){
		int[][] result=new int[numOfsource][m];
		
		for(int i=0;i<numOfsource;i++){
			int[] cache=sources[i].cache;
			for(int k=0;k<cache.length;k++){
				result[i][cache[k]]=1;
			}
		}
		return result;
	}
	
	double[][] calDist(){
		double[][] result=new double[numOfsource][numOfdest];
		
		for(int i=0;i<numOfsource;i++){
			for(int j=0;j<numOfdest;j++){
				double t=0;
				t=t+Math.pow(sources[i].x-dests[j].x, 2);
				t=t+Math.pow(sources[i].y-dests[j].y, 2);
				result[i][j]=Math.sqrt(t);
			}
		}
		
		return result;
	}
	
	double[][] calCost(int[][] request,double[][] dist){
		double[][] result=new double[numOfsource][m];
		
		for(int i=0;i<numOfsource;i++){
			for(int j=0;j<m;j++){
				result[i][j]=bigM;
			}
		}
		
		for(int i=0;i<numOfsource;i++){
			int[] m_id=sources[i].cache;
			for(int j=0;j<m_id.length;j++){
				int[] dest_id=findDests(request,m_id[j],dist,i);
				if(dest_id.length>0){
					double max=max_dist(dist,i,dest_id);
					double power=-T0*Math.pow(max, alpha)/Math.log(Ps);
					result[i][m_id[j]]=pow2db(power);
				}
			}
		}
		
		return result;
	}
	
	public int[] findDests(int[][] request,int file,double[][] dist,int src){

		// find id of dests that asking for file which is cache by src
		
		int num=0;
		for(int i=0;i<numOfdest;i++){
			if(request[file][i]==1 && dist[src][i]<=L)
				num++;
		}
		int[] result=new int[num];
		int index=0;
		for(int i=0;i<numOfdest;i++){
			if(request[file][i]==1 && dist[src][i]<=L){
				result[index]=i;
				index++;
			}
				
		}
		return result;
		
	}

	
	
	double max_dist(double[][] dist,int source, int[] dest_id){// find max distance
		double max=0;
		
			for(int j=0;j<dest_id.length;j++){
				int index=dest_id[j];
				if(dist[source][index]>max){
					max=dist[source][index];
				}
			}
		
		return max;
	}
	
	public static double pow2db(double input){
		double result;
		result=10*Math.log10(input);
		return result;
	}
	
	double[][] calBSCost(int[][] request,double[][] dist){
		double[][] result=new double[numOfsource][m];
		
		for(int i=0;i<numOfsource;i++){
			for(int j=0;j<m;j++){
				result[i][j]=bigM;
			}
		}
		
		for(int i=0;i<numOfsource;i++){
			int[] m_id=sources[i].cache;
			for(int j=0;j<m_id.length;j++){
				int[] dest_id=findDests(request,m_id[j],dist,i);
				double cost=0.0;
				
				for(int k=0;k<dest_id.length;k++){
					double t=0;
					t=t+Math.pow(0-dests[dest_id[k]].x, 2);
					t=t+Math.pow(0-dests[dest_id[k]].y, 2);
					t=Math.sqrt(t);
					double power=-T0*Math.pow(t, alpha)/Math.log(Ps);
					cost=cost+pow2db(power);
					
				}//end of for k
				result[i][m_id[j]]=cost;
			}
		}
		
		return result;
	}
	
	double[] calBSCost2(int[][] request){
		double[] result=new double[m];
		for(int i=0;i<m;i++){
			double cost=0;
			for(int j=0;j<numOfdest;j++){
				if(request[i][j]==1){
					double t=0;
					t=t+Math.pow(0-dests[j].x, 2);
					t=t+Math.pow(0-dests[j].y, 2);
					t=Math.sqrt(t);
					double power=-T0*Math.pow(t, alpha)/Math.log(Ps);
					cost=cost+pow2db(power);
				}
			}
			result[i]=cost;
		}
		return result;
	}
	
	void run(){
		this.request=genRequest();
		this.dist=calDist();
		this.mik=cal_mi();
		this.t=cal_t(this.dist);
		this.cost=calCost(this.request,this.dist);
		this.costBS=calBSCost(this.request,this.dist);
		//this.costBS2=calBSCost2(this.request);
	}

	public double costOfMCKP(int[][] xik){
		double result=0;
		int[][] undone=new int[m][numOfdest];
		for(int i=0;i<m;i++){
			for(int j=0;j<numOfdest;j++){
				undone[i][j]=request[i][j];
			}
		}
		
		for(int i=0;i<numOfsource;i++){
			for(int k=0;k<m;k++){
				if(xik[i][k]==1){
					double max=0;
					for(int j=0;j<numOfdest;j++){
						if(undone[k][j]==1&&t[i][j]==1){
							undone[k][j]=0;
							if(dist[i][j]>max){
								max=dist[i][j];
							}
						}
					}
					if(max>0){
						double power=-T0*Math.pow(max, alpha)/Math.log(Ps);
						result=result+pow2db(power);
					}
				}// for each xik, add the power it sonsumes.
			}
		}// cal power of D2D
		
		for(int k=0;k<m;k++){
			for(int j=0;j<numOfdest;j++){
				if(undone[k][j]==1){
					double x=dests[j].x;
					double y=dests[j].y;
					double dis=Math.sqrt(x*x+y*y);
					double power=-T0*Math.pow(dis, alpha)/Math.log(Ps);
					result=result+pow2db(power);
				}
			}
		}
			
		
		
		return result;
	}

	public double costOfL(int[][] xik){
		double result=0;
		int[][] undone=new int[m][numOfdest];
		for(int i=0;i<m;i++){
			for(int j=0;j<numOfdest;j++){
				undone[i][j]=request[i][j];
			}
		}
		
		for(int i=0;i<numOfsource;i++){
			for(int k=0;k<m;k++){
				if(xik[i][k]==1){
					double max=0;
					for(int j=0;j<numOfdest;j++){
						if(undone[k][j]==1&&t[i][j]==1){
							undone[k][j]=0;
							if(dist[i][j]>max){
								max=dist[i][j];
							}
						}
					}
					if(max>0){
						double power=-T0*Math.pow(max, alpha)/Math.log(Ps);
						result=result+pow2db(power);
					}
				}// for each xik, add the power it sonsumes.
			}
		}// cal power of D2D
		
		for(int k=0;k<m;k++){
			for(int j=0;j<numOfdest;j++){
				if(undone[k][j]==1){
					double x=dests[j].x;
					double y=dests[j].y;
					double dis=Math.sqrt(x*x+y*y);
					double power=-T0*Math.pow(dis, alpha)/Math.log(Ps);
					result=result+pow2db(power);
				}
			}
		}
			
		
		
		return result;
	}
	
	public double costOfUnicast(){
		double result=0;
		for(int i=0;i<m;i++){
			for(int j=0;j<numOfdest;j++){
				if(request[i][j]==1){
					double x=dests[j].x;
					double y=dests[j].y;
					double dis=Math.sqrt(x*x+y*y);
					double power=-T0*Math.pow(dis, alpha)/Math.log(Ps);
					result=result+pow2db(power);
				}
			}
		}
		return result;
	}
	
	public double costOfSC(int[][] xik){
		double result=0;
		int[][] undone=new int[m][numOfdest];
		for(int i=0;i<m;i++){
			for(int j=0;j<numOfdest;j++){
				undone[i][j]=request[i][j];
			}
		}
		
		for(int i=0;i<numOfsource;i++){
			for(int k=0;k<m;k++){
				if(xik[i][k]==1){
					double max=0;
					for(int j=0;j<numOfdest;j++){
						if(undone[k][j]==1&&t[i][j]==1){
							undone[k][j]=0;
							if(dist[i][j]>max){
								max=dist[i][j];
							}
						}
					}
					if(max>0){
						double power=-T0*Math.pow(max, alpha)/Math.log(Ps);
						result=result+pow2db(power);
					}
				}// for each xik, add the power it sonsumes.
			}
		}// cal power of D2D
		
		for(int k=0;k<m;k++){
			if(xik[numOfsource][k]==0)continue;
			for(int j=0;j<numOfdest;j++){
				if(undone[k][j]==1){
					double x=dests[j].x;
					double y=dests[j].y;
					double dis=Math.sqrt(x*x+y*y);
					double power=-T0*Math.pow(dis, alpha)/Math.log(Ps);
					result=result+pow2db(power);
				}
			}
		}
			
		
		
		return result;
	}
	
	public double calRatio(int[][] xik){
		double result=0;
		double num=0;
		int[][] undone=new int[m][numOfdest];
		for(int i=0;i<m;i++){
			for(int j=0;j<numOfdest;j++){
				undone[i][j]=request[i][j];
			}
		}
		int numOfD2D=0;
		for(int i=0;i<numOfsource;i++){
			for(int k=0;k<m;k++){
				if(xik[i][k]==1){
					
					for(int j=0;j<numOfdest;j++){
						if(undone[k][j]==1&&t[i][j]==1){
							undone[k][j]=0;
							numOfD2D++;
						}
					}
					
				}// for each xik, add the power it sonsumes.
			}
		}// cal power of D2D
		
		for(int k=0;k<m;k++){
			for(int j=0;j<numOfdest;j++){
				if(undone[k][j]==1){
					num=num+1;
				}
			}
		}
			
		
		if((numOfD2D+num)==0) return 0;
		
		return numOfD2D/(numOfD2D+num);
	}
}
