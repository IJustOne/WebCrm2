package cn.ijustone.crm.utils.page;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;

import cn.ijustone.crm.utils.date.DateUtils;

/**
 * PageResource 用于ajax返回json列表数据构造
 */
public class PageResource <T extends Object> extends RowBounds implements Serializable {

    private static final long serialVersionUID = 8119418445224607913L;

    //当前页面数
    private int pageNum;
    //每页显示数
    private int pageCount;
    //总数
    private long total;
    //结果集
    private List<T> list;

    //返回一个当前时间到页面上去
    public String getSystemNowDateTime() {
        return DateUtils.formatDateTime(new Date());
    }

//已经省略部分set.get方法

    /**
     * 重写父类的相应方法,以提供自实现取得起始点
     */
    @Override
    public int getOffset() {
        return (pageNum - 1) * pageCount;
    }

    /**
     * 重写父类的相应方法,以提供自实现限制
     */
    @Override
    public int getLimit() {
        return pageCount;
    }

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "PageResource [pageNum=" + pageNum + ", pageCount=" + pageCount + ", total=" + total + ", list=" + list
				+ "]";
	}
    
    
    
}
