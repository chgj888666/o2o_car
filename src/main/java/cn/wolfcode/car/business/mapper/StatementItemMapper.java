package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.common.base.query.QueryObject;

import java.util.List;

public interface StatementItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StatementItem record);

    StatementItem selectByPrimaryKey(Long id);

    List<StatementItem> selectAll();

    int updateByPrimaryKey(StatementItem record);

    List<StatementItem> selectForList(QueryObject qo);

    void breakRelation(Long dictId);

}