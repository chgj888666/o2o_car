package cn.wolfcode.car.business.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StatementItemJSON {
    // 总消费金额
    private BigDecimal totalPrice;

    // 折扣
    private BigDecimal discountAmount;

    // 服务数量
    private BigDecimal totalQuantity;

    private List<StatementItem> list;
}
