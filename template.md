```json
const messageFlow = {
    "configuration": {
        "port": 3306
    },
    "listener":{
        // <from uri="netty4-http:http://0.0.0.0:{{nettyPort}}/orderItemsForm1?httpMethodRestrict=POST"/>
        "type": "netty-http-listener",
        "params": {
            "protocol":"http",
            "host": "0.0.0.0",
            "port": "{{port}}",
            "path": "/orderItems",
            "method": "POST"
        }
    },
    "endpoints":[
        {
            /*
            <convertBodyTo type="java.lang.String"/>
            <unmarshal>
                <json library="Gson" unmarshalTypeName="io.terminus.test.OrderItem"/>
            </unmarshal>
            */
            "type": "json2object",
            "params": {
                "outType":"io.terminus.test.OrderItem"
            }
        }, {
            /*
            <to uri="dozer?targetModel=io.terminus.test.ExtOrderItem&amp;mappingFile=dozer/OrderItem-mapping.xml"/>

            <!-- mapping.xml -->
            <mapping>
                <class-a>org.apache.camel.component.dozer.ExpressionMapper</class-a>
                <class-b>io.terminus.test.ExtOrderItem</class-b>
                <field custom-converter-id="_expressionMapping" custom-converter-param="simple:\${header.name}">
                    <a>expression</a>
                    <b>itemName</b>
                </field>
                <field custom-converter-id="_expressionMapping" custom-converter-param="simple:\${body.price}">
                    <a>expression</a>
                    <b>itemPrice</b>
                </field>
                <field custom-converter-id="_expressionMapping" custom-converter-param="simple:\${header.Content-Type}">
                    <a>expression</a>
                    <b>test</b>
                </field>
            </mapping>
            */
            "type": "object-mapping",
            "params": {
                "targetModel": "io.terminus.test.ExtOrderItem",
                "mapping": {
                    "itemName": "${header.name}",
                    "itemPrice": "${body.price}",
                    "test": "${header.Content-Type}"
                }
            }
        }, {
            /*
            <marshal>
                <costom ref="formMarshal"/>
            </marshal>
            */
            "type": "object2json",
            "params": {}
        }, {
            // /*
            // <marshal>
            //     <costom ref="formMarshal"/>
            // </marshal>
            // */
            // "type": "unmarshal",
            // "params": {
            //     "outputType":"form-urlencoded"
            // }
        }, {
            /*
            <setHeader headerName="CamelHttpMethod">
                <constant>POST</constant>
            </setHeader>
            <to uri="http4://{{targetHost}}:{{targetPort}}/orders?bridgeEndpoint=true&amp;mapHttpMessageFormUrlEncodedBody=true"/>
            */
            "type": "http-request",
            "params": {
                "method": "POST",
                "host":"{{targetHost}}",
                "port":"{{targetPort}}",
                "path":"/orders"
            }
        }
    ]
}
```