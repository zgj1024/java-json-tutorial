package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public final class JSONArray implements JSONElement, Iterable<JSONElement>{

    public static final JSONArray EMPTY = new JSONArray();

    private final List<JSONElement> elements;

    public List<JSONElement> getElements() {
        return elements;
    }

    public JSONArray(int initialCapacity) {
        elements = new ArrayList<>(initialCapacity);
    }

    public JSONArray() {
        elements = new ArrayList<>();
    }

    public static JSONArray of(Object[] elements) {
        if (elements == null) {
            return EMPTY;
        }
        JSONArray array = new JSONArray(elements.length);
        for(Object obj:elements){
            array.add(JSONElement.of(obj));
        }
        return array;
    }

    public static JSONArray of(JSONElement element){
        JSONArray array = new JSONArray();

        array.add(element);
        return array;
    }
    public static JSONArray of(Object object) throws CastException {
        if(object == null){
            return EMPTY;
        }
        if(!object.getClass().isArray())
            throw new CastException(object.toString() + " can not cast to array");


        JSONArray array = new JSONArray(Array.getLength(object));
        int length = Array.getLength(object);
        for (int i = 0; i < length; i += 1) {
            array.add(JSONElement.of(Array.get(object, i)));
        }
        return array;
    }

    public static JSONArray of(Collection collection){
        if (collection == null) {
            return EMPTY;
        }
        JSONArray array = new JSONArray(collection.size());
        for(Object obj:collection){
            array.add(JSONElement.of(obj));
        }
        return array;
    }
    public static JSONArray of(JSONElement... args){
        JSONArray array = new JSONArray(args.length);
        for (JSONElement arg : args) {
            array.add(arg);
        }
        return array;
    }

    @Override
    public boolean isJSONArray() {
        return true;
    }

    @Override
    public JSONArray getAsJSONArray() {
        return this;
    }

    public void add(Boolean bool){
        elements.add(bool == null? JSONNull.INSTANCE :JSONPrimitive.of(bool));
    }

    public void add(Character character){
        elements.add(character == null? JSONNull.INSTANCE :JSONPrimitive.of(character));
    }

    public void add(Number number){
        elements.add(number == null? JSONNull.INSTANCE :JSONPrimitive.of(number));
    }

    public void add(String string){
        elements.add(string == null? JSONNull.INSTANCE :JSONPrimitive.of(string));
    }

    public void add(JSONElement element){
        elements.add(element == null? JSONNull.INSTANCE :element);
    }

    public void addAll(JSONArray array){
        elements.addAll(array.elements);
    }

    public JSONElement set(int index, JSONElement element) {
        return elements.set(index, element);
    }

    public boolean remove(JSONElement element) {
        return elements.remove(element);
    }

    public JSONElement remove(int index) {
        return elements.remove(index);
    }

    public boolean contains(JSONElement element) {
        return elements.contains(element);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<JSONElement> iterator() {
        return elements.iterator();
    }

    public JSONElement get(int i) {
        return elements.get(i);
    }


    @Override
    public boolean getAsBoolean() {
        if (elements.size() == 1) {
            return elements.get(0).getAsBoolean();
        }
        throw new IllegalStateException();
    }

    @Override
    public Number getAsNumber() {
        if (elements.size() == 1) {
            return elements.get(0).getAsNumber();
        }
        throw new IllegalStateException();
    }

    @Override
    public String getAsString() {
        if (elements.size() == 1) {
            return elements.get(0).getAsString();
        }
        throw new IllegalStateException();
    }

    @Override
    public double getAsDouble() {
        if (elements.size() == 1) {
            return elements.get(0).getAsDouble();
        }
        throw new IllegalStateException();
    }

    @Override
    public float getAsFloat() {
        if (elements.size() == 1) {
            return elements.get(0).getAsFloat();
        }
        throw new IllegalStateException();
    }

    @Override
    public long getAsLong() {
        if (elements.size() == 1) {
            return elements.get(0).getAsLong();
        }
        throw new IllegalStateException();
    }

    @Override
    public int getAsInt() {
        if (elements.size() == 1) {
            return elements.get(0).getAsInt();
        }
        throw new IllegalStateException();
    }

    @Override
    public short getAsShort() {
        if (elements.size() == 1) {
            return elements.get(0).getAsShort();
        }
        throw new IllegalStateException();
    }

    @Override
    public byte getAsByte() {
        if (elements.size() == 1) {
            return elements.get(0).getAsByte();
        }
        throw new IllegalStateException();
    }

    @Override
    public char getAsCharacter() {
        if (elements.size() == 1) {
            return elements.get(0).getAsCharacter();
        }
        throw new IllegalStateException();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        if (elements.size() == 1) {
            return elements.get(0).getAsBigDecimal();
        }
        throw new IllegalStateException();
    }

    @Override
    public BigInteger getAsBigInteger() {
        if (elements.size() == 1) {
            return elements.get(0).getAsBigInteger();
        }
        throw new IllegalStateException();
    }

    @Override
    public String stringify() {
        return StringifyUtils.Stringify(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JSONArray)) return false;
        JSONArray that = (JSONArray) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return this.stringify();
    }

    @Override
    public JSONArray deepCopy() {
        if (!elements.isEmpty()) {
            JSONArray result = new JSONArray(elements.size());
            for (JSONElement element : elements) {
                result.add(element.deepCopy());
            }
            return result;
        }
        return new JSONArray();
    }
}
