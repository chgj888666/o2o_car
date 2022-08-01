package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarPackageAuditServiceImpl implements ICarPackageAuditService {

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private TaskService taskService;

    @Override
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<CarPackageAudit>(carPackageAuditMapper.selectForList(qo));
    }

    @Override
    public CarPackageAudit get(Long id) {
        return carPackageAuditMapper.selectByPrimaryKey(id);
    }

    @Override
    public InputStream processImg(Long id) {
        CarPackageAudit carPackageAudit = carPackageAuditMapper.selectByPrimaryKey(id);
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(carPackageAudit.getBpmnInfoId());
        List<String> activeActivityIds = new ArrayList<>();
        // 判断是否还在审核中 审核中返回一个有高亮的图片，无则返回一个普通图片
        if (CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            activeActivityIds = runtimeService.getActiveActivityIds(carPackageAudit.getInstanceId());
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(bpmnInfo.getActProcessId());
        ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
        InputStream inputStream = pdg.generateDiagram(bpmnModel, activeActivityIds, Collections.EMPTY_LIST,
                "宋体", "宋体", "宋体");
        return inputStream;
    }

    @Override
    @Transactional
    public void cancelApply(Long id) {
        CarPackageAudit carPackageAudit = this.get(id);
        // 服务单项 恢复初始化
        serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_INIT);
        // 审核记录改为撤销
        carPackageAuditMapper.changeStatus(id,CarPackageAudit.STATUS_CANCEL);
        // 流程实例删除
        runtimeService.deleteProcessInstance(carPackageAudit.getInstanceId(),"撤销");
    }

    @Override
    @Transactional
    public void audit(Long id, Integer auditStatus, String info) {
        // 判断状态是否是在审核中
        CarPackageAudit carPackageAudit = this.get(id);
        if (!CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            throw new BusinessException("非法操作");
        }
        // 提交任务
        Task task = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("auditStatus",auditStatus);
        taskService.addComment(task.getId(),carPackageAudit.getInstanceId(),info);
        taskService.complete(task.getId(),map);
        carPackageAudit.setInfo(info);
        // 判断是同意还是拒绝
        if (CarPackageAudit.STATUS_PASS.equals(auditStatus)) {//通过
            Task nextTask = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
            if (nextTask == null) {// 无需下一步审批
                // 修改服务单项状态
                serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_APPROVED);
                // 修改审核流程状态
                carPackageAudit.setStatus(CarPackageAudit.STATUS_PASS);
            }else{
                // 需要下一步审核，设置审核人Id
                carPackageAudit.setAuditorId(Long.valueOf(nextTask.getAssignee()));
            }
        }else{//拒绝
            // 修改服务单项状态
            serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_REPLY);
            // 修改审核流程状态
            carPackageAudit.setStatus(CarPackageAudit.STATUS_REJECT);
        }
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
    }


}
