package io.terminus.dalaran.example

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
    var itemName: List<String>? = null
    var test: String? = null
    var itemPrice: Double? = null
}

@RestController
class TestController {
    @PostMapping("/orders")
    fun orders(@RequestBody orderItem: TargetOrderItem, req: HttpServletRequest) = Order("${orderItem.itemName}: ${orderItem.itemPrice}")

    @PostMapping("/form_orders")
    fun formOrders(orderItem: TargetOrderItem, req: HttpServletRequest) = Order("form ${orderItem.itemName}: ${orderItem.itemPrice}")
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java)
}