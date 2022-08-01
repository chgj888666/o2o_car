package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.common.base.query.QueryObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ServiceItem record);

    ServiceItem selectByPrimaryKey(Long id);

    List<ServiceItem> selectAll();

    int updateByPrimaryKey(ServiceItem record);

    List<ServiceItem> selectForList(QueryObject qo);

    void changeStatus(@Param("id") Long id, @Param("saleStatus") Integer saleStatus);

    void changeAuditStatus(@Param("id") Long id, @Param("status") Integer status);
}