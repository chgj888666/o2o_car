package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementServiceImpl implements IStatementService {

    @Autowired
    private StatementMapper statementMapper;

    @Autowired
    private StatementItemMapper statementItemMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;


    @Override
    public TablePageInfo<Statement> query(StatementQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Statement>(statementMapper.selectForList(qo));
    }


    @Override
    public void save(Statement statement) {
        statement.setCreateTime(new Date());
        statementMapper.insert(statement);
    }

    @Override
    public Statement get(Long id) {
        return statementMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Statement statement) {
        statementMapper.updateByPrimaryKey(statement);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 如是经过预约到店消费，将预约状态更改为到店，使其能再次生成结算单
            Long appointmentId = this.get(dictId).getAppointmentId();
            if (appointmentId != null) {
                appointmentMapper.changeStatus(appointmentId, Appointment.STATUS_ARRIVAL);
            }
            statementMapper.deleteByPrimaryKey(dictId);
            statementItemMapper.breakRelation(dictId);
        }
    }

    @Override
    public List<Statement> list() {
        return statementMapper.selectAll();
    }

    @Override
    public Statement selectByAppointmentId(Long id) {
        return statementMapper.selectByAppointmentId(id);
    }

    @Override
    public void createSettlement(Long id) {
        Appointment appointment = appointmentMapper.selectByPrimaryKey(id);
        Statement statement = new Statement();
        statement.setCustomerName(appointment.getCustomerName());
        statement.setCustomerPhone(appointment.getCustomerPhone());
        statement.setActualArrivalTime(appointment.getActualArrivalTime());
        statement.setLicensePlate(appointment.getLicensePlate());
        statement.setCarSeries(appointment.getCarSeries());
        statement.setServiceType(appointment.getServiceType());
        statement.setAppointmentId(id);
        statement.setStatus(Statement.STATUS_CONSUME);
        statement.setCreateTime(new Date());
        statement.setInfo(appointment.getInfo());
        statementMapper.insert(statement);
    }

}
