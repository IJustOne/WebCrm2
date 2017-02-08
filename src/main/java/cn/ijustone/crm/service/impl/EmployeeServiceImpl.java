package cn.ijustone.crm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.ijustone.crm.domain.employee.Employee;
import cn.ijustone.crm.mapper.employee.EmployeeMapper;
import cn.ijustone.crm.service.IEmployeeService;
import cn.ijustone.crm.utils.page.PageResource;

@Service("employeeService")
public class EmployeeServiceImpl  implements IEmployeeService {

	@Autowired
	EmployeeMapper employeeMapper;

	@Override
	public void insert(Employee employee) {
		employeeMapper.insert(employee);
	}

	@Override
	public void update(Employee employee) {
		
		employeeMapper.update(employee);
	}

	@Override
	public void delete(Long id) {
		
		employeeMapper.delete(id);
	}

	@Override
	public Employee queryById(Long id) {
		return employeeMapper.queryById(id);
	}

	@Override
	public List<Employee> queryAll() {
		
		return employeeMapper.queryAll();
	}

	@Override
	public  PageResource  queryAllByPage(PageResource<Employee> pageResource){
		
		List<Employee> queryAllByPage = employeeMapper.queryAllByPage(pageResource);
		System.out.println(queryAllByPage);
		pageResource.setList(queryAllByPage);
		return pageResource;
	}

	
}
