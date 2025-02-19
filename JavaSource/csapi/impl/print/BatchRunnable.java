package csapi.impl.print;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;
import csapi.impl.log.LogAgent;
import csapi.security.AuthorizeToken;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;


public class BatchRunnable {
public static int percentcomplete = 0;
public static int batchId = 0;
public static int size = 0;

	public static ResponseVO threadPrint(RequestVO vo) {
		String processid = Operator.randomString();
		vo.setProcessid(processid);
		final RequestVO req = vo;
		ResponseVO r = new ResponseVO();
		r.setProcessid(processid);
		String batchidstr = vo.getGroupid();
		int batch = Operator.toInt(batchidstr);
		Token u = AuthorizeToken.authenticate(vo);
		r = LogAgent.createResponse("SAVE BATCH");
		req.setProcessid(processid);
		boolean unprinted = false;
		if (batch < 1) {
			batch = PrintAgent.insertBatch(processid, "", u.getId(), vo.getReference(), vo.getGroup());
			unprinted = true;
		}
		else {
			PrintAgent.updateBatchProcess(batch, processid);
		}
		LogAgent.updateLog(processid, 1);
		//vo.setGroupid(Operator.toString(id));
		//BatchRunnable.batchId = id;
		r.setId(batch);
		r.setPercentcomplete(1);
		req.setGroupid(Operator.toString(batch));
		final int batchid = batch;
		final boolean newbatch = unprinted;
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
							PrintImpl.doBatch(req, batchid, processid, newbatch);
						}
					catch (Exception e) {
						System.out.println("Error while saving in thread "+e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
        	System.out.println("Error Thread Process "+e.getMessage());
        }
		return r;
    }
	
	public static ResponseVO threadRenewal(RequestVO vo) {
		String processid = Operator.randomString();
		vo.setProcessid(processid);
		final RequestVO req = vo;
		ResponseVO r = new ResponseVO();
		r.setProcessid(processid);
		Token u = AuthorizeToken.authenticate(vo);
		r = LogAgent.createResponse("SAVE BATCH");
		req.setProcessid(processid);
		int batch = PrintAgent.insertBatch(processid, "", u.getId(), vo.getReference(), vo.getGroup());
		LogAgent.updateLog(processid, 1);
		//vo.setGroupid(Operator.toString(id));
		//BatchRunnable.batchId = id;
		r.setId(batch);
		r.setPercentcomplete(1);
		final int batchid = batch;
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
							PrintImpl.doBatchRenewal(req, batchid, processid);
						}
					catch (Exception e) {
						System.out.println("Error while saving in thread "+e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
        	System.out.println("Error Thread Process "+e.getMessage());
        }
		return r;
    }
	
	public static boolean printBatch(String type) {
		/*Logger.highlight("PRINT BATCH Using Thread");
		boolean result = PrintAgent.insertBatchLog(type, 0, "BATCH PRINT STARTED "+type, "BATCH PRINT STARTED "+type, "");
		threadPrint(type);
		return result;*/
		return true;
	}
	
	/*public static ResponseVO threadRenewal(RequestVO vo) {
		final RequestVO req = vo;
		ResponseVO r = new ResponseVO();
		int id = 0;
		Token u = AuthorizeToken.authenticate(vo);
		r = LogAgent.createResponse("SAVE BATCH");
		vo.setProcessid(r.getProcessid());
		id = PrintAgent.insertBatch(vo.getProcessid(), "", u.getId(), vo.getReference(), vo.getGroup());

		r.setId(id);
		r.setPercentcomplete(1);
		final int batchid = id;
		try{
			new Thread(new Runnable() {
				public void run() {
					try {
							PrintImpl.doBatchRenewal(req, batchid);
						}
					catch (Exception e) {
						System.out.println("Error while saving in thread "+e.getMessage());
					}
                }
            }).start();
        }
        catch(Exception e){
        	System.out.println("Error Thread Process "+e.getMessage());
        }
		return r;
    }*/

}