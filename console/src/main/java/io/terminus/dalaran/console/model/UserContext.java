package io.terminus.dalaran.console.model;

import io.terminus.dalaran.model.user.UserInfo;
import lombok.Data;

/**
 * @author jiazhiyuan
 * @date 2023/7/2 3:50 PM
 */

@Data
public class UserContext {
    
    private static UserInfo userInfo;

    public static UserInfo getUserInfo() {
        return userInfo;
    }

    public static Object getCookies() {
        return new Object();
    }
}



    
