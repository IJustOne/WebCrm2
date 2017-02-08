package cn.ijustone.crm.utils.date;

import java.util.Date;

/**
 * 时间范围对象,用于表示一段时间
 *
 * @author flym
 */
public class DateRange {
	/** 开始时间 */
	private Date startDate;
	/** 结束时间 */
	private Date endDate;

	public DateRange() {
	}

	public DateRange(Date startDate, Date endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void swap() {
		if(startDate == null || endDate == null)
			return;
		DateUtils.swap(startDate, endDate);
	}
}
