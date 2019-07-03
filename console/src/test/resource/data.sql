DROP TABLE IF EXISTS `dalaran_trigger_flow`;

CREATE TABLE `dalaran_trigger_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `in_model` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `out_model` bigint(20) DEFAULT NULL,
  `pipeline` longtext NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `trigger_config` text NOT NULL,
  `trigger_type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

INSERT INTO `dalaran_trigger_flow` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `in_model`, `module_id`, `name`, `out_model`, `pipeline`, `status`, `trigger_config`, `trigger_type`)
VALUES
	(4,'2019-05-13 17:42:35',NULL,'2019-05-13 17:53:31',NULL,NULL,23,1,'',23,'[{"config":"{\"inModelId\":23,\"messageMapping\":{\"root.summary\":{\"mappingType\":\"MAPPING\",\"value\":\"root.data.summary\"},\"root.author\":{\"mappingType\":\"MAPPING\",\"value\":\"root.data.authorName\"}},\"outModelId\":23}","id":"2","name":"map","type":"mapper-convert"}]',NULL,'{"type":"netty-http-listener","itemType":"Start","inModelId":23,"outModelId":23,"protocol":"HTTP","method":"POST","path":"/news","timeout":"3000"}','netty-http-listener');


DROP TABLE IF EXISTS `dalaran_connector`;

CREATE TABLE `dalaran_connector` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `component_name` varchar(255) NOT NULL,
  `component_type` varchar(255) NOT NULL,
  `config` text NOT NULL,
  `description` text,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `dalaran_connector` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `component_name`, `component_type`, `config`, `description`, `module_id`, `name`)
VALUES
	(1,'2019-05-20 15:59:56',NULL,'2019-05-20 16:01:36',NULL,'sql','Processor','{"databaseType":"MYSQL","host":"127.0.0.1","port":"3306","username":"root","password":"secret","schema":"dalaran"}',NULL,1,'Test'),
	(2,'2019-06-11 10:41:27',NULL,'2019-06-11 10:45:45',NULL,'Kafka','Processor','{"brokers":"127.0.0.1:9092"}',NULL,1,'Kafka'),
	(3,'2019-06-11 10:56:57',NULL,'2019-06-11 10:57:26',NULL,'kafka-consumer','Processor','{"brokers":"127.0.0.1:9092"}',NULL,1,'Kafka consumer'),
	(4,'2019-06-30 16:16:58',NULL,'2019-07-01 10:18:09',NULL,'http-client','Processor','{"host":"dalaran-console-develop.app.terminus.io","protocol":"HTTP","port":"8081","timeout":"3000"}',NULL,1,'Http-client'),
	(5,'2019-07-01 10:17:10',NULL,'2019-07-01 10:18:47',NULL,'soap-client','Processor','{"host":"127.0.0.1","protocol":"HTTP","port":"8081","timeout":"3000"}',NULL,1,'Soap-client');

DROP TABLE IF EXISTS `dalaran_model`;

CREATE TABLE `dalaran_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `model_schema` text NOT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `type` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `dalaran_model` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `model_schema`, `module_id`, `name`, `type`)
VALUES
	(19,'2019-07-01 16:53:26',NULL,'2019-07-01 17:01:56',NULL,NULL,'{"fields":{"root":{"type":"OBJECT","fields":{"countryName":{"type":"ARRAY", "subtype":"OBJECT"}}}}, "operationConfig":{"outPut":"getCountryRequest"}}',1,'Soap-trigger-1','SOAP'),
	(20,'2019-07-01 16:54:39',NULL,'2019-07-01 17:50:12',NULL,NULL,'{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false, "type":"OBJECT"}}, "operationConfig":{"binding":"CountriesPortSoap11","inModel":{"modelType":"SOAP","modelSchema":{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false}}}},"input":"getCountryRequest","name":"getCountry","outModel":{"modelType":"JSON","modelSchema":{"fields":{"root":{"fields":{"country":{"fields":{"capital":{"nullable":false,"type":"STRING"},"name":{"nullable":false,"type":"STRING"},"currency":{"nullable":false},"population":{"nullable":false,"type":"INTEGER"}},"nullable":false,"subType":"OBJECT","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"outPut":"getCountryResponse","portType":"getCountryRequest","wsdl":"http://127.0.0.1:8081/ws/countries.wsdl", "baseUrl":"http://127.0.0.1:8081/ws"}, "wsdlDoc":"<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:sch=\"http://www.baeldung.com/springsoap/gen\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:tns=\"http://www.baeldung.com/springsoap/gen\" targetNamespace=\"http://www.baeldung.com/springsoap/gen\"><wsdl:types><xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"http://www.baeldung.com/springsoap/gen\"><xs:element name=\"getCountryRequest\"><xs:complexType><xs:sequence maxOccurs=\"unbounded\"><xs:element name=\"name\" type=\"xs:string\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"getCountryResponse\"><xs:complexType><xs:sequence maxOccurs=\"unbounded\"><xs:element name=\"country\" type=\"tns:country\"/></xs:sequence></xs:complexType></xs:element><xs:complexType name=\"country\"><xs:sequence><xs:element name=\"name\" type=\"xs:string\"/><xs:element name=\"population\" type=\"xs:int\"/><xs:element name=\"capital\" type=\"xs:string\"/><xs:element name=\"currency\" type=\"tns:currency\"/></xs:sequence></xs:complexType><xs:simpleType name=\"currency\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"GBP\"/><xs:enumeration value=\"EUR\"/><xs:enumeration value=\"PLN\"/></xs:restriction></xs:simpleType></xs:schema></wsdl:types><wsdl:message name=\"getCountryResponse\"><wsdl:part element=\"tns:getCountryResponse\" name=\"getCountryResponse\"></wsdl:part></wsdl:message><wsdl:message name=\"getCountryRequest\"><wsdl:part element=\"tns:getCountryRequest\" name=\"getCountryRequest\"></wsdl:part></wsdl:message><wsdl:portType name=\"CountriesPort\"><wsdl:operation name=\"getCountry\"><wsdl:input message=\"tns:getCountryRequest\" name=\"getCountryRequest\"></wsdl:input><wsdl:output message=\"tns:getCountryResponse\" name=\"getCountryResponse\"></wsdl:output></wsdl:operation></wsdl:portType><wsdl:binding name=\"CountriesPortSoap11\" type=\"tns:CountriesPort\"><soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/><wsdl:operation name=\"getCountry\"><soap:operation soapAction=\"\"/><wsdl:input name=\"getCountryRequest\"><soap:body use=\"literal\"/></wsdl:input><wsdl:output name=\"getCountryResponse\"><soap:body use=\"literal\"/></wsdl:output></wsdl:operation></wsdl:binding><wsdl:service name=\"CountriesPortService\"><wsdl:port binding=\"tns:CountriesPortSoap11\" name=\"CountriesPortSoap11\"><soap:address location=\"http://127.0.0.1:8081/ws\"/></wsdl:port></wsdl:service></wsdl:definitions>"}',1,'Soap-client-1','SOAP'),
	(21,'2019-07-01 17:02:32',NULL,'2019-07-01 17:05:39',NULL,NULL,'{"fields":{"root":{"type":"OBJECT","subType":null,"nullable":false,"description":"根节点","fields":{"country":{"type":"ARRAY","subType":"OBJECT","nullable":true,"description":"结算单号","fields":{"name":{"type":"STRING","subType":null,"nullable":true,"description":"公司名称","fields":null},"population":{"type":"STRING","subType":null,"nullable":true,"description":"处理状态","fields":null},"capital":{"type":"STRING","subType":null,"nullable":false,"description":"结算单行项目","fields":null}}}}}}, "operationConfig":{"binding":"CountriesPortSoap11","inModel":{"modelType":"SOAP","modelSchema":{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false}}}},"input":"getCountryRequest","name":"getCountry","outModel":{"modelType":"JSON","modelSchema":{"fields":{"root":{"fields":{"country":{"fields":{"capital":{"nullable":false,"type":"STRING"},"name":{"nullable":false,"type":"STRING"},"currency":{"nullable":false},"population":{"nullable":false,"type":"INTEGER"}},"nullable":false,"subType":"OBJECT","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"outPut":"getCountryResponse","portType":"getCountryRequest","wsdl":"http://127.0.0.1:8081/ws/countries.wsdl", "baseUrl":"127.0.0.1:8081/ws", "operationKey":"getCountryRequest:::getCountry"}}',1,'Soap-client-2','SOAP'),
	(23,'2019-07-01 17:06:37',NULL,'2019-07-01 17:08:46',NULL,NULL,'{"fields":{"root":{"type":"OBJECT","subType":null,"nullable":false,"description":"根节点","fields":{"country":{"type":"OBJECT","subType":null,"nullable":true,"description":"结算单号","fields":{"name":{"type":"STRING","subType":null,"nullable":true,"description":"公司名称","fields":null},"population":{"type":"STRING","subType":null,"nullable":true,"description":"处理状态","fields":null},"capital":{"type":"STRING","subType":null,"nullable":false,"description":"结算单行项目","fields":null}}}}}}}',1,'Soap-trigger-2','SOAP');



DROP TABLE IF EXISTS `dalaran_module`;

CREATE TABLE `dalaran_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `dependency_ids` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `dalaran_module` (`id`, `name`)
VALUES
	(1,'test1-update');

DROP TABLE IF EXISTS `dalaran_property`;

CREATE TABLE `dalaran_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `dalaran_property` (`id`, `created_at`, `created_by`, `description`, `name`, `updated_at`, `updated_by`, `value`)
VALUES
	(1,NULL,NULL,NULL,'Host',NULL,NULL,'127.0.0.1');


DROP TABLE IF EXISTS `dalaran_release_record`;

CREATE TABLE `dalaran_release_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `operator` bigint(20) DEFAULT NULL,
  `release_log` text,
  `release_time` datetime DEFAULT NULL,
  `successful` bit(1) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_tlnox72t66nurvbpfv1er4nvs` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dalaran_released_connector`;

CREATE TABLE `dalaran_released_connector` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `component_name` varchar(255) NOT NULL,
  `component_type` varchar(255) NOT NULL,
  `config` text NOT NULL,
  `description` text,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `origin_id` bigint(20) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `dalaran_released_connector` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `component_name`, `component_type`, `config`, `description`, `module_id`, `name`, `origin_id`, `version`)
VALUES
	(1,'2019-05-20 15:59:51',NULL,'2019-06-27 10:24:56',NULL,'sql','Processor','{"databaseType":"MYSQL","host":"127.0.0.1","port":"3306","username":"root","password":"secret","schema":"ability-test"}',NULL,1,'',1,'1.0.0'),
	(2,'2019-06-11 10:45:55',NULL,'2019-06-11 10:58:11',NULL,'kafka-producer','Processor','{"brokers":"127.0.0.1:9092"}',NULL,1,'',2,'1.0.0'),
	(3,'2019-06-11 10:57:35',NULL,'2019-06-11 10:58:19',NULL,'kafka-consumer','Trigger','{"brokers":"127.0.0.1:9092"}',NULL,1,'',3,'1.0.0'),
	(4,'2019-06-30 16:18:52',NULL,'2019-06-30 16:20:57',NULL,'http-client','Processor','{"host":"dalaran-console-develop.app.terminus.io","protocol":"HTTP","port":"80","timeout":"3000"}',NULL,1,'',4,'1.0.0'),
	(5,'2019-07-01 10:18:57',NULL,'2019-07-01 10:19:51',NULL,'soap-client','Processor','{"host":"127.0.0.1","protocol":"HTTP","port":"8081","timeout":"3000"}',NULL,1,'',5,'1.0.0');


DROP TABLE IF EXISTS `dalaran_released_model`;

CREATE TABLE `dalaran_released_model` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `model_schema` text NOT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `type` varchar(255) NOT NULL,
  `origin_id` bigint(20) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `dalaran_released_model` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `model_schema`, `module_id`, `name`, `type`, `origin_id`, `version`)
VALUES
	(18,'2019-07-01 16:53:26',NULL,'2019-07-01 17:01:56',NULL,NULL,'{"fields":{"root":{"type":"OBJECT","fields":{"countryName":{"type":"ARRAY", "subtype":"OBJECT"}}}}, "operationConfig":{"outPut":"getCountryRequest"}}',1,'Soap-trigger-1','SOAP',19,'1.0.0'),
	(19,'2019-07-01 16:54:39',NULL,'2019-07-01 17:50:12',NULL,NULL,'{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false, "type":"OBJECT"}}, "operationConfig":{"binding":"CountriesPortSoap11","inModel":{"modelType":"SOAP","modelSchema":{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false}}}},"input":"getCountryRequest","name":"getCountry","outModel":{"modelType":"JSON","modelSchema":{"fields":{"root":{"fields":{"country":{"fields":{"capital":{"nullable":false,"type":"STRING"},"name":{"nullable":false,"type":"STRING"},"currency":{"nullable":false},"population":{"nullable":false,"type":"INTEGER"}},"nullable":false,"subType":"OBJECT","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"outPut":"getCountryResponse","portType":"getCountryRequest","wsdl":"http://127.0.0.1:8081/ws/countries.wsdl", "baseUrl":"http://127.0.0.1:8081/ws"}, "wsdlDoc":"<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:sch=\"http://www.baeldung.com/springsoap/gen\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:tns=\"http://www.baeldung.com/springsoap/gen\" targetNamespace=\"http://www.baeldung.com/springsoap/gen\"><wsdl:types><xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\" targetNamespace=\"http://www.baeldung.com/springsoap/gen\"><xs:element name=\"getCountryRequest\"><xs:complexType><xs:sequence maxOccurs=\"unbounded\"><xs:element name=\"name\" type=\"xs:string\"/></xs:sequence></xs:complexType></xs:element><xs:element name=\"getCountryResponse\"><xs:complexType><xs:sequence maxOccurs=\"unbounded\"><xs:element name=\"country\" type=\"tns:country\"/></xs:sequence></xs:complexType></xs:element><xs:complexType name=\"country\"><xs:sequence><xs:element name=\"name\" type=\"xs:string\"/><xs:element name=\"population\" type=\"xs:int\"/><xs:element name=\"capital\" type=\"xs:string\"/><xs:element name=\"currency\" type=\"tns:currency\"/></xs:sequence></xs:complexType><xs:simpleType name=\"currency\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"GBP\"/><xs:enumeration value=\"EUR\"/><xs:enumeration value=\"PLN\"/></xs:restriction></xs:simpleType></xs:schema></wsdl:types><wsdl:message name=\"getCountryResponse\"><wsdl:part element=\"tns:getCountryResponse\" name=\"getCountryResponse\"></wsdl:part></wsdl:message><wsdl:message name=\"getCountryRequest\"><wsdl:part element=\"tns:getCountryRequest\" name=\"getCountryRequest\"></wsdl:part></wsdl:message><wsdl:portType name=\"CountriesPort\"><wsdl:operation name=\"getCountry\"><wsdl:input message=\"tns:getCountryRequest\" name=\"getCountryRequest\"></wsdl:input><wsdl:output message=\"tns:getCountryResponse\" name=\"getCountryResponse\"></wsdl:output></wsdl:operation></wsdl:portType><wsdl:binding name=\"CountriesPortSoap11\" type=\"tns:CountriesPort\"><soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/><wsdl:operation name=\"getCountry\"><soap:operation soapAction=\"\"/><wsdl:input name=\"getCountryRequest\"><soap:body use=\"literal\"/></wsdl:input><wsdl:output name=\"getCountryResponse\"><soap:body use=\"literal\"/></wsdl:output></wsdl:operation></wsdl:binding><wsdl:service name=\"CountriesPortService\"><wsdl:port binding=\"tns:CountriesPortSoap11\" name=\"CountriesPortSoap11\"><soap:address location=\"http://127.0.0.1:8081/ws\"/></wsdl:port></wsdl:service></wsdl:definitions>"}',1,'Soap-client-1','SOAP',21,'1.0.0'),
	(20,'2019-07-01 17:02:32',NULL,'2019-07-01 17:05:39',NULL,NULL,'{"fields":{"root":{"type":"OBJECT","subType":null,"nullable":false,"description":"根节点","fields":{"country":{"type":"ARRAY","subType":"OBJECT","nullable":true,"description":"结算单号","fields":{"name":{"type":"STRING","subType":null,"nullable":true,"description":"公司名称","fields":null},"population":{"type":"STRING","subType":null,"nullable":true,"description":"处理状态","fields":null},"capital":{"type":"STRING","subType":null,"nullable":false,"description":"结算单行项目","fields":null}}}}}}, "operationConfig":{"binding":"CountriesPortSoap11","inModel":{"modelType":"SOAP","modelSchema":{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false}}}},"input":"getCountryRequest","name":"getCountry","outModel":{"modelType":"JSON","modelSchema":{"fields":{"root":{"fields":{"country":{"fields":{"capital":{"nullable":false,"type":"STRING"},"name":{"nullable":false,"type":"STRING"},"currency":{"nullable":false},"population":{"nullable":false,"type":"INTEGER"}},"nullable":false,"subType":"OBJECT","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"outPut":"getCountryResponse","portType":"getCountryRequest","wsdl":"http://127.0.0.1:8081/ws/countries.wsdl", "baseUrl":"127.0.0.1:8081/ws", "operationKey":"getCountryRequest:::getCountry"}}',1,'Soap-client-2','SOAP',20,'1.0.0'),
	(21,'2019-07-01 17:06:37',NULL,'2019-07-01 17:08:46',NULL,NULL,'{"fields":{"root":{"type":"OBJECT","subType":null,"nullable":false,"description":"根节点","fields":{"country":{"type":"OBJECT","subType":null,"nullable":true,"description":"结算单号","fields":{"name":{"type":"STRING","subType":null,"nullable":true,"description":"公司名称","fields":null},"population":{"type":"STRING","subType":null,"nullable":true,"description":"处理状态","fields":null},"capital":{"type":"STRING","subType":null,"nullable":false,"description":"结算单行项目","fields":null}}}}}}}',1,'Soap-trigger-2','SOAP',23,'1.0.0');


DROP TABLE IF EXISTS `dalaran_released_property`;

CREATE TABLE `dalaran_released_property` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `name` varchar(64) NOT NULL,
  `value` longtext NOT NULL,
  `origin_id` bigint(20) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `dalaran_released_service`;

CREATE TABLE `dalaran_released_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `import_config` text NOT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `service_config` longtext NOT NULL,
  `type` varchar(64) NOT NULL,
  `origin_id` bigint(20) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `dalaran_released_service` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `import_config`, `module_id`, `name`, `service_config`, `type`, `origin_id`, `version`)
VALUES
	(1,'2019-06-05 11:01:09',NULL,'2019-06-10 10:09:51',NULL,NULL,'{"wsdlUrl":"http://127.0.0.1:8081/ws/countries.wsdl"}',1,'soapservice1','{"soapOperations":[{"binding":"CountriesPortSoap11","inModel":{"modelType":"SOAP","modelSchema":{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false}}}},"input":"getCountryRequest","name":"getCountry","outModel":{"modelType":"JSON","modelSchema":{"fields":{"root":{"fields":{"country":{"fields":{"capital":{"nullable":false,"type":"STRING"},"name":{"nullable":false,"type":"STRING"},"currency":{"nullable":false},"population":{"nullable":false,"type":"INTEGER"}},"nullable":false,"subType":"OBJECT","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"outPut":"getCountryResponse","portType":"getCountryRequest","wsdl":"http://127.0.0.1:8081/ws/countries.wsdl", "operationKey":"getCountryRequest:::getCountry", "baseUrl":"127.0.0.1:8081/ws"}],"wsdl":"http://127.0.0.1:8081/ws/countries.wsdl"}','soap-connector',1,'1.0.0');


DROP TABLE IF EXISTS `dalaran_released_sub_flow`;

CREATE TABLE `dalaran_released_sub_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `in_model` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `out_model` bigint(20) DEFAULT NULL,
  `pipeline` longtext NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `origin_id` bigint(20) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dalaran_released_trigger_flow`;

CREATE TABLE `dalaran_released_trigger_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `in_model` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `out_model` bigint(20) DEFAULT NULL,
  `pipeline` text NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `trigger_config` text NOT NULL,
  `trigger_type` varchar(255) NOT NULL,
  `origin_id` bigint(20) NOT NULL,
  `version` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `dalaran_released_trigger_flow` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `in_model`, `module_id`, `name`, `out_model`, `pipeline`, `status`, `trigger_config`, `trigger_type`, `origin_id`, `version`)
VALUES
	(4,'2019-05-13 17:42:35',NULL,'2019-05-13 17:53:31',NULL,NULL,7,1,'',8,'[{"config":"{\"inModelId\":7,\"messageMapping\":{\"root.summary\":{\"mappingType\":\"MAPPING\",\"value\":\"root.data.summary\"},\"root.author\":{\"mappingType\":\"MAPPING\",\"value\":\"root.data.authorName\"}},\"outModelId\":8}","id":"2","name":"map","type":"mapper-convert"}]',NULL,'{"type":"netty-http-listener","itemType":"Start","inModelId":7,"outModelId":8,"protocol":"HTTP","method":"POST","path":"/news","timeout":"3000"}','netty-http-listener',4,'1.0.0');


DROP TABLE IF EXISTS `dalaran_service`;

CREATE TABLE `dalaran_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `import_config` text NOT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `service_config` longtext NOT NULL,
  `type` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `dalaran_service` (`id`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `import_config`, `module_id`, `name`, `service_config`, `type`)
VALUES
	(1,'2019-06-05 10:25:42',NULL,'2019-06-10 10:07:47',NULL,NULL,'{"wsdlUrl":"http://127.0.0.1:8081/ws/countries.wsdl"}',1,'Soap-service1','{"soapOperations":[{"binding":"CountriesPortSoap11","inModel":{"modelType":"XML","modelSchema":{"fields":{"root":{"fields":{"name":{"nullable":false,"subType":"STRING","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"input":"getCountryRequest","name":"getCountry","outModel":{"modelType":"XML","modelSchema":{"fields":{"root":{"fields":{"country":{"fields":{"capital":{"nullable":false,"type":"STRING"},"name":{"nullable":false,"type":"STRING"},"currency":{"nullable":false},"population":{"nullable":false,"type":"INTEGER"}},"nullable":false,"subType":"OBJECT","type":"ARRAY"}},"nullable":false}},"forceTopLevelObject":false}},"outPut":"getCountryResponse","portType":"getCountryRequest","wsdl":"http://127.0.0.1:8081/ws/countries.wsdl", "baseUrl":"127.0.0.1:8081/ws", "operationKey":""}],"wsdl":"http://127.0.0.1:8081/ws/countries.wsdl"}','soap-connector');


DROP TABLE IF EXISTS `dalaran_sub_flow`;

CREATE TABLE `dalaran_sub_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` timestamp DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` timestamp DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `description` text,
  `in_model` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `out_model` bigint(20) DEFAULT NULL,
  `pipeline` longtext NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `dalaran_tracing_log`;

CREATE TABLE `dalaran_tracing_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `module_id` bigint(20) DEFAULT NULL,
  `elapsed` bigint(20) DEFAULT NULL,
  `flow_id` bigint(20) DEFAULT NULL,
  `input_body` text,
  `input_body_type` varchar(255) DEFAULT NULL,
  `output_body` text,
  `output_body_type` varchar(255) DEFAULT NULL,
  `processor_id` bigint(20) DEFAULT NULL,
  `timestamp` bigint(20) DEFAULT NULL,
  `trigger_id` bigint(20) DEFAULT NULL,
  `record_id` varchar(255) DEFAULT NULL,
  `test_flow` bit(1) NOT NULL DEFAULT NULL,
  `tracing_type` varchar(255) DEFAULT NULL,
  `successful` bit(1) DEFAULT NULL,
  `main` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `dalaran_tracing_log` (`id`, `module_id`, `elapsed`, `flow_id`, `input_body`, `input_body_type`, `output_body`, `output_body_type`, `processor_id`, `timestamp`, `trigger_id`, `record_id`, `test_flow`, `tracing_type`, `successful`, `main`)
VALUES
	(2, 1, 89090, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', 'com.google.gson.internal.LinkedTreeMap cannot be cast to io.terminus.dalaran.component.processor.mapper.model.MappingField', 'EXCEPTION', 30, 1556007836627, NULL, 'ID-jingdideMacBook-Pro-local-1556007795710-0-1', 0, 'Flow', 0, 1),
	(3, 1, 89091, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', 'com.google.gson.internal.LinkedTreeMap cannot be cast to io.terminus.dalaran.component.processor.mapper.model.MappingField', 'EXCEPTION', NULL, 1556007836626, 5, 'ID-jingdideMacBook-Pro-local-1556007795710-0-1', 0, 'Trigger', 0, 0),
	(4, 1, 786441, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', 'com.google.gson.internal.LinkedTreeMap cannot be cast to io.terminus.dalaran.component.processor.mapper.model.MappingField', 'EXCEPTION', 30, 1556007965236, NULL, 'ID-jingdideMacBook-Pro-local-1556007795710-0-3', 0, 'Flow', 0, 1),
	(5, 1, 786442, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', 'com.google.gson.internal.LinkedTreeMap cannot be cast to io.terminus.dalaran.component.processor.mapper.model.MappingField', 'EXCEPTION', NULL, 1556007965235, 5, 'ID-jingdideMacBook-Pro-local-1556007795710-0-3', 0, 'Trigger', 0, 0),
	(6, 1, 321343, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', 30, 1556008921442, NULL, 'ID-jingdideMacBook-Pro-local-1556008881530-0-1', 0, 'Flow', 0, 1),
	(7, 1, 321345, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', NULL, 1556008921440, 5, 'ID-jingdideMacBook-Pro-local-1556008881530-0-1', 0, 'Trigger', 0, 1),
	(8, 1, 120135, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', 30, 1556009498778, NULL, 'ID-jingdideMacBook-Pro-local-1556009278490-0-1', 0, 'Flow', 0, 1),
	(9, 1, 120138, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', NULL, 1556009498776, 5, 'ID-jingdideMacBook-Pro-local-1556009278490-0-1', 0, 'Trigger', 0, 1),
	(11, 1, 154623, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', 30, 1556009902934, NULL, 'ID-jingdideMacBook-Pro-local-1556009858729-0-1', 0, 'Flow', 0, 1),
	(12, 1, 154625, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', NULL, 1556009902932, 5, 'ID-jingdideMacBook-Pro-local-1556009858729-0-1', 0, 'Trigger', 0, 1),
	(13, 1, 72223, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', 30, 1556010128345, NULL, 'ID-jingdideMacBook-Pro-local-1556010093585-0-1', 0, 'Flow', 0, 1),
	(14, 1, 72225, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', NULL, 1556010128343, 5, 'ID-jingdideMacBook-Pro-local-1556010093585-0-1', 0, 'Trigger', 0, 1),
	(15, 1, 88147, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', 30, 1556010541181, NULL, 'ID-jingdideMacBook-Pro-local-1556010502487-0-1', 0, 'Flow', 0, 0),
	(16, 1, 88149, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', NULL, 1556010541179, 5, 'ID-jingdideMacBook-Pro-local-1556010502487-0-1', 0, 'Trigger', 0, 0),
	(17, 1, 40632, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', 30, 1556010701861, NULL, 'ID-jingdideMacBook-Pro-local-1556010664258-0-1', 0, 'Flow', 0, 0),
	(18, 1, 40633, NULL, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', NULL, 'EXCEPTION', NULL, 1556010701860, 5, 'ID-jingdideMacBook-Pro-local-1556010664258-0-1', 0, 'Trigger', 0, 0),
	(19, 1, 117003, 8, '{userId=1, userName=lala, order=[{orderId=101, orderStatus=0}, {orderId=102, orderStatus=1}], consumer={consumerId=201, consumerName=lalaConsumer}}', 'JSON', 'No value for xpath: order/orderId', 'EXCEPTION', 30, 1556011122636, NULL, 'ID-jingdideMacBook-Pro-local-1556011039617-0-1', 0, 'Flow', 0, 0);
