package io.terminus.dalaran.core.component;

public interface DalaranDynamicBodySerializeType<T> {

    BodySerializeType customBodySerializeType(T config);
}
