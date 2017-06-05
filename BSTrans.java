
// data structure for unicast transmission from BS
public class BSTrans extends Transmission{
	double request_time;
	int file;
	Dest d;
	
	BSTrans(int file,double time,Dest d){
		this.file=file;
		this.request_time=time;
		this.d=d;
	}
}
