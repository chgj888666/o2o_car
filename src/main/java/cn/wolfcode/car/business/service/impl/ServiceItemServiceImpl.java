package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ServiceItemServiceImpl implements IServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;


    @Override
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<ServiceItem>(serviceItemMapper.selectForList(qo));
    }


    @Override
    public void save(ServiceItem serviceItem) {
        // 新增服务项为 套餐时，将审核状态设置为 初始化
        if (serviceItem.getCarPackage()) {
            //如 服务型为套餐 则将 审核状态设置为初始化
            serviceItem.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);
        }
        serviceItem.setCreateTime(new Date());
        // 防止用户输入时将审核状态更改为 审核通过
        serviceItem.setSaleStatus(ServiceItem.SALESTATUS_OFF);
        serviceItemMapper.insert(serviceItem);
    }

    @Override
    public ServiceItem get(Long id) {
        return serviceItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(ServiceItem serviceItem) {
        // 如服务项为 审核中 或 已上架 不可编辑 抛出异常
        ServiceItem oldServiceItem = this.get(serviceItem.getId());
        if (ServiceItem.AUDITSTATUS_AUDITING.equals(oldServiceItem.getAuditStatus())
                || ServiceItem.SALESTATUS_ON.equals(oldServiceItem.getSaleStatus())) {
            throw new BusinessException("非法操作");
        }
        // 将审核状态更改为 无需审核
        serviceItem.setAuditStatus(ServiceItem.AUDITSTATUS_NO_REQUIRED);
        serviceItem.setCarPackage(oldServiceItem.getCarPackage());
        if (serviceItem.getCarPackage()) {
            // 如将服务项为套餐，则将审核状态改为重新调整
            serviceItem.setAuditStatus(ServiceItem.AUDITSTATUS_REPLY);
        }
        serviceItemMapper.updateByPrimaryKey(serviceItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            serviceItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<ServiceItem> list() {
        return serviceItemMapper.selectAll();
    }

    @Override
    public void saleOn(Long id) {
        ServiceItem serviceItem = this.get(id);
        // 如非审核通过 或 无需审核 则不允许上架
        if (!(ServiceItem.AUDITSTATUS_APPROVED.equals(serviceItem.getAuditStatus()) ||
                ServiceItem.AUDITSTATUS_NO_REQUIRED.equals(serviceItem.getAuditStatus()))) {
            throw new BusinessException("非法操作");
        }
        serviceItemMapper.changeStatus(id,ServiceItem.SALESTATUS_ON);
    }

    @Override
    public void saleOff(Long id) {
        serviceItemMapper.changeStatus(id,ServiceItem.SALESTATUS_OFF);
    }


    @Override
    @Transactional
    public void startAudit(CarPackageAuditVO vo) {
        // 判断服务单项 是否为套餐、是否非审核中、审核通过的状态
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(vo.getId());
        // 不是套餐
        if (!serviceItem.getCarPackage()) {
            throw new BusinessException("非法操作");
        }
        // 审核中和审核通过状态
        Integer status = serviceItem.getAuditStatus();
        if (ServiceItem.AUDITSTATUS_AUDITING.equals(status) ||
                ServiceItem.AUDITSTATUS_APPROVED.equals(status)) {
            throw new BusinessException("非法操作");
        }
        CarPackageAudit carPackageAudit = new CarPackageAudit();
        // 把已知的数据保存起来，返回一个id，将该数据与流程实例绑定
        carPackageAudit.setServiceItemId(vo.getId());
        carPackageAudit.setServiceItemInfo(serviceItem.getInfo());
        carPackageAudit.setServiceItemPrice(serviceItem.getDiscountPrice());
        carPackageAudit.setCreator(ShiroUtils.getUser().getUserName());
        carPackageAudit.setAuditorId(vo.getDirector());
        carPackageAudit.setBpmnInfoId(vo.getBpmnInfoId());
        carPackageAudit.setInfo(vo.getInfo());
        carPackageAudit.setCreateTime(new Date());
        carPackageAuditMapper.insert(carPackageAudit);
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(vo.getBpmnInfoId());
        // 创建流程实例
        Map<String,Object> map = new HashMap<>();
        map.put("director",vo.getDirector());
        if (vo.getFinance()!=null) {
            map.put("finance",vo.getFinance());
        }
        map.put("discountPrice",serviceItem.getDiscountPrice().longValue());
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), carPackageAudit.getId().toString(), map);
        carPackageAudit.setInstanceId(processInstance.getId());
        // 保存流程实例的值
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
        //更改 服务单项的状态
        serviceItemMapper.changeAuditStatus(serviceItem.getId(),ServiceItem.AUDITSTATUS_AUDITING);
    }
}
