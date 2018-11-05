package com.zhangguojian.json;

import com.zhangguojian.json.bean.Person;
import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.JSONException;
import com.zhangguojian.json.exception.NullException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class JSONObjectTest {
    @Test
    public void testFromObjectWithStringParse() throws Exception {
        String content = new String(Files.readAllBytes(Paths.get("src/test/data/juejin-me.json")));
        JSONObject jsonObject = JSONObject.fromObject(content);

        JSONObject d =jsonObject.getJSONObject("d");
        assertNotNull(d);

        assertThat(d.getString("role")).isEqualTo("guest");

        assertThat(d.getString("username")).isEqualTo("挖坑英雄小王");

        assertThatThrownBy(() -> d.getString("userName"))
                .isInstanceOf(NullException.class);

        assertThatThrownBy(() -> d.getInt("username"))
                .isInstanceOf(CastException.class);

        assertThat(d.getBoolean("isAuthor")).isEqualTo(false);

        assertThat(d.getInt("totalViewsCount")).isEqualTo(26);
        assertThat(d.getShort("totalViewsCount")).isEqualTo(Integer.valueOf(26).shortValue());

        assertThat(d.getJSONArray("tags_id")).isEqualTo(Arrays.asList(132, 133,134,135));

        assertThat(d.getFloat("float")).isEqualTo(3.14E10f);
        assertThat(d.getDouble("float")).isEqualTo(3.14E10);
        assertThat(d.getBigDecimal("float")).isEqualTo(new BigDecimal(3.14E10));
        assertThat(d.getBigDecimal("big_float")).isEqualTo(new BigDecimal("3.14E310"));


        assertThat(jsonObject.getByte("s")).isEqualTo(Byte.valueOf("1"));
        assertThat(d.getLong("int")).isEqualTo(Long.valueOf("314"));
        assertThat(d.getBigInt("big_int")).isEqualTo(new BigInteger("3140000000000000000"));
        assertThat(d.getBigInt("very_big_int")).isEqualTo(new BigInteger("314000000000000000000000000000000000"));
    }

    @Test
    public void testMapToJSONObject() throws JSONException {
        Map<String,Object> map1 = new HashMap<>();
        map1.put("name","张三");
        map1.put("age",26);
        map1.put("firstName",null);

        JSONObject<String, Object> j1 = JSONObject.fromObject(map1);
        assertThat(j1.get("age")).isEqualTo(26);
        assertThat(j1.get("name")).isEqualTo("张三");
        assertThat(j1.get("name")).isEqualTo("张三");
        assertThat(j1.get("firstName")).isEqualTo(null);


        Map<String,Object> map2 = new HashMap<>();
        map2.put(null,null);
        assertThatThrownBy(() -> JSONObject.fromObject(map2))
                .isInstanceOf(NullException.class);

        Map nullMap = null ;
        assertThat(JSONObject.fromObject(nullMap)).isEqualTo(JSONObject.EMPTY);
    }

    @Test
    public void testObjectToJSONObject() throws JSONException {
        Person person = new Person();
        person.setName("张三");
        person.setArray1(Arrays.asList(1,2,3));

        JSONObject result = JSONObject.fromObject(person);
        assertThat(result.get("name")).isEqualTo("张三");
        //Object to Map 泛型也没用啊，value 不能确认类型的!!!
        JSONArray<Integer> resultArray = (JSONArray<Integer>) result.get("array1");
        assertThat(resultArray).isEqualTo(JSONArray.asList(1,2,3));
        assertThat(result.get("a")).isEqualTo(null);

    }

}