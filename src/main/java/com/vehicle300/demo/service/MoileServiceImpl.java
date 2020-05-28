package com.vehicle300.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.vehicle300.demo.CacheService;
import com.vehicle300.demo.client.CallableRestTemplate;
import com.vehicle300.demo.dao.MobileInfoMapper;
import com.vehicle300.demo.dto.ResponseDto;
import com.vehicle300.demo.example.MobileInfoExample;
import com.vehicle300.demo.model.MobileInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Service实现
 *
 * @author : LL
 * @date : 2020/5/27
 */
@Service
public class MoileServiceImpl implements MobileService {

    @Resource
    private MobileInfoMapper mobileInfoMapper;
    @Resource
    private CacheService cacheService;

    private static final long TIME_OUT=5000L;

    /**
     * 线程池参数可根据请求压力和机器性能调整
     */
    private static final ExecutorService executorService = new ThreadPoolExecutor(5,
            20, 1000, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());


    @Override
    public ResponseDto getMobileInfo(String mobile) {
        ResponseDto response=new ResponseDto();
        //查询一级缓存
        Object value = cacheService.get(mobile);
        if (value != null) {
            response.setResp(ResponseDto.RES.SUCCESS);
            response.setResult(value);
            return response;
        }
        //查询二级缓存
        MobileInfo mobileInfo = mobileInfoMapper.selectOneByExample
                (MobileInfoExample.newAndCreateCriteria()
                        .andMobileEqualTo(mobile).example());
        if(mobileInfo!=null){
            response.setResp(ResponseDto.RES.SUCCESS);
            response.setResult(mobileInfo);
            //创建一级缓存
            cacheService.put(mobileInfo.getMobile(),mobileInfo,5000);
            return response;
        }

        //Callanle异步、支持超时方式调用外部接口
        Callable<String> callableRestTemplate = new CallableRestTemplate(mobile);
        FutureTask<String> task = new FutureTask<>(callableRestTemplate);
        // 开始执行
        executorService.submit(task);
        String result;
        try {
            // 如果5秒没有返回值,直接熔断
            result = task.get(TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            response.setResp(ResponseDto.RES.FAIL);
            response.setMsg("接口请求超时！");
            e.printStackTrace();
            return response;
        }
        if(!StringUtils.isEmpty(result)){
            MobileInfo info= JSONObject.parseObject(result,MobileInfo.class);
            //数据获取成功
            if(info.getStatus()==1){
                response.setResp(ResponseDto.RES.SUCCESS);
                response.setResult(info);
                //维护一级缓存，方便测试，缓存过期时间设置为5s
                cacheService.put(info.getMobile(),info,5000);
                //维护二级缓存
                MobileInfo dbCacheInfo=mobileInfoMapper.selectOneByExample(MobileInfoExample.newAndCreateCriteria()
                        .andMobileEqualTo(info.getMobile()).example());
                if(dbCacheInfo!=null){
                    info.setUpdateTime(new Date());
                    mobileInfoMapper.updateByExampleSelective(info,MobileInfoExample.newAndCreateCriteria()
                            .andMobileEqualTo(info.getMobile()).example());
                }else{
                    info.setCreateTime(new Date());
                    mobileInfoMapper.insert(info);
                }
            }else {
                //接口错误信息
                response.setResp(ResponseDto.RES.FAIL);
                response.setMsg(info.getInfo());
            }
        }
        return response;
    }
}
