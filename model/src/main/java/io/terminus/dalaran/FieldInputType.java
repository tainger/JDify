package io.terminus.dalaran;

public enum FieldInputType {
    Hidden, Auto,
    String, Integer, Password, Select, Radio, Switch, CheckBox,
    FileUpload, FileDownload,
    Script, SQL, Expression,
    Connector, Model, SubFlow, Service, ServiceOperation,
    Routes, Pipeline, Branches, ErrorCatch, TrantorModule, TrantorIntegration, TrantorIntegrationPoint
}
