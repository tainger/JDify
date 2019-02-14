package io.terminus.dalaran.example;

import com.google.gson.Gson;
import io.terminus.dalaran.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Test {

    public static void main(String[] args) {
        Gson gson = new Gson();
        Gson newGson = gson.newBuilder().
                registerTypeAdapter(DalaranListener.class, new DalaranComponentTypeAdapter(gson)).
                registerTypeAdapter(DalaranEndpoint.class, new DalaranComponentTypeAdapter(gson)).create();
        InputStream in = Test.class.getResourceAsStream("/example-flow.json");

        Reader reader = new InputStreamReader(in);
        MessageFlow messageFlow = newGson.fromJson(reader, MessageFlow.class);
        System.out.println(messageFlow);

    }

}
