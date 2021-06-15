package io.terminus.dalaran.open.common.service;

public interface DalaranOpenService<Request, Response> {

    Response execute(Request request);
}
