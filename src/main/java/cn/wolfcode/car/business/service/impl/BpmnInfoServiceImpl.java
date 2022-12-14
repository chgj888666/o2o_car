package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.file.FileUploadUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BpmnInfoServiceImpl implements IBpmnInfoService {

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BpmnInfo>(bpmnInfoMapper.selectForList(qo));
    }


    @Override
    public void save(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.insert(bpmnInfo);
    }

    @Override
    public BpmnInfo get(Long id) {
        return bpmnInfoMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.updateByPrimaryKey(bpmnInfo);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            bpmnInfoMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<BpmnInfo> list() {
        return bpmnInfoMapper.selectAll();
    }

    @Override
    public String load(MultipartFile file) throws IOException {
        // ????????????????????????
        if (file == null || file.getSize() <= 0  ) {
            throw new BusinessException("????????????");
        }
        // ????????????????????????????????????
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!"bpmn".equals(extension)) {
            throw new BusinessException("??????????????????");
        }
        // ????????????
        String path = FileUploadUtils.upload(SystemConfig.getProfile(), file);
        return path;
    }

    @Override
    @Transactional
    public void deploy(String path, String serviceType, String info) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(SystemConfig.getProfile(), path));
        // ??????
        Deployment deploy = repositoryService.createDeployment()
                .addInputStream(path, fileInputStream)
                .deploy();
        // ??????
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploy.getId()).singleResult();
        // ?????????bpmnInfo???
        BpmnInfo bpmnInfo = new BpmnInfo();
        bpmnInfo.setBpmnName(processDefinition.getName());
        bpmnInfo.setBpmnType(serviceType);
        bpmnInfo.setDeploymentId(deploy.getId());
        bpmnInfo.setActProcessId(processDefinition.getId());
        bpmnInfo.setActProcessKey(processDefinition.getKey());
        bpmnInfo.setDeployTime(deploy.getDeploymentTime());
        bpmnInfo.setInfo(info);
        this.save(bpmnInfo);
        fileInputStream.close();
    }

    @Override
    public InputStream readResource(String deploymentId, String type) throws FileNotFoundException {
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();
        if ("xml".equalsIgnoreCase(type)) {
            String resourceName = processDefinition.getResourceName();
            return new FileInputStream(new File(SystemConfig.getProfile(),resourceName));
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        ProcessDiagramGenerator pdg = new DefaultProcessDiagramGenerator();
        InputStream inputStream = pdg.generateDiagram(bpmnModel, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                "??????", "??????", "??????");
        return inputStream;
    }

    @Override
    @Transactional
    public void deleteDeploy(Long id) {
        BpmnInfo bpmnInfo = this.get(id);
        // ????????????
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(bpmnInfo.getDeploymentId()).singleResult();
        File file = new File(SystemConfig.getProfile(), processDefinition.getResourceName());
        file.delete();
        // ?????? bpmnInfo????????????
        bpmnInfoMapper.deleteByPrimaryKey(id);
        // ?????? ????????????
        repositoryService.deleteDeployment(bpmnInfo.getDeploymentId());
        // ?????? ????????????
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().deploymentId(bpmnInfo.getDeploymentId()).list();
        for (ProcessInstance processInstance : list) {
            runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(),"??????????????????");
        }
    }

    @Override
    public BpmnInfo queryByType(String car_package) {
        return bpmnInfoMapper.queryByType(car_package).get(0);
    }


}
