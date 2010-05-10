package adapters.MySql;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

import core.Attribute;
import core.Introspector;

//TODO:Refactoring
public class TypeCast {

	public static Object toJava(Object value) {
		if (value == null) return null;
		if (Introspector.isDate(value)) return dateToJava((Date) value);
		if (Introspector.isTimestamp(value)) return timeStampToJava((Timestamp) value);
		return value;
	}

	private static Timestamp timeStampToJava(Timestamp value) {
		return value;
	}

	@SuppressWarnings("deprecation")
	private static Calendar dateToJava(Date value) {
		Calendar date = Calendar.getInstance();
		value.setMonth(value.getMonth() + 1); // Calendar JANUARY = 0..
		date.setTime(value);
		return date;
	}

	public static Object value(Attribute attribute) {
		if (attribute.getSqlType().equals(Mapping.BOOLEAN)) return booleanToSql(attribute);
		if (attribute.getSqlType().equals(Mapping.STRING)) return sanitize(attribute);
		if (attribute.getSqlType().equals(Mapping.DATE)) return dateToSql(attribute);
		if (attribute.getSqlType().equals(Mapping.TIMESTAMP)) return timestampToSql(attribute);
		return attribute.getValue();
	}

	private static Object timestampToSql(Attribute attribute) {
		return attribute.getValue();
	}

	public static Object dateToSql(Attribute attribute) {
		if (attribute.getValue() == null) return null;
		int year = ((Calendar) attribute.getValue()).get(Calendar.YEAR);
		int month = ((Calendar) attribute.getValue()).get(Calendar.MONTH);
		int day = ((Calendar) attribute.getValue()).get(Calendar.DAY_OF_MONTH);

		return year + "-" + month + "-" + day;
	}

	public static int booleanToSql(Attribute attribute) {
		return (attribute.getValue().equals(new Boolean(true))) ? 1 : 0;
	}

	private static String sanitize(Attribute attribute) {
		String value = (String) attribute.getValue();
		if (value == null) return null;
		value = value.replace("'", "''");
		return value;
	}

}
