import java.util.ArrayList;
//data structure for multicast transmission from source s

public class D2DTrans extends Transmission{
	ArrayList<Double> requesttime;
	ArrayList<Dest> d;
	int file;
	Source s;
	D2DTrans(ArrayList<Double> requesttime,ArrayList<Dest> d,int file,Source s){
		this.requesttime=requesttime;
		this.d=d;
		this.file=file;
		this.s=s;
	}
}
