package template;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import engine.Printable;
import increate.InCreatedJSP;

public class ClassName extends InCreatedJSP implements Printable {

	public ClassName(ServletRequest request, ServletResponse response) {
		super(request, response);
	}

	@Override
	public void print() {
		// 以下为输出内容
		// ===================================
		
		//===================================
	}

}
