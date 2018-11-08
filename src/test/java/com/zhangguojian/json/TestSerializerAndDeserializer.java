package com.zhangguojian.json;

import com.zhangguojian.json.bean.Hero;
import com.zhangguojian.json.bean.Person;
import com.zhangguojian.json.exception.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;


public class TestSerializerAndDeserializer {

    @Test
    public void testSerializer(){
        Hero hero = new Hero();
        hero.setName("金庸");
        hero.setBirthDate(Timestamp.valueOf("1924-03-10 00:00:00"));
        hero.setDeathDate(Date.valueOf("2018-10-30"));

        Assert.assertEquals("{\"name\":\"金庸\",\"birthDate\":-1445760000000,\"deathDate\":\"2018-10-30 00:00:00\"}",JSON.stringify(hero));
    }

    @Test
    public void testDeserializer() throws JSONException {

        String jinyongJSON = "{\"name\":\"金庸\",\"birthDate\":-1445760000000,\"deathDate\":\"2018-10-30 00:00:00\"}";

        JSONObject jsonObject = JSON.parse(jinyongJSON).objectValue();
        Assert.assertEquals("金庸",jsonObject.getString("name"));
        Assert.assertEquals(new Timestamp(-1445760000000L),jsonObject.get("birthDate"));
        Assert.assertEquals(Date.valueOf("2018-10-30"),jsonObject.get("birthDate"));

    }
}
