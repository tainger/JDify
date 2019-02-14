package io.terminus.dalaran;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;

public class DalaranComponentTypeAdapter extends TypeAdapter {
    private final Gson gson;

    public DalaranComponentTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    public void write(JsonWriter out, Object value) throws IOException {
    }

    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        if(token == BEGIN_OBJECT) {
            in.beginObject();
            if("type".equals(in.nextName())){

            }
            in.endObject();
        }
        return gson.fromJson(in, Object.class);
    }
}
