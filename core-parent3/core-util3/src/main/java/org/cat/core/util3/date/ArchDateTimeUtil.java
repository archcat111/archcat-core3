package org.cat.core.util3.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;

/**
 * 
 * @author 王云龙
 * @date 2021年8月12日 下午1:59:01
 * @version 1.0
 * @description 日期时间处理类
 *
 */
public class ArchDateTimeUtil {
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月12日 下午1:59:50
	 * @version 1.0
	 * @description 日期时间处理类-格式相关常量
	 *
	 */
	public static class FormatConstants{
		public static final String DATETIME_DETAIL="yyyy-MM-dd'T'HH:mm:ss.SSS";
		public static final String DATETIME_NORMAL="yyyy-MM-dd HH:mm:ss";
		public static final String DATE_NORMAL="yyyy-MM-dd";
		public static final String DATE_CHINA="yyyy年MM月dd日";
		public static final String DATE_NUMBER="yyyy/MM/dd";
		public static final String DATE_SMALL="yyyyMMdd";
		public static final String TIME_NORMAL="HH:mm:ss";
	}
	
	/**
	 * 
	 * @author 王云龙
	 * @date 2021年8月12日 下午2:02:27
	 * @version 1.0
	 * @description 日期时间处理类-时区相关常量
	 *
	 */
	public static class ZoneConstants{
		public static final String BEIJING="+08";
	}
	
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:10:29
	 * @version 1.0
	 * @description 获取北京时间 
	 * @return
	 */
	public static Date getNowDateTime4BeiJing(){
		Clock clock=Clock.system(ZoneId.of(ZoneConstants.BEIJING));
		Instant instant=clock.instant();
		Date date=Date.from(instant);
		return date;
//		ZonedDateTime.now(clock);
//		ZonedDateTime zonedDateTime=ZonedDateTime.of(LocalDateTime.of(LocalDate.now(clock), LocalTime.now(clock)), null);
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//		String dateTimeStr=zonedDateTime.format(formatter);
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:11:29
	 * @version 1.0
	 * @description 获取UTC时间 
	 * @return
	 */
	public static Date getNowDateTime4UTC(){
		Clock clock=Clock.system(ZoneOffset.UTC);
		Instant instant=clock.instant();
		Date date=Date.from(instant);
		return date;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2021年8月12日 下午2:07:07
	 * @version 1.0
	 * @description 将时间格式化为固定格式的字符串  
	 * @param date 时间
	 * @param dateFormat 格式符，可以使用本工具类提供的格式符，也可以自定义
	 * 			{@linkplain ArchDateTimeUtil.FormatConstants}
	 * @return  满足格式符的时间字符串
	 */
	public static String getFormatDateStr(Date date,String dateFormat){
		if(date==null){
			throw new IllegalArgumentException("date不能为null");
		}
		if(StringUtils.isBlank(dateFormat)){
			dateFormat=FormatConstants.DATETIME_NORMAL;
		}
		SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
		String dateStr=sdf.format(date);
		return dateStr;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2017年8月6日 下午9:22:31
	 * @version 1.0
	 * @description 将String转化成Date
	 * @param dateStr 表示时间的字符串
	 * @param sourceDateFormat 字符串符合的时间格式符
	 * @return date
	 */
	public static Date getFormatDate(String dateStr,String sourceDateFormat){
		SimpleDateFormat sdf=new SimpleDateFormat(sourceDateFormat);
		Date date=null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new ArchDateTimeUtilException("将时间字符串转换为Date时出错", e);
		}
		return date;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 上午11:47:52
	 * @version 1.0
	 * @description 获取某年某月的最后天的日期，格式为：yyyy-MM-dd
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getMonthLastDay(String year, String month) {
		SimpleDateFormat format = new SimpleDateFormat(FormatConstants.DATE_NORMAL); 
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, Integer.parseInt(year));
		ca.set(Calendar.MONTH, Integer.parseInt(month)-1);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format.format(ca.getTime());
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 下午2:32:46
	 * @version 1.0
	 * @description 获取当月有多少天 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDayCountForMonth(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DATE, 1);
		calendar.roll(Calendar.DATE, -1);
		int dayCount = calendar.get(Calendar.DATE);
        return dayCount;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 上午11:54:04
	 * @version 1.0
	 * @description 将年月日 转换为  LocalDate对象
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static LocalDate getLocalDate(int year, int month, int day) {
		LocalDate localDate = LocalDate.of(year, month, day);
        return localDate;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年10月10日 下午2:09:46
	 * @version 1.0
	 * @description 获取中国式的某日是星期几，返回星期一、星期二、等 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getWeekName4China(int year, int month, int day) {
		LocalDate localDate = getLocalDate(year, month, day);
		String weekName = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINA);
        return weekName;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年10月8日 下午3:57:48
	 * @version 1.0
	 * @description 将年月日 转换为  yyyyMMdd的字符串 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String getStr4YYYYMMDD(int year, int month, int day) {
		LocalDate localDate = getLocalDate(year, month, day);
		String localDateStr = localDate.format(DateTimeFormatter.ofPattern(FormatConstants.DATE_SMALL));
        return localDateStr;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 下午12:43:51
	 * @version 1.0
	 * @description 获得 年月日 在 一年中的第几周 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static int getWeekCountForYear(int year, int month, int day) {
		LocalDate localDate = getLocalDate(year, month, day);
		int weekCountForYear = getWeekCountForYear(localDate);
        return weekCountForYear;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 下午12:45:47
	 * @version 1.0
	 * @description 获得 该LocalDate对象 在 一年中的第几周  
	 * @param localDate
	 * @return
	 */
	public static int getWeekCountForYear(LocalDate localDate) {
		WeekFields weekFields = WeekFields.ISO;
		int weekCountForYear = localDate.get(weekFields.weekOfYear());
        return weekCountForYear+1;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 下午12:13:13
	 * @version 1.0
	 * @description  将年月日 转换为  Calendar对象
	 * @param date
	 * @return
	 */
	public static Calendar getCalendar(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault(Locale.Category.FORMAT));
        calendar.setFirstDayOfWeek(Calendar.WEEK_OF_MONTH);
        calendar.set(year, month, day);
        return calendar;
    }
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 上午11:48:55
	 * @version 1.0
	 * @description 将yyyy-MM-dd格式的字符串日期转换为毫秒时间戳 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	public static long changeDateStrToTimeMillis(String date) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat(FormatConstants.DATE_NORMAL);
		Date dateTime = format.parse(date);
		return dateTime.getTime();
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月15日 上午11:49:27
	 * @version 1.0
	 * @description 返回Date日期前后N天的日期对象Date 
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date offsetDay(Date date, int day) {
		DateTime newDateTime = DateUtil.offsetDay(date, day);
		return newDateTime;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年9月23日 下午5:21:06
	 * @version 1.0
	 * @description 返回执行日期前后多少秒的新localDateTime
	 * @param localDateTime
	 * @param second
	 * @return
	 */
	public static LocalDateTime offsetSecond(LocalDateTime localDateTime, long second) {
		LocalDateTime newLocalDateTime = LocalDateTimeUtil.offset(localDateTime, second, ChronoUnit.SECONDS);
		return newLocalDateTime;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年12月29日 下午3:22:58
	 * @version 1.0
	 * @description 将LocalDateTime转换为Date 
	 * @param localDateTime
	 * @return
	 */
	public static Date asDate(LocalDateTime localDateTime) {
		Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		return date;
	}
	
	/**
	 * 
	 * @author wangyunlong
	 * @date 2022年12月29日 下午3:23:11
	 * @version 1.0
	 * @description 将LocalDate转换为Date 
	 * @param localDate
	 * @return
	 */
	public static Date asDate(LocalDate localDate) {
		LocalDateTime localDateTime = localDate.atStartOfDay();
		Date date = asDate(localDateTime);
		return date;
	}
}
