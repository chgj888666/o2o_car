package cn.wolfcode.car.business.web.controller;



import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.BusLeave;
import cn.wolfcode.car.business.domain.BusLeave;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.BusLeaveQuery;
import cn.wolfcode.car.business.query.BusLeaveQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.IBusLeaveService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.business.vo.BusLeaveVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/busLeave")
public class BusLeaveController {
    //模板前缀
    private static final String prefix = "business/busLeave/";

    @Autowired
    private IBusLeaveService busLeaveService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    @Autowired
    private IUserService userService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:busLeave:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:busLeave:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:busLeave:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BusLeave> query(BusLeaveQuery qo){
        TablePageInfo<BusLeave> query = busLeaveService.query(qo);
        return query;
    }



    //新增
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BusLeaveVO vo){
        busLeaveService.audit(vo);
        return AjaxResult.success();
    }

    //发起审核
    @RequestMapping("/auditPage")
    public String saleOff(Model model){
        //部门经理
        model.addAttribute("manager", userService.queryByRoleKey("shopOwner"));
        //财务
        model.addAttribute("hr", userService.queryByRoleKey("financial"));
        //流程
        model.addAttribute("bpmnInfo", bpmnInfoService.queryByType("casual-leave"));
        return prefix + "auditPage";
    }

    // 查看我的待办页面
    @RequestMapping("/todoPage")
    public String todoQuery() {
        return prefix+"todoPage";
    }

    //我的待办
    @RequestMapping("/todoQuery")
    @ResponseBody
    public TablePageInfo<BusLeave> todoQuery(BusLeaveQuery qo) {
        // 设置登陆人的 id、状态为审核中的
        qo.setStatus("0");
        qo.setId(ShiroUtils.getUserId());
        TablePageInfo<BusLeave> query = busLeaveService.query(qo);
        return query;
    }
    // 查看我的待办页面
    @RequestMapping("/toAuditPage")
    public String auditPage(Long id,Model model) {
        model.addAttribute("id",id);
        return prefix+"toAuditPage";
    }
    // 审批
    @RequestMapping("/audit")
    @ResponseBody
    public AjaxResult audit(Long id,Integer auditStatus,String info) {
        busLeaveService.toAudit(id,auditStatus,info);
        return AjaxResult.success();
    }
}
