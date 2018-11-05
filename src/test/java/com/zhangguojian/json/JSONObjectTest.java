package com.zhangguojian.json;

import com.zhangguojian.json.exception.CastException;
import com.zhangguojian.json.exception.NullException;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

public class JSONObjectTest {
    @Test
    public void testFromObject() throws Exception {
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


}