// Test class
public class Test {
	static int numOfsource=50;
	static int numOfdest=80;
	static int m=10;// file library size
	static Source[] sources;
	static Dest[] dests;
	
	static double T0=2;
	static double alpha=3;
	static double R=500;
	static double L=200;
	static double Ps=0.8;
	static double zipfc=0.7;
	static double zipfr=0.9;
	
	static int cacheSize=2;
	
	static boolean notIn(int[] a,int len,int b){
		for(int i=0;i<len;i++)
			if(b==a[i]) return false;
		return true;
	}
	
	static void printd(double[][] a){
		for(int i=0;i<2;i++){
			for(int j=0;j<a[i].length;j++){
				if(a[i][j]<10E20)
					System.out.print((int)a[i][j]+",");
				else
					System.out.print("0,");
			}
			System.out.println();
		}
	}
	
	static void printi(int[][] a){
		for(int i=0;i<2;i++){
			for(int j=0;j<a[i].length;j++){
				if(a[i][j]<10E20)
					System.out.print((int)a[i][j]+",");
				else
					System.out.print("0,");
			}
			System.out.println();
		}
	}
	
	static void genDevices(int s,int d){
		sources=new Source[s];
		dests=new Dest[d];
		for(int i=0;i<s;i++){
			double x=(Math.random()-0.5)*R;
			double y=(Math.random()-0.5)*R;
			int[] cache=new int[cacheSize];
			for(int j=0;j<cacheSize;){
				int file=Distribution.zipf(m, zipfc);
				if(notIn(cache,j,file)){
					cache[j]=file;
					j++;
				}
				
			}// gen cache
			
			sources[i]=new Source(x,y,cache);
		}// gen source
		for(int i=0;i<d;i++){
			double x=(Math.random()-0.5)*R;
			double y=(Math.random()-0.5)*R;
			dests[i]=new Dest(x,y);
		}
		
	}

	static double testMCKP(RawData data){

		//print(data.costBS);
		MCKPdata mdata=new MCKPdata(data);
		
		//DZ dz=new DZ(mdata.numOfbags,mdata.itemarray,mdata.C,
			//	mdata.price,mdata.weight,mdata.numOfm);
		System.out.println(mdata.C);
		FPTAS f=new FPTAS(mdata.numOfbags,mdata.itemarray,mdata.C,
				mdata.price,mdata.weight,0.01,mdata.numOfm);
		if(f.P0==0){
			for(int i=0;i<f.solution0.length;i++){
				System.out.print(f.solution0[i]+",");
				
			}
			System.out.println();
			//printd(data.cost);
			//printd(data.costBS);
		}
		System.out.println("FPTAS"+",P:"+f.P);
		double w=data.costOfMCKP(f.x_ik);
		System.out.println("P0:"+f.P0+",P0_new:"+f.P0_new+",W:"+w);
		//System.out.println("DZ:"+"P:"+dz.P+"W:"+dz.W+",integer solution:"+result);
		//print(data.costBS);
		//System.out.print("solution:");
		//print(dz.result);
		//print(data.cost);
		return w;
	}

	static double testSC(RawData data){
		SCdata sdata=new SCdata(data);
		
		SetCover sc=new SetCover(sdata.numOfsource,sdata.m,sdata.r,sdata.sets,sdata.weight);
		sc.run();
		double w=data.costOfL(sc.xik);
		System.out.println("setcover:W:"+w);
		return w;
	}
	
	static double testL(RawData data){
		Lagurange L=new Lagurange(data,0.01);
		L.run();
		double w=data.costOfL(L.x_ik);
		System.out.println("L.W:"+w);
		//printi(L.x_ik);
		//printi(data.mik);
		//printd(data.cost);
		return w;
	}
	
	public static void main(String[] args){
		genDevices(numOfsource,numOfdest);
		RawData data=new RawData(sources,dests,T0,alpha,R,L,Ps,m,zipfc,zipfr);
		data.run();
		testMCKP(data);
		testSC(data);
		testL(data);
	}

}
