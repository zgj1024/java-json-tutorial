package com.zhangguojian.json;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ParserUtils {
    /**
     * 是否是浮点数
     * @param val 数字的字符串
     * @return
     */
    public static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }

    /**
     * 根据精度自动转
     * @param val
     * @return
     */
    public static Number parseNumber(final String val){
        if (isDecimalNotation(val)) {
            final Double d = Double.valueOf(val);
            if (d.isInfinite() || d.isNaN()) {
                return new BigDecimal(val);
            }
            return d;
        }

        BigInteger bigIntValue = new BigInteger(val);
        if(bigIntValue.bitLength()<=31){
            return bigIntValue.intValue();
        }
        if(bigIntValue.bitLength()<=63){
            return bigIntValue.longValue();
        }
        return bigIntValue;

    }
}
