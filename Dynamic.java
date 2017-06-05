import java.util.ArrayList;
// class for dynamic scenarios
public class Dynamic {

	
	int m;
	ArrayList<Source> sources;
	ArrayList<Dest> dests;
	
	double lambda_c,lambda_r; //average arrival rate of cache and requester
	double lambda_q;// average rate for generating an request
	double mu_c,mu_r;// average staying time of cache and requester
	double T0,alpha,R,L,Ps;
	double zipfc,zipfr;
	int cache_size;
	double time;
	
	int numOfd2d;
	int numOfbs;
	double total_latency;
	double total_cost;
	
	ArrayList<D2DTrans> d2dtrans;
	ArrayList<BSTrans> bstrans;
	public double xc;
	public double yc;
	public static double file_size=1000;
	public static double unit_time=0.5;
	public static double P_BS=10;
	public static double P_C=0.1;
	public static double P0=0.1;
	public static int numOfslots=100;
	public static double epsilon_mckp=0.1;
	public static double epsilon_l=0.1;
	public static double bandwith=Math.pow(10, 6);
	public static double N0=bandwith*Math.pow(10,-147/10.0);
	
 	Dynamic(double lambda_c,double lambda_r,double lambda_q,
			double mu_c,double mu_r,double T0,double alpha,
			double R,double L,double Ps,int m, double zipfc,
			double zipfr,int cache_size){
		this.lambda_c=lambda_c;
		this.lambda_r=lambda_r;
		this.lambda_q=lambda_q;
		this.mu_c=mu_c;
		this.mu_r=mu_r;
		
		this.T0=T0;
		this.alpha=alpha;
		this.R=R;
		this.L=L;
		this.Ps=Ps;
		this.m=m;
		this.zipfc=zipfc;
		this.zipfr=zipfr;
		this.cache_size=cache_size;
		this.time=0;
		numOfd2d=0;
		numOfbs=0;
		total_latency=0;
		total_cost=0;
		sources=new ArrayList<Source>();
		dests=new ArrayList<Dest>();
		
		d2dtrans=new ArrayList<D2DTrans>();
		bstrans=new ArrayList<BSTrans>();
		
		xc=(Math.random()-0.5)*R;
		yc=(Math.random()-0.5)*R;
	}
	
	 boolean notIn(int[] a,int len,int b){
		for(int i=0;i<len;i++)
			if(b==a[i]) return false;
		return true;
	}// return if b isnot in a[]
	
	Source new_Source(){
		double x=(Math.random()-0.5)*R;
		double y=(Math.random()-0.5)*R;
		int[] cache=new int[cache_size];
		for(int j=0;j<cache_size;){
			int file=Distribution.zipf(m, zipfc);
			if(notIn(cache,j,file)){
				cache[j]=file;
				j++;
			}
			
		}// generate cache devices
		
		
		return new Source(x,y,cache);
	}
	
	Dest new_Dest(){
		double x=(Math.random()-0.5)*R;
		double y=(Math.random()-0.5)*R;
		
		return new Dest(x,y);
	}

	void update_devices(){
		
		for(int i=0;i<sources.size();i++){
			Source s=sources.get(i);
			if(s.time_leave<=time){
				sources.remove(s);
				continue;
			}
		}// remove leaving cache
		
		for(int i=0;i<dests.size();i++){
			Dest d=dests.get(i);
			if(d.time_leave<=time){
				dests.remove(d);
				continue;
			}
		}// remove leaving requester*/
		int c=(int)(Distribution.possion(lambda_c*unit_time));
		int t=(int)(Distribution.possion(lambda_r*unit_time));
		for(int i=0;i<c;i++){
			Source s=new_Source();
			s.time_arrive=time;
			s.time_leave=time+Distribution.exp(1/mu_c);
			sources.add(s);
		}
		for(int i=0;i<t;i++){
			Dest d=new_Dest();
			d.time_arrive=time;
			d.time_leave=time+Distribution.exp(1/mu_r);
			d.time_next_request=time+Distribution.exp(lambda_q);
			dests.add(d);
		}
		
	}
	//update devices data structure
	void update_requests(){
		for(int i=0;i<dests.size();i++){
			Dest d=dests.get(i);
			if(d.time_next_request<=time){
				int file=Distribution.zipf(m, zipfr);
				int index=d.requested_files.indexOf(file);
				if(index>=0){
					d.timeOfrequest.set(index, d.time_next_request);
				}
				else{
				d.requested_files.add(file);
				d.timeOfrequest.add(d.time_next_request);
				d.time_next_request+=Distribution.exp(lambda_q);
				}
			}
		}
	}
	// updata new request
	Source[] get_Source(ArrayList<Source> sources){
		Source[] s=sources.toArray(new Source[sources.size()]);
		return s;
	}
	
	Dest[] get_Dest(ArrayList<Dest> dests){
		Dest[] d=dests.toArray(new Dest[dests.size()]);
		return d;
	}

	int[][] get_request(Dest[] d){
		int[][] r=new int[m][d.length];
		for(int i=0;i<d.length;i++){
			for(int j=0;j<d[i].requested_files.size();j++){
				int file=d[i].requested_files.get(j);
				r[file][i]=1;
			}
		}
		return r;
	}

	

	int[][] getMCKP(RawData data){
		MCKPdata mdata=new MCKPdata(data);
		FPTAS f=new FPTAS(mdata.numOfbags,mdata.itemarray,mdata.C,
				mdata.price,mdata.weight,epsilon_mckp,mdata.numOfm);
		
		return f.x_ik;
	} //get MCKP solution
	
	int[][] getSC(RawData data){
		SCdata sdata=new SCdata(data);
		
		SetCover sc=new SetCover(sdata.numOfsource,sdata.m,sdata.r,sdata.sets,sdata.weight);
		sc.run();
		//double w=data.costOfL(sc.xik);
		//double m=data.calRatio(sc.xik);
		//System.out.println("setcover:W:"+w+"Ratio:"+m);
		return sc.xik;
	}
	
	int[][] getL(RawData data){
		Lagurange L=new Lagurange(data,epsilon_l);
		L.run();
		//double w=data.costOfL(L.x_ik);
		//double m=data.calRatio(L.x_ik);
		//System.out.println("L.W:"+w+"Ratio:"+m);
		//printi(L.x_ik);
		//printi(data.mik);
		//printd(data.cost);
		return L.x_ik;
	}
	
	
/*	void clearRequests(){
		for(int i=0;i<dests.size();i++){
			dests.get(i).requested_files.clear();
			dests.get(i).timeOfrequest.clear();
		}
	}*/
	
	void allocate(int[][] xik,RawData data){
		int[][] undone=new int[data.m][data.numOfdest];
		for(int i=0;i<data.m;i++){
			for(int j=0;j<data.numOfdest;j++){
				if(data.request[i][j]==1){
					undone[i][j]=1;
				}
			}
		}
		
		for(int i=0;i<data.numOfsource;i++){
			for(int k=0;k<data.m;k++){
				if(xik[i][k]==1 && sources.get(i).state==0){
					sources.get(i).state=1;
					ArrayList<Dest> d=new ArrayList<Dest>();
					ArrayList<Double> rt=new ArrayList<Double>();
					for(int j=0;j<data.numOfdest;j++){
						if(undone[k][j]==1 && data.t[i][j]==1 && dests.get(j).state==0){
							dests.get(j).state=1;
							undone[k][j]=0;
							d.add(dests.get(j));
							int index=dests.get(j).requested_files.indexOf(k);
							rt.add(dests.get(j).timeOfrequest.get(index));
							dests.get(j).requested_files.remove(index);
							dests.get(j).timeOfrequest.remove(index);
						}
					}
					D2DTrans dt=new D2DTrans(rt,d,k,sources.get(i));
					d2dtrans.add(dt);
				}
			}
		}
		
		for(int i=0;i<data.m;i++){
			for(int j=0;j<data.numOfdest;j++){
				if(undone[i][j]==1 && dests.get(j).state==0){
					dests.get(j).state=1;
					int index=dests.get(j).requested_files.indexOf(i);
					double time=dests.get(j).timeOfrequest.get(index);
					undone[i][j]=0;
					BSTrans bt=new BSTrans(i,time,dests.get(j));
					bstrans.add(bt);
				}
			}
		}
		
	}
	// based on the allocation, allocate the requests
	void updateTrans(){
		for(int i=0;i<d2dtrans.size();i++){
			D2DTrans t=d2dtrans.get(i);
			if(sources.indexOf(t.s)<0){
				for(int j=0;j<t.d.size();j++){
					Dest d=t.d.get(j);
					if(dests.indexOf(d)>=0){
						d.state=0;
						d.requested_files.add(t.file);
						d.timeOfrequest.add(t.requesttime.get(j));
					}
				}
				//t.s.state=0;
				d2dtrans.remove(t);
				
				continue;
			}
			else{
				
				t.size=t.rate*unit_time;
				if(t.size>=file_size){
					Dest ds=Maxdistance(t.s,t.d);
					if(ds!=null){
						double dis=Math.sqrt(Math.pow(t.s.x-ds.x, 2)+Math.pow(
								t.s.y-ds.y, 2));
						double cost=-T0*Math.pow(dis, alpha)/Math.log(Ps);
						total_cost+=RawData.pow2db(cost);
					}
					for(int j=0;j<t.d.size();j++){
						Dest d=t.d.get(j);
						if(dests.indexOf(d)>=0){
							d.state=0;
							numOfd2d++;
							double diff=(t.size-file_size)/t.rate;
							total_latency+=time-diff-t.requesttime.get(j);
						}
							
					}
					t.s.state=0;
					d2dtrans.remove(i);
					continue;
				}
			}
		}// end of update d2d
		for(int i=0;i<bstrans.size();i++){
			BSTrans t=bstrans.get(i);
			if(dests.indexOf(t.d)<0){
				Dest d=t.d;
				d.requested_files.add(t.file);
				d.timeOfrequest.add(t.request_time);
				bstrans.remove(i);
				continue;
			}
			else{
				t.size=t.rate*unit_time;
				
				if(t.size>=file_size){
					numOfbs++;
					double diff=(t.size-file_size)/t.rate;
					total_latency+=time-diff-t.request_time;
					t.d.state=0;
					double dis=Math.sqrt(Math.pow(t.d.x, 2)+Math.pow(
							t.d.y, 2));
					double cost=-T0*Math.pow(dis, alpha)/Math.log(Ps);
					total_cost+=RawData.pow2db(cost);
					bstrans.remove(i);
					continue;
				}
			}
		}
	}
	// update the transmissions
	void updateRate(ArrayList<ArrayList<Double>> p){
		for(int i=0;i<bstrans.size();i++){
			Dest d=bstrans.get(i).d;
			double dis=Math.sqrt(d.x*d.x+d.y*d.y);
			double inter=N0;
			double signal=P_BS*Math.pow(dis, -alpha);
			bstrans.get(i).rate=Math.log(1+signal/inter)*(bandwith/bstrans.size())/Math.log(2);
			//if(dests.indexOf(bstrans.get(i).d)<0)bstrans.get(i).size=file_size;
			
		}
		
		for(int i=0;i<d2dtrans.size();i++){
			
			ArrayList<Dest> d=d2dtrans.get(i).d;
			Source sr=d2dtrans.get(i).s;
			double r=minrate(p,sr,d);
			if(r>=0)
			d2dtrans.get(i).rate=r;
			else{
				d2dtrans.get(i).size=file_size;
			}
		}
	}
	// update the rate for transmission
	Dest Maxdistance(Source s,ArrayList<Dest> ds){
		double max=0;
		int index=-1;
		for(int i=0;i<ds.size();i++){
			Dest d=ds.get(i);
			double delta_x=s.x-d.x;
			double delta_y=s.y-d.y;
			double distance=Math.sqrt(delta_x*delta_x+delta_y*delta_y);
			if(distance>max){
				index=i;
				max=distance;
			}
		}
		if(index<0)return null;
		else return ds.get(index);
	}
	
	ArrayList<ArrayList<Double>> updatePower(){
		ArrayList<ArrayList<Double>> r=new ArrayList<ArrayList<Double>>();
		for(int i=0;i<sources.size();i++){
			ArrayList<Double> t=new ArrayList<Double>();
			for(int j=0;j<dests.size();j++){
				double v=power(sources.get(i),dests.get(j));
				t.add(v);
			}
			r.add(t);
		}
		
		return r;
	}
	
	double minrate(ArrayList<ArrayList<Double>> p,Source s,ArrayList<Dest> d){
		double min=Integer.MAX_VALUE;
		double inter=N0;
		int indexOfs=sources.indexOf(s);
		if(indexOfs<0)return -1;
		for(int i=0;i<d.size();i++){
			int indexOfd=dests.indexOf(d.get(i));
			double dis=Math.pow(d.get(i).x-xc, 2)+Math.pow(d.get(i).x-xc, 2);
			dis=Math.sqrt(dis);
			inter=N0+Distribution.exp(1/(P_C*Math.pow(dis, -alpha)));
			
			if(indexOfd<0){continue;}
			double signal=p.get(indexOfs).get(indexOfd);
			
			for(int j=0;j<d2dtrans.size();j++){
				Source a=d2dtrans.get(j).s;
				int t=sources.indexOf(a);
				if(t<0)continue;
				//System.out.println(t+","+indexOfd);
				inter+=p.get(t).get(indexOfd);
			}
			double rate=Math.log(1+signal/inter)*bandwith/Math.log(2);
			if(rate<min)min=rate;
		}
		return min;
	}
	
	double power(Source s,Dest d){
		
		double result=0;
		double delta_x=s.x-d.x;
		double delta_y=s.y-d.y;
		double distance=Math.sqrt(delta_x*delta_x+delta_y*delta_y);
		result=P0*Math.pow(distance, -alpha);
		return Distribution.exp(1/result);
	}
	
	void run(String alg){
		ArrayList<ArrayList<Double>> p;
		for(int i=0;i<numOfslots;i++){
			
			update_devices();
			update_requests();
			p=updatePower();
			
			if(0==(time%(2*unit_time))){
			Source[] s=get_Source(sources);
			Dest[] d=get_Dest(dests);
			int[][] r=get_request(d);
			//System.out.println(s.length+","+d.length);
			//System.out.println("trans:"+d2dtrans.size()+","+bstrans.size());
			RawData data=new RawData(s,d,r,T0,alpha,R,L,Ps,m,zipfc,zipfr);
			/*int num=0;
			for(int j=0;j<m;j++)
				for(int k=0;k<data.numOfdest;k++)
					if(r[j][k]==1)num++;
			System.out.println(num);*/
			int[][] xik=null;
			if(alg.equals("MCKP"))
				xik=getMCKP(data);
			if(alg.equals("L"))
				xik=getL(data);
			if(alg.equals("SC"))
				xik=getSC(data);
			allocate(xik,data);
			
			}
			updateRate(p);
			time=time+unit_time;
			updateTrans();
			
			
			
			
			
			
			
			
			
			
		}
	}
	
	

}
