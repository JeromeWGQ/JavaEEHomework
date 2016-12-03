package control;

import annotation.Controller;
import annotation.RequestMapping;
import modelAndView.ModelAndView;

/**
 * �������������ǽ���model���󣬷���view����
 * 
 * @author Jerome
 *
 */
@Controller
public class Test {
	@RequestMapping("/hello")
	public ModelAndView hello(ModelAndView mdv) {
		ModelAndView mav = mdv;
		mav.setViewName("test");
		mav.addObject("name", mav.getMap("name"));
		mav.addObject("pas", mav.getMap("pas"));
		return mav;
	}

	@RequestMapping("/hello2")
	public ModelAndView hello2(ModelAndView mdv) {
		ModelAndView mav = mdv;
		mav.setViewName("test");
		return mav;
	}
}
