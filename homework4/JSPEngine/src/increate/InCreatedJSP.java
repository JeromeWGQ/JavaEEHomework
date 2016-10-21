package increate;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * �����ڽ�����ת�����java���̳������
 * 
 * @author Jerome
 *
 */
public class InCreatedJSP {

	protected PrintWriter out;

	protected ServletRequest request;
	protected ServletResponse response;

	public InCreatedJSP(ServletRequest request, ServletResponse response) {
		this.request = request;
		this.response = response;

		try {
			this.out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
