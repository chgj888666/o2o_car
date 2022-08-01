package cn.wolfcode.car.business.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarPackageAuditVO  {
    private Long id;
    private Long bpmnInfoId;
    private Long director;
    private Long finance;
    private String info;
}
