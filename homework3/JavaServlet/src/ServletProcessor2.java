
import java.io.IOException;
import java.net.URLClassLoader;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class ServletProcessor2 {

	public void process(Request request, Response response)
			throws ParserConfigurationException, SAXException, IOException {

		String uri = request.getUri();
		String servletName = null;
		if (uri.indexOf("?") != -1) {
			servletName = uri.substring(uri.indexOf("/") + 1, uri.indexOf("?"));
		} else {
			servletName = uri.substring(uri.lastIndexOf("/") + 1);
		}
		String servletName1 = XMLParser.getServlet(servletName);
		Object myClass = null;
		// 类加载器，用于从指定JAR文件或目录加载类
		URLClassLoader loader = null;
		try {
			myClass = Class.forName(servletName1).newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Servlet servlet = null;
		// 给request、response增加外观类，安全性考虑，防止用户在servlet里直接将ServletRequest、ServletResponse向下转型为Request和Response类型，
		// 并直接调用其内部的public方法，因为RequestFacade、ResponseFacade里不会有parse、sendStaticResource等方法；
		RequestFacade requestFacade = new RequestFacade(request);
		ResponseFacade responseFacade = new ResponseFacade(response);
		try {
			servlet = (Servlet) myClass;
			System.out.println(servlet);
			servlet.service((ServletRequest) requestFacade, (ServletResponse) responseFacade);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}

	}
}