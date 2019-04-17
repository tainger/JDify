package io.terminus.dalaran.cnooc

import com.alibaba.dubbo.config.ApplicationConfig
import com.alibaba.dubbo.config.ReferenceConfig
import com.alibaba.dubbo.config.RegistryConfig
import com.alibaba.dubbo.config.utils.ReferenceConfigCache
import java.io.Serializable

class SapOrder : Serializable {
    var id: Long? = null

    var no: String? = null

    var price: Double? = null
}


class TerminusOrder : Serializable {

    var id: Long? = null

    var orderNumber: String? = null

    var orderPrice: Double? = null
}

interface TestDalaranConsumer {
    fun execute(order: SapOrder): TerminusOrder
}

fun main(args: Array<String>) {
    val reference = ReferenceConfig<TestDalaranConsumer>()
    reference.application = ApplicationConfig("test")
    reference.registry = RegistryConfig("zookeeper://localhost:2181")
//        reference.setInterface("io.terminus.dalaran.example.OrderService"); // 接口名
    reference.setInterface("io.terminus.dalaran.cnooc.TestDalaranConsumer") // 接口名
    reference.version = "1.0.0"
    val cache = ReferenceConfigCache.getCache()
    val genericService = cache.get(reference)
    val sapOrder = SapOrder().apply {
        id = 1
        no = "teset"
        price = 998.00
    }
    val abc = genericService.execute(sapOrder)//, new String[]{"java.lang.String"}, new Object[]{"testAbc"});
    println(abc.orderNumber)
}