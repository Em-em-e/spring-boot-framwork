package com.vehicle300.demo.service;

import com.vehicle300.demo.dto.ResponseDto;
import org.springframework.stereotype.Service;

/**
 * Service 接口
 *
 * @author : LL
 * @date : 2020/5/27 13:27
 */
@Service
public interface MobileService {
    /**
     * 查询手机号信息
     *
     * @param mobile 手机号
     * @return 三方接口返回手机号信息
     */
    ResponseDto getMobileInfo(String mobile);
}
