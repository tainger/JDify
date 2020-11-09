package io.terminus.dalaran.core.resource.entity;

import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_shop_token")
public class DalaranShopTokenEntity extends BasicEntity {
    @Column(nullable = false)
    private Long moduleId;

    @Column(nullable = false)
    private Long shopId;

    @Column(nullable = false)
    private String appKey;

    @Column(nullable = false)
    private String appSecret;

    @Column(nullable = false)
    private String gateWay;

    @Column(nullable = false)
    private String accessToken;
}
