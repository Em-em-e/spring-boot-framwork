package com.vehicle300.demo.controller;

import com.vehicle300.demo.dto.ResponseDto;
import com.vehicle300.demo.service.MobileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Controller
 *
 * @author : LL
 * @date : 2020/5/27
 */
@RestController
public class ApiController {

    @Resource
    private MobileService mobileService;

    @RequestMapping("/index")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /**
     * 获取手机号码信息
     * @param mobile 查询手机号，不可为空
     * @return
     */
    @GetMapping("/mobileInfo/{mobile}")
    public ResponseDto mobileInfo(@PathVariable(value = "mobile") String mobile) {
        ResponseDto response=new ResponseDto();
        try{
            response=mobileService.getMobileInfo(mobile);
        }catch (Exception e){
            e.printStackTrace();
            response.setMsg("请求出错，错误信息："+e.getMessage());
        }
        return response;
    }
}
