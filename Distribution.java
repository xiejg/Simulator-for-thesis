
public class Distribution {
	public static double exp(double lambda){
		//double lambda=1.0/avg;
		double z=Math.random();
		double x=-(1/lambda)*Math.log(z);
		
		return x;
	}
	public static double possion(double lambda){
		double L = Math.exp(-lambda);
		  double p = 1.0;
		  int k = 0;

		  do {
		    k++;
		    p *= Math.random();
		  } while (p > L);

		  return k - 1;
	}
	
	public static int zipf(int m,double k){
		double c=0.0;
		int x=-1;
		for(int i=0;i<m;i++){
			c+=1.0/Math.pow(i+1, k);
		}
		double sum=0.0;
		double[] cdf=new double[m];
		for(int i=0;i<m;i++){
			sum+=1.0/(Math.pow(i+1, k)*c);
			cdf[i]=sum;
		}
		double r=Math.random();
		for(int i=0;i<m;i++){
			if(r<cdf[i]){
				x=i;
				break;
			}
		}
		return x;
	}
	
}
