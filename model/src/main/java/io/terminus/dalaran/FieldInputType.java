package io.terminus.dalaran;

public enum FieldInputType {
    Hidden, Auto,
    String, Integer, Password, Select, Radio, Switch, CheckBox, Tag,
    FileUpload, FileDownload,
    Script, SQL, Expression,
    Connector, Model, SubFlow, Service, ServiceOperation,
    TriggerSelector, ProcessorSelector, ConnectorSelector, ModelSelector, ServiceSelector, ModuleSelector,
    Routes, Pipeline, Branches, ErrorCatch, TrantorModule, TrantorIntegration, TrantorIntegrationPoint
}
