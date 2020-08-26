package io.terminus.dalaran.component.utils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.trigger.rest.model.EncryptionAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAuthenticatorInfo;
import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignUtilsTest {

    @Test
    public void md5() {

        String body = "{\n" +
                "    \"apiKey\": \"TEST\",\n" +
                "    \"timestamp\": \"20181106121212\",\n" +
                "    \"docNo\": \"S03M0116000075\",\n" +
                "    \"storeCode\": \"S1000168\",\n" +
                "    \"tillId\": \"01\",\n" +
                "    \"txDate\": \"2018-09-11\",\n" +
                "    \"txTime\": \"151322\",\n" +
                "    \"netAmount\": 1500\n" +
                "}";
        JsonObject jsonObject;
        Gson gson = new Gson();
        jsonObject = gson.toJsonTree(JSON.parseObject((String)body)).getAsJsonObject();
        String ssss = SignUtils.getJsonValue(jsonObject);
//        if (body instanceof byte[]) {
//            jsonObject = gson.toJsonTree(JSON.parse((byte[])body)).getAsJsonObject();
//        } else if (body instanceof String) {
//        } else {
//            jsonObject = gson.toJsonTree(body).getAsJsonObject();
//        }

        String s = DigestUtils.md5Hex("S03M0116000075150.0000S100016801201811061212122018-09-11151322" + "amhIEWlsG0rVhg4ffRyfssF1Aff03cu6C6ERkFv2nDlKv811jGfWqFKrE2am0Ue");

        Assert.assertNotNull(s);
    }

    @Test
    public void sign() {
        Map<String, Object> body = new HashMap<>();
        body.put("merchantCode", "code");
        body.put("merchantExts", "false");
        body.put("merchantId", "001");
        body.put("userId", "user");


        String DEMO_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwTAewIdIC+Mb+PRP2Hu1\n"
                +
                "KhPhu65dkxPxYQMPZmBvHYXLgzTbR+6XtYTPNhC7B3jXGCW2WbOpppGKrRo32W7/\n"
                +
                "kg3SpJ9HLF5u9iFtawKsj7Cr+B9BKU/fi69YLNWVmTIKAzD0ELuKaeeZdH2fYGid\n"
                +
                "T/hW32YUgnBSM1+z4GRYY2aux3hAAaS4t3hAyPWkCdDbBjhvKOVzQ57KO4Ew+q/X\n"
                +
                "8wKQB5JrXJkaYomj5zKlQK11U1hdjob9q9i920OEAFjoOlEOrST4Yz7oNHpXv/Sl\n"+
                "MLtJY1XdMizOdcIyN3yMZEdblkyNz2DLBZBAZmX24O7nYG9hwLE16s2dFv2c8Whx\n" + "MQIDAQAB";

        String DEMO_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDBMB7Ah0gL4xv4\n"
                +
                "9E/Ye7UqE+G7rl2TE/FhAw9mYG8dhcuDNNtH7pe1hM82ELsHeNcYJbZZs6mmkYqt\n"
                +
                "GjfZbv+SDdKkn0csXm72IW1rAqyPsKv4H0EpT9+Lr1gs1ZWZMgoDMPQQu4pp55l0\n"
                +
                "fZ9gaJ1P+FbfZhSCcFIzX7PgZFhjZq7HeEABpLi3eEDI9aQJ0NsGOG8o5XNDnso7\n"
                +
                "gTD6r9fzApAHkmtcmRpiiaPnMqVArXVTWF2Ohv2r2L3bQ4QAWOg6UQ6tJPhjPug0\n"
                +
                "ele/9KUwu0ljVd0yLM51wjI3fIxkR1uWTI3PYMsFkEBmZfbg7udgb2HAsTXqzZ0W\n"
                +
                "/ZzxaHExAgMBAAECggEAKWnIXdtiPXIdD/xHlY/HYVLLxDD3zEytJO/EAF1R1JFW\n"
                +
                "9DSDgWMLUJU82kkx3sMv0PRcp9QU3Sk8AimpqUnbLZMbAcFHP6KqxMabryHungrw\n"
                +
                "gosoq9N8h3Q6QbozjJimftKir2eUn2lPgyJe5QroFjmVXeSDJwcvB+ZPKB46uGvt\n"
                +
                "yGByKjQApVz2INIFLDL3fXgcooXhOh8b+ozhyvt9nMNF4C2nsiUe7QWPSThplf9o\n"
                +
                "I4LyWoTxs0U2g/0cnpvxPZZ44mV2dNN20aCRQ6rxyh1sDsEmZPrtforZxt2hJJTL\n"
                +
                "XMrlCy2aSX4t7lKmBdImKyWXiXglVNtsIXAgJcglcQKBgQD383Gl94NzTQWBWMv4\n"
                +
                "NRznZulw1tdp+x5nXHSZDwQfaLdMcfkEn6UdsBde8wjvTSy1ccqmXrIwoUTY2YTY\n"
                +
                "BGbxuUrBu5cNK1vVp8Q3sfBtRKN3SFOKMOlyAK+48psB3/1S5A5LXDPHnD2Erx/y\n"
                +
                "2/fKcWPNRcBZcLA0oYJJI22u1QKBgQDHdZPLLBxfSMf/0fm2EU56tjiGHfeXZWU3\n"
                +
                "Kb5S709cenYS7G9wQOtwkViyyAju2c9klzTeLF0Y1zYu202l9TFcGZDQrB9ZzU7v\n"
                +
                "QIlkb/lLkHzNtfbzdwaQau86BqCiZuFQQucIvV0hBTQO2nxm4hEwVbGbF/u5ovkS\n"
                +
                "334IIW4+7QKBgFdvCxZhT4MrF9PiwhpGcGjRC2R0/gUrPi9olN0cRHHm9SwJzKvq\n"
                +
                "t8jePX+H6BCqgCeDdthv8Bnh5GsrWQLrmnmGw2vJgJxuO7X9sN2K66M8XwBDCuJi\n"
                +
                "GQ/QWia3th1VJPlD3h6I5uFuoOrW7kcvdhN3JKBQBmIDsL7+bPjpe1X5AoGAQPZx\n"

                +
                "gW3He903joF0BDGUzvDEbiIafsu+cH3/CAH7ZhJYnIc3MGdkh1A/hFMUXb7BBnEc\n"
                +
                "/fOTCsElW5N/sbqvLeEBwoUc3OG6gMATP4wctNjFXTaTzO4KTEUXz11TOjGp1rtD\n"
                +
                "hhwu1c9dCi1s8RFWKdjvNKVWDjsfRl9WLA2/W20CgYEAo48igN2rE+JYZXkj1c8Q\n"
                +
                "cON8riVOMmAjB6HbPQSgEn3bHhJsfEF7Mhh9sGok2oUyroM7BSpbM7XDdrkZwvP3\n"
                +
                "3UPrJ2qSVcoYfc9NbwWQxBC2hTQR2kAD1kFpDnn0wv/i5nwwQ+5hkX52j1iW4Oem\n" + "dsXylCRV8E7d60xZFSCyIEM=";

        SignAuthenticatorInfo authenticatorInfo = new SignAuthenticatorInfo();
        authenticatorInfo.setDalaranPrivateKey("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCwp58vM0dbTtdW\n" +
                "ghr15vLeCaUoDkrAwQ+JgmG0wrNGV40RvE9IkWTZr9Wyhf+o+OgRihBadnk/kYOU\n" +
                "zCW4KhcZ0pBo5IEPYksCCBn07grFhQufDcp+5NqvLeGiY/EyzjGLlIUo40O1dNbl\n" +
                "/1+qBd5cJKk2oemv4vMw7demdeHq0nVwme/00cU97lBsRO8fL/RB8Myu405md3me\n" +
                "KVFqfFoflKXRKDJT+pJeRJOHouCOK+ffVRXeMsplJ4YG2wrT0HyLmAErLYTXgxy6\n" +
                "o+Kbts2AsKMJVVC0m3KA5lvEOhtfN0qVmoTm8imO0DJFfBHfr7QZQsmZ72gnNNw7\n" +
                "d2WwUkUTAgMBAAECggEAHliNFdSfO2Ytw6SR8Q/9RHsvrdPWr4n6m0qUcjLIDRtZ\n" +
                "FmTc0cB4LJTyBrrSEjq/0tN+v6t+ZJcgnVz8VciVM1BikkI+kV/3NqAhpVuG6itS\n" +
                "tb3uvSGfF1ywV+8HLg0RocKfpUh1ByTNDbFyw4hCjF2X29SwihBYal1RID0709SO\n" +
                "GRpmGne0u6KostVVQLAklmoCxTUbLzpOAeesNlkRZaGHlD9ycJXAN4rGfp2CUgTx\n" +
                "p7C68d5U8xLflv+3Ab6rZHExnO5AAmRWY4pHqWTllXMFUJNEiD+/hWzJpUCy0jaZ\n" +
                "CkpI02Wlz6DA1vyWtNAmz2f64d5vX9m4yjZHOeGbQQKBgQDnbmWvCjZ5kV5+Iv/S\n" +
                "96Sf5UbgGEgRGQv0qNIM0RCk6KBIN+3XeQt4MnervV4+TNiPqTYrX/dUCnZ3lOcN\n" +
                "MgDhksKmtHuCHlglk84fjqAbmB1/vD0it2NsNEC3kJZyVYM29vMNahMUR/RGXesX\n" +
                "O+IorOcj6P+kjLujZ9WCb9Ti4wKBgQDDaJDGg2tstOXzXF7Ns/8W6FDCQ+3GRuH2\n" +
                "poiQdRl88QhZSHrUAoqqt/0oVp2RqojhaAw1s4zCf85rrfSU88Vkau0jNEEFHnyV\n" +
                "AuQ+CHuLbPMgTwlrWdpMT15ltoT14FS5gZAn/4Vqv3S4M3u/WWFQT5jyMJxrL6oY\n" +
                "20tDb5Y8EQKBgEW1PvOGQ/pCCxA7QostBG0VBlAhgDOGM8+xufMaVcUWPJEEJ6Or\n" +
                "VgUSTnFwTJ6/TsuW0DYoZbrum9hRBXc4BIPtTKwh2MHMmghXiNKAh0FIhDohXNjE\n" +
                "HDJPWt7vsiEmGKvJnlQAC1ckhTNcvp707p4J1mU+nSHXMf16HRl4zcq5AoGAWD0t\n" +
                "sxbBObNuazh47vaq6BM79J1sD68Odqy42lKmkc7d8ENUg8wqBfFTdhW2zYJukSrE\n" +
                "6LMNiv7fnaxN171Ek5XdQ5pjCga8RyWpH4IBF/K4zwwhusI2W2Mzw0ZqQAVu/N3O\n" +
                "1OEDxDJ0aIGHDd80nGl1LVhaKeS6MMNF90u6UaECgYAy37Wcj0Ha70k7qEayokgv\n" +
                "+ZMqMOxANYH+vxxH25IOTCvJ1msIGh5LpW0jpCrE36y2LmRrLR9mYvOwXWsE80Nf\n" +
                "aRWm1jAgEAu1V/ojOAIZJd+X5S/qk6cPRCj+15VjGEvYbkJn/8lIJx49v7Gb2+i1\n" +
                "Bs0gs+tKXUqnMN0CuTSUiQ==\n");
        authenticatorInfo.setPartnerPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsKefLzNHW07XVoIa9eby\n" +
                "3gmlKA5KwMEPiYJhtMKzRleNEbxPSJFk2a/VsoX/qPjoEYoQWnZ5P5GDlMwluCoX\n" +
                "GdKQaOSBD2JLAggZ9O4KxYULnw3KfuTary3homPxMs4xi5SFKONDtXTW5f9fqgXe\n" +
                "XCSpNqHpr+LzMO3XpnXh6tJ1cJnv9NHFPe5QbETvHy/0QfDMruNOZnd5nilRanxa\n" +
                "H5Sl0SgyU/qSXkSTh6Lgjivn31UV3jLKZSeGBtsK09B8i5gBKy2E14McuqPim7bN\n" +
                "gLCjCVVQtJtygOZbxDobXzdKlZqE5vIpjtAyRXwR36+0GULJme9oJzTcO3dlsFJF\n" +
                "EwIDAQAB\n");


        authenticatorInfo.setDalaranPrivateKey("-----BEGIN PRIVATE KEY-----\n" +
                "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDoY7ZtzxweNuXE\n" +
                "ucWwOUikPiuglzQFS5mbng1ZlksA1hkQ62srh1BKxAWPDooow5HpsydYlPB3CG7G\n" +
                "9vcdyHJGoG2BnLIeCGTREVEMtMcdZpG4XVMF+ZkjE220UNbHEKL6ZcA7KpwETPNe\n" +
                "KGEF6alkrEelf1vEFY3C/Z4JmR+JtSWtr6pY5aCIRCTEwHdCboMuouhOCHY26Isq\n" +
                "9J9WjC1AX40UHPCnTv5XULgM1aerePds5sTvAJ/vxFUiLuHl9otZIoqoLubxQQwU\n" +
                "O7ff5a3Pa3CYtxFYLzB36w1KyS4iMWzgwsHf0IzmiVX8OGSKKEK9f3wGo4w50Xda\n" +
                "LryEs2YlAgMBAAECggEAVc+8JYA82ctbvPD8Vr5QXJW4dmxfATWRWy1ZsKMWvxBP\n" +
                "4TeNM12cAH+xrcmoqrTMxIcrGEKnKM0sWrwHNDA4QmwrA5lhDEX9NFY4n4CBphGh\n" +
                "7XpIlgQ8z/70m4jeRlOWWvAHs9FgGxZvJ22xpgVKT6puKmaE9DshRcdw/ApW4voU\n" +
                "1ImbA8wdrnAsUSyVazGZYx+WOOH4I2rd/zwNZ9ZRP1hmaZNWC+01dXfKPTzEfEpL\n" +
                "UFcjQZtmmwInns8O5TioOMGNXeJg1bI3UEg44uqpMIh+UQTI/hQjNOkRAvnE44pq\n" +
                "LqsF6iA0Mwvnr1kIVMAJ0P8htq5TOZ98MrksBIrOAQKBgQD9Uzz7UqUER3NeTMEJ\n" +
                "EQqQJ31PfaaoXyIhyXTd4f5sHwfrooWp5cp9pckJ49rGEc6MSFF7K1Ub7anERb8B\n" +
                "RCmZtU8pthadS2quKSk8F++HiuCX6hBYs+qgLF+O4Fiv9fqL8cfmw9U3VRvTb/n8\n" +
                "SECXDwX6mEjR0R9zLSPteoXhMQKBgQDq1+IkxQNoml/v1m7miVRUTN6/vOpiDee9\n" +
                "epymP58tI/LHFRLsCtsQBnDrH65xoEeTvIyeNx3uO7o7NQ5+rAuYgviiidXz6zM8\n" +
                "S2JGhE5fhvUmxykcpExV+aoRCJR2E635dtZq1oWiYEH7cmyzxOfFOnCBn5uIepuc\n" +
                "6AN1nK53NQKBgQCgEa+v0rnoqUlR5cf39aeqDPnWd7wOGgbUOJq69WkxYq47i3dQ\n" +
                "Mp4vpMkSkcKUc34DEFNEM85Ulmk2VyfpIevzbyh1X9SMUbI4GFQw36MAD5X5B/KK\n" +
                "Si1QRpmfC02e6hwFv6Ijw8x/aSzq/o+EhRcjHGAXx5AD3FM2EOjpzwi3kQKBgQCy\n" +
                "21J/gJC1BQXWCvGRoLvaLGVlkELOBRse4xgVQdVAMuW/G9y6axYmIVG0sP9RyKla\n" +
                "6joKcZ3ZCCIw35q3fN6j+/PTDrklOVdfL2acoD10YbuqGfrEtpjwzeHpcShouVpB\n" +
                "6XEqE1HZtgfqsl35mBiQzI5NGrsA+ag0mzuvQnJZlQKBgQCJGvdsX9t9lTXHKTN1\n" +
                "kZnhOKHqv0ZCX/DgN7SJpZ5UaKx0wZi+pwsJ7B0K3jrDSE6GxcRZF6vwmo10eqgu\n" +
                "zjbW6TmDCJgxWbVQHT5JwXkrlzDYK2aap5fuQZI9juoDo9nCe5sZsJV4lgSzrSR8\n" +
                "hsgjsPCwXJmRNyz690C+E7hHsA==\n" +
                "-----END PRIVATE KEY-----\n");

        authenticatorInfo.setPartnerPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6GO2bc8cHjblxLnFsDlI\n" +
                "pD4roJc0BUuZm54NWZZLANYZEOtrK4dQSsQFjw6KKMOR6bMnWJTwdwhuxvb3Hchy\n" +
                "RqBtgZyyHghk0RFRDLTHHWaRuF1TBfmZIxNttFDWxxCi+mXAOyqcBEzzXihhBemp\n" +
                "ZKxHpX9bxBWNwv2eCZkfibUlra+qWOWgiEQkxMB3Qm6DLqLoTgh2NuiLKvSfVowt\n" +
                "QF+NFBzwp07+V1C4DNWnq3j3bObE7wCf78RVIi7h5faLWSKKqC7m8UEMFDu33+Wt\n" +
                "z2twmLcRWC8wd+sNSskuIjFs4MLB39CM5olV/DhkiihCvX98BqOMOdF3Wi68hLNm\n" +
                "JQIDAQAB");

        authenticatorInfo.setPartnerPublicKey(authenticatorInfo.getPartnerPublicKey().replace("\n", ""));
        authenticatorInfo.setDalaranPrivateKey(authenticatorInfo.getDalaranPrivateKey().replace("\n", ""));

        String platformDK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmB2d4NasdZEQ//lg7/h7ZFwuiyBn86a9SoE0gfquIkdFmEv2+8dj7AwxlsXidzxI4Ta9zkiZFHqgC3bmtlRuF6BgtS1+ubs7ksd3YG+kyk+H6dAb6LnhGf7rv7PTUxSb8WN8ytZbl/5li2NYJva2igiWhOQ9VITPFobYcbZLiaaRfRRUmkPGgbuP2ScgrKQJB6cy34/wpc0bYMoqLETTCKctZRnfX1G1d1E8meCKdWWHmQqsRFkA8+OxzBKMeKhrJYT3fa2lDdA9yQDQsWj+jbmMd42NE6VnOQWpI/afsCNalFBVOM/RTYY2yLjhmX20P0ytVfs4Ep1h2SM4g9PP8wIDAQAB";

        authenticatorInfo.setDalaranPrivateKey(DEMO_PRIVATE_KEY.replace("\n", ""));
        authenticatorInfo.setPartnerPublicKey(DEMO_PUBLIC_KEY.replace("\n", ""));

        authenticatorInfo.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);
        authenticatorInfo.setSignAlgorithm(SignAlgorithm.SHA256withRSA);

        SignProcessor processor = new SignProcessor(null, authenticatorInfo);
        String in = processor.buildSignBody(body);
        String signCode = processor.sign(in, authenticatorInfo);

        body.put(ComponentConstants.SIGNATURE, signCode);

        boolean result = processor.verify(in, signCode, authenticatorInfo);
        Assert.assertTrue(result);
    }

    @Test
    public void aes() {
        String ssss = "yins";

        String da = "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";

        String d = "GMbynK0Q8WXzm1IjL80ntQ==";

        String dd = "qo/4D91HL1q7jvASzpJxCg==";
        String data = "9GdXIColbGHM8QKNuE8FQQ==";
//        String s = AESUtils.encrypt(ssss, "89622015104709087435617163207900");

        String in = "{\"data\":{\"totxdate\":\"2020-07-20 10:10:10\",\"identity\":\"CRM\",\"openid\":\"\",\"action\":\"\",\"remark\":\"\",\"userId\":\"99899\",\"frmtxdate\":\"2020-07-01 10:10:10\",\"vipcode\":\"\"}}";

        /**
         * 8962201510470905
         */
        String s = AESUtils.encrypt("17150137665", "8962201510470905");

        String soo = AESUtils.encryptNoPadding( "17150137665", "8962201510470905");

        String somoo = AESUtils.decryptNoPadding("eZkmKwvwdIjqm0WftKlUhA==", "8962201510470905");

        String ss = AESUtils.decrypt("eZkmKwvwdIjqm0WftKlUhA==", "8962201510470905");


        Assert.assertTrue(false);
    }
}
