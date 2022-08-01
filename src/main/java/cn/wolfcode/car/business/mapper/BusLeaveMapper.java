package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.BusLeave;
import cn.wolfcode.car.business.query.BusLeaveQuery;

import java.util.List;

public interface BusLeaveMapper {
    int insert(BusLeave record);

    List<BusLeave> selectAll();

    List<BusLeave> selectForList(BusLeaveQuery qo);

    BusLeave selectById(Long id);
}