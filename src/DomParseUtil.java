import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DomParseUtil {

	public static  Map<String,Map<String,String>> parseDom(URL url) throws DocumentException {
		 SAXReader reader = new SAXReader();
	        Document document = reader.read(url); 
	        Map<String,Map<String,String>> result = new LinkedHashMap<String,Map<String,String>>();
	        Element root = document.getRootElement();
	        bar(root,result);
	        return result; 
	}
	
	public static void bar(Element root,Map<String,Map<String, String>> result) throws DocumentException {

       

        // iterate through child elements of root
        for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
            Element element = (Element) i.next();
            String eleName = element.getName();
            if(eleName!=null && eleName.equals("dependencies")){
            	  parseDependences(element,result);
            }else if(element!= null &&  element.elementIterator().hasNext()){
            	 bar(element,result);
            }else{
            	continue;
            }
        } 
     }
	
	public static void parseDependences(Element dependences, Map<String,Map<String, String>> result){ 
		for( Iterator i = dependences.elementIterator(); i.hasNext();){
			 Element element = (Element) i.next(); 
			 Map<String,String> resultMap = new HashMap<String,String>(); 
			 String key = "";
			 for( Iterator j = element.elementIterator(); j.hasNext();){
				 element = (Element) j.next();
				 String eleName = element.getName();
				 if(eleName!=null && !eleName.equals("")){
					 String value = element.getTextTrim(); 
					 resultMap.put(eleName, value);
					 if(eleName.equals("artifactId")){
						 key = value;
					 }
					//System.out.println(resultMap);
				 }
			 }
			 
			
			 result.put(key,resultMap);
		}
	}

}
