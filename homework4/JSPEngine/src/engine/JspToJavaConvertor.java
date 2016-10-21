package engine;

import java.util.ArrayList;

import tools.MyFileReader;

/**
 * jsp转java转换器
 * 
 * @author Jerome
 *
 */
public class JspToJavaConvertor {

	private String head;
	private String fName;

	public JspToJavaConvertor(String fName) {
		this.fName = fName;
	}

	public String convert(String primitiveString) throws ConvertorException {
		// 判断jsp文件是否存在 <% 和 %> 不配对的情况
		judgePair(primitiveString);
		// 提出 <%@ 部分
		primitiveString = trimStr(primitiveString);
		// 替换
		primitiveString = replace(primitiveString);
		// 添加头和尾
		primitiveString = addHead(primitiveString);
		return primitiveString;
	}

	private String addHead(String primitiveString) {
		String header = "";
		header = addHeader();
		String start = MyFileReader.readFromFile("start.txt");
		start = start.replace("ClassName", fName);
		String end = MyFileReader.readFromFile("end.txt");
		return header + start + primitiveString + end;
	}

	// 提取出 <%@ 部分的import内容，并添加到java文件的顶部
	private String addHeader() {
		String header = "";

		ArrayList<String> listImports = new ArrayList<String>();
		int lastIndex = 0;
		while (true) {
			int index = head.indexOf("import=\"", lastIndex);
			if (index == -1)
				break;
			lastIndex = head.indexOf("\"", index + 8);
			listImports.add(head.substring(index + 8, lastIndex));
		}

		for (String str : listImports) {
			header += "import " + str + ";\r\n";
		}
		header += "\r\n";

		/*
		 * SAXBuilder builder = new SAXBuilder(); try { Document document =
		 * builder.build(new InputSource(new StringReader(head))); Element root
		 * = document.getRootElement(); List<Element> list = root.getChildren();
		 * ArrayList<String> imports = new ArrayList<String>(); for (Element e :
		 * list) { String imp = e.getAttributeValue("import"); if (imp != null
		 * && !imp.equals("")) imports.add(imp); } System.out.println(imports);
		 * } catch (JDOMException e) { e.printStackTrace(); } catch (IOException
		 * e) { e.printStackTrace(); }
		 */

		return header;
	}

	private String trimStr(String primitiveString) {
		int lastIndex = primitiveString.lastIndexOf("<%@");
		int nextIndex = primitiveString.indexOf("%>", lastIndex);
		head = primitiveString.substring(0, nextIndex + 2);
		primitiveString = primitiveString.substring(nextIndex + 2);
		return primitiveString;
	}

	// “ 替换为 \"
	// <%= 替换为 " + "
	// <% 替换为 ");
	// %> 替换为 out.write("
	private String replace(String primitiveString) {
		primitiveString = primitiveString.replace("\"", "\\\"");
		primitiveString = primitiveString.replace("<%=", " + ");
		primitiveString = primitiveString.replace("<%", "\");");
		primitiveString = primitiveString.replace("%>", "out.write(\"");

		int index = primitiveString.indexOf("\");");
		primitiveString = primitiveString.substring(index + 3);

		primitiveString += "\");";

		return primitiveString;
	}

	private void judgePair(String primitiveString) throws ConvertorException {
		String[] splited = primitiveString.split("<%");
		for (int i = 1; i < splited.length; i++) {
			String string = splited[i];
			if (!string.contains("%>"))
				throw new ConvertorException(0);
		}
	}

}
