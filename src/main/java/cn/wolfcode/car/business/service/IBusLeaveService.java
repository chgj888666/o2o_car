package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.BusLeave;
import cn.wolfcode.car.business.query.BusLeaveQuery;
import cn.wolfcode.car.business.vo.BusLeaveVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IBusLeaveService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<BusLeave> query(BusLeaveQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    BusLeave get(Long id);


    /**
     * 保存
     * @param busLeave
     */
    void save(BusLeave busLeave);


    /**
     * 查询全部
     * @return
     */
    List<BusLeave> list();


    void audit(BusLeaveVO vo);

    void toAudit(Long id, Integer auditStatus, String info);
}
