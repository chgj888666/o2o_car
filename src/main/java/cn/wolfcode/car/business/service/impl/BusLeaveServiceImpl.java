package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.BusLeave;
import cn.wolfcode.car.business.domain.BusLeave;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.BusLeaveMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.BusLeaveQuery;
import cn.wolfcode.car.business.service.IBusLeaveService;
import cn.wolfcode.car.business.vo.BusLeaveVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BusLeaveServiceImpl implements IBusLeaveService {

    @Autowired
    private BusLeaveMapper busLeaveMapper;

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Override
    public TablePageInfo<BusLeave> query(BusLeaveQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BusLeave>(busLeaveMapper.selectForList(qo));
    }


    @Override
    public void save(BusLeave busLeave) {
        busLeaveMapper.insert(busLeave);
    }

    @Override
    public BusLeave get(Long id) {
        return busLeaveMapper.selectById(id);
    }



    @Override
    public List<BusLeave> list() {
        return busLeaveMapper.selectAll();
    }

    @Override
    @Transactional
    public void audit(BusLeaveVO vo) {
        // 插入数据
        BusLeave busLeave = new BusLeave();
        busLeave.setName(vo.getName());
        busLeave.setReason(vo.getReason());
        busLeave.setStartTime(vo.getStartTime());
        busLeave.setEndTime(vo.getEndTime());
        busLeave.setAuditId(Integer.valueOf(vo.getManager().toString()));
        busLeave.setInfo(vo.getInfo());
        busLeave.setStatus("0");
        busLeaveMapper.insert(busLeave);
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(Long.valueOf(vo.getBpmnInfoId()));
        // 创建流程实例
        Map<String,Object> map = new HashMap<>();
        map.put("manager",vo.getManager());
        map.put("hr",vo.getHr());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), busLeave.getId().toString(), map);

    }

    @Override
    public void toAudit(Long id, Integer auditStatus, String info) {
        // 判断状态是否是在审核中
        BusLeave busLeave = this.get(id);
        if (!"0".equals(busLeave.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 提交任务
    }


}
