## 数据模型

用于承载节点中流转的数据模型, 用于格式化接收数据, 数据转换也是模型间的转换.

数据模型部分现在考虑有两种方案:

### 抽象数据类型

只有一种可用模型类型, 即抽象数据类型, 所有的数据转换序列化等等, 都是基于此类型, 可以认为是 Java 中的 Map.
所以流程中需要显式声明做 Json 等序列化处理, 数据映射也只支持该数据类型, 举个例子会比较好理解:

HttpListener -> JsonToObject -> ObjectMapping -> ObjectToJson -> HttpClient -> JsonToObject -> ObjectMapping -> ObjectToJson -> Return

其中所有的数据操作例如 Mapping, 未加入的 Router 等, 都是基于 Object 做操作, 用作传输时, 需要自行声明序列化和反序列化.

### 带类型的数据模型

所有数据模型需先声明数据类型, 如 Object, Json, XML, Database 等等, 维护的结构也跟数据类型不同而有所不同, 比如 Database 就会有 Blob, 而 Json 等只有 String.
这样的好处是可以在流程中更直观更简化的处理, 以上述为例:

HttpListener -> ObjectMapping(Json -> Json) -> HttpClient -> ObjectMapping(Json -> Json) -> Return

其中数据格式的转化被 Mapping 处理掉了, 因为模型已经带了具体类型, 当然也可以做不同类型的转化, 实现方面其实是我们自己做掉 ToObject 这个过程.
