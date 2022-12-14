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
        // ??????????????????????????? ????????????????????????????????????????????????????????????????????????
        if (CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            activeActivityIds = runtimeService.getActiveActivityIds(carPackageAudit.getInstanceId());
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(bpmnInfo.getActProcessId());
        ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
        InputStream inputStream = pdg.generateDiagram(bpmnModel, activeActivityIds, Collections.EMPTY_LIST,
                "??????", "??????", "??????");
        return inputStream;
    }

    @Override
    @Transactional
    public void cancelApply(Long id) {
        CarPackageAudit carPackageAudit = this.get(id);
        // ???????????? ???????????????
        serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_INIT);
        // ????????????????????????
        carPackageAuditMapper.changeStatus(id,CarPackageAudit.STATUS_CANCEL);
        // ??????????????????
        runtimeService.deleteProcessInstance(carPackageAudit.getInstanceId(),"??????");
    }

    @Override
    @Transactional
    public void audit(Long id, Integer auditStatus, String info) {
        // ?????????????????????????????????
        CarPackageAudit carPackageAudit = this.get(id);
        if (!CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            throw new BusinessException("????????????");
        }
        // ????????????
        Task task = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
        Map<String, Object> map = new HashMap<>();
        map.put("auditStatus",auditStatus);
        taskService.addComment(task.getId(),carPackageAudit.getInstanceId(),info);
        taskService.complete(task.getId(),map);
        carPackageAudit.setInfo(info);
        // ???????????????????????????
        if (CarPackageAudit.STATUS_PASS.equals(auditStatus)) {//??????
            Task nextTask = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
            if (nextTask == null) {// ?????????????????????
                // ????????????????????????
                serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_APPROVED);
                // ????????????????????????
                carPackageAudit.setStatus(CarPackageAudit.STATUS_PASS);
            }else{
                // ???????????????????????????????????????Id
                carPackageAudit.setAuditorId(Long.valueOf(nextTask.getAssignee()));
            }
        }else{//??????
            // ????????????????????????
            serviceItemMapper.changeAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_REPLY);
            // ????????????????????????
            carPackageAudit.setStatus(CarPackageAudit.STATUS_REJECT);
        }
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
    }


}
