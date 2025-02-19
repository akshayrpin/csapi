package csapi.impl.review;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import alain.core.security.Token;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

import com.fasterxml.jackson.databind.ObjectMapper;

import csapi.common.Choices;
import csapi.impl.activities.ActivitiesAgent;
import csapi.impl.log.LogAgent;
import csapi.impl.people.PeopleAgent;
import csapi.impl.tasks.TasksImpl;
import csapi.search.GlobalSearch;
import csapi.security.AuthorizeToken;
import csapi.utils.CsDeleteCache;
import csapi.utils.CsTools;
import csapi.utils.objtools.Group;
import csapi.utils.objtools.Types;
import csshared.utils.ObjMapper;
import csshared.vo.ComboReviewGroup;
import csshared.vo.ComboReviewList;
import csshared.vo.ComboReviewVO;
import csshared.vo.DataVO;
import csshared.vo.ObjGroupVO;
import csshared.vo.ObjVO;
import csshared.vo.RequestVO;
import csshared.vo.ResponseVO;
import csshared.vo.SubObjVO;
import csshared.vo.TypeVO;


public class ReviewImpl {

	public static final String LOG_CLASS= "ReviewImpl.java  : ";

	public static String summary(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getSummary(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String info(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getInfo(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String details(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			ComboReviewVO v = ReviewAgent.getCombo(vo, u);
			//TypeVO v = Types.getDetails(vo);
			//TypeVO v = Types.getGroup(vo);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String list(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			ObjGroupVO g = ReviewFields.list(Operator.toInt(vo.getGroupid()), vo.getReviewid());
			String command = ReviewSQL.list(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getGroupid()), vo.getReviewid());
			ObjGroupVO rg = Group.horizontal(g, command);
			rg.setDisplay("hz");
			rg.setEditable(false);
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = rg;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String fields(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			TypeVO v = Types.getType(vo);
			ObjGroupVO g = ReviewFields.details();
			g.setAction(vo.getAction());
			ObjGroupVO[] gs = new ObjGroupVO[1];
			gs[0] = g;
			v.setGroups(gs);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String reviewstatus(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			Token u = AuthorizeToken.authenticate(vo);
			Logger.highlight("primary contact");
			boolean pricontact = PeopleAgent.hasPrimaryContact(vo.getType(), vo.getTypeid());
			if (vo.getReviewrefid() > 0) {
				o.setChoices(Choices.getChoices(ReviewOptSQL.getRevRefStatus(vo.getReviewrefid(), pricontact)));
			}
			else if (Operator.toInt(vo.getId()) > 0) {
				o.setChoices(Choices.getChoices(ReviewOptSQL.getStatus(Operator.toInt(vo.getId()), pricontact)));
			}
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String reviewtype(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(ReviewOptSQL.getReviews(vo.getType(), vo.getTypeid(), vo.getId())));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String types(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);
			SubObjVO[] o = Choices.getChoices(ReviewOptSQL.getReviews(vo.getType(), vo.getTypeid(), vo.getGroup()));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String reviewappttype(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(ReviewOptSQL.getApptReviews(vo.getType(), vo.getTypeid(), vo.getId())));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	public static String reviewusers(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(ReviewOptSQL.getUserGroups(Operator.toInt(vo.getId()))));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	// Added after review process change
	public static String team(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(ReviewOptSQL.getTeam(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), vo.getReviewrefid(), "")));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String comboTeam(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(ReviewOptSQL.getComboTeam(Operator.toInt(vo.getId()), "")));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String inspectors(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			o.setChoices(Choices.getChoices(ReviewOptSQL.getTeam(vo.getType(), vo.getTypeid(), Operator.toInt(vo.getId()), vo.getReviewrefid(), "inspector"), true));
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String add(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		String r = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO resp = new ResponseVO();
			resp  = ReviewAgent.saveCombo(vo, u);
			
			TasksImpl.runImmediate(vo.getTypeid(), vo, u);
			
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r = mapper.writeValueAsString(resp);
			GlobalSearch.index(GlobalSearch.REVIEW_DELTA);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
	
				
		return r;
	}

	public static String create(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		Token u = AuthorizeToken.authenticate(vo);	
		return create(vo, u);
	}

	public static String create(RequestVO vo, Token u) {
		String r = "";
		ResponseVO resp = LogAgent.updateLog(vo.getProcessid(), 50, "Saving");

		try {
			ObjectMapper mapper = new ObjectMapper();
			resp  = ReviewAgent.createCombo(vo, u);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			resp.setPercentcomplete(100);
			LogAgent.saveLog(resp);
			r = mapper.writeValueAsString(resp);
			GlobalSearch.index(GlobalSearch.REVIEW_DELTA);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}

	public static String update(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		String r = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			Token u = AuthorizeToken.authenticate(vo);	
			ResponseVO resp = new ResponseVO();
			resp  = ReviewAgent.updateCombo(vo, u);
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r = mapper.writeValueAsString(resp);
			GlobalSearch.index(GlobalSearch.REVIEW_DELTA);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}

	public static String saveTeam(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		String r = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			Token u = AuthorizeToken.authenticate(vo);	
			DataVO m = DataVO.toDataVO(vo);
			ResponseVO resp = new ResponseVO();
			resp  = ReviewAgent.addTeam(resp, vo.getReviewrefid(), -1, m.get("REF_TEAM_ID"), u.getId(), u.getIp());
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r = mapper.writeValueAsString(resp);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}

	public static String saveDue(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		String r = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			Token u = AuthorizeToken.authenticate(vo);	
			DataVO m = DataVO.toDataVO(vo);
			ResponseVO resp = new ResponseVO();
			boolean result  = ReviewAgent.updateDue(vo.getReviewrefid(), m.get("DUE_DATE"), u.getId(), u.getIp());
			if(result){
				resp.setMessagecode("cs200");
		}
		else {
			resp.setMessagecode("cs500");
		}
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r = mapper.writeValueAsString(resp);
		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}

	public static String updateTeam(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		String r = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			Token u = AuthorizeToken.authenticate(vo);	
			DataVO m = DataVO.toDataVO(vo);
			ResponseVO resp = new ResponseVO();
			resp  = ReviewAgent.updateTeam(resp, vo.getReviewrefid(), -1, m.get("REF_TEAM_ID"), u.getId(), u.getIp());
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r = mapper.writeValueAsString(resp);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}

//	public static ResponseVO saveReview(RequestVO vo, Token u){
//		ResponseVO r = new ResponseVO();
//		try{
//			ObjGroupVO o = vo.getData()[0];
//			//r = ValidateRequest.process(o, vo.getType());
//			if(r.isValid()){
//				boolean result = ReviewAgent.save(vo,u);
//				if(result){
//					r.setMessagecode("cs200");
//				}
//				else {
//					r.setMessagecode("cs500");
//				}
//			}
//			
//		}catch(Exception e){
//			Logger.error(e.getMessage());
//			r.setMessagecode("cs500");
//		}
//		return r;
//	}
//

	public static String appt(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = AuthorizeToken.authenticate(vo);
			ObjGroupVO o = new ObjGroupVO();
			ComboReviewList v = ReviewAgent.appt(vo, u);
			o.setComboreview(v);
			//TypeVO v = Types.getDetails(vo);
			//TypeVO v = Types.getGroup(vo);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String notifications(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ReviewAgent.getNotifications(Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String notification(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			ObjVO o = new ObjVO();
			SubObjVO[] v = ReviewAgent.getNotification(Operator.toInt(vo.getId()));
			o.setChoices(v);
			s = mapper.writeValueAsString(o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}




	public static String cycledetails(HttpServletRequest request, HttpServletResponse response, String json) {
		String s = "";
		try {
			//TODO secure
			RequestVO vo = ObjMapper.toRequestObj(json);
			ObjectMapper mapper = new ObjectMapper();
			Token u = Token.retrieve(vo.getToken(), vo.getIp());
			List<ComboReviewVO> v = ReviewAgent.getCycleDetails(vo, u);
			s = mapper.writeValueAsString(v);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}





	public static String addTeam(HttpServletRequest request, HttpServletResponse response, String json) {
		RequestVO vo = ObjMapper.toRequestObj(json);
		String r = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			Token u = AuthorizeToken.authenticate(vo);	
			DataVO m = DataVO.toDataVO(vo);
			ResponseVO resp = new ResponseVO();
			resp  = ReviewAgent.addUpdateTeam(resp, vo.getReviewrefid(), -1, m.get("REF_TEAM_ID"), u.getId(), u.getIp());
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "review");
			CsDeleteCache.deleteCache(vo.getType(), vo.getTypeid(), "appointment");
			r = mapper.writeValueAsString(resp);
			GlobalSearch.index(GlobalSearch.INSPECTIONS_DELTA);

		}
		catch (Exception e) {
			Logger.error(e.getMessage());
		}
		return r;
	}








}















