package cn.ijustone.crm.mapper.base;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * 公共的mapper
 * @author iJustONE
 * 2017-1-5
 */

public interface IBaseMapper<T> {
	/**
	 * 公共的新增方法
	 */
	void insert(T t);
	/**
	 * 公共的更新方法
	 */
	void update(T t);
	/**
	 * 公共的删除方法
	 */
	void delete(Long id);
	/**
	 * 公共的查询一条数据方法
	 */
	T queryById(Long id);
	/**
	 * 公共的查询所有的数据方法()不分页
	 */
	List<T> queryAll();
	
	
}
