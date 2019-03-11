package io.terminus.dalaran.example

import io.terminus.dalaran.DalaranFlowLoader
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@SpringBootApplication
open class Application

data class Order(
        val orderNumber: String
)

class TargetOrderItem {
    var id: Long? = null
    var itemName: String? = null
    var test: String? = null
    var itemPrice: Double? = null
}

@RestController
class TestController {
    @PostMapping("/orders")
    fun orders(@RequestBody orderItem: ExtOrderItem, req: HttpServletRequest) = Order("${orderItem.itemName}: ${orderItem.itemPrice}")

    @PostMapping("/orders_dubbo")
    fun ordersDubbo(@RequestBody orderItem: TargetOrderItem, req: HttpServletRequest) = Order("${orderItem.itemName}: ${orderItem.itemPrice}")

    @PostMapping("/form_orders")
    fun formOrders(orderItem: TargetOrderItem, req: HttpServletRequest) = Order("form ${orderItem.itemName}: ${orderItem.itemPrice}")
}

fun main(args: Array<String>) {
    DalaranFlowLoader.loadMessageFlows()
    SpringApplication.run(Application::class.java)
}