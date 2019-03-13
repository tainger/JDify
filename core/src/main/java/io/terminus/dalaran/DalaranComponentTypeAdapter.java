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
                if ("trigger".equals(type)) {
                    componentContainer = DalaranComponentLoader.getTriggerContainer(componentType);
                } else {
                    componentContainer = DalaranComponentLoader.getProcessorContainer(componentType);
                }
            }
            if (componentContainer == null) {

                // TODO throw
                in.endObject();
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


        // TODO 理论上可以搞个基类, 但是担心 trigger 和 processor 后期分歧, 先瞎写一下
        if ("trigger".equals(type)) {
            DalaranTriggerConfig trigger = new DalaranTriggerConfig();
            trigger.setType(componentType);
            trigger.setConfig(config);
            return trigger;
        } else {
            DalaranProcessorConfig processor = new DalaranProcessorConfig();
            processor.setType(componentType);
            processor.setConfig(config);
            return processor;
        }
    }
}
