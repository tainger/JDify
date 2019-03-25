# 接口设计

所有接口, 都是用 `/__dalaran_management` 开头的 Rest API, 因为集成平台也会对外暴露 Http 服务, 考虑到 Path 冲突, 所以以特定路径开始.

如 Dice 支持多端口多域名, 可以考虑使用多端口

> 主要是流程相关接口

## 结构体(模型)

### 查询结构体

GET:/__dalaran_management/structure

``` json
{
    "list": [{
        "id": 1,
        "moduleId": 1,
        "name": "xxx",
        "description": "",
        "structure_type": "XML",
        "structure_schema": {
            "root": "Order"
        }
    }],
    "current": 1,
    "pageSize": 20,
    "total": 53
}
```

### 创建结构体

POST:/__dalaran_management/structure

```json
{
    "moduleId": 1,
    "name": "xxx",
    "description": "",
    "structure_type": "XML",
    "structure_schema": {
        "root": "Order"
    }
}
```

### 修改结构体

PUT:/__dalaran_management/structure

```json
{
    "id": 1,
    "moduleId": 1,
    "name": "xxx",
    "description": "",
    "structure_type": "XML",
    "structure_schema": {
        "root": "Order"
    }
}
```

### 删除结构体

DELETE:/__dalaran_management/structure/{structure_id}

## 触发器

### 获取触发器类型(前期前端写死)

### 查询触发器

GET:/__dalaran_management/trigger

``` json
{
    "list": [{
        "id": 1,
        "moduleId": 1,
        "inStructure": 1,
        "outStructure": 1,
        "name": "xxx",
        "description": "",
        "type": "netty-http-listener",
        "config": {
            "protocol": "HTTP",
            "host": "0.0.0.0",
            "port": "{{port}}",
            "path": "/categories",
            "method": "GET"
        }
    }],
    "current": 1,
    "pageSize": 20,
    "total": 53
}
```

### 创建触发器

POST:/__dalaran_management/trigger

```json
{
    "moduleId": 1,
    "inStructure": 1,
    "outStructure": 1,
    "name": "xxx",
    "description": "",
    "type": "netty-http-client",
    "config": {
        "protocol": "HTTP",
        "host": "0.0.0.0",
        "port": "{{port}}",
        "path": "/categories",
        "method": "GET"
    }
}
```

### 修改触发器

PUT:/__dalaran_management/trigger

```json
{
    "id": 1,
    "moduleId": 1,
    "inStructure": 1,
    "outStructure": 1,
    "name": "xxx",
    "description": "",
    "type": "netty-http-listener",
    "config": {
        "protocol": "HTTP",
        "host": "0.0.0.0",
        "port": "{{port}}",
        "path": "/categories",
        "method": "GET"
    }
}
```

### 删除触发器

DELETE:/__dalaran_management/trigger/{trigger_id}

## 处理器

### 获取处理器类型(前期前端写死)
### 查询处理器

GET:/__dalaran_management/processor

``` json
{
    "list": [{
        "id": 1,
        "moduleId": 1,
        "inStructure": 1,
        "outStructure": 1,
        "name": "xxx",
        "description": "",
        "type": "netty-http-listener",
        "config": {
            "protocol": "HTTP",
            "host": "0.0.0.0",
            "port": "{{port}}",
            "path": "/categories",
            "method": "GET"
        }
    }],
    "current": 1,
    "pageSize": 20,
    "total": 53
}
```
### 创建处理器

POST:/__dalaran_management/processor

```json
{
    "moduleId": 1,
    "inStructure": 1,
    "outStructure": 1,
    "name": "xxx",
    "description": "",
    "type": "netty-http-listener",
    "config": {
        "protocol": "HTTP",
        "host": "0.0.0.0",
        "port": "{{port}}",
        "path": "/categories",
        "method": "GET"
    }
}
```

### 修改处理器

PUT:/__dalaran_management/processor

```json
{
    "id": 1,
    "moduleId": 1,
    "inStructure": 1,
    "outStructure": 1,
    "name": "xxx",
    "description": "",
    "type": "netty-http-listener",
    "config": {
        "protocol": "HTTP",
        "host": "0.0.0.0",
        "port": "{{port}}",
        "path": "/categories",
        "method": "GET"
    }
}
```

### 删除处理器

DELETE:/__dalaran_management/processor/{processor_id}

## 集成流

### 查询集成流

GET:/__dalaran_management/flow

params:

``` json
{
    "list": [{
        "id": 1,
        "moduleId": 1,
        "triggerId": 1,
        "processorIds": [1,2,3],
        "name": "xxx",
        "description": "",
        "status": "Enable",
        "properties": {
            "password": "anywhere"
        }
    }],
    "current": 1,
    "pageSize": 20,
    "total": 53
}
```

### 创建集成流

POST:/__dalaran_management/flow

```json
{
    "moduleId": 1,
    "triggerId": 1,
    "processorIds": [1,2,3],
    "name": "xxx",
    "description": "",
    "status": "Enable",
    "properties": {
        "password": "anywhere"
    }
}
```

### 修改集成流

PUT:/__dalaran_management/flow

{
    "id": 1,
    "moduleId": 1,
    "triggerId": 1,
    "processorIds": [1,2,3],
    "name": "xxx",
    "description": "",
    "status": "Enable",
    "properties": {
        "password": "anywhere"
    }
}

### 删除集成流

DELETE:/__dalaran_management/flow/{flow_id}

### 启用集成流

POST:/__dalaran_management/flow/{flow_id}/enable

### 禁用集成流

POST:/__dalaran_management/flow/{flow_id}/disable