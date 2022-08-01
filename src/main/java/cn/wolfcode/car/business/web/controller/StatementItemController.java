package cn.wolfcode.car.business.web.controller;



import cn.wolfcode.car.business.domain.*;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/statementItem")
public class StatementItemController {
    //模板前缀
    private static final String prefix = "business/statementItem/";

    @Autowired
    private IStatementService statementervice;

    @Autowired
    private IStatementItemService statementItemService;

    @RequestMapping("/itemDetail")
    public String itemDetail(Model model, Long statementId){
        Statement statement = statementervice.get(statementId);
        // 判断是否已经支付
        model.addAttribute("statement",statement);
        if (Statement.STATUS_CONSUME.equals(statement.getStatus())) {
            return  prefix + "consume";
        }
        return prefix + "history";
    }

    @RequiresPermissions("system:appointment:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<StatementItem> query(StatementItemQuery qo){
        TablePageInfo<StatementItem> query = statementItemService.query(qo);
        return query;
    }


    @RequestMapping("/saveItems")
    @ResponseBody
    public AjaxResult saveItems(@RequestBody StatementItemJSON data){
        statementItemService.saveItems(data);
        return AjaxResult.success();
    }

    @RequestMapping("/pay")
    @ResponseBody
    public AjaxResult pay(Long statementId){
        statementItemService.pay(statementId);
        return AjaxResult.success();
    }






}
