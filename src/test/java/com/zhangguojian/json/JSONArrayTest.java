package com.zhangguojian.json;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

public class JSONArrayTest {
    @Test
    public void testToString(){
        assertThat(JSONArray.fromObject(Arrays.asList(1,2,3,4)).toString()).isEqualTo("[1,2,3,4]");
        assertThat(JSONArray.fromObject(Collections.emptyList()).toString()).isEqualTo("[]");

        Queue<Integer> q=  new LinkedBlockingQueue();
        q.add(1);
        q.add(2);
        q.add(3);
        q.add(4);
        assertThat(JSONArray.fromObject(q).toString()).isEqualTo("[1,2,3,4]");

    }
}