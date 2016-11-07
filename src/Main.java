
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import com.sun.xml.internal.ws.util.StringUtils;

import sun.misc.JarFilter;

/**
 * @author lihzh-home
 */
public class Main {

	private static final Log _log = LogFactory.getLog(Main.class);
	private static PropertyHelper propHelper = new PropertyHelper("config");
	private static boolean isDelete = Boolean.valueOf(propHelper.getValue("delete-installed-jar"));
	private static final String KEY_JARPATH = "jar-path";
	private static final String ENCODE = "gbk";
	private static String CMD_INSTALL_FILE;

	public static void main(String[] args) throws DocumentException {
		URL url = Main.class.getClassLoader().getResource("pom.xml");
		String pomPath = url.getPath();
		Map<String, Map<String, String>> result = null;
		if (!pomPath.equals("") || pomPath != null) {
			File pomXml = new File(pomPath);
			if (pomXml != null) {
				result = DomParseUtil.parseDom(url);
				// System.out.println(result.toString());
			}
		}

		String path = propHelper.getValue(KEY_JARPATH);
		_log.info("The path of the jars is [" + path + "].");
		File file = new File(path);
		if (!file.isDirectory()) {
			_log.warn("The path must be a directory.");
			return;
		}
		FilenameFilter filter = new JarFilter();
		File[] jarFiles = file.listFiles(filter);
		for (File jar : jarFiles) {
			installJarToMaven(jar, result);
			/*if (isDelete) {
				_log.info("Delete the original jar file [" + jar.getName() + "].");
				//jar.delete();
			}*/
		}
	}

	private static void installJarToMaven(File file, Map<String, Map<String, String>> result) {
		String fileName = file.getName();
		String jarName = getJarName(fileName);
		String bathName = jarName;
		if(bathName.contains("snmp4j")){
			System.out.println("snmp4jsnmp4j");
		}
		String version = null;
		if(bathName.matches("(.*)(\\d*)")){
			version = bathName.substring(bathName.lastIndexOf("-")+1);
			//bathName = bathName.replaceAll("\\d|\\.|(-)$", ""); 
			bathName = bathName.substring(0,bathName.lastIndexOf("-"));
			/*if(bathName.matches("(.*)(-)")){
				bathName = bathName.substring(0,bathName.lastIndexOf("-"));
			}*/
		}
		
		//StringTokenizer strToken = new StringTokenizer(jarName, "-");
		String groupId = null;
		String artifactId = null; 
		//Set<String> keys = result.keySet();
		//if (strToken.hasMoreTokens()) {
			//if (groupId == null) {
				//groupId = strToken.nextToken();
				/*String artifictKey = "";
				Set<String> containersKeys = getContainsGroupId(bathName, keys);
				if (containersKeys == null || containersKeys.size() == 0) {
					return;
				}

				if (containersKeys!=null && containersKeys.size() == 1) {
					artifictKey = containersKeys.iterator().next();
				} 
				if(artifictKey!=null && artifictKey.equals("backport-util-concurrent")){
				    System.out.println("ahhahahahaha");	
				} */
		       Map<String,String> resultMap = result.get(bathName);
		       if("snmp4j".equals(bathName)){
		    	   System.out.println("snmp4j");
		       }
		       if(resultMap == null || resultMap.size() == 0){
		    	   System.out.println(bathName+"----"+resultMap);
		    	   return;
		       } 
				
				groupId = resultMap.get("groupId");
				artifactId = resultMap.get("artifactId");  
			//}
		//}
		_log.info("Jar [" + jarName + "] will be installed with the groupId=" + groupId + " ," + "artifactId="
				+ artifactId + " , version=" + version + ".");
		executeInstall(groupId, artifactId, version, file.getPath());
	}

	private static Set<String> getContainsGroupId(String groupId, Set<String> keys) {
		Set<String> resultkey = new HashSet<String>();
		for (String key : keys) {
			if (key.contains(groupId)) {
				resultkey.add(key);
			}
		}
		return resultkey;
	}

	private static void executeInstall(String groupId, String artifactId, String version, String path) {
		CMD_INSTALL_FILE = createInstallFileCMD(groupId, artifactId, version, path);
		String[] cmds = new String[] { "cmd", "/C", CMD_INSTALL_FILE };
		try {
			Process process = Runtime.getRuntime().exec(cmds);
			printResult(process);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void printResult(Process process) throws IOException {
		InputStream is = process.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is, ENCODE));
		String lineStr;
		while ((lineStr = br.readLine()) != null) {
			System.out.println(lineStr);
		}
	}

	private static String createInstallFileCMD(String groupId, String artifactId, String version, String path) {
		StringBuffer sb = new StringBuffer();
		sb.append("mvn install:install-file -DgroupId=").append(groupId).append(" -DartifactId=").append(artifactId)
				.append(" -Dversion=").append(version).append(" -Dpackaging=jar").append(" -Dfile=").append(path);
		_log.debug(sb.toString());
		return sb.toString();
	}

	private static String getJarName(String fileName) {
		int index = fileName.indexOf(".jar");
		return fileName.substring(0, index);
	}

}
