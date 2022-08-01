package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 岗位服务接口
 */
public interface IBpmnInfoService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    BpmnInfo get(Long id);


    /**
     * 保存
     * @param bpmnInfo
     */
    void save(BpmnInfo bpmnInfo);

  
    /**
     * 更新
     * @param bpmnInfo
     */
    void update(BpmnInfo bpmnInfo);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<BpmnInfo> list();

    String load(MultipartFile multipartFile) throws IOException;

    void deploy(String path, String serviceType, String info) throws IOException;

    InputStream readResource(String deploymentId, String type) throws FileNotFoundException;

    void deleteDeploy(Long id);

    BpmnInfo queryByType(String car_package);
}
