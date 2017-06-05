import java.util.*;

// DZ algorithm
public class DZ {
	int numOfbags;
	int[][] numOfitems;
	int C;//limit of weight
	double P,W;// result of price and weight
	ArrayList<ArrayList<Integer>> price;
	ArrayList<ArrayList<Double>> weight;
	ArrayList<ArrayList<Integer>> solution;
	double[][] result;
	int numOfm;
	
	DZ(int numOfbags,int[][] numOfitems,int C, 
			ArrayList<ArrayList<Integer>> price,
			ArrayList<ArrayList<Double>> weight,
			int numOfm){
		this.C=C;
		this.numOfbags=numOfbags;
		this.numOfitems=numOfitems;
		this.price=price;
		this.weight=weight;
		this.P=0;
		this.W=0;
		this.numOfm=numOfm;
		
		this.result=new double[numOfbags][numOfm];
		solution=new ArrayList<ArrayList<Integer>>();
		for(int i=0;i<numOfbags;i++){  //initialize the solution
			ArrayList<Integer> temp=new ArrayList<Integer>();
			for(int j=0;j<numOfm;j++){
				if(numOfitems[i][j]==1)
				temp.add(j);
				result[i][j]=-1;
			}
			solution.add(temp);
		}
		
	}// end of initialize
	
	boolean Dominate(int bag,int item1,int item2){
		if(weight.get(bag).get(item1)<=weight.get(bag).get(item2)){
			if(price.get(bag).get(item1)>=price.get(bag).get(item2))
				return true;
		}
	
		return false;
	}
	
	boolean Order(int bag,int item1,int item2){
		if(weight.get(bag).get(item1)<weight.get(bag).get(item2)){
			return true;
		}
		if((weight.get(bag).get(item1)-weight.get(bag).get(item2))==0){
			if(price.get(bag).get(item1)>=price.get(bag).get(item2))
				return true;
		}
		return false;
	}
	
	ArrayList<ArrayList<Pair>> PairItems(){
		ArrayList<ArrayList<Pair>> result =new ArrayList<ArrayList<Pair>>();
		for(int i=0;i<numOfbags;i++){
			ArrayList<Pair> temp=new ArrayList<Pair>();
			
			while(solution.get(i).size()>1){
				int item1=solution.get(i).get(0);
				int item2=solution.get(i).get(1);
				
				if(Order(i,item1,item2)==false){
					int t=item1;
					item1=item2;
					item2=t;
				}
				
				if(Dominate(i,item1,item2)){
					int index=solution.get(i).indexOf(item2);
					solution.get(i).remove(index);
					continue;
					//solution.get(i).remove(new Integer(item2));
					//System.out.println("remove"+" from ");
				}
				else{
					
					if((weight.get(i).get(item1)-weight.get(i).get(item2))==0){
						int index=solution.get(i).indexOf(item1);
						solution.get(i).remove(index);
					}
					else{
					Pair p=new Pair(i,item1,item2);
					temp.add(p);
					int index=solution.get(i).indexOf(item1);
					solution.get(i).remove(index);
					index=solution.get(i).indexOf(item2);
					solution.get(i).remove(index);
					}
					
				}
				
			}
			result.add(temp);
			/*for(int j=0;j<temp.size();j++){
				
				System.out.print("pair:("+temp.get(j).item1+","+temp.get(j).item2+"),");
				if(j==temp.size()-1) System.out.println("bags:"+i);
			}*/
		}
		return result;
	}

	double CalSlope(ArrayList<ArrayList<Pair>> p){
		double slope=0.0;
		int num=0;
		for(int i=0;i<numOfbags;i++){
			if(solution.get(i).size()==1&&p.get(i).size()==0){
				int item=solution.get(i).get(0);
				if(result[i][item]==-1){
					double w=this.W+weight.get(i).get(item);
					
					if(w<C){
					this.W=this.W+weight.get(i).get(item);
					this.P=this.P+price.get(i).get(item);
					result[i][item]=1;
					}
				}
						
			}
			for(int j=0;j<p.get(i).size();j++){
				Pair t=p.get(i).get(j);
				double delta_p=price.get(i).get(t.item1)-price.get(i).get(t.item2);
				double delta_w=weight.get(i).get(t.item1)-weight.get(i).get(t.item2);
				if(delta_w==0){
					System.out.println("weight"+delta_w);
					System.out.println("price"+delta_p);
				}
				
				t.slope=delta_p/delta_w;
				slope=slope+t.slope;
				num++;
				
			}// for each pair
		}// for each bag
		
		if(num==0) return 0;
		
		return slope/num;
	}

	int argmin(int bags,ArrayList<Integer> t,double slope){
		int result=-1;
		double max=0;
		for(int i=0;i<t.size();i++){
			double value=price.get(bags).get(t.get(i))-slope*weight.get(bags).get(t.get(i));
			if(value>=max)max=value;
		}
		ArrayList<Integer> temp=new ArrayList<Integer>();
		for(int i=0;i<t.size();i++){
			double value=price.get(bags).get(t.get(i))-slope*weight.get(bags).get(t.get(i));
			if(value==max)temp.add(t.get(i));
		}// get M_i
		
		double w=Double.MAX_VALUE;
		result=-1;
		for(int i=0;i<temp.size();i++){
			double value=weight.get(bags).get(temp.get(i));
			if(value<=w){
				w=value;
				result=temp.get(i);
			}
		}
		
		
		return result;
	}
	
	ArrayList<Integer> CalLow(ArrayList<ArrayList<Pair>> p,double slope){
		ArrayList<Integer> result=new ArrayList<Integer>();
		for(int i=0;i<numOfbags;i++){
			if(solution.get(i).size()==1&&p.get(i).size()==0){
				result.add(-1);
				continue;
			}
			else{
				ArrayList<Integer> t=new ArrayList<Integer>();
				if(solution.get(i).size()==1) t.add(solution.get(i).get(0));
				for(int j=0;j<p.get(i).size();j++){
					t.add(p.get(i).get(j).item1);
					t.add(p.get(i).get(j).item2);
				}
				int item=argmin(i,t,slope);
				result.add(item);
			}
		}
		return result;
	}
	
	int argmax(int bags,ArrayList<Integer> t,double slope){
		int result=-1;
		double max=0;
		for(int i=0;i<t.size();i++){
			double value=price.get(bags).get(t.get(i))-slope*weight.get(bags).get(t.get(i));
			if(value>=max)max=value;
		}
		ArrayList<Integer> temp=new ArrayList<Integer>();
		for(int i=0;i<t.size();i++){
			double value=price.get(bags).get(t.get(i))-slope*weight.get(bags).get(t.get(i));
			if(value==max)temp.add(t.get(i));
		}// get M_i
		
		double w=Double.MAX_VALUE;
		result=-1;
		for(int i=0;i<temp.size();i++){
			double value=weight.get(bags).get(temp.get(i));
			if(value>=w){
				w=value;
				result=temp.get(i);
			}
		}
		
		
		return result;
	}
	
	ArrayList<Integer> CalUp(ArrayList<ArrayList<Pair>> p,double slope){
		ArrayList<Integer> result=new ArrayList<Integer>();
		for(int i=0;i<numOfbags;i++){
			if(solution.get(i).size()==1&&p.get(i).size()==0){
				result.add(-1);
				continue;
			}
			else{
				ArrayList<Integer> t=new ArrayList<Integer>();
				if(solution.get(i).size()==1) t.add(solution.get(i).get(0));
				for(int j=0;j<p.get(i).size();j++){
					t.add(p.get(i).get(j).item1);
					t.add(p.get(i).get(j).item2);
				}
				int item=argmax(i,t,slope);
				result.add(item);
			}
		}
		return result;
	}
	
	boolean optimal(ArrayList<Integer> low,ArrayList<Integer> up){
		double l=0;
		double u=0;
		boolean r=true;
		for(int i=0;i<numOfbags;i++){
			if(low.get(i)>=0){
				l=l+weight.get(i).get(low.get(i));
				r=false;
			}
			if(up.get(i)>=0){
				u=u+weight.get(i).get(up.get(i));
				r=false;
			}
		}
		if(u==0) return r;
		if((l+W)<=C && (u+W)>C )
			return true;
//		System.out.println(l+","+u+","+W+","+C+","+P);
		return false;
	}
	
	void update (ArrayList<Integer> low,ArrayList<Integer> up,ArrayList<ArrayList<Pair>> p,double slope){
		double l=0;
		double u=0;
		for(int i=0;i<numOfbags;i++){
			if(low.get(i)>=0)
				l=l+weight.get(i).get(low.get(i));
			if(up.get(i)>=0)
				u=u+weight.get(i).get(up.get(i));
		}
//		System.out.println("l:"+l+"u:"+u+"W:"+W+"P:"+P+"slope:"+slope);
		{
			for(int i=0;i<numOfbags;i++)
				for(int j=0;j<p.get(i).size();j++){
					double value=p.get(i).get(j).slope;
					Pair t=p.get(i).get(j);
					if((l+W)>=C){
					if(value<=slope)solution.get(i).add(t.item1);
					}
					else if((u+W)<C){
						if(value>=slope)solution.get(i).add(t.item2);
					}
					else{
						solution.get(i).add(t.item1);
						solution.get(i).add(t.item2);
					}
				}
          //System.out.println(0);
		}
		
		
	}

	int run(){
		double slope;
		ArrayList<Integer> l,u;
		//Scanner reader = new Scanner(System.in);
		while(true){
		//reader.nextLine();
		ArrayList<ArrayList<Pair>> p=PairItems();
		slope=CalSlope(p);
		//System.out.println(slope);
		l=CalLow(p,slope);
		u=CalUp(p,slope);
		if(optimal(l,u)){
			
			for(int i=0;i<l.size();i++){
				int item=l.get(i);
				if(item>=0){
					W=W+weight.get(i).get(item);
					P=P+price.get(i).get(item);
					result[i][item]=1;
				}
			} // for loop for add W and P
			
			for(int i=0;i<l.size();i++){
				int iteml=l.get(i);
				int itemh=u.get(i);
				if(itemh>0  ){
					double diff=weight.get(i).get(itemh)-weight.get(i).get(iteml);
					double diff2=price.get(i).get(itemh)-price.get(i).get(iteml);
					if(W+diff<=C){
						result[i][iteml]=0;
						result[i][itemh]=1;
						W=W+diff;
						P=P+diff2;
					}
					else{
						result[i][itemh]=(C-W)/diff;
						result[i][iteml]=1-result[i][itemh];
						W=C;
						P=P-price.get(i).get(iteml)+price.get(i).get(iteml)*result[i][iteml];
						P=P+result[i][itemh]*price.get(i).get(itemh);
						return 0;
					}
				}
			}
			
			break;
		}// if optimal
		update(l,u,p,slope);
//		System.out.println(/*"l:"+l+",u:"+u+*/",W:"+W+",P:"+P+",slope:"+slope+",C:"+C);
		}
		return 1;
	}
}
