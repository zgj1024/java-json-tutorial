package com.zhangguojian.json;

import com.zhangguojian.json.bean.Hero;
import com.zhangguojian.json.bean.Person;
import com.zhangguojian.json.exception.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;


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
    public void testDeserializer() throws JSONException, IllegalAccessException, InvocationTargetException, InstantiationException, ParseException, ClassNotFoundException {

        String jinyongJSON = "{\"name\":\"金庸\",\"birthDate\":-1445760000000,\"deathDate\":\"2018-10-30 00:00:00\"}";

        Hero hero = JSON.parse(jinyongJSON,Hero.class);
        Assert.assertEquals("金庸",hero.getName());
        Assert.assertEquals(Date.valueOf("2018-10-30"),hero.getDeathDate());
        Assert.assertEquals(new Timestamp(-1445760000000L),hero.getBirthDate());

    }
}
