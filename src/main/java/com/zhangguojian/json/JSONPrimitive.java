package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JSONPrimitive implements JSONElement {
    private Object value;
    private JSONPrimitive(Object value) {
        this.value = value;
    }

    public static JSONPrimitive of(Boolean bool){
        return new JSONPrimitive(bool);
    }

    public static JSONPrimitive of(Number number){
        return new JSONPrimitive(number);
    }

    public static JSONPrimitive of(String string){
        return new JSONPrimitive(string);
    }

    public static JSONPrimitive of(Character c){
        return new JSONPrimitive(c);
    }

    public static JSONPrimitive of(Object c) throws CastException {
        if(c instanceof Character || c instanceof String||c instanceof Number){
            return new JSONPrimitive(c);
        }
        throw new CastException(c.getClass() + "can not cast to JSONPrimitive");
    }

    public Object getValue() {
        return value;
    }

    public boolean isBoolean(){
        return value instanceof Boolean;
    }

    public boolean getAsBoolean(){
        if(isBoolean()){
            return (Boolean)value;
        }else {
            return Boolean.parseBoolean(getAsString());
        }
    }

    public boolean isNumber(){
        return value instanceof Number;
    }

    public boolean isCharacter(){
        return value instanceof Character;
    }

    public Number getAsNumber(){
        return value instanceof String? ParserUtils.parseNumber((String) value) : (Number)value;
    }

    public boolean isString() {
        return value instanceof String || value instanceof Character;
    }

    @Override
    public JSONPrimitive getAsJSONPrimitive() {
        return this;
    }

    @Override
    public String getAsString() {
        if (isNumber()) {
            return getAsNumber().toString();
        } else if (isBoolean()) {
            return getAsBoolean()?"true":"false";
        } else if (isCharacter()){
            return value.toString();
        }else {
            return (String) value;
        }
    }

    public boolean isDouble(){
        return value.getClass() == Double.class || value.getClass() == double.class;
    }

    @Override
    public double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    public boolean isFloat(){
        return value.getClass() == Float.class || value.getClass() == float.class;
    }

    @Override
    public float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }

    public boolean isLong(){
        return value.getClass() == Long.class || value.getClass() == long.class;
    }

    @Override
    public long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    public boolean isInt(){
        return value.getClass() == Integer.class || value.getClass() == int.class;
    }

    @Override
    public int getAsInt() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }

    public boolean isShort(){
        return value.getClass() == Short.class || value.getClass() == short.class;
    }

    @Override
    public short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
    }

    public boolean isByte(){
        return value.getClass() == Byte.class || value.getClass() == byte.class;
    }

    @Override
    public byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }

    public boolean isBigDecimal(){
        return value.getClass() == BigDecimal.class;
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
    }

    public boolean isBigInteger(){
        return value.getClass() == BigInteger.class;
    }

    @Override
    public BigInteger getAsBigInteger() {
        return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
    }

    @Override
    public String stringify() {
        return StringifyUtils.Stringify(value);
    }

    @Override
    public char getAsCharacter() {
        return  getAsString().charAt(0);
    }

    @Override
    public boolean isJSONPrimitive() {
        return true;
    }

    @Override
    public JSONPrimitive deepCopy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass()!= getClass() ){
            return false;
        }
        JSONPrimitive other = (JSONPrimitive)o;
        if(this.value == other.value){
            return true;
        }

        if(isIntegral(this) && isIntegral(other)){
            return getAsNumber().longValue() == other.getAsNumber().longValue();
        }
        if (value instanceof Number && other.value instanceof Number) {
            double a = getAsNumber().doubleValue();
            // Java standard types other than double return true for two NaN. So, need
            // special handling for double.
            double b = other.getAsNumber().doubleValue();
            return a == b || (Double.isNaN(a) && Double.isNaN(b));
        }
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        if(value == null){
            return 31;
        }
        //高效 hash
        if(isIntegral(this)){
            long value = getAsNumber().longValue();
            return (int) (value ^ (value >>> 32));
        }
        if (value instanceof Number) {
            long value = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) (value ^ (value >>> 32));
        }
        return value.hashCode();
    }

    private static boolean isIntegral(JSONPrimitive primitive) {
        if (primitive.value instanceof Number) {
            Number number = (Number) primitive.value;
            return number instanceof BigInteger || number instanceof Long || number instanceof Integer
                    || number instanceof Short || number instanceof Byte;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.stringify();
    }
}