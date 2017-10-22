package com.jt.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 这个类用来定位京淘首页的
 * @author ysq
 *
 */
@Controller
public class PageController {

	@RequestMapping("/index")
	public String goIndex(){
		return "index";
	}
	
	
}
