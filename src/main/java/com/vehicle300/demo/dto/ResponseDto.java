package com.vehicle300.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口响应entity
 *
 * @author : LL
 * @date : 2020/5/27
 */
@Data
public class ResponseDto {
    private boolean success;
    private String msg;
    private long errCode;
    private Object result;


    public ResponseDto() {
        errCode = RES.FAIL.getErrCode();
        msg = RES.FAIL.getMsg();
        success = RES.FAIL.isSuccess();
    }

    /**
     * 响应状态枚举
     */
    @Getter
    @AllArgsConstructor
    @Slf4j
    public enum RES {
        SUCCESS(0, "成功", true),
        UNKNOWN_CODE(-2, "未知返回值", false),
        FAIL(-1, "失败", false);

        private Integer errCode;
        private String msg;
        private boolean success;
    }

    public void setResp(RES resp) {
        this.errCode = resp.getErrCode();
        this.success = resp.isSuccess();
        this.msg = resp.getMsg();
    }
}
