package cn.wolfcode.car.business.web.controller;



import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/statement")
public class StatementController {
    //模板前缀
    private static final String prefix = "business/statement/";

    @Autowired
    private IStatementService statementService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("system:statement:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("system:statement:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("system:statement:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("statement", statementService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("system:statement:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Statement> query(StatementQuery qo){
        TablePageInfo<Statement> query = statementService.query(qo);
        return query;
    }



    //新增
    @RequiresPermissions("system:statement:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Statement statement){
        statementService.save(statement);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("system:statement:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Statement statement){
        statementService.update(statement);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("system:statement:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        statementService.deleteBatch(ids);
        return AjaxResult.success();
    }




}
