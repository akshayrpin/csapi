package csapi.gateway;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import net.authorize.Environment;
import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.ArrayOfBatchDetailsType;
import net.authorize.api.contract.v1.ArrayOfTransactionSummaryType;
import net.authorize.api.contract.v1.BatchDetailsType;
import net.authorize.api.contract.v1.GetSettledBatchListRequest;
import net.authorize.api.contract.v1.GetSettledBatchListResponse;
import net.authorize.api.contract.v1.GetTransactionDetailsRequest;
import net.authorize.api.contract.v1.GetTransactionDetailsResponse;
import net.authorize.api.contract.v1.GetTransactionListRequest;
import net.authorize.api.contract.v1.GetTransactionListResponse;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.Paging;
import net.authorize.api.contract.v1.TransactionDetailsType;
import net.authorize.api.contract.v1.TransactionListOrderFieldEnum;
import net.authorize.api.contract.v1.TransactionListSorting;
import net.authorize.api.contract.v1.TransactionSummaryType;
import net.authorize.api.controller.GetSettledBatchListController;
import net.authorize.api.controller.GetTransactionDetailsController;
import net.authorize.api.controller.GetTransactionListController;
import net.authorize.api.controller.base.ApiOperationBase;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;

import alain.core.db.Sage;
import alain.core.utils.Logger;
import alain.core.utils.MapSet;
import alain.core.utils.Operator;
import alain.core.utils.Timekeeper;
import csapi.common.ApiHandler;
import csapi.utils.CsApiConfig;
import csshared.utils.ObjMapper;
import csshared.vo.TypeVO;


public class Authorize {

	
	
	public static String TRANS_KEY = CsApiConfig.getString("payment.authorize.production.transactionkey");
	
	public static String LOGIN_ID = CsApiConfig.getString("payment.authorize.production.loginid");
	public static String CLIENT_KEY = CsApiConfig.getString("payment.authorize.production.clientkey");
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Timekeeper k = new Timekeeper();
		System.out.println(TRANS_KEY);
		
		String url = "https://api.authorize.net/xml/v1/request.api";
		try{
		JSONObject o = new JSONObject();
		JSONObject merchantAuthentication = new JSONObject();
		merchantAuthentication.put("name", LOGIN_ID);
		merchantAuthentication.put("transactionKey", TRANS_KEY);
		
		o.put("lastSettlementDate", "2022-05-26T16:00:00Z");
		o.put("merchantAuthentication", merchantAuthentication);
		o.put("firstSettlementDate", "2022-04-24T16:00:00Z");
		
		
		
		
		JSONObject getSettledBatchListRequest = new JSONObject();
		getSettledBatchListRequest.put("getSettledBatchListRequest", o);
		
		System.out.println(getSettledBatchListRequest.toString());
		
		//String json = "{ 	\"getSettledBatchListRequest\": { 		\"merchantAuthentication\": { 			\"name\": \""+LOGIN_ID+"\", 			\"transactionKey\": \""+TRANS_KEY+"\" 		}, 		\"firstSettlementDate\": \"2022-04-20T16:00:00Z\", 		\"lastSettlementDate\": \"2022-04-26T16:00:00Z\"   	} }";
		
		String s = ApiHandler.post(url, getSettledBatchListRequest.toString());
		
		System.out.println(s.toString());
		
		run(LOGIN_ID, TRANS_KEY);
		}catch (Exception e){
			Logger.error(e.getMessage());
		}
	}
	
	public static ANetApiResponse run(String apiLoginId, String transactionKey) {

        ApiOperationBase.setEnvironment(Environment.PRODUCTION);

        MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(apiLoginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);

        GetSettledBatchListRequest getRequest = new GetSettledBatchListRequest();
        getRequest.setMerchantAuthentication(merchantAuthenticationType);

        try {
        	GregorianCalendar pastDate = new GregorianCalendar();
            pastDate.add(Calendar.DAY_OF_YEAR, -7);
            
            getRequest.setFirstSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(pastDate));
            GregorianCalendar currentDate = new GregorianCalendar();

            getRequest.setLastSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(currentDate));

        } catch (Exception ex) {
            System.out.println("Error : while setting dates");
            ex.printStackTrace();
        }

        GetSettledBatchListController controller = new GetSettledBatchListController(getRequest);
        controller.execute();
        GetSettledBatchListResponse getResponse = controller.getApiResponse();
        if (getResponse != null) {

            if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {

                System.out.println(getResponse.getMessages().getMessage().get(0).getCode());
                System.out.println(getResponse.getMessages().getMessage().get(0).getText());

                ArrayOfBatchDetailsType batchList = getResponse.getBatchList();
                if (batchList != null) {
                    System.out.println("List of Settled Transaction :");
                    for (BatchDetailsType batch : batchList.getBatch()) {
                        System.out.println(batch.getBatchId() + " - " + batch.getMarketType() + " - " + batch.getPaymentMethod() + " - " + batch.getProduct() + " - " + batch.getSettlementState());
                       // getTransactionList(merchantAuthenticationType, batch.getBatchId());
                        
                        
                    }
                    
                 //   getTransaction(merchantAuthenticationType,"63678388507");
                }
            } else {
                System.out.println("Failed to get settled batch list:  " + getResponse.getMessages().getResultCode());
                System.out.println(getResponse.getMessages().getMessage().get(0).getText());
            }
        }
		return getResponse;
    }

	
	
	
	
	
	public static MapSet getTransaction(String transactionId) {
		  MapSet m = new MapSet();
        ApiOperationBase.setEnvironment(Environment.PRODUCTION);
      //  ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);
    
        GetTransactionDetailsRequest getRequest = new GetTransactionDetailsRequest();
        getRequest.setMerchantAuthentication(getCredentials());
        getRequest.setTransId(transactionId);
       
        GetTransactionDetailsController controller = new GetTransactionDetailsController(getRequest);
        controller.execute();
        GetTransactionDetailsResponse getResponse = controller.getApiResponse();

       if (getResponse!=null) {

                 if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {
                	 Sage db = new Sage();
                	
                	 TransactionDetailsType  transaction =   getResponse.getTransaction();
                	 
                	// Logger.info(""+t.getAuthAmount());
                	 Logger.info(getResponse.getMessages().getMessage().get(0).getCode());
                	 Logger.info(getResponse.getMessages().getMessage().get(0).getText());
                    
                  
                	 m.add("TRANS_ID",transaction.getTransId());
                	 m.add("BATCH_ID",transaction.getBatch().getBatchId());
                	 m.add("NAME",transaction.getCustomer().getType());
                	 m.add("EMAIL",transaction.getCustomer().getEmail());
                	 m.add("AMOUNT",transaction.getSettleAmount());
                	 m.add("CUSTOMER_IP",transaction.getCustomerIP());
                	 m.add("CARD_RESPONSE",transaction.getCardCodeResponse());
                	 
                	 
                	 m.add("CARD_NUMBER",transaction.getPayment().getCreditCard().getCardNumber());
                	 m.add("TRANS_DESC",transaction.getResponseReasonDescription().toString());
                	 
                	 m.add("INVOICE_NO",transaction.getOrder().getInvoiceNumber());
                    
                    Timekeeper k = new Timekeeper();
                    k.setDate(transaction.getSubmitTimeLocal().toString());
                    m.add("TRANS_DATE",k.getString("MM/DD/YYYY @ HH:MM"));
                    m.add("TRANS_STATUS",transaction.getTransactionStatus());
                    
                    db.query("select ID from PAYMENT WHERE ONLINE_TRANS_ID ='"+m.getString("TRANS_ID")+"'");
                    if(db.next()){
                    	m.add("CS_REF_NO", db.getInt("ID"));
                    	m.add("COLOR", "WHITE");
                    }else {
                    	m.add("CS_REF_NO", 0);
                    	m.add("COLOR", "RED");
                    }
                    db.clear();
                    
                }
                else
                {
                    System.out.println("Failed to get transaction details:  " + getResponse.getMessages().getResultCode());
                }
            }
       return m;
}
	
	
	
	public static ArrayList<MapSet> getList(String startdate,String enddate,String transid){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		if(Operator.hasValue(transid)){
			a= getTransactionIdList(transid);
		}else {
		
		ArrayList<MapSet> b = getResults(startdate, enddate);
		
		
		
		a= getTransactionList(b);
		}
		return a;
			
	}	
	
	
	public static ArrayList<MapSet> getInvoiceList(int invoiceId){
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		String s = "";
		Sage db = new Sage();
		
		db.query("select * from PAYMENT_ONLINE_PREVIEW WHERE ID = "+invoiceId);
		if(db.next()){
			s = db.getString("CART");
		}
		
		db.clear();
		TypeVO t = new TypeVO();
		if(Operator.hasValue(s)){
			t = ObjMapper.toTypeObj(StringEscapeUtils.unescapeJava(s));
			
			
		   for(int i=0;i<t.getStatements().length;i++ ){
			   MapSet o = new MapSet();
			   o.add("ACTIVITY_NO", t.getStatements()[i].getActivitynumber());
			   o.add("BALANCE", t.getStatements()[i].getBalancedue());
			   o.add("PAID", t.getStatements()[i].getPaidamount());
			   o.add("AMOUNT", t.getStatements()[i].getAmount());
			   o.add("ACTIVITY_ID", t.getStatements()[i].getActivityid());
			   a.add(o);
		   }
			
			
		}
		
		
		return a;
			
	}	
	
	
	public static MerchantAuthenticationType getCredentials(){
		MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
        merchantAuthenticationType.setName(LOGIN_ID);
        merchantAuthenticationType.setTransactionKey(TRANS_KEY);
        
        return merchantAuthenticationType;
	}
	
	
	
	public static ArrayList<MapSet> getResults(String startdate,String enddate) {
		ArrayList<MapSet> a = new ArrayList<MapSet>();
        ApiOperationBase.setEnvironment(Environment.PRODUCTION);

        GetSettledBatchListRequest getRequest = new GetSettledBatchListRequest();
        getRequest.setMerchantAuthentication(getCredentials());

        try {
        	GregorianCalendar pastDate = new GregorianCalendar();
        	Timekeeper c  = new Timekeeper();
            
            if(Operator.hasValue(startdate)){
            	Timekeeper d  = new Timekeeper(startdate);
            	Logger.info(d.getString("MM/DD/YYYY"));
            	long l = d.DAYS_SINCE(c);
            	
            
            	int i=(int)l;  
            	
            	Logger.info(d.getString("MM/DD/YYYY") +"---"+l+"%%%"+i);
            	
            	pastDate.add(Calendar.DAY_OF_YEAR, i);
            }else {
            	  pastDate.add(Calendar.DAY_OF_YEAR, -1);
            }
            
            
            getRequest.setFirstSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(pastDate));
            GregorianCalendar currentDate = new GregorianCalendar();
           // currentDate.add(Calendar.DAY_OF_YEAR, -18);
            
            
            if(Operator.hasValue(enddate)){
            	Timekeeper d  = new Timekeeper(enddate);
            	Logger.info(d.getString("MM/DD/YYYY"));
            	long l = d.DAYS_SINCE(c);
            	
            
            	int i=(int)l;  
            	
            	Logger.info(d.getString("MM/DD/YYYY") +"---"+l+"%%%"+i);
            	
            	currentDate.add(Calendar.DAY_OF_YEAR, i);
            }
            
            
            getRequest.setLastSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(currentDate));

        } catch (Exception ex) {
            Logger.error("Error : while setting dates");
            ex.printStackTrace();
        }

        GetSettledBatchListController controller = new GetSettledBatchListController(getRequest);
        controller.execute();
        GetSettledBatchListResponse getResponse = controller.getApiResponse();
        if (getResponse != null) {

            if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {

                Logger.info(getResponse.getMessages().getMessage().get(0).getCode());
                Logger.info(getResponse.getMessages().getMessage().get(0).getText());

                ArrayOfBatchDetailsType batchList = getResponse.getBatchList();
                if (batchList != null) {
                	Logger.info("List of Settled Batches :");
                    for (BatchDetailsType batch : batchList.getBatch()) {
                    	Logger.info(batch.getBatchId() + " - " + batch.getMarketType() + " - " + batch.getPaymentMethod() + " - " + batch.getProduct() + " - " + batch.getSettlementState());
                    
                       MapSet m = new MapSet();
                       m.add("BATCH_ID", batch.getBatchId());
                       a.add(m);
                        
                        
                    }
                    
                   // getTransaction(merchantAuthenticationType,"63678388507");
                }
            } else {
            	Logger.info("Failed to get settled batch list:  " + getResponse.getMessages().getResultCode());
            	Logger.info(getResponse.getMessages().getMessage().get(0).getText());
            }
        }
		return a;
    }
	
	
	public static ArrayList<MapSet> getTransactionIdList(String transactionId) {
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		
		 ApiOperationBase.setEnvironment(Environment.PRODUCTION);
		
		
		GetTransactionListRequest getRequest = new GetTransactionListRequest();
		getRequest.setMerchantAuthentication(getCredentials());
		
		MapSet b = getTransaction(transactionId);
		if(Operator.hasValue(b.getString("BATCH_ID"))){
		getRequest.setBatchId(b.getString("BATCH_ID"));
		
       Paging paging = new Paging();
       paging.setLimit(1000);
       paging.setOffset(1);
       
		getRequest.setPaging(paging);
		
		TransactionListSorting sorting = new TransactionListSorting();
		sorting.setOrderBy(TransactionListOrderFieldEnum.ID);
		sorting.setOrderDescending(true);
		
		getRequest.setSorting(sorting);

		GetTransactionListController controller = new GetTransactionListController(getRequest);
       controller.execute();

		GetTransactionListResponse getResponse = controller.getApiResponse();
		if (getResponse!=null) {

		     if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {
		    	 ArrayOfTransactionSummaryType transList = getResponse.getTransactions();
		    	 
		    	 if (transList != null) {
		    		 Sage db = new Sage();
		    		 Logger.info("List of Settled Transaction :");
	                    for (TransactionSummaryType transaction : transList.getTransaction()) {
	                        Logger.info(transaction.getTransId() + " - " + transaction.getInvoiceNumber() + " - " + transaction.getSettleAmount() + " - " + transaction.getFirstName() + " - " + transaction.getSubmitTimeLocal());
	                        if(transaction.getTransactionStatus().equalsIgnoreCase("settledSuccessfully") && transactionId.equals(transaction.getTransId())){
	                        
		                        MapSet m = new MapSet();
		                        m.add("TRANS_ID",transaction.getTransId());
		                        m.add("INVOICE_NBR",transaction.getInvoiceNumber());
		                        m.add("NAME",transaction.getFirstName());
		                        m.add("AMOUNT",transaction.getSettleAmount());
		                       
		                        
		                        Timekeeper k = new Timekeeper();
		                        k.setDate(transaction.getSubmitTimeLocal().toString());
		                        m.add("TRANS_DATE",k.getString("MM/DD/YYYY @ HH:MM"));
		                        m.add("TRANS_DATE_1",transaction.getSubmitTimeLocal().toString());
		                        m.add("TRANS_STATUS",transaction.getTransactionStatus());
		                        
		                        db.query("select ID from PAYMENT WHERE ONLINE_TRANS_ID ='"+m.getString("TRANS_ID")+"'");
		                        if(db.next()){
		                        	m.add("CS_REF_NO", db.getInt("ID"));
		                        	m.add("COLOR", "WHITE");
		                        }else {
		                        	m.add("CS_REF_NO", 0);
		                        	m.add("COLOR", "RED");
		                        }
		                        
		                        a.add(m);
	                        }
	                    }
	                    
	                    db.clear();
	                }
		    	 
		    	 Logger.info(getResponse.getMessages().getMessage().get(0).getCode());
		    	 Logger.info(getResponse.getMessages().getMessage().get(0).getText());
		    	 
		    	 Logger.info("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+a.size());
		     }
		        else
		        {
		        	Logger.info("Failed to get transaction list:  " + getResponse.getMessages().getResultCode());
		        }
		}
		
		}
		return a;
	
	}	
	
	public static ArrayList<MapSet> getTransactionList(ArrayList<MapSet> batches) {
		ArrayList<MapSet> a = new ArrayList<MapSet>();
		
		
		 ApiOperationBase.setEnvironment(Environment.PRODUCTION);
		
		 Logger.info(batches.size()+"BATCH IDDDDDDDDDDDDDD5555DDDDDD");
		for(int i=0;i<batches.size();i++){
		 MapSet r = batches.get(i);
		 Logger.info(r.getString("BATCH_ID")+"BATCH IDDDDDDDDDDDDDDDDDDDDDDDDDD");
		
		GetTransactionListRequest getRequest = new GetTransactionListRequest();
		getRequest.setMerchantAuthentication(getCredentials());
		getRequest.setBatchId(r.getString("BATCH_ID"));
		
        Paging paging = new Paging();
        paging.setLimit(1000);
        paging.setOffset(1);
        
		getRequest.setPaging(paging);
		
		TransactionListSorting sorting = new TransactionListSorting();
		sorting.setOrderBy(TransactionListOrderFieldEnum.ID);
		sorting.setOrderDescending(true);
		
		getRequest.setSorting(sorting);

		GetTransactionListController controller = new GetTransactionListController(getRequest);
        controller.execute();

		GetTransactionListResponse getResponse = controller.getApiResponse();
		if (getResponse!=null) {

		     if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {
		    	 ArrayOfTransactionSummaryType transList = getResponse.getTransactions();
		    	 
		    	 if (transList != null) {
		    		 Sage db = new Sage();
		    		 Logger.info("List of Settled Transaction :");
	                    for (TransactionSummaryType transaction : transList.getTransaction()) {
	                        Logger.info(transaction.getTransId() + " - " + transaction.getInvoiceNumber() + " - " + transaction.getSettleAmount() + " - " + transaction.getFirstName() + " - " + transaction.getSubmitTimeLocal());
	                        if(transaction.getTransactionStatus().equalsIgnoreCase("settledSuccessfully")){
	                        
		                        MapSet m = new MapSet();
		                        m.add("TRANS_ID",transaction.getTransId());
		                        m.add("INVOICE_NBR",transaction.getInvoiceNumber());
		                        m.add("NAME",transaction.getFirstName());
		                        m.add("AMOUNT",transaction.getSettleAmount());
		                       
		                        
		                        Timekeeper k = new Timekeeper();
		                        k.setDate(transaction.getSubmitTimeLocal().toString());
		                        m.add("TRANS_DATE",k.getString("MM/DD/YYYY @ HH:MM"));
		                        m.add("TRANS_DATE_1",transaction.getSubmitTimeLocal().toString());
		                        m.add("TRANS_STATUS",transaction.getTransactionStatus());
		                        
		                        db.query("select ID from PAYMENT WHERE ONLINE_TRANS_ID ='"+m.getString("TRANS_ID")+"'");
		                        if(db.next()){
		                        	m.add("CS_REF_NO", db.getInt("ID"));
		                        	m.add("COLOR", "WHITE");
		                        }else {
		                        	m.add("CS_REF_NO", 0);
		                        	m.add("COLOR", "RED");
		                        }
		                        
		                        a.add(m);
	                        }
	                    }
	                    
	                    db.clear();
	                }
		    	 
		    	 Logger.info(getResponse.getMessages().getMessage().get(0).getCode());
		    	 Logger.info(getResponse.getMessages().getMessage().get(0).getText());
		    	 
		    	 Logger.info("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+a.size());
		        }
		        else
		        {
		        	Logger.info("Failed to get transaction list:  " + getResponse.getMessages().getResultCode());
		        }
		}
		
		}
		return a;
	
	}	
	

}
