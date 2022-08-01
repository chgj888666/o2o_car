package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.service.IAppointmentService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private StatementMapper statementMapper;


    @Override
    public TablePageInfo<Appointment> query(AppointmentQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Appointment>(appointmentMapper.selectForList(qo));
    }


    @Override
    public void save(Appointment appointment) {
        appointment.setCreateTime(new Date());
        appointmentMapper.insert(appointment);
    }

    @Override
    public Appointment get(Long id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Appointment appointment) {
        appointmentMapper.updateByPrimaryKey(appointment);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            if (Appointment.STATUS_APPOINTMENT.equals(this.get(dictId).getStatus())) {
                throw new BusinessException("非法操作");
            }
            appointmentMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<Appointment> list() {
        return appointmentMapper.selectAll();
    }

    @Override
    public void cancel(Long id) {
        Appointment appointment = this.get(id);
        if (!Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus())) {
           throw new BusinessException("非法操作");
        }
        appointmentMapper.changeStatus(id,Appointment.STATUS_CANCEL);

    }

    @Override
    public void arrive(Long id) {
        Appointment appointment = this.get(id);
        // 只有已到店和生成结算单才可以点击结算单
        if (Appointment.STATUS_APPOINTMENT.equals(appointment.getStatus()) || Appointment.STATUS_CANCEL.equals(appointment.getStatus())
                || Appointment.STATUS_OVERTIME.equals(appointment.getStatus())
                || Appointment.STATUS_CANCEL.equals(appointment.getStatus())) {
            throw new BusinessException("非法操作");
        }
        appointmentMapper.arrive(id,Appointment.STATUS_ARRIVAL);
    }

    @Override
    public void createSettlement(Long id) {

    }


}
