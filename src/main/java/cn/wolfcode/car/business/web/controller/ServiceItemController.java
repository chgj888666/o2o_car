package cn.wolfcode.car.business.web.controller;



import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.business.vo.CarPackageAuditVO;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
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
@RequestMapping("business/serviceItem")
public class ServiceItemController {
    //模板前缀
    private static final String prefix = "business/serviceItem/";

    @Autowired
    private IServiceItemService serviceItemService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:serviceItem:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:serviceItem:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("system:serviceItem:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("serviceItem", serviceItemService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:serviceItem:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo){
        TablePageInfo<ServiceItem> query = serviceItemService.query(qo);
        return query;
    }



    //新增
    @RequiresPermissions("system:serviceItem:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ServiceItem serviceItem){
        serviceItemService.save(serviceItem);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:serviceItem:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(ServiceItem serviceItem){
        serviceItemService.update(serviceItem);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("system:serviceItem:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        serviceItemService.deleteBatch(ids);
        return AjaxResult.success();
    }

    //上架
    @RequestMapping("/saleOn")
    @ResponseBody
    public AjaxResult saleOn(Long id){
        serviceItemService.saleOn(id);
        return AjaxResult.success();
    }

    //下架
    @RequestMapping("/saleOff")
    @ResponseBody
    public AjaxResult saleOff(Long id){
        serviceItemService.saleOff(id);
        return AjaxResult.success();
    }

    //发起审核
    @RequestMapping("/auditPage")
    public String saleOff(Model model,Long id){
        model.addAttribute("serviceItem",serviceItemService.get(id));
        //店长
        model.addAttribute("directors", userService.queryByRoleKey("shopOwner"));
        //财务
        model.addAttribute("finances", userService.queryByRoleKey("financial"));
        //流程
        model.addAttribute("bpmnInfo", bpmnInfoService.queryByType("car_package"));
        return prefix + "auditPage";
    }

    // 发起审核
    @RequestMapping("/startAudit")
    @ResponseBody
    public AjaxResult startAudit(CarPackageAuditVO vo){
        serviceItemService.startAudit(vo);
        return AjaxResult.success();
    }
}
