package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.domain.StatementItemJSON;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.base.query.QueryObject;

import java.util.List;
import java.util.Map;

/**
 * 岗位服务接口
 */
public interface IStatementItemService {

    TablePageInfo<StatementItem> query(StatementItemQuery qo);


    void saveItems(StatementItemJSON data);

    void pay(Long id);
}
