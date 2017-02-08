package cn.ijustone.crm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 菜单
 * @author JustOne
 *
 */
@Controller
@RequestMapping("/mainMenu")
public class MainMenuControl {
	
	@RequestMapping("/index")
	public String defalutMainMenu(){
		return "forward:/WEB-INF/jsp/main.jsp";
	}
	
	@RequestMapping("/right")
	public String defalutMenuRight() {
		return "forward:/WEB-INF/jsp/main_right.jsp";
	}
	
}
