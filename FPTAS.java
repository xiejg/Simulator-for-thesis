import java.util.ArrayList;
// FPTAS algorithm
public class FPTAS {
	int numOfbags;
	int[][] numOfitems;
	int numOfm;
	int C;//limit of weight
	double P,W;// result of price and weight
	double epsilon;
	ArrayList<ArrayList<Integer>> price;
	ArrayList<ArrayList<Double>> weight;
	ArrayList<ArrayList<Integer>> x0;
	int[] solution0;
	int P0;
	int P0_new;
	double infinit=Double.POSITIVE_INFINITY; 
	int[][] x_ik;

	FPTAS(int numOfbags,int[][] numOfitems,int C, 
			ArrayList<ArrayList<Integer>> price,
			ArrayList<ArrayList<Double>> weight,
			double epsilon,int numOfm){
		this.C=C;
		this.numOfbags=numOfbags;
		this.numOfitems=numOfitems;
		this.price=price;
		this.weight=weight;
		this.epsilon=epsilon;
		this.numOfm=numOfm;
		this.x_ik=new int[numOfbags][numOfm];
		int[] s=run();
		this.W=calW(s);
		for(int i=0;i<numOfbags;i++){
			if(s[i]>=0)
				x_ik[i][s[i]]=1;
		}
	}
	
	DZ useDZ(int numOfbags,int[][] numOfitems,int C, 
			ArrayList<ArrayList<Integer>> price,
			ArrayList<ArrayList<Double>> weight){
		DZ dz=new DZ(numOfbags,numOfitems,C,price,weight,numOfm);
		dz.run();
		return dz;
	}
	
	ArrayList<ArrayList<Integer>> solutionOfdz(DZ dz){
		ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
		double[][] x=dz.result;
		for(int i=0;i<x.length;i++){
			ArrayList<Integer> xi=new ArrayList<Integer>();
			for(int j=0;j<x[i].length;j++){
				
					xi.add(j);
				
			} // end for j
			result.add(xi);
		}//end for i
		
		return result;
	}
	
	int[] Cal_P0(DZ dz){
		double result=0;
		double pa=0,pb=0;
		int xa=0,xb=0;
		int[] s;
		
		ArrayList<ArrayList<Integer>> solution=solutionOfdz(dz);
		int[] solutionA=new int[numOfbags];
		int[] solutionB=new int[numOfbags];
		int[] solutionC=new int[numOfbags];
		for(int i=0;i<numOfbags;i++){
			solutionA[i]=-1;
			solutionB[i]=-1;
			solutionC[i]=-1;
		}
		result=dz.P;
		for(int i=0;i<solution.size();i++){
			if(solution.get(i).size()>1){
				xa=solution.get(i).get(0);
				xb=solution.get(i).get(1);
				pa=price.get(i).get(xa);
				pb=price.get(i).get(xb);
				solutionA[i]=xa;
				solutionB[i]=xb;
				
				}
			else{
				int xc=solution.get(i).get(0);
				
				solutionC[i]=xc;
			}
		}// end calculation for pa pb pc
		s=solutionC;
		if(pa>result){
			result=pa;
			s=solutionA;
		}
	    if(pb>result){
			result=pb;
			s=solutionB;
			
		}
		P0=(int)result;
		return s;
	}
	
	void Cal_P0_new(DZ dz,ArrayList<ArrayList<Integer>> price){
		double result=0;
		double pa=0,pb=0;
		int xa=0,xb=0;
		
		
		ArrayList<ArrayList<Integer>> solution=solutionOfdz(dz);
		
		result=dz.P;
		for(int i=0;i<solution.size();i++){
			if(solution.get(i).size()>1){
				xa=solution.get(i).get(0);
				xb=solution.get(i).get(1);
				pa=price.get(i).get(xa);
				pb=price.get(i).get(xb);
				
				
				}
			else{
				int xc=solution.get(i).get(0);
				
				
			}
		}// end calculation for pa pb pc
		
		if(pa>result){
			result=pa;
			
		}
	    if(pb>result){
			result=pb;
			
			
		}
		P0_new=(int)result;
		
	}

	ArrayList<ArrayList<Integer>> scaleDown(){
		ArrayList<ArrayList<Integer>> result=new ArrayList<ArrayList<Integer>>();
		Double K;
		
		int n=0; 
		DZ dz=useDZ(numOfbags,numOfitems,C,price,weight);
		solution0=Cal_P0(dz);
	
		for(int i=0;i<numOfbags;i++){
			for(int j=0;j<numOfm;j++)
				if(numOfitems[i][j]==1)
					n++;
		}
		K=epsilon*P0/n;
		for(int i=0;i<price.size();i++){
			ArrayList<Integer> temp= new ArrayList<Integer>();
			for(int j=0;j<price.get(i).size();j++){
				int value=price.get(i).get(j);
				value=(int) (value/K);
				temp.add(value);
			}
			result.add(temp);
		}
		
		return result;
	}

	ArrayList<Integer> Candidates(ArrayList<ArrayList<Integer>> newPrice,int bag,int p){
		ArrayList<Integer> result=new ArrayList<Integer>();
		for(int i=0;i<newPrice.get(bag).size();i++){
			if(newPrice.get(bag).get(i)<=p){
				result.add(i);
			}
		}
		return result;
	}

	int minOfweight(ArrayList<ArrayList<Integer>> newPrice,int bag,int p,ArrayList<Integer> can,Function[][] F){
		double min=infinit;
		int index=-1;
		for(int i=0;i<can.size();i++){
			int item=can.get(i);
			double w=weight.get(bag).get(item);
			int delta_p=p-newPrice.get(bag).get(item);
			w=w+F[bag][delta_p].weight;
			if(w<=min){
				min=w;
				index=i;
			}
		}
		return index;
	}
	
	Function[][] Cal_F(){
		
		
		ArrayList<ArrayList<Integer>> newPrice=scaleDown();// calculate P0 and new price
		//System.out.println("scale down");
		if(P0==0)P0_new=0;
		else{
		DZ dz=useDZ(numOfbags,numOfitems,C,newPrice,weight);
		Cal_P0_new(dz,newPrice);
		}
		
		Function[][] F=new Function[numOfbags+1][3*P0_new+1];
		for(int i=0;i<=numOfbags;i++)
			F[i][0]=new Function(0.0,numOfbags);
		for(int p=1;p<=3*P0_new;p++){
			F[0][p]=new Function(infinit,numOfbags);
		}
		
		
		for(int p=1;p<=3*P0_new;p++){
			for(int i=0;i<numOfbags;i++){
				ArrayList<Integer> can=Candidates(newPrice,i,p);
				if(can.size()>0){
					int index=minOfweight(newPrice,i,p,can,F);
					//System.out.println(can.size());
					int item=can.get(index);
					double w=weight.get(i).get(item);
					int delta_p=p-newPrice.get(i).get(item);
					w=w+F[i][delta_p].weight;
					if(w<F[i][p].weight){
						F[i+1][p]=new Function(w,numOfbags);
						F[i+1][p].copy(F[i][delta_p].solution, i);
						F[i+1][p].solution[i]=item;
					}
					else{
						F[i+1][p]=new Function(F[i][delta_p].weight,numOfbags);
						F[i+1][p].copy(F[i][p].solution, i);
					}
				}
				else{
					F[i+1][p]=new Function(F[i][p].weight,numOfbags);
					F[i+1][p].copy(F[i][p].solution, i);
				}
				
			}
		}
		
		
		return F;
	}

	int[] run(){
		Function[][] F=Cal_F();// calculate P0 new_P0 and F function array
		int index=0;
		
		for(int i=1;i<=3*P0_new;i++){
			if(C>=F[numOfbags][i].weight){
				index=i;
				
			}
		}
		int p_star=0;
		for(int i=0;i<numOfbags;i++){
			int item=F[numOfbags][index].solution[i];
			if(item>=0)
				p_star=p_star+price.get(i).get(item);
		}// end of calculate p_star
		//System.out.println(p_star);
		//int[] a=F[numOfbags][index].solution;
		//System.out.println(a[0]+","+a[1]+","+a[2]);
		if(P0>p_star){
			P=P0;
			return solution0;
		}
		else {
			P=p_star;
			return F[numOfbags][index].solution;
		}
	}// Main algorithm based on algorithm presented in paper

	double calW(int[] s){
		double result=0;
		for(int i=0;i<s.length;i++){
			if(s[i]>=0){
				result=result+weight.get(i).get(s[i]);
			}
		}
		return result;
	}
}
