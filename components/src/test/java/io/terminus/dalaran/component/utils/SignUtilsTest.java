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
        body.put("id", "1");
        body.put("name", "momo");
        body.put("code", "mmmmm");
        body.put("description", "Hello");


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
