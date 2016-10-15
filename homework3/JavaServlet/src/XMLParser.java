
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser {
	public static String getServlet(String key) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// step 2：获得具体的dom解析器
		DocumentBuilder db = dbf.newDocumentBuilder();
		// step 3:解析一个xml文档，获得Document对象（根节点）
		// 此文档放在项目目录下即可
		Document document = db.parse(new File("web.xml"));
		Map<String, String> map = new HashMap<String, String>();
		NodeList dyclist1 = document.getElementsByTagName("servlet-class");
		NodeList dyclist2 = document.getElementsByTagName("url-pattern");
		System.out.println("----------servlet-file------------");
		System.out.println(dyclist2.getLength());
		// 遍历每一个节点
		for (int i = 0; i < dyclist1.getLength(); ++i) {
			Element ele_servlet = (Element) dyclist1.item(i);
			Element ele_url = (Element) dyclist2.item(i);
			String ctnt_servlet = ele_servlet.getFirstChild().getNodeValue();
			String ctnt_url = ele_url.getFirstChild().getNodeValue();
			map.put(ctnt_url.substring(ctnt_url.lastIndexOf("/") + 1), ctnt_servlet);

		}
		// System.out.println(map.get(key));
		return map.get(key);
	}
}
