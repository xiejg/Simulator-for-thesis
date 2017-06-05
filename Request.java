// data structure for set cover algorithm

public class Request {
	int desti;
	int message;
	int source;
	boolean done;
	
	Request(int d,int m){
		message=m;
		desti=d;
		done=false;
		source=-1;
		
	}
}
