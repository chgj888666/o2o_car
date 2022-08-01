package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.io.InputStream;
import java.util.List;

/**
 * 岗位服务接口
 */
public interface ICarPackageAuditService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    CarPackageAudit get(Long id);


    InputStream processImg(Long id);

    void cancelApply(Long id);

    void audit(Long id, Integer auditStatus, String info);
}
