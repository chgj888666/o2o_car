package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarPackageAuditQuery extends QueryObject {
    // 发起人
    private String creator;
    // 登陆用户 id
    private Long id;
    // 审核状态
    private Integer status;
}
