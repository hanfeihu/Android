package com.stoplight.blu.toys.utils;

import java.math.BigDecimal;

public class MathDataUtil {
    public static BigDecimal stripTrailingZeros(double d) {//去除double尾巴的0
        return new BigDecimal(d).stripTrailingZeros();
    }
}