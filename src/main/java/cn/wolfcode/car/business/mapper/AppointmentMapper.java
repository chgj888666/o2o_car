package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.common.base.query.QueryObject;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

public interface AppointmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Appointment record);

    Appointment selectByPrimaryKey(Long id);

    List<Appointment> selectAll();

    int updateByPrimaryKey(Appointment record);

    List<Appointment> selectForList(QueryObject qo);

    void changeStatus(@Param("id") Long id, @Param("status") Integer status);

    void arrive(@Param("id") Long id, @Param("status") Integer statusArrival);
}