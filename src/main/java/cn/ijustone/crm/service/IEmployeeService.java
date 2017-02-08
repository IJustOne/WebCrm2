package cn.ijustone.crm.service;

import cn.ijustone.crm.domain.employee.Employee;
import cn.ijustone.crm.utils.page.PageResource;

public interface IEmployeeService extends IBaseService<Employee>{

	PageResource queryAllByPage(PageResource<Employee> pageResource);
 
 
}
