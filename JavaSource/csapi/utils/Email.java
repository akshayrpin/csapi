package csapi.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import alain.core.email.ExchangeMessenger;
import alain.core.utils.Config;
import alain.core.utils.Logger;
import alain.core.utils.Operator;

public class Email {

	
	
	public static boolean notifyAdministrators(String method,String problem) {
		boolean result = true;
		String email = "aromero@beverlyhills.org,sunvoyage@gmail.com,svijay@edgesoftinc.com;jdeanda@beverlyhills.org";
		StringBuffer sb = new StringBuffer();
		sb.append(" Problem in the method have a look into the script.");
		sb.append(method);
		sb.append("<BR><BR><BR>");
		sb.append(problem);
		String message = sb.toString();
		ExchangeMessenger m = new ExchangeMessenger();
		m.setRecipient(email);
		m.setSubject("CITY SMART");
		m.setContent(message);
		result = m.deliver();
		Logger.info(message);
		return result;
	}

	public static boolean send(String recipient, String subject, String content) {
		if (!Operator.hasValue(recipient)) { return false; }
		if (!Operator.hasValue(content)) { return false; }
		if (!Operator.isEmail(recipient)) { return false; }
		ExchangeMessenger m = new ExchangeMessenger();
		m.setRecipient(recipient);
		//m.setBCC(Config.adminemail());
		m.setSubject(subject);
		m.setContent(content);
		m.setAttachments("");
		return m.deliver();
	}

	public static String genericTemplate(String message) {
		return genericTemplate("", "", message, new LinkedHashMap<String, String>());
	}

	public static String genericTemplate(LinkedHashMap<String, String> data) {
		return genericTemplate("", "", "", data);
	}

	public static String genericTemplate(String name, String message) {
		return genericTemplate("", name, message, new LinkedHashMap<String, String>());
	}

	public static String genericTemplate(String title, String name, String message) {
		return genericTemplate(title, name, message, new LinkedHashMap<String, String>());
	}

	public static String genericTemplate(String message, LinkedHashMap<String, String> data) {
		return genericTemplate("", "", message, data);
	}

	public static String genericTemplate(String name, String message, LinkedHashMap<String, String> data) {
		return genericTemplate("", name, message, data);
	}

	public static String genericTemplate(String title, String name, String message, LinkedHashMap<String, String> data) {
		StringBuilder sb = new StringBuilder();
		sb.append(" <html> ");
		sb.append(" 	<body> ");
		sb.append(" 		<table cellpadding=\"15\" cellspacing=\"2\" border=\"0\" width=\"500\" align=\"center\"> ");
		sb.append(" 			<tr> ");
		sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; border-bottom: 1px dashed #cccccc; text-align: left\" rowspan=\"2\"><img src=\"https://cs.beverlyhills.org/csportal/images/logo.png\" /></td> ");
		sb.append(" 				<td style=\"font-family: Times New Roman, serif; text-align: right; font-size: 12px\" valign=\"top\" nowrap> ");
		sb.append(" 					<div style=\"font-family: Times New Roman, serif; font-weight: bold; font-size: 15px\">City of Beverly Hills</div>");
		sb.append(" 					455 North Rexford Drive<br/>");
		sb.append(" 					Beverly Hills, CA 90210<br/>");
		sb.append(" 				</td> ");
		sb.append(" 			</tr> ");

		sb.append(" 			<tr> ");
		sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; border-bottom: 1px dashed #cccccc; text-align: right; font-size: 14px\" valign=\"top\" nowrap> ").append(title).append("</td>");
		sb.append(" 			</tr> ");
		sb.append(" 		</table> ");

		sb.append(" 		<table cellpadding=\"15\" cellspacing=\"2\" border=\"0\" width=\"500\" align=\"center\"> ");
		if (Operator.hasValue(name)) {
			sb.append(" 			<tr> ");
			sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; font-size: 12px\" colspan=\"2\">Hello ").append(name).append(",</td>");
			sb.append(" 			</tr> ");
		}
		else {
			sb.append(" 			<tr> ");
			sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; font-size: 12px\" colspan=\"2\">Hello,</td>");
			sb.append(" 			</tr> ");
		}

		if (Operator.hasValue(message)) {
			sb.append(" 			<tr> ");
			sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; font-size: 12px\" colspan=\"2\">").append(message).append("</td> ");
			sb.append(" 			</tr> ");
		}

		if (data.size() > 0) {
			sb.append(" 			<tr> ");
			sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; border-bottom: 1px dashed #cccccc\" colspan=\"2\">&nbsp;</td> ");
			sb.append(" 			</tr> ");

			for (Map.Entry<String, String> entry : data.entrySet()) {
				String field = entry.getKey();
				String value = entry.getValue();
				sb.append(" 			<tr> ");
				sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; font-size: 12px; background-color: #eeeeee; width: 1%\" nowrap>").append(field).append(":</td> ");
				sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; font-size: 12px\">").append(value).append("</td> ");
				sb.append(" 			</tr> ");
			}
		}

		sb.append(" 			<tr> ");
		sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; border-top: 1px dashed #cccccc\" colspan=\"2\">&nbsp;</td> ");
		sb.append(" 			</tr> ");
		sb.append(" 			<tr> ");
		sb.append(" 				<td style=\"font-family: Arial, Helvetica, sans-serif; font-size: 10px\" colspan=\"2\">Please do not reply to this message; it was sent from an unmonitored email address. This message is a service email related to your use of City of Beverly Hills online account. 455 N Rexford Dr, Beverly Hills CA 90210</td> ");
		sb.append(" 			</tr> ");
		sb.append(" 		</table> ");
		sb.append(" 	</body> ");
		sb.append(" </html> ");
		return sb.toString();
	}










}


