package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.common.base.query.QueryObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StatementMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Statement record);

    void createSettlement(Statement record);

    Statement selectByPrimaryKey(Long id);

    List<Statement> selectAll();

    int updateByPrimaryKey(Statement record);

    List<Statement> selectForList(QueryObject qo);

    void saveItems(@Param("totalPrice") Object totalPrice, @Param("discountAmount") Object discountAmount, @Param("totalQuantity") Object totalQuantity, @Param("id") Long id);

    void pay(@Param("statusPaid") Integer statusPaid, @Param("id") Long id, @Param("payId") Long payId);

    Statement selectByAppointmentId(Long id);


}