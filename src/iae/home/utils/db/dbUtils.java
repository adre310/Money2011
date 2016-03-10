package iae.home.utils.db;

import java.util.Date;

import android.database.Cursor;

public class dbUtils {
	public static double getDoubleFromCursor(Cursor c, String col) {
		return c.getDouble(c.getColumnIndex(col));
	}
	public static long getLongFromCursor(Cursor c, String col) {
		return c.getLong(c.getColumnIndex(col));
	}
	public static int getIntFromCursor(Cursor c, String col) {
		return c.getInt(c.getColumnIndex(col));
	}
	public static String getStringFromCursor(Cursor c, String col) {
		return c.getString(c.getColumnIndex(col));
	}
	public static Boolean getBooleanFromCursor(Cursor c, String col) {
		return c.getLong(c.getColumnIndex(col)) != 0;
	}
	public static Date getDateFromCursor(Cursor c, String col) {
		return new Date(c.getLong(c.getColumnIndex(col)));
	}
}
