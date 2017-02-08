package cn.ijustone.crm.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.ijustone.crm.domain.employee.Employee;
import cn.ijustone.crm.service.IEmployeeService;
import cn.ijustone.crm.utils.page.PageResource;

/**
 * 	2017-1-9	
 * @author iJustONE
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {
	
	@Autowired
	private IEmployeeService employeeService;
	
	//显示列表页面
	@RequestMapping("/index")
	public String Index(){
		return "employee/index";//后台拼接路径
	}
	
	//从列表发出ajax请求获取json数据employee/json
	@RequestMapping("/json")
	@ResponseBody
	public List<Employee> queryAllEmployee(){
		return employeeService.queryAll();
	}
	
	@RequestMapping("/dataPage")
	@ResponseBody
	public PageResource queryAllByPage(){
		PageResource pageResource = new PageResource<>();
		pageResource.setPageCount(10);
		pageResource.setPageNum(1);
//		model.addAttribute("employeeLists",employeeService.queryAllByPage(pageResource));
//		model.addObject("employeeLists",employeeService.queryAllByPage(pageResource));
		
//		ModelAndView mv = new ModelAndView();
//		mv.addAllAttributes("employeeLists",employeeService.queryAllByPage(pageResource));
//		employeeService.queryAllByPage(pageResource);
		System.out.println(pageResource);
//		return pageCount;
		return employeeService.queryAllByPage(pageResource);
	}
	
	
	
	
}
