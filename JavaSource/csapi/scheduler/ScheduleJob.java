package csapi.scheduler;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import alain.core.db.Sage;
import alain.core.utils.Operator;
import csapi.impl.activity.ActivityAgent;
import csapi.impl.activity.ActivitySQL;

public class ScheduleJob extends HttpServlet{
	
	private int HOUR = 6;
	private int MINUTES = 0;
	private int DAYS = 1;
	Date firstTime = new Date();
	public void init() throws ServletException
    {
		
		Runnable task = new Runnable(){
			public void run() 
	        { 
				updateActivity();
	        } 
		};
		
		ZonedDateTime now = ZonedDateTime.now();//(ZoneId.of("America/Los_Angeles"));
		ZonedDateTime nextRun = now.withHour(HOUR).withMinute(MINUTES).withSecond(0);
		if(now.compareTo(nextRun) > 0)
		    nextRun = nextRun.plusDays(DAYS);

		Duration duration = Duration.between(now, nextRun);
		long initalDelay = duration.getSeconds();

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);            
		scheduler.scheduleAtFixedRate(task, 0, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
    }
	
	public static void updateActivity() {
		Sage db = new Sage();
		String command;
		boolean result = false;
		int status = 0;
		db.query("select TOP 1 ID from LKUP_ACT_STATUS WHERE EXPIRED = 'Y' AND ACTIVE = 'N'");
		if(db.next()) {
			status = db.getInt("ID");
		}
		
		db.query("select a.id from activity a join lkup_act_status s on a.lkup_act_status_id = s.id where (issued !='Y' and expired !='Y') and exp_date <= getdate() ");
		while(db.next()) {
			int actid = db.getInt("ID");
			command = "UPDATE ACTIVITY SET UPDATED_DATE = CURRENT_TIMESTAMP, LKUP_ACT_STATUS_ID = "+ status +" WHERE ID = "+actid;
			result = db.update(command);
			
			command = ActivitySQL.insertStatus(actid, status, 890);
			if (db.update(command)) {
				result = true;
			}
			
			db.clear();
			
			if(result){
				ActivityAgent.addHistory(actid, "activity", actid, "update");
			}
			
		}
	}
	
	
}