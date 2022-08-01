package cn.wolfcode.car.business.web.controller;



import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/bpmnInfo")
public class BpmnInfoController {
    //模板前缀
    private static final String prefix = "business/bpmnInfo/";

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:bpmnInfo:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:bpmnInfo:add")
    @RequestMapping("/deployPage")
    public String addPage(){
        return prefix + "deploy";
    }


    @RequiresPermissions("system:bpmnInfo:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("bpmnInfo", bpmnInfoService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:bpmnInfo:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo){
        TablePageInfo<BpmnInfo> query = bpmnInfoService.query(qo);
        return query;
    }


    //删除
    @RequiresPermissions("system:bpmnInfo:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        bpmnInfoService.deleteBatch(ids);
        return AjaxResult.success();
    }

    // 保存文件
    @RequestMapping("/load")
    @ResponseBody
    public AjaxResult load(MultipartFile file) throws IOException {
        String path = bpmnInfoService.load(file);
        return AjaxResult.success("操作成功！",path);
    }

    //部署流程定义
    @RequestMapping("/deploy")
    @ResponseBody
    public AjaxResult deploy(String path,String serviceType,String info) throws IOException {
        bpmnInfoService.deploy(path,serviceType,info);
        return AjaxResult.success();
    }

    // 查看流程定义文件
    @RequestMapping("/readResource")
    public void readResource(String deploymentId, String type, HttpServletResponse response) throws IOException {
        IOUtils.copy(bpmnInfoService.readResource(deploymentId,type),response.getOutputStream());
    }

    //删除
    @RequiresPermissions("system:bpmnInfo:remove")
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Long id){
        bpmnInfoService.deleteDeploy(id);
        return AjaxResult.success();
    }
}
