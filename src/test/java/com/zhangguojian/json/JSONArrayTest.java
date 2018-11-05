package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import org.junit.Assert;
import org.junit.Test;


import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JSONArrayTest {

    @Test
    public void testAsList() {
        JSONArray array = JSONArray.asList(1, 2, 3, 4);
        assertThat(array.size()).isEqualTo(4);
        assertThat(array).isEqualTo(Arrays.asList(1, 2, 3, 4));
    }

    @Test
    public void testFromObject() throws CastException {
        Object NULL = null;
        assertThatThrownBy(() -> JSONArray.fromObject(NULL))
                .isInstanceOf(CastException.class);

        //not array Object
        assertThatThrownBy(() -> JSONArray.fromObject(Double.valueOf("1")))
                .isInstanceOf(CastException.class);

        //not array Object
        Integer array[] = null;
        assertThat(JSONArray.fromObject(array))
                .isEqualTo(JSONArray.EMPTY);

        int a[] = {1, 2, 3, 4};
        assertThat(JSONArray.fromObject(a))
                .isEqualTo(JSONArray.asList(1, 2, 3, 4));

        LinkedList<Short> shortList = new LinkedList<>();
        shortList.add((short) 1);
        shortList.add((short) 2);
        shortList.add((short) 3);
        shortList.add((short) 4);
        assertThat(JSONArray.fromObject(shortList))
                .isEqualTo(JSONArray.asList((short) 1, (short) 2, (short) 3, (short) 4));

        List<int[]> mulArray =new ArrayList<>();
        int row1[] = {1,2,3};
        mulArray.add(row1);
        int row2[] = {4,5,6};
        mulArray.add(row2);

        ArrayList<int[]> mulResult = JSONArray.fromObject(mulArray);
        assertThat(mulResult).isEqualTo(JSONArray.asList(row1,row2));

        List<List<Integer>> nestArray = new LinkedList<>();
        nestArray.add(Arrays.asList(1,2));
        nestArray.add(Arrays.asList(2,4));

        JSONArray<JSONArray<Integer>> resultArray = new JSONArray<>();
        resultArray.add(JSONArray.asList(1,2));
        resultArray.add(JSONArray.asList(2,4));

        Assert.assertEquals(resultArray, JSONArray.fromObject(nestArray));
    }

    @Test
    public void testToString() {
        assertThat(JSONArray.fromObject(Arrays.asList(1, 2, 3, 4)).toString()).isEqualTo("[1,2,3,4]");
        assertThat(JSONArray.fromObject(Collections.emptyList()).toString()).isEqualTo("[]");

        Queue<Integer> q = new LinkedBlockingQueue<>();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(4);
        assertThat(JSONArray.fromObject(q).toString()).isEqualTo("[1,2,3,4]");

    }
}