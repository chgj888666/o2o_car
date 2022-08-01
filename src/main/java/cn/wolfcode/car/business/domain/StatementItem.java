package cn.wolfcode.car.business.domain;

import java.math.BigDecimal;

public class StatementItem {
    /** */
    private Long id;

    /** 结算单ID*/
    private Long statementId;

    /** 服务项明细ID*/
    private Long itemId;

    /** 服务项明细名称*/
    private String itemName;

    /** 服务项价格*/
    private BigDecimal itemPrice;

    /** 购买数量*/
    private Long itemQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatementId() {
        return statementId;
    }

    public void setStatementId(Long statementId) {
        this.statementId = statementId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Long getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(Long itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
}