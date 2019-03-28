package io.terminus.dalaran.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.xml.bind.annotation.XmlRootElement

@SpringBootApplication
open class Application


data class Order(
        val orderNumber: String,
        val orderPrice: Double = 666.66
)

@XmlRootElement(name = "Order")
class OrderXML {
    var price: Double = 0.00
}

@XmlRootElement(name = "OutOrder")
class ReturnOrderXML {
    var orderNumber: String = "defaultNumber"
    var orderPrice: Double = 666.66
}

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

    @PostMapping("/testOrder")
    fun testOrder() = Order("test: Order", 999.98)


    @PostMapping("/callA")
    fun a(req: HttpServletRequest): String {
        println("start a")
        Thread.sleep(3000)
        println("end a")
        return "a"
    }

    @PostMapping("/callB")
    fun b(req: HttpServletRequest): String {
        println("start b")
        Thread.sleep(5000)
        println("end b")
        return "b"
    }

    @PostMapping("/callAll")
    fun all(req: HttpServletRequest) = "all"

    @PostMapping("/list")
    fun list(@RequestBody data: Map<String, Double>): Order {
        return Order("test: Order", data.get("price") ?: 0.00)
    }

    @PostMapping("/xml", produces = ["application/xml"], consumes = ["application/xml"])
    fun xml(@RequestBody data: OrderXML): ReturnOrderXML {
        return ReturnOrderXML().apply {
            orderNumber = "test: Order"
            orderPrice = data.price
        }
    }

    @GetMapping("/error")
    fun err(req: HttpServletRequest): Order {
        println(req.inputStream.reader().readText())
        throw RuntimeException("just fuck off")
    }
}

fun main(args: Array<String>) {
//    DalaranFlowLoader.loadMessageFlows()
    SpringApplication.run(Application::class.java)
}