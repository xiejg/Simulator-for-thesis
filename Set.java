// data structure for set cover
public class Set {
	int source;
	int message;
	int[] destinations;
	boolean choose;
	boolean BS;
	double weight;
	Set(int source,int message,int[] destinations,boolean BS,double weight){
		this.source=source;
		this.message=message;
		this.destinations=destinations;
		this.choose=false;
		this.BS=BS;
		this.weight=weight;
	}
}
