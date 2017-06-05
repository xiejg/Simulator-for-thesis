import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
// Test class for dynamic scenario
public class Test3 {

	static int m=10;
	ArrayList<Source> sources;
	ArrayList<Dest> dests;
	
	static double lambda_c=0.5;
	static double lambda_r=1; //average arrival rate of cache and requester
	static double lambda_q=1;// average rate for generating an request
	static double mu_c=60;
	static double mu_r=60;// average staying time of cache and requester
	static double T0=2;
	static double alpha=3;
	static double R=500;
	static double L=200;
	static double Ps=0.8;
	static double zipfc=0.7;
	static double zipfr=0.9;
	static int cache_size=2;
	static int times=1000;
	static ArrayList<Double> var=new ArrayList<Double>();
	
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
			else if(args[0].equals("cache_arrive")){
				lambda_c=Double.parseDouble(args[1]);
			}
			else if(args[0].equals("requester_arrive")){
				lambda_r=Double.parseDouble(args[1]);
			}
			else if(args[0].equals("cache_stay")){
				mu_c=Double.parseDouble(args[1]);
			}
			else if(args[0].equals("requester_stay")){
				mu_r=Double.parseDouble(args[1]);
			}
			else if(args[0].equals("request")){
				lambda_q=Double.parseDouble(args[1]);
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
				cache_size=Integer.parseInt(args[1]);
			}
			else if(args[0].equals("C")){
				MCKPdata.para_c=Integer.parseInt(args[1]);
			}
			else if(args[0].equals("filesize")){
				Dynamic.file_size=Integer.parseInt(args[1]);
			}
			line=br.readLine();
		}
		return s;
	}

	static void set(String s,double val){
		if(s.equals("cache_arrive")){
			lambda_c=val;
		}
		if(s.equals("requester_arrive")){
			lambda_r=val;
		}
		if(s.equals("cache_stay")){
			mu_c=val;
		}
		if(s.equals("requester_stay")){
			mu_r=val;
		}
		else if(s.equals("request")){
			lambda_q=val;
		}
		if(s.equals("zipfc"))
			zipfc=val;
		if(s.equals("zipfr"))
			zipfr=val;
		if(s.equals("library_size"))
			m=(int)val;
		if(s.equals("cache_size"))
			cache_size=(int)val;
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
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		result.add(new ArrayList<Double>());
		for(int i=0;i<var.size();i++){
			double w_m=0;
			double w_l=0;
			double w_sc=0;
			
			double aw_m=0;
			double aw_l=0;
			double aw_sc=0;
			
			double r_m=0;
			double r_l=0;
			double r_sc=0;
			
			double l_m=0;
			double l_l=0;
			double l_sc=0;
			set(s,var.get(i));
			for(int j=0;j<times;j++){
				System.out.println(i+","+j+s);
				Dynamic d=new Dynamic(lambda_c,lambda_r, lambda_q,
						mu_c,mu_r,T0,alpha,R,L,Ps,m,zipfc,zipfr,cache_size);
				d.run("MCKP");
				Dynamic d2=new Dynamic(lambda_c,lambda_r, lambda_q,
						mu_c,mu_r,T0,alpha,R,L,Ps,m,zipfc,zipfr,cache_size);
				d2.run("L");
				Dynamic d3=new Dynamic(lambda_c,lambda_r, lambda_q,
						mu_c,mu_r,T0,alpha,R,L,Ps,m,zipfc,zipfr,cache_size);
				d3.run("SC");
				w_m=w_m+d.total_cost;
				w_l=w_l+d2.total_cost;
				w_sc=w_sc+d3.total_cost;
				
				aw_m=aw_m+d.total_cost/(d.numOfbs+d.numOfd2d);
				aw_l=aw_l+d2.total_cost/(d2.numOfbs+d2.numOfd2d);
				aw_sc=aw_sc+d3.total_cost/(d3.numOfbs+d3.numOfd2d);
				
				r_m+=(double)d.numOfd2d/(d.numOfbs+d.numOfd2d);
				r_l+=(double)d2.numOfd2d/(d2.numOfbs+d2.numOfd2d);
				r_sc+=(double)d3.numOfd2d/(d3.numOfbs+d3.numOfd2d);
				l_m+=d.total_latency/(d.numOfbs+d.numOfd2d);
				l_l+=d2.total_latency/(d2.numOfbs+d2.numOfd2d);
				l_sc+=d3.total_latency/(d3.numOfbs+d3.numOfd2d);
				
			}
			result.get(0).add(w_m/times);
			result.get(1).add(w_l/times);
			result.get(2).add(w_sc/times);
			
			result.get(3).add(r_m/times);
			result.get(4).add(r_l/times);
			result.get(5).add(r_sc/times);
			
			result.get(6).add(l_m/times);
			result.get(7).add(l_l/times);
			result.get(8).add(l_sc/times);
			
			result.get(9).add(aw_m/times);
			result.get(10).add(aw_l/times);
			result.get(11).add(aw_sc/times);
		}
		return result;
	}

	static void save(ArrayList<ArrayList<Double>> r,
			String filename) throws IOException{
		//mckp,l,sc
		String[] s=filename.split("/");
		File file=new File(s[0]+"/result/"+s[1]+"_cost.txt");
		FileWriter out = new FileWriter(file);
		for(int i=0;i<r.get(0).size();i++){
			
			
			out.write(r.get(0).get(i)+",");
			out.write(r.get(1).get(i)+",");
			out.write(r.get(2).get(i)+"\n");
			
		}
		out.close();
		s=filename.split("/");
		file=new File(s[0]+"/result/"+s[1]+"_ratio.txt");
		out = new FileWriter(file);
		for(int i=0;i<r.get(0).size();i++){
			
			
			out.write(r.get(3).get(i)+",");
			out.write(r.get(4).get(i)+",");
			out.write(r.get(5).get(i)+"\n");
			
		}
		out.close();
		
		s=filename.split("/");
		file=new File(s[0]+"/result/"+s[1]+"_latency.txt");
		out = new FileWriter(file);
		for(int i=0;i<r.get(0).size();i++){
			
			
			out.write(r.get(6).get(i)+",");
			out.write(r.get(7).get(i)+",");
			out.write(r.get(8).get(i)+"\n");
			
		}
		out.close();
		
		s=filename.split("/");
		file=new File(s[0]+"/result/"+s[1]+"_avrcost.txt");
		out = new FileWriter(file);
		for(int i=0;i<r.get(0).size();i++){
			
			
			out.write(r.get(9).get(i)+",");
			out.write(r.get(10).get(i)+",");
			out.write(r.get(11).get(i)+"\n");
			
		}
		out.close();
	}
	
	static void test(){
		Dynamic d=new Dynamic(lambda_c,lambda_r, lambda_q,
				mu_c,mu_r,T0,alpha,R,L,Ps,m,zipfc,zipfr,cache_size);
		d.run("MCKP");
		System.out.println("ratio:"+(double)d.numOfd2d/(d.numOfbs+d.numOfd2d)+
				"bs:"+d.numOfbs+"d2d:"+d.numOfd2d);
		System.out.println("cost:"+d.total_cost);
		System.out.println("latency:"+d.total_latency/(d.numOfbs+d.numOfd2d));
	}

	static void runtest() throws IOException{
		String[] exp={"cache_arrive","cache_stay","requester_arrive","requester_stay","request","zipfr","zipfc"};
		//String[] exp={"zipfc"};
		for(int i=0;i<exp.length;i++){
		String name="dynamic/"+exp[i];
		String s=conf(name);
		ArrayList<ArrayList<Double>> r=run(s);
		save(r,name);
		}
	}
	
	public static void main(String[] args) throws IOException{
		//test();
		runtest();
		
	}
	

}
