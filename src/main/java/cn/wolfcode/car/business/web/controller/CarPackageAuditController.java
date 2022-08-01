package cn.wolfcode.car.business.web.controller;



import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/carPackageAudit")
public class CarPackageAuditController {
    //模板前缀
    private static final String prefix = "business/carPackageAudit/";

    @Autowired
    private ICarPackageAuditService carPackageAuditService;

    @Autowired
    private IStatementService statementService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:carPackageAudit:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:carPackageAudit:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo){
        qo.setCreator(ShiroUtils.getUser().getUserName());
        TablePageInfo<CarPackageAudit> query = carPackageAuditService.query(qo);
        return query;
    }

    // 查看流程定义文件
    @RequestMapping("/processImg")
    public void processImg(Long id, HttpServletResponse response) throws IOException {
        IOUtils.copy(carPackageAuditService.processImg(id),response.getOutputStream());
    }

    // 撤销审核
    @RequestMapping("/cancelApply")
    @ResponseBody
    public AjaxResult cancelApply(Long id) {
        carPackageAuditService.cancelApply(id);
        return AjaxResult.success();
    }

    // 查看我的待办页面
    @RequestMapping("/todoPage")
    public String todoQuery() {
        return prefix+"todoPage";
    }

    //我的待办
    @RequestMapping("/todoQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> todoQuery(CarPackageAuditQuery qo) {
        // 设置登陆人的 id、状态为审核中的
        qo.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        qo.setId(ShiroUtils.getUserId());
        TablePageInfo<CarPackageAudit> query = carPackageAuditService.query(qo);
        return query;
    }

    //我的已经
    @RequestMapping("/doneQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> doneQuery(CarPackageAuditQuery qo) {
        // 设置登陆人的 id、状态为审核中的
        qo.setStatus(CarPackageAudit.STATUS_PASS);
        qo.setId(ShiroUtils.getUserId());
        TablePageInfo<CarPackageAudit> query = carPackageAuditService.query(qo);
        return query;
    }

    // 查看我的待办页面
    @RequestMapping("/auditPage")
    public String auditPage(Long id,Model model) {
        model.addAttribute("id",id);
        return prefix+"auditPage";
    }

    // 查看我的已办页面
    @RequestMapping("/donePage")
    public String donePage() {
        return prefix+"donePage";
    }

    // 审批
    @RequestMapping("/audit")
    @ResponseBody
    public AjaxResult audit(Long id,Integer auditStatus,String info) {
        carPackageAuditService.audit(id,auditStatus,info);
        return AjaxResult.success();
    }
}
