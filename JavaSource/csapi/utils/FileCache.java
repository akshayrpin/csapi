package csapi.utils;

import java.io.File;

import csshared.utils.CsConfig;
import alain.core.utils.Operator;


/**
 * USE:
 * 
 * 		String result = "";
 * 		try {
 * 			// Get cached string
 * 			result = FileCache.getCache("mycacheid"); // Use a unique id intended for your specific function. Be careful that the id you choose is not already in use as conflicts can occur.
 * 
 * 			// Alternatively, you can also specify a cache interval specifying the age (in hours) before the cache is expired.
 * 			// result = FileCache.getCache(2, "mycacheid");
 * 		}
 * 		catch (Exception e) {
 * 			// Exception will occur if the cache has expired or does not exist. In this instance you will need to recreate the String that will be cached.
 * 
 * 			// Create new String.
 * 			result = "My new String";
 * 			// Cache new String
 * 			FileCache.setCache(result, "mycacheid");
 * 		}
 * 		return result;
 
 */
public class FileCache {

	private String CACHEID = "";
	public static String FILECACHEDIRECTORY = CsConfig.getString("choiceslocation").concat("lkupchoices/");

	public FileCache() { }

	public void clear() {
		CACHEID = "";
	}

	public void setCacheVar(String var) {
		if (Operator.hasValue(var)) {
			StringBuilder sb = new StringBuilder();
			sb.append(CACHEID);
			sb.append("/");
			sb.append((FileUtil.simplifyFilename(var)));
			CACHEID = sb.toString();
			sb = new StringBuilder();
		}
	}

	public String getCacheId() {
		return CACHEID;
	}

	public boolean setCache(String content) {
		if (!Operator.hasValue(getCacheId())) { return false; }
		setCache(content, getCacheId());
		return true;
	}

	public String getCache() throws Exception {
		if (!Operator.hasValue(getCacheId())) { throw new Exception("Cache id is required"); }
		return getCache(getCacheId());
	}

	public String getCache(int interval) throws Exception {
		if (!Operator.hasValue(getCacheId())) { throw new Exception("Cache id is required"); }
		return getCache(interval, getCacheId());
	}

	public static String getCache(String cacheid) throws Exception {
		return getCache(2, cacheid);
	}

	public static String getCache(int interval, String cacheid) throws Exception {
		if (!Operator.hasValue(cacheid)) { throw new Exception("Cache not specified"); }
		String r = "";
		if (Operator.hasValue(cacheid)) {
			StringBuilder sb = new StringBuilder();
			sb.append(FILECACHEDIRECTORY);
			sb.append(cacheid);
			String path = sb.toString();
			try {
				File f = new File(path);
				if (f.exists()) {
					long lm = f.lastModified();
					if (interval > 0 && FileUtil.fileAgeLessThan(interval, lm)) {
						throw new Exception("Cache expired");
					}
					else {
						r = FileUtil.getString(path);
					}
				}
				else {
					throw new Exception("Cache not found");
				}
			}
			catch (Exception e) { throw new Exception("Cache not found"); }
		}
		return r;
	}

	public static void setCache(String content, String cacheid) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILECACHEDIRECTORY);
		sb.append("/").append(cacheid);
		String path = sb.toString();
		FileUtil.saveString(content, path);
	}

	public static void clearCache(String cacheid) {
		StringBuilder sb = new StringBuilder();
		sb.append(FILECACHEDIRECTORY);
		sb.append("/").append(cacheid);
		String path = sb.toString();
		FileUtil.deleteDir(path);
	}












}
