package com.stoplight.blu.toys.utils;

public class NumberUtils {


	public static String iTofixed(Double values, int numbers) {
		int nums = numbers - 0 + 1;
		int base = 10;
		double redValue= Math.floor(values * base) / base;
		String redText = BigDecimalUtils.formatToString(redValue, numbers);
		return redText;

	}

}
