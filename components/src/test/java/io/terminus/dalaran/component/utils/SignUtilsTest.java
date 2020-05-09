package io.terminus.dalaran.component.utils;

import io.terminus.dalaran.ComponentConstants;
import io.terminus.dalaran.component.trigger.rest.model.EncryptionAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAlgorithm;
import io.terminus.dalaran.component.trigger.rest.model.SignAuthenticatorInfo;
import io.terminus.dalaran.component.trigger.rest.processor.SignProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignUtilsTest {

    @Test
    public void sign() {
        Map<String, Object> body = new HashMap<>();
        body.put("merchantCode", "merchantCode0");
        body.put("merchantExts", "merchantExts0");
        body.put("merchantId", "merchantId0");
        body.put("userId", "userId0");


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
}
