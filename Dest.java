import java.util.ArrayList;
// data structure for requesting device
public class Dest {
	double x;
	double y;
	public double time_arrive;
	public double time_leave;
	public double time_next_request;
	ArrayList<Integer> requested_files;
	ArrayList<Double> timeOfrequest;
	int state;
	Dest(double x,double y){
		this.x=x;
		this.y=y;
		state=0;
		requested_files=new ArrayList<Integer>();
		timeOfrequest=new ArrayList<Double>();
	}
}
