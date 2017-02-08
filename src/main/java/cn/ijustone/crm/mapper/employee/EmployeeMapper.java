package cn.ijustone.crm.mapper.employee;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.ijustone.crm.domain.User;
import cn.ijustone.crm.domain.employee.Employee;
import cn.ijustone.crm.mapper.base.IBaseMapper;
import cn.ijustone.crm.utils.page.PageResource;

/**
 * 
 * @author IJustOne
 *	2017-1-5
 */
//@Repository
public interface EmployeeMapper extends IBaseMapper<Employee> {

	
	List<Employee> queryAllByPage(PageResource<Employee> pageResource);
}
