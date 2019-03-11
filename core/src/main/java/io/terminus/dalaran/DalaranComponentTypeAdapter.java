package io.terminus.dalaran;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import static com.google.gson.stream.JsonToken.BEGIN_OBJECT;
import static com.google.gson.stream.JsonToken.END_OBJECT;

public class DalaranComponentTypeAdapter extends TypeAdapter {
    private final Gson gson;

    private final String type;

    public DalaranComponentTypeAdapter(Gson gson, String type) {
        this.gson = gson;
        this.type = type;
    }

    public void write(JsonWriter out, Object value) throws IOException {
    }

    public Object read(JsonReader in) throws IOException {
        JsonToken token = in.peek();
        String componentType = null;
        DalaranComponentContainer componentContainer = null;
        Object config = null;

        // TODO 临时瞎写一下
        if (token == BEGIN_OBJECT) {
            in.beginObject();
            if ("type".equals(in.nextName())) {
                componentType = in.nextString();
                if ("listener".equals(type)) {
                    componentContainer = DalaranComponentLoader.getListenerContainer(componentType);
                } else {
                    componentContainer = DalaranComponentLoader.getEndpointContainer(componentType);
                }
            }
            if (componentContainer == null) {

                // TODO throw
                return null;
            }
            if (in.peek() != END_OBJECT) {
                if ("config".equals(in.nextName())) {
                    TypeToken typeToken = TypeToken.get(componentContainer.getConfigClass());
                    TypeAdapter typeAdapter = gson.getAdapter(typeToken);
                    config = typeAdapter.read(in);
                }
            }
            in.endObject();
        }


        // TODO 理论上可以搞个基类, 但是担心 listener 和 endpoint 后期分歧, 先瞎写一下
        if ("listener".equals(type)) {
            Pipeline.Listener listener = new Pipeline.Listener();
            listener.setType(componentType);
            listener.setConfig(config);
            return listener;
        } else {
            Pipeline.Endpoint endpoint = new Pipeline.Endpoint();
            endpoint.setType(componentType);
            endpoint.setConfig(config);
            return endpoint;
        }
    }
}
