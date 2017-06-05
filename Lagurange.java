import java.util.*;
// algorithm based on paper
public class Lagurange {
	RawData data;
	int[][] x_ik,x_ikt,x_ikt1;
	double h_p;
	double obj_best,obj_t,obj_t1;
	double[][] lambda_t,lambda_t1,gamma;
	double epsilon;
	
	double W;
	
	Lagurange(RawData data,double epsilon){
		this.data=data;
		this.x_ik=new int[data.numOfsource][data.m];
		this.x_ikt=new int[data.numOfsource][data.m];
		this.x_ikt1=new int[data.numOfsource][data.m];
		
		this.lambda_t=new double[data.m][data.numOfdest];
		this.lambda_t1=new double[data.m][data.numOfdest];
		this.gamma=new double[data.m][data.numOfdest];
		this.W=0;
		this.epsilon=epsilon;
	}
	
	void Init(){
		this.h_p=1;
		this.obj_best=Double.MIN_VALUE;
		obj_t=0;
		
		
		
		for(int i=0;i<data.numOfsource;i++){
			int[] mi=data.sources[i].cache;
			for(int k=0;k<mi.length;k++){
				int file=mi[k];
				int[] dest_id=data.findDests(data.request, file, data.dist, i);// get R_ik
				for(int j=0;j<dest_id.length;j++){
					if(data.request[file][dest_id[j]]==1&& data.t[i][dest_id[j]]==1){
						
						double value=data.cost[i][file]/dest_id.length;
						lambda_t[file][dest_id[j]]+=value;
						
						
					}
				}// for R_ik loop
			}// for m_k loop
		}// for s_i to init lambda_t
		
		for(int i=0;i<data.numOfsource;i++){
			int[] mi=data.sources[i].cache;
			int index=-1;
			double max=Double.POSITIVE_INFINITY;
			for(int k=0;k<mi.length;k++){
				int file=mi[k];
				// calculation argmin k
				int[] dest_id=data.findDests(data.request, file, data.dist, i);// get R_ik
				double value=data.cost[i][file];
				for(int j=0;j<dest_id.length;j++){
					if(data.request[file][dest_id[j]]==1&& data.t[i][dest_id[j]]==1){						
						value=value-lambda_t[file][dest_id[j]];					
					}
				}// for R_ik loop
				if(value<max){
					index=file;
					max=value;
				}
			}// end of mk loop
			if(index>=0 && data.cost[i][index]<Double.POSITIVE_INFINITY){
				x_ikt[i][index]=1;
				obj_t=obj_t+data.cost[i][index];
			}
		}// init x_ikt 
		
		
		
		for(int j=0;j<data.numOfdest;j++){
			for(int k=0;k<data.m;k++){
				if(data.request[k][j]==1){
					gamma[k][j]=1;
					for(int i=0;i<data.numOfsource;i++){
						if(x_ikt[i][k]==1&&data.t[i][j]==1)
							gamma[k][j]=gamma[k][j]-1;
					}
					obj_t=obj_t+gamma[k][j]*lambda_t[k][j];
				}
			}
		}
		
		obj_best=obj_t;
		copy(x_ik,x_ikt);
		
	}
	
	void iteration(){
		double diff=0;
		do{
			this.x_ikt1=new int[data.numOfsource][data.m];
			this.lambda_t1=new double[data.m][data.numOfdest];
			for(int j=0;j<data.numOfdest;j++){
				for(int k=0;k<data.m;k++){
					if(data.request[k][j]==1){ //for all r
						
						for(int i=0;i<data.numOfsource;i++){
							if(data.mik[i][k]==1&&data.t[i][j]==1&&	data.request[k][j]==1)
								lambda_t1[k][j]=Math.max(0, lambda_t[k][j]+h_p*gamma[k][j]);
						}
						
					}
				}
			}// cal lambda(t+1)
			obj_t1=0;
			for(int i=0;i<data.numOfsource;i++){
				int[] mi=data.sources[i].cache;
				int index=-1;
				double max=Double.POSITIVE_INFINITY;
				for(int k=0;k<mi.length;k++){
					int file=mi[k];
					// calculation argmin k
					int[] dest_id=data.findDests(data.request, file, data.dist, i);// get R_ik
					double value=data.cost[i][file];
					for(int j=0;j<dest_id.length;j++){
						if(data.request[file][dest_id[j]]==1&& data.t[i][dest_id[j]]==1){						
							value=value-lambda_t1[file][dest_id[j]];					
						}
					}// for R_ik loop
					if(value<max){
						index=file;
						max=value;
					}
				}// end of mk loop
				if(index>=0 && data.cost[i][index]<Double.POSITIVE_INFINITY){
					x_ikt1[i][index]=1;
					obj_t1=obj_t1+data.cost[i][index];
				}
			}// cal x_ikt1 
			
			this.gamma=new double[data.m][data.numOfdest];
			
			for(int j=0;j<data.numOfdest;j++){
				for(int k=0;k<data.m;k++){
					if(data.request[k][j]==1){
						gamma[k][j]=1;
						for(int i=0;i<data.numOfsource;i++){
							if(x_ikt1[i][k]==1&&data.t[i][j]==1&&	data.request[k][j]==1)
								gamma[k][j]=gamma[k][j]-1;
						}
						obj_t1=obj_t1+gamma[k][j]*lambda_t1[k][j];
					}
				}
			}// end of cal obj_t1
			
			if(obj_t1>obj_best){
				obj_best=obj_t1;
				copy(x_ik,x_ikt1);
			}
			lambda_t=lambda_t1;
			diff=obj_t-obj_t1;
			obj_t=obj_t1;
			h_p=h_p/2;
			
		}while(Math.abs(diff)>epsilon);
	}
	
	int[][] getUnfulfilled(int[][] xik){
		int[][] result=new int[data.m][data.numOfdest];
		
		for(int k=0;k<data.m;k++)
			for(int j=0;j<data.numOfdest;j++){
				if(data.request[k][j]==1)
					result[k][j]=1;
			}
		
		for(int k=0;k<data.m;k++){
			for(int j=0;j<data.numOfdest;j++){
				if(data.request[k][j]==1){
					for(int i=0;i<data.numOfsource;i++){
						if(xik[i][k]==1&&data.t[i][j]==1)
							result[k][j]=0;
					}
				}
			}
		}
		
		return result;
	}

	int[] getUnallocated(int[][] xik){
		int[] result=new int[data.numOfsource];
		for(int i=0;i<data.numOfsource;i++){
			result[i]=1;
			for(int k=0;k<data.m;k++){
				if(xik[i][k]==1)result[i]=0;
			}
		}
		return result;
	}
	
	void run(){
		Init();
		iteration();
		int[][] undone=getUnfulfilled(x_ik);
		int[] idlesrc=getUnallocated(x_ik);
		
		
		for(int i=0;i<data.numOfsource;i++){
				if(idlesrc[i]==1){
					int[] m_id=data.sources[i].cache;
					double cost=Double.POSITIVE_INFINITY;
					int index=-1;
					for(int k=0;k<m_id.length;k++){
						if(data.mik[i][m_id[k]]==1){
							for(int j=0;j<data.numOfdest;j++){
								if(undone[m_id[k]][j]==1 && data.t[i][j]==1){
									if(data.cost[i][m_id[k]]<cost){
										index=k;
										cost=data.cost[i][k];
									}
								}
							}
						}
					}//
					if(index>=0)x_ik[i][m_id[index]]=1;
				}
				undone=getUnfulfilled(x_ik);
				idlesrc=getUnallocated(x_ik);
		}
		calW();
		
	}
	// Main algorithm
	void calW(){
		for(int i=0;i<data.numOfsource;i++){
			for(int j=0;j<data.m;j++){
				if(x_ik[i][j]==1){
					W=W+data.cost[i][j];
				}
			}
		}
	}

	void copy(int[][] a, int[][] b){
		for(int i=0;i<a.length;i++){
			for(int j=0;j<a[i].length;j++){
				a[i][j]=b[i][j];
			}
		}
	}
}
