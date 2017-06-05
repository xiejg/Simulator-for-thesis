import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
// Test class for static scenario
public class Test2 {
	static ArrayList<Double> var=new ArrayList<Double>();
	static int times=1000;
	static int numOfsource=10;
	static int numOfdest=20;
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
	static double epsilon_mckp=0.1;
	static double epsilon_l=0.01;
	static int cacheSize=2;
	
	static boolean notIn(int[] a,int len,int b){
		for(int i=0;i<len;i++)
			if(b==a[i]) return false;
		return true;
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

	static Metrics testMCKP(RawData data){

		
		MCKPdata mdata=new MCKPdata(data);
	
		//System.out.println(mdata.C);
		FPTAS f=new FPTAS(mdata.numOfbags,mdata.itemarray,mdata.C,
				mdata.price,mdata.weight,epsilon_mckp,mdata.numOfm);
		//System.out.println("FPTAS"+",P:"+f.P);
		double w=data.costOfMCKP(f.x_ik);
		double m=data.calRatio(f.x_ik);
		System.out.println("P0:"+f.P0+",P0_new:"+f.P0_new+",W:"+w+"Ratio:"+m);
		
		return new Metrics(w,m);
	}

	static Metrics testSC(RawData data){
		SCdata sdata=new SCdata(data);
		
		SetCover sc=new SetCover(sdata.numOfsource,sdata.m,sdata.r,sdata.sets,sdata.weight);
		sc.run();
		double w=data.costOfL(sc.xik);
		double m=data.calRatio(sc.xik);
		System.out.println("setcover:W:"+w+"Ratio:"+m);
		return new Metrics(w,m);
	}
	
	static Metrics testL(RawData data){
		Lagurange L=new Lagurange(data,epsilon_l);
		L.run();
		double w=data.costOfL(L.x_ik);
		double m=data.calRatio(L.x_ik);
		System.out.println("L.W:"+w+"Ratio:"+m);
		//printi(L.x_ik);
		//printi(data.mik);
		//printd(data.cost);
		return new Metrics(w,m);
	}
	
	static String conf(String name) throws IOException{
		String s=null;
		var.clear();
		File file=new File(name);
		FileReader in = new FileReader(file);
		BufferedReader br= new BufferedReader(in);
		String line=br.readLine();
		while(line!=null){
			String[] args=line.split("=");
			String[] read=args[1].split(",");
			if(read.length>1){
				s=args[0];
				for(int i=0;i<read.length;i++){
					double d=Double.parseDouble(read[i]);
					var.add(d);
				}
			}
			else if(args[0].equals("cache")){
				numOfsource=Integer.parseInt(args[1]);
			}
			else if(args[0].equals("requester")){
				numOfdest=Integer.parseInt(args[1]);
			}
			else if(args[0].equals("zipfc")){
				zipfc=Double.parseDouble(args[1]);
			}
			else if(args[0].equals("zipfr")){
				zipfc=Double.parseDouble(args[1]);
			}
			else if(args[0].equals("library_size")){
				m=Integer.parseInt(args[1]);
			}
			else if(args[0].equals("cache_size")){
				cacheSize=Integer.parseInt(args[1]);
			}
			else if(args[0].equals("C")){
				MCKPdata.para_c=Integer.parseInt(args[1]);
			}
			line=br.readLine();
		}
		return s;
	}

	static void set(String s,double val){
		if(s.equals("cache"))
			numOfsource=(int)val;
		if(s.equals("requester"))
			numOfdest=(int)val;
		if(s.equals("zipfc"))
			zipfc=val;
		if(s.equals("zipfr"))
			zipfr=val;
		if(s.equals("library_size"))
			m=(int)val;
		if(s.equals("cache_size"))
			cacheSize=(int)val;
		if(s.equals("C"))
			MCKPdata.para_c=val;
	}
	
	static ArrayList<ArrayList<Double>> run(String s){
		ArrayList<ArrayList<Double>> result=new ArrayList<ArrayList<Double>>();
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		
		
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		for(int i=0;i<var.size();i++){
			double w_m=0;
			double w_l=0;
			double w_sc=0;
			double w_unicast=0;
			double r_m=0;
			double r_l=0;
			double r_sc=0;
			set(s,var.get(i));
			for(int j=0;j<times;j++){
				System.out.println(i+":"+j);
				genDevices(numOfsource,numOfdest);
				RawData data=new RawData(sources,dests,T0,alpha,R,L,Ps,m,zipfc,zipfr);
				data.run();
				Metrics m=testMCKP(data);
				Metrics l=testL(data);
				Metrics sc=testSC(data);;
				w_m=w_m+m.cost;
				w_l=w_l+l.cost;
				w_sc=w_sc+sc.cost;
				r_m+=m.ratio;
				r_l+=l.ratio;
				r_sc+=sc.ratio;
				w_unicast+=data.costOfUnicast();
			}
			result.get(0).add(w_m/times);
			result.get(1).add(w_l/times);
			result.get(2).add(w_sc/times);
			result.get(3).add(w_unicast/times);
			result.get(4).add(r_m/times);
			result.get(5).add(r_l/times);
			result.get(6).add(r_sc/times);
		}
		return result;
	}

	static void save(ArrayList<ArrayList<Double>> r,
			String filename) throws IOException{
		//mckp,l,sc
		File file=new File(filename+"_cost.txt");
		FileWriter out = new FileWriter(file);
		for(int i=0;i<r.get(0).size();i++){
			
			
			out.write(r.get(0).get(i)+",");
			out.write(r.get(1).get(i)+",");
			out.write(r.get(2).get(i)+",");
			out.write(r.get(3).get(i)+"\n");
		}
		out.close();
		
		file=new File(filename+"_ratio.txt");
		out = new FileWriter(file);
		for(int i=0;i<r.get(0).size();i++){
			
			
			out.write(r.get(4).get(i)+",");
			out.write(r.get(5).get(i)+",");
			out.write(r.get(6).get(i)+"\n");
			
		}
		out.close();
	}

	public static void main(String[] args) throws IOException{
		String name="static_zipfr";
		String s=conf(name);
		ArrayList<ArrayList<Double>> r=run(s);
		save(r,name);
	}
}
