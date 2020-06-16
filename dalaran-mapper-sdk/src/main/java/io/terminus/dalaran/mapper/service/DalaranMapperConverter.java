package io.terminus.dalaran.mapper.service;

import io.terminus.dalaran.mapper.model.DalaranMapperConfig;

public interface DalaranMapperConverter {

    <T> T convert(Object source, DalaranMapperConfig mapperConfig, Class<T> outClass);
}
