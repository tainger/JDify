package io.terminus.dalaran.tarntor

import io.terminus.dalaran.DalaranIntegration
import io.terminus.trantor.annotation.BaseModel
import io.terminus.trantor.annotation.TModel
import io.terminus.trantor.annotation.TModelField
import io.terminus.trantor.annotation.TModelFieldType
import io.terminus.trantor.annotation.typemeta.EnumMeta
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
open class Application

@DalaranIntegration(key = "UserLoaderExt", name = "测试用集成扩展点")
interface UserLoaderExt {
    fun findUserByQuery(query: UserQuery): User
}

class UserQuery {
    var username: String? = null
    var sex: Sex? = null
}

@TModel(desc = "用户", mainField = "username")
class User {
    @TModelField(desc = "用户名", name = "用户名", unique = true, nullable = false)
    var username: String? = null

    @TModelField(type = TModelFieldType.Password, desc = "密码", name = "密码", nullable = false)
    var password: String? = null

    @TModelField(desc = "姓名", name = "姓名", nullable = false)
    var name: String? = null

    @TModelField(desc = "年龄", name = "年龄", nullable = false)
    var age: Int? = null

    @TModelField(desc = "性别", name = "性别")
    var sex: Sex? = null

    @TModelField(desc = "是否锁定", name = "锁定")
    var locked: Boolean? = null
}

@RestController
class UserQueryController(
        @Autowired
        private val userLoader: UserLoaderExt
) {

    @GetMapping("/user")
    fun call(username: String?, sex: Sex?) = userLoader.findUserByQuery(UserQuery().apply {
        this.username = username
        this.sex = sex
    })
}

enum class Sex {
    Man, Woman
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java)
}