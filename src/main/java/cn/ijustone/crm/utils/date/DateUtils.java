package cn.ijustone.crm.utils.date;


import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import cn.ijustone.crm.exception.DateParseException;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 时间工具类，用于封装各个时间对象信息
 *
 * @author flym
 */
public class DateUtils {
	private static final Pattern datePattern = Pattern
			.compile("(?:19|20)[0-9]{2}-(?:0?[1-9]|1[012])-(?:0?[1-9]|[12][0-9]|3[01])");
	private static final Pattern timePattern = Pattern.compile("(?:[01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]");
	private static final Pattern dateTimePattern = Pattern.compile(datePattern.pattern() + " " + timePattern.pattern());
	private static final BiMap<Integer, Integer> dayOfWeekMap;//处理星期几的双向转换

	/**默认日期格式不包含时间**/
	public static final SimpleDateFormat FORMATSHOT = new SimpleDateFormat("yyyy-MM-dd");
	/**默认日期格式包含时间**/
	public static final SimpleDateFormat FORMATLONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		ImmutableBiMap.Builder<Integer, Integer> biMap = ImmutableBiMap.builder();
		biMap.put(Calendar.MONDAY, 1);
		biMap.put(Calendar.TUESDAY, 2);
		biMap.put(Calendar.WEDNESDAY, 3);
		biMap.put(Calendar.THURSDAY, 4);
		biMap.put(Calendar.FRIDAY, 5);
		biMap.put(Calendar.SATURDAY, 6);
		biMap.put(Calendar.SUNDAY, 7);
		dayOfWeekMap = biMap.build();
	}

	private static final ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		}
	};

	private static final ThreadLocal<DateFormat> timeFormatThreadLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		}
	};
	private static final ThreadLocal<DateFormat> timeNewFormatThreadLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("HH:mm");
		}
	};
	private static final ThreadLocal<DateFormat> dateTimeFormatThreadLocal = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};
	private static final ThreadLocal<DateFormat> dateTimeFormatForHM = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
	};
	private static final ThreadLocal<Calendar> calendarThreadLocal = new ThreadLocal<Calendar>() {
		@Override
		protected Calendar initialValue() {
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.setFirstDayOfWeek(Calendar.MONDAY);
			return calendar;
		}
	};

	/** 用于表示日期的常量对象 */
	public static class DateX implements Serializable, Comparable<DateX> {
		private static final long serialVersionUID = 1L;
		private final int year;
		private final int month;
		private final int weekOfMonth;
		private final int dayOfMonth;

		public DateX(int year, int month, int dayOfMonth) {
			this.year = year;
			this.month = month;
			this.dayOfMonth = dayOfMonth;
			weekOfMonth = DateUtils.getWeekOfMonth(DateUtils.buildDate(year, month, dayOfMonth));
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getWeekOfMonth() {
			return weekOfMonth;
		}

		public int getDayOfMonth() {
			return dayOfMonth;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}

			DateX dateX = (DateX) o;

			if(dayOfMonth != dateX.dayOfMonth) {
				return false;
			}
			if(month != dateX.month) {
				return false;
			}
			if(year != dateX.year) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = year;
			result = 31 * result + month;
			result = 31 * result + dayOfMonth;
			return result;
		}

		@Override
		public int compareTo(DateX o) {
			if(year != o.year) {
				return year > o.year ? 1 : -1;
			}
			if(month != o.month) {
				return month > o.month ? 1 : -1;
			}
			if(dayOfMonth != o.dayOfMonth) {
				return dayOfMonth > o.dayOfMonth ? 1 : -1;
			}
			return 0;
		}
	}

	/** 用于表示日期的常量对象 */
	public static class TimeX implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int hour;
		private final int minute;
		private final int second;

		public TimeX(int hour, int minute, int second) {
			this.hour = hour;
			this.minute = minute;
			this.second = second;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}

		public int getSecond() {
			return second;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}

			TimeX timeX = (TimeX) o;

			if(hour != timeX.hour) {
				return false;
			}
			if(minute != timeX.minute) {
				return false;
			}
			if(second != timeX.second) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = hour;
			result = 31 * result + minute;
			result = 31 * result + second;
			return result;
		}
	}

	/** 用于表示日期时间的常量对象 */
	public static class DateTimeX implements Serializable {
		private static final long serialVersionUID = 1L;
		private final int year;
		private final int month;
		private final int dayOfMonth;
		private final int hour;
		private final int minute;
		private final int second;

		public DateTimeX(int year, int month, int dayOfMonth, int hour, int minute, int second) {
			this.year = year;
			this.month = month;
			this.dayOfMonth = dayOfMonth;
			this.hour = hour;
			this.minute = minute;
			this.second = second;
		}

		public int getYear() {
			return year;
		}

		public int getMonth() {
			return month;
		}

		public int getDayOfMonth() {
			return dayOfMonth;
		}

		public int getHour() {
			return hour;
		}

		public int getMinute() {
			return minute;
		}

		public int getSecond() {
			return second;
		}

		@Override
		public boolean equals(Object o) {
			if(this == o) {
				return true;
			}
			if(o == null || getClass() != o.getClass()) {
				return false;
			}

			DateTimeX dateTimeX = (DateTimeX) o;

			if(dayOfMonth != dateTimeX.dayOfMonth) {
				return false;
			}
			if(hour != dateTimeX.hour) {
				return false;
			}
			if(minute != dateTimeX.minute) {
				return false;
			}
			if(month != dateTimeX.month) {
				return false;
			}
			if(second != dateTimeX.second) {
				return false;
			}
			if(year != dateTimeX.year) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = year;
			result = 31 * result + month;
			result = 31 * result + dayOfMonth;
			result = 31 * result + hour;
			result = 31 * result + minute;
			result = 31 * result + second;
			return result;
		}
	}

	/** 转换日期至年月日时分 */
	public static String formatDateHM(Date date) {
		return dateTimeFormatForHM.get().format(date);
	}

	/** 时间转年月日 */
	public static String formatDate(Date date) {
		return dateFormatThreadLocal.get().format(date);
	}

	/** 时间转时分秒 */
	public static String formatTime(Date date) {
		return timeFormatThreadLocal.get().format(date);
	}
	/** 时间转时分秒 */
	public static String formatTimeNew(Date date){
		return timeNewFormatThreadLocal.get().format(date);
	}
	/** 时间转年月日 时分秒 */
	public static String formatDateTime(Date date) {
		return dateTimeFormatThreadLocal.get().format(date);
	}

	/** 时间转指定格式的字符串 */
	public static String format(Date date, String pattern) {
		return DateFormatUtils.format(date, pattern, Locale.CHINA);
	}

	/** 年月日转时间,转换失败时抛出转换异常 */
	public static Date parseDate(String dateStr) throws DateParseException {
		Date d = parseDate(dateStr, null);
		if(d == null) {
			throw new DateParseException(dateStr + " 不能进行转换为Date");
		}
		return d;
	}

	/** 年月日转时间,转换失败时返回默认时间 */
	public static Date parseDate(String dateStr, Date defaultDate) {
		if(!datePattern.matcher(dateStr).matches()) {
			return defaultDate;
		}
		try {
			return dateFormatThreadLocal.get().parse(dateStr);
		} catch(ParseException e) {
			return defaultDate;
		}
	}

	/** 时分秒转时间,转换失败时抛出转换异常 */
	public static Date parseTime(String dateStr) throws DateParseException {
		Date d = parseTime(dateStr, null);
		if(d == null) {
			throw new DateParseException(dateStr + " 不能转换为Time");
		}
		return d;
	}

	/** 时分秒转时间,转换失败时返回默认时间 */
	public static Date parseTime(String dateStr, Date defaultDate) {
		if(!timePattern.matcher(dateStr).matches()) {
			return defaultDate;
		}
		try {
			return timeFormatThreadLocal.get().parse(dateStr);
		} catch(ParseException e) {
			return defaultDate;
		}
	}

	/** 年月日 时分秒转时间,转换失败时抛出转换异常 */
	public static Date parseDateTime(String dateStr) throws DateParseException {
		Date d = parseDateTime(dateStr, null);
		if(d == null) {
			throw new DateParseException(dateStr + " 不能转换为DateTime");
		}
		return d;
	}

	/** 年月日 时分秒转时间,转换失败时返回默认值 */
	public static Date parseDateTime(String dateStr, Date defaultDate) {
		if(!dateTimePattern.matcher(dateStr).matches()) {
			return defaultDate;
		}
		try {
			return dateTimeFormatThreadLocal.get().parse(dateStr);
		} catch(ParseException e) {
			return defaultDate;
		}
	}

	public static Calendar getCalendar(Date date) {
		Calendar calendar = calendarThreadLocal.get();
		calendar.setTime(date);
		return calendar;
	}

	/** 是否是闰年 */
	private static boolean isLeapYear(int year) {
		return year % 400 == 0 || (year % 100 != 0 && year % 4 == 0);
	}

	/**
	 * 时间转为当天 23:59:59
	 *
	 * @deprecated 方法命名应与turnToDateStart相对应, 修正为turnToDateEnd, 请使用turnToDateEnd方法
	 */
	@Deprecated
	public static Date turnToDateLine(Date date) {
		return turnToDateEnd(date);
	}

	/** 时间转为当天 23:59:59 */
	public static Date turnToDateEnd(Date date) {
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	/** 时间转为当天 00:00:00 */
	public static Date turnToDateStart(Date date) {
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	/** 时间转为当周第一天 00:00:00 */
	public static Date turnToWeekStart(Date date) {
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		DateX dateX = getDateX(calendar.getTime());
		return buildDateTime(dateX.year, dateX.month, dateX.dayOfMonth, 0, 0, 0);
	}

	/** 时间转为当周最后一天 23:59:59 */
	public static Date turnToWeekEnd(Date date) {
		Calendar calendar = getCalendar(date);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		DateX dateX = getDateX(calendar.getTime());
		return buildDateTime(dateX.year, dateX.month, dateX.dayOfMonth, 23, 59, 59);
	}

	/** 时间转为当月最后一天 23:59:59 */
	public static Date turnToMonthEnd(Date date) {
		DateX dateX = getDateX(date);
		int month = dateX.month;
		int[] monthDays = {31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		int day = month == 1 ? (isLeapYear(dateX.year) ? 29 : 28) : monthDays[month];
		return buildDateTime(dateX.year, month, day, 23, 59, 59);
	}

	/** 时间转为当月第一天 00:00:00 */
	public static Date turnToMonthStart(Date date) {
		DateX dateX = getDateX(date);
		return buildDateTime(dateX.year, dateX.month, 1, 0, 0, 0);
	}

	/** 转换为明天 */
	public static Date turnToTomorrow(Date date) {
		Calendar calendar = getCalendar(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	/** 转换为昨天 */
	public static Date turnToYesterday(Date date) {
		Calendar calendar = getCalendar(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	/** 时间对换 开始 结束 */
	public static void swap(Date startDate, Date endDate) {
		if(startDate == null || endDate == null) {
			return;
		}
		if(startDate.after(endDate)) {
			long startDateLong = startDate.getTime();
			startDate.setTime(endDate.getTime());
			endDate.setTime(startDateLong);
		}
	}

	/** 时间对换 开始 结束 返回复合DateRange */
	public static void swap(DateRange dateRange) {
		dateRange.swap();
	}

	/** 根据指定的时间创建一个时间间隔 */
	public static DateRange createDateRange(Date date) {
		return new DateRange(turnToDateStart(date), turnToDateLine(date));
	}

	/** 获取指定时间的年份 */
	public static int getFullYear(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.YEAR);
	}

	/** 获取指定时间的月份 */
	public static int getMonth(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.MONTH);
	}

	/** 获取指定的周 */
	public static int getWeekOfMonth(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}

	/** 获取指定时间的天(按月计) */
	public static int getDayOfMonth(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/** 获取指定的天(按周计),按照中国算法，返回相应的实际数字，星期1为1，星期日为7 */
	public static int getDayOfWeek(Date date) {
		Calendar calendar = getCalendar(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Integer result = dayOfWeekMap.get(dayOfWeek);
		return result == null ? 0 : result;
	}

	/** 获取小时 */
	public static int getHour(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/** 获取分钟 */
	public static int getMinute(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.MINUTE);
	}

	/** 获取秒 */
	public static int getSecond(Date date) {
		Calendar calendar = getCalendar(date);
		return calendar.get(Calendar.SECOND);
	}

	/** 获取日期 */
	public static DateX getDateX(Date date) {
		Calendar calendar = getCalendar(date);
		return new DateX(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	/** 获取时间 */
	public static TimeX getTimeX(Date date) {
		Calendar calendar = getCalendar(date);
		return new TimeX(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND));
	}

	/** 获取日期时间 */
	public static DateTimeX getDateTimeX(Date date) {
		Calendar calendar = getCalendar(date);
		return new DateTimeX(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND));
	}

	/** 判断两个时间是否是同一天 */
	public static boolean isSameDay(Date aDate, Date bDate) {
		DateX aDateX = getDateX(aDate);
		DateX bDateX = getDateX(bDate);

		return aDateX.equals(bDateX);
	}

	/** 判断指定的时间是否为今天 */
	public static boolean isToday(Date date) {
		return isSameDay(date, new Date());
	}

	/** 根据年,月,日创建时间 */
	public static Date buildDate(int year, int month, int dayOfMonth) {
		Calendar calendar = getCalendar(new Date());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		return calendar.getTime();
	}

	/** 根据年，月，周，星期几创建时间 */
	public static Date buildDate(int year, int month, int weekOfMonth, int dayOfWeek) {
		Calendar calendar = getCalendar(new Date());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
		calendar.set(Calendar.DAY_OF_WEEK, dayOfWeekMap.inverse().get(dayOfWeek));

		return calendar.getTime();
	}

	/** 根据小时,分,秒创建时间 */
	public static Date buildTime(int hour, int minute, int second) {
		Calendar calendar = getCalendar(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}

	/** 根据年,月,日,小时,分,秒创建时间 */
	public static Date buildDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second) {
		Calendar calendar = getCalendar(new Date());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}

	/**判断时间大小*/
	public static boolean compareDate(Date date1,Date date2){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(formatDate(date1));
			Date dt2 = df.parse(formatDate(date2));
			if(dt1.getTime()>dt2.getTime()){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取当期传入日期前一个月当前日期(如: 传入2016-03-03 返回2016-02-03)
	 * 当格式参数为空时返回默认格式"yyyy-MM-dd"
	 * @param date 当前日期参数
	 * @param formatStr 格式化字符串; (如: "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss")
	 * @return String日期类型
	 */
	public static String getPreMonthNowDateStr(Date date, String formatStr) {
		String result = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		if( formatStr != null && formatStr.length() > 0) {
			SimpleDateFormat format = new SimpleDateFormat(formatStr);
			result = formatStr(format, calendar.getTime());
		} else {
			result = formatStr(FORMATSHOT, calendar.getTime());
		}

		return result;
	}

	/**
	 * 获取当期传入日期前一个月当前日期(如: 传入2016-03-03 返回2016-02-03)
	 * 当格式参数为空时返回默认格式"yyyy-MM-dd"
	 * @param date 当前日期参数
	 * @param formatStr 格式化字符串; (如: "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss")
	 * @return Date日期类型
	 */
	public static Date getPreMonthNowDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		return calendar.getTime();
	}

	/**
	 * 获取当期月的第一天日期
	 * @return String日期类型
	 */
	public static String getMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return formatStr(FORMATSHOT, calendar.getTime());
	}

	/**
	 * 得到本月的最后一天
	 *
	 * @return
	 */
	public static String getMonthLastDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		return formatStr(FORMATSHOT, calendar.getTime());
	}

	/**
	 * 格式化日期 将String日期转换成Date(yyyyMMdd), 或者(yyyy-MM-dd)
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date dateParse(String date) throws ParseException{
		if(date.length() == 8){
			date = date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
		}
		return formatDate(date);
	}

	/**
	 * 格式化日期 将String身份证转换成Date(yyyy-MM-dd)
	 * @param idcards
	 * @return
	 * @throws ParseException
	 */
	public static Date dateParseTwo(String idcards) throws ParseException{

		String  idcard=idcards.substring(6, 14);
		String y = idcard.substring(0,4);
		String m=idcard.substring(4,6);

		String d=idcard.substring(6,8);

		String stb = y+"-"+m+"-"+d;
		return formatDate(stb);
	}

	/**
	 * 得到给定日期相距现在多少天
	 * @param date
	 * @return
	 */
	public static Long getAgeToDay(Date date){
		//当date为空时,默认为当前日期
		if(date == null){
			date = new Date();
		}
		Calendar today = new GregorianCalendar();
		Calendar birthday = Calendar.getInstance();
		birthday.setTime(date);
		Long day = (Long)(today.getTimeInMillis()-birthday.getTimeInMillis())/(24*60*60*1000L);
		return day;
	}

	/***
	 * 获取以"年-月-日 时:分:秒"格式的当前时间
	 * @return
	 */
	public static String getYesterdayMinutesSeconds(){
		Calendar   cal   =   Calendar.getInstance();
		String yesterday = FORMATLONG.format(cal.getTime());
		return yesterday;
	}
	/***
	 * 获取当前上一天，以"年-月-日"格式的时间
	 * @return
	 */
	public static String getYesterday(){
		Calendar   cal   =   Calendar.getInstance();
		cal.add(Calendar.DATE,   -1);
		return formatStr(FORMATSHOT, cal.getTime());
	}

	/**
	 * 获取当期日期;
	 * @return
	 */
	public static String getNow(){
		Calendar cal = Calendar.getInstance();
		return formatStr(FORMATSHOT, cal.getTime());
	}

	/**
	 * 字符串转换为Date类型;
	 * @param date
	 * @return
	 */
	public static Date formatDate(String date) {
		try {
			return FORMATSHOT.parse(date);
		} catch (ParseException e) {
			//TODO 目前抛出异常后设置为空;
			return null;
		}
	}

	/**
	 * 字符串转换为Date类型;
	 * @param date
	 * @return
	 */
	public static Date formatDateLong(String date) {
		try {
			return FORMATLONG.parse(date);
		} catch (ParseException e) {
			//TODO 目前抛出异常后设置为空;
			return null;
		}
	}


	/**
	 * 格式化日期
	 * @param format 所需格式化格式
	 * @param date 格式化时间
	 * @return
	 */
	public static String formatStr(SimpleDateFormat dateFormat, Date date){
		String result = "";
		try {
			result = dateFormat.format(date);
		} catch (Exception e) {
			//TODO 可能抛出异常;暂时设置为空,后面可能会考虑重新处理
			result = null;
		}
		return result;
	}

	/**两个日期之间相差多少分钟date2-date1*/
	public static int minuteBetween(Date date1,Date date2){
		int i= Minutes.minutesBetween(new DateTime(date1), new DateTime(date2)).getMinutes();
		// int i= Weeks.weeksBetween(new DateTime(date1), new DateTime(date2)).getWeeks();
		return i;
	}

	/**两个日期之间相差多少小时date2-date1*/
	public static int hoursBetween(Date date1,Date date2){
		int i= Hours.hoursBetween(new DateTime(date1), new DateTime(date2)).getHours();
		return i;
	}

	/**两个日期之间相差多少天 date2-date1*/
	public static int daysBetween(Date date1,Date date2){
		int i= Days.daysBetween(new DateTime(turnToDateStart(date1)), new DateTime(turnToDateStart(date2))).getDays();
		return i;
	}

	/**两个日期之间相差多少秒date2-date1*/
	public static int secondsBetween(Date date1,Date date2){
		int i= Seconds.secondsBetween(new DateTime(date1), new DateTime(date2)).getSeconds();
		return i;
	}

	/**
	 * 获取指定的日期是星期几
	 * @param date
	 * @return
	 */
	public static int getWeekOfDay(Date date){
		return new DateTime(date).dayOfWeek().get();
	}


	/**
	 * 指定时间增加指定天数
	 * @param date
	 * @param d
	 * @return
	 */
	public static Date addDays(Date date,int d){
		return new DateTime(date).plusDays(d).toDate();
	}


	/**
	 * 指定时间增加指定小时
	 * @param date
	 * @param h
	 * @return
	 */
	public static Date addHour(Date date,int h){
		return new DateTime(date).plusHours(h).toDate();
	}

	/**
	 * 指定时间增加指定分钟
	 * @param date
	 * @param m
	 * @return
	 */
	public static Date addMinu(Date date,int m){
		return new DateTime(date).plusMinutes(m).toDate();
	}

	/**
	 * 指定时间减少指定分钟
	 * @param date
	 * @param m
	 * @return
	 */
	public static Date minusMinutes(Date date,int m){
		return new DateTime(date).minusMinutes(m).toDate();
	}

	/**
	 * 指定时间减少指定天数
	 * @param date
	 * @param d
	 * @return
	 */
	public static Date minusDays(Date date,int d){
		return new DateTime(date).minusDays(d).toDate();
	}

	/**
	 * 计算两个日期月份
	 * @param startDate
	 * @return
	 */
	public static int getMonthSpace(Date startDate) {
		int monthday;
		Calendar starCal = Calendar.getInstance();
		starCal.setTime(startDate);
		int sYear = starCal.get(Calendar.YEAR);
		int sMonth = starCal.get(Calendar.MONTH);
		int sDay = starCal.get(Calendar.DATE);
		//开始时间与今天相比较
		Date endDate = new Date();
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(endDate);
		int eYear = endCal.get(Calendar.YEAR);
		int eMonth = endCal.get(Calendar.MONTH);
		int eDay = endCal.get(Calendar.DATE);
		monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));
		if(eDay<sDay){
			monthday = monthday-1;
		}
		return monthday;
	}

	/**
	 * 将日期转为yyyyMMdd格式的数字
	 * @param date 日期
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal date2Number(Date date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String str=sdf.format(date);
		return new BigDecimal(str);
	}

	/**
	 * 将String类型的日期转为yyyyMMdd格式的数字
	 * @param date String类型的字符串
	 * @return
	 * @throws ParseException
	 */
	public static BigDecimal date2Number(String date) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Date tempDate=sdf.parse(date);
		return new BigDecimal(sdf2.format(tempDate));
	}

	/**
	 * 格式为yyyyMMdd的数字日期天数相加
	 * @param currentDate 计算之前的日期
	 * @param days 相加天数
	 * @return 计算之后为yyyyMMdd格式的数字日期
	 * @throws ParseException
	 */
	public static BigDecimal addDays4Number(BigDecimal currentDate,int days) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date execDate = sdf.parse(currentDate.toString());
		Calendar  calendar =Calendar.getInstance();
		calendar.setTime(execDate);
		//发生日期相加天数
		calendar.add(calendar.DATE,days);
		execDate=calendar.getTime();
		return new BigDecimal(sdf.format(execDate));
	}

	/**
	 * 指定日期+天数是否等于另一个指定天数
	 * @param currentDate 计算之前的日期
	 * @param targetDate  预计的日期
	 * @param days 天数
	 * @return
	 * @throws ParseException
	 */
	public static boolean addDaysIsEqTargetDate(Date currentDate,Date targetDate,int days) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date execDate = sdf.parse(sdf.format(currentDate));
		Calendar  calendar =new GregorianCalendar();
		calendar.setTime(execDate);
		//发生日期相加天数
		calendar.add(Calendar.DATE,days);
		execDate=calendar.getTime();
		return sdf.parse(sdf.format(execDate)).getTime()-sdf.parse(sdf.format(targetDate)).getTime()==0;
	}

}
