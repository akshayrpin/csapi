package csapi.workflow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;

public class RunWorkFlow {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		boolean result = true;
		int i =0;
		while(result){
			
			System.out.println(i+"*************"+result);
			i = i+1;
			
			if(i==9){
				result = false;
			}
		}

	}
	
	
	
	
	/*public static ResponseVO prerun(RequestVO vo){
		
		
		
		return o;
	}
	
	public static boolean postrun(RequestVO vo){
		boolean result = true;
		
		
		return result;
	}
	
	public static ResponseVO runCommon(RequestVO vo){
		
		
		
		return o;
	}
	*/
	public void runservice(){
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
		    String threadName = Thread.currentThread().getName();
		    System.out.println("Hello " + threadName);
		});
		
		try {
		    System.out.println("attempt to shutdown executor");
		    executor.shutdown();
		    executor.awaitTermination(5, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
		    System.err.println("tasks interrupted");
		}
		finally {
		    if (!executor.isTerminated()) {
		        System.err.println("cancel non-finished tasks");
		    }
		    executor.shutdownNow();
		    System.out.println("shutdown finished");
		}
	}
	
	
	
	

}
