package com.zhangguojian.json;

public final class JSONNull implements JSONElement {

    public static final JSONNull INSTANCE = new JSONNull();

    private JSONNull(){}

    @Override
    public boolean isJSONNull() {
        return true;
    }

    @Override
    public String stringify() {
        return "null";
    }

    @Override
    public JSONNull deepCopy() {
        return INSTANCE;
    }

    @Override
    public int hashCode() {
        return 445884362;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return this.stringify();
    }
}
