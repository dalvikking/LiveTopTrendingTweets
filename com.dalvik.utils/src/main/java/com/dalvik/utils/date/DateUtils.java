package com.dalvik.utils.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	public static String DACX_JAVA_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss Z";
	public static String JAVA_DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
	public static String YYYYMMDD_FORMAT = "yyyy-MM-dd";
	public static String YYYYMMDD_HHMMSS_FORMAT = "yyyy-MM-dd_HH-mm-ss";
	public static String EEE_MMM_DD_HHMMSS_Z_YYYY_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";
	public static String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public static String yyyy_MM_dd_HH_mm_ss_SSS_NOSPACE = "yyyy-MM-dd_HH-mm-ss-SSS";

	public static final String[] supportedDatePatterns = { "MM/dd/yy HH:mm", "MM/dd/yy", "MM/dd/yyyy",
			"MMM dd, yyyy hh:mm:ss a", "MMM dd, yyyy", "dd-MMM-yy", "dd-MM-yyyy HH:mm:ss", "yyyy-MM-dd_HH-mm-ss",
			"yyyy-MM-dd", "E, dd MMM yyyy HH:mm:ss Z", "yyyy.MM.dd.HH.mm.ss", "EEE MMM dd HH:mm:ss z yyyy",
			"yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ssZ" };
	public static final String POSTGRESQL_FORMAT = "YYYY-MM-DD HH24:MI:SS";

	public static final String AMEYO_STD_DATE_FORMAT_FOR_POSTGRES = "YYYY-MM-DD HH24:MI:SS";
	public static final String AMEYO_STD_DATE_FORMAT_FOR_JAVA = "yyyy-MM-dd HH:mm:ss";

	public static final SimpleDateFormat SDF_AMEYO_STD_DATE_FORMAT_FOR_JAVA = new SimpleDateFormat(
			AMEYO_STD_DATE_FORMAT_FOR_JAVA);

	public static String getDateString(Date date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
		return dateFormat.format(date);
	}

	public static Date getDate(String dateString, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	public static boolean isValidDate(String inputDate, boolean ignoreNull) {

		if (inputDate == null) {
			return ignoreNull;
		}

		for (String datePattern : supportedDatePatterns) {

			DateFormat formatter = new SimpleDateFormat(datePattern);
			ParsePosition parsePosition = new ParsePosition(0);
			Date date = formatter.parse(inputDate, parsePosition);

			if (date != null) {
				return true;
			}
		}

		return false;
	}

	public static Date getValidDate(String inputDate) {

		return getValidDate(inputDate, supportedDatePatterns);
	}

	public static Date getValidDate(String inputDate, String dateFormats[]) {

		if (inputDate == null) {
			return null;
		}

		for (String datePattern : dateFormats) {

			DateFormat formatter = new SimpleDateFormat(datePattern);
			ParsePosition parsePosition = new ParsePosition(0);
			Date date = formatter.parse(inputDate, parsePosition);

			if (date != null) {
				return date;
			}
		}

		return null;
	}
}
