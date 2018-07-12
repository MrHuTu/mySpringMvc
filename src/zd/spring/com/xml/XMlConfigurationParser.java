package zd.spring.com.xml;

import java.io.InputStream;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XMlConfigurationParser {
	
	public static String readXMLBasePackage(String path){
		
		SAXReader reader  = new SAXReader();
		InputStream inputStream = XMlConfigurationParser.class.getClassLoader().getResourceAsStream(path);
		Document document  = null;
		try {
			 document = reader.read(inputStream);
			inputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Element rootElement = document.getRootElement();
		Element element = rootElement.element("component-scan");
		String  basePackge = element.attributeValue("base-packge");
		return basePackge;
		
	}
	

}
