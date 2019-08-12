package io.terminus.dalaran.model.flow;

import lombok.Data;

@Data
public class FlowValidateMessage {

    public FlowValidateMessage(ValidateMessageLevel level, String key, String text) {
        this.level = level;
        this.key = key;
        this.text = text;
    }

    private ValidateMessageLevel level;

    private String key;

    // TODO 其实展示文字让前端处理更合适, 因为考虑到 I18N 的问题, 不过 组件化需要自定义展示内容, 注册是个问题, 先后端给吧
    private String text;
}
