package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.domain.StatementItemJSON;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BaseException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StatementItemServiceImpl implements IStatementItemService {

    @Autowired
    private StatementItemMapper statementItemMapper;

    @Autowired
    private StatementMapper statementMapper;


    @Override
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<StatementItem>(statementItemMapper.selectForList(qo));
    }

    @Override
    public void saveItems(StatementItemJSON data) {
        List<StatementItem> list = data.getList();
        Long statementId = list.get(0).getStatementId();
        if (list == null || list.size() == 0) {
            throw new BaseException("服务项不能为空");
        }
        // 保存 总消费金额、服务项数量、折扣金额
        statementMapper.saveItems(data.getTotalPrice(),data.getTotalQuantity(),data.getDiscountAmount(),statementId);
        statementItemMapper.breakRelation(statementId);
        list.forEach(item->statementItemMapper.insert(item));
    }

    @Override
    public void pay(Long id) {
        Statement statement = statementMapper.selectByPrimaryKey(id);
        // 如以及支付则，防止支付
        if (Statement.STATUS_PAID.equals(statement.getStatus())) {
            throw new BaseException("非法操作");
        }
        // 如已经支付 则创建支付时间以及支付状态,以及收款人信息
        statementMapper.pay(Statement.STATUS_PAID,id, ShiroUtils.getUser().getId());
    }
}
