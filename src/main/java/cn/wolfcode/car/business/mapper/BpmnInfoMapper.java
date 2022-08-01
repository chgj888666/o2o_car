package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;

import java.util.List;

public interface BpmnInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(BpmnInfo record);

    BpmnInfo selectByPrimaryKey(Long id);

    List<BpmnInfo> selectAll();

    int updateByPrimaryKey(BpmnInfo record);

    List<BpmnInfo> selectForList(BpmnInfoQuery qo);

    List<BpmnInfo> queryByType(String car_package);
}