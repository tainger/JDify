package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.repository.FlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowManagementServiceImpl {

    @Autowired
    private FlowRepository flowRepository;
}
