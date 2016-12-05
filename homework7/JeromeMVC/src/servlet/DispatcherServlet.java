package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.crypto.Mac;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import annotation.Controller;
import annotation.RequestMapping;
import modelAndView.ModelAndView;
import modelAndView.Obj;

/**
 * Servlet implementation class DispatcherServlet
 */
@SuppressWarnings("serial")
public class DispatcherServlet extends HttpServlet {
	
	private ModelAndView mav;
	
    /**
     * Default constructor. 
     */
    public DispatcherServlet() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		out.println("Hello World");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// �����ύpost�����󣬸�����������
		// �ȰѶ�Ӧcontroller�е�mapping�ҵ�������������֮
		mav = new ModelAndView();
		String name = request.getParameter("name");
		String pas = request.getParameter("pas");
		mav.addMap("name", name);
		mav.addMap("pas", pas);
		
		loadController(request.getServletPath());
		
		Obj[] obj = mav.getObjects();
		for (int i = 0; i < obj.length; i++) {
			request.setAttribute(obj[i].getName(), obj[i].getObj());
		}
		request.getRequestDispatcher(mav.getViewName()).forward(request, response);
		
	}

	// ʵ��@controller
	private void loadController(String url) {
		String packageName = "";
		File root = new File("src");
		try{
			loadJava(root,packageName,url);
		}catch(Exception e){
			System.out.println("����java�ļ�ʱ����");
			e.printStackTrace();
		}
	}

	private void loadJava(File folder, String packageName, String url) throws Exception{
		File[] files = folder.listFiles();
		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
			File file = files[fileIndex];
			if(file.isDirectory()){
				loadJava(file, packageName+file.getName()+".", url);
			}else{
				// ��ÿ��java�������ƥ��controller
				String filename = file.getName();
				try{
					String name = filename.substring(0,filename.length()-5);
					Class<?> obj = Class.forName(packageName+name);
					// �ҵ�componet
					if((Controller)obj.getAnnotation(Controller.class)!=null){
						Controller con = (Controller) obj.getAnnotation(Controller.class);
						// �ҵ�controller�󣬲���url��Ӧ����
						findRequestMapping(url,obj);
					}
				}catch(Exception e){
					System.out.println("ƥ��component����");
				}
			}
		}
	}

	private void findRequestMapping(String url, Class<?> obj1) {
		
		Class<?> obj = obj1;
		Method[] ms = obj.getMethods();
		
		//����ÿ��method����ע�����
		for (Method m : ms) {
			if((RequestMapping)m.getAnnotation(RequestMapping.class)!=null){
				RequestMapping rm = (RequestMapping) m.getAnnotation(RequestMapping.class);
				if(url.equals(rm.value())){
					try{
						Object argList[] = new Object[1];
						argList[0] = mav;
						
						mav = (ModelAndView) m.invoke(obj.newInstance(), argList);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		
	}

}
