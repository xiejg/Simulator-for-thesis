// data structure for cache device

public class Source {

	double x;
	double y;
	int[] cache;
	public double time_arrive;
	public double time_leave;
	int state;
	Source(double x,double y,int[] cache){
		this.x=x;
		this.y=y;
		state=0;
		this.cache=cache;
	}
	
}
