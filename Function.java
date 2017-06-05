//data structure for F function
public class Function {
	double weight;
	int[] solution;
	Function(double weight,int num){
		this.weight=weight;
		this.solution=new int[num];
		for(int i=0;i<num;i++)
			solution[i]=-1;
	}
	void copy(int[] from,int num){
		for(int i=0;i<num;i++){
			solution[i]=from[i];
		}
	}
}
