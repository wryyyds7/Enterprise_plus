package com.example.enterprise.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.ResourceLoader;

import com.example.common.constant.APIConstant;
import com.example.common.domain.entity.Result;
import com.example.common.feign.EnterpriseRecommendationClient;
import com.example.common.feign.SearchClient;
import com.example.common.feign.TagClient;
import com.example.common.domain.dto.EnterpriseRecommendationRequestDTO;
import com.example.common.domain.dto.EnterpriseRecommendationResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.common.aop.Log;
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.AjaxResult;
import com.example.common.domain.enums.BusinessType;
import com.example.common.domain.entity.Enterprise;
import com.example.common.domain.entity.Position;
import com.example.common.domain.entity.PositionInfo;
import com.example.common.utils.PositionUtils;
import com.example.enterprise.service.IEnterpriseService;
import com.example.enterprise.service.IPositionService;
import com.example.common.utils.ExcelUtil;
import com.example.common.domain.entity.TableDataInfo;
import org.springframework.web.client.RestTemplate;

/**
 * 企业信息Controller
 * 
 * @author wry thanks for ruoyi
 * @date 2025-10-1
 */
@Api("企业相关API")
@RestController
@RequestMapping("/system/enterprise")
public class EnterpriseController extends BaseController
{

    @Autowired
    private IEnterpriseService enterpriseService;
    @Autowired
    private DiscoveryClient discoveryClient;
    /**
     * 无法直接自动注入，采取构造器注入试试
     */
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SearchClient searchClient;

    @Autowired
    private TagClient tagClient;

    @Autowired
    private EnterpriseRecommendationClient enterpriseRecommendationClient;
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Autowired
    private IPositionService positionService;

//    @Autowired
//    private LoadBalancerClient loadBalancerClient;

    /**
     * 查询企业信息列表
     */
    @ApiOperation(value = "查询企业信息服务", notes = "没有该企业时会自动搜索")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/list")
    public TableDataInfo list(@RequestParam String enterpriseName)
    {

        startPage();
        System.out.println("Enterprise:"+enterpriseName);
        Enterprise enterprise = new Enterprise();
        enterprise.setName(enterpriseName);
        List<Enterprise> list = enterpriseService.selectEnterpriseList(enterprise);
        System.out.println("List:"+list);
        if (list == null || list.size()==0 || list.get(0).getName()==null) {
            System.out.println("db中没有，开始搜索");
            System.out.println("开始搜索:"+enterpriseName);
            Result result = searchEnterprise(enterpriseName);
            if(result.isSuccess()){
                list = (List<Enterprise>) result.getData();
            }
        }
        System.out.println("List:"+list);
        return getDataTable(list);
    }
    
    /**
     * 前端用户搜索企业信息列表，支持多条件筛选
     */
    @ApiOperation(value = "前端用户搜索企业信息", notes = "支持多条件筛选，无需管理员权限")
    @PostMapping("/user/list")
    public AjaxResult userList(@RequestBody Enterprise enterprise)
    {
        List<Enterprise> list = enterpriseService.selectEnterpriseList(enterprise);
        return AjaxResult.success(list);
    }
    /**
     * http爬虫寻找相关的页面
     * 10.12 换成nacos注册中心远程调用,利用注解完成负载均衡
     * TODO: 寻找岗位
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/searchEnterpriseByName")
    @Log(title = "寻找", businessType = BusinessType.OTHER)
    @ApiOperation("企业寻找服务")
    public Result searchEnterprise(@RequestParam String enterpriseName){
        // 用http服务获取
        // TODO: 记得换一下地址，不要写死， 还有url注意改，还有注意锁要加上后面 已完成，看下面
        // TODO: 10.12 换成nacos注册中心远程调用,利用注解完成负载均衡

        // ResponseEntity<Result> response = restTemplate.getForEntity(url, Result.class);
        // 新修改后代码 负载均衡的搜索 nocus的获取实例
//        List<ServiceInstance> instances = discoveryClient.getInstances("search");
//        ServiceInstance instance = instances.get(0);
//        String url = "http://" + instance.getHost() + ":" + instance.getPort();
//        ResponseEntity<List<Enterprise>> response = restTemplate.exchange(
//                url + "?enterpriseName=" + enterpriseName,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {}
//        );

        // 上面基本弃用,改用openfeign自动搞
        System.out.println("发现为空，现在开始自动添加");
        List<Enterprise> enterprise = searchClient.list(enterpriseName);
        try{
            for (Enterprise enterprise1 : enterprise){
                // 保存企业信息
                insert(enterprise1);
                
                // 尝试获取职位信息
                if (enterprise1.getWebsite() != null && !enterprise1.getWebsite().isEmpty()) {
                    try {
                        // 连接到企业官网
                        Document enterpriseWebsiteDoc = Jsoup.connect(enterprise1.getWebsite()).get();
                        
                        // 寻找招聘页面
                        String recruitmentUrl = PositionUtils.findRecruitmentPageUrl(enterpriseWebsiteDoc);
                        
                        if (recruitmentUrl != null) {
                            // 连接到招聘页面
                            Document jobPageDoc = Jsoup.connect(recruitmentUrl).get();
                            
                            // 提取职位信息
                            List<PositionInfo> positionInfos = PositionUtils.extractPositionInfo(jobPageDoc);
                            
                            if (positionInfos != null && !positionInfos.isEmpty()) {
                                // 转换为Position实体并保存
                                List<Position> positions = new ArrayList<>();
                                for (PositionInfo positionInfo : positionInfos) {
                                    Position position = new Position();
                                    position.setEnterpriseId(enterprise1.getEnterpriseId());
                                    position.setEnterpriseName(enterprise1.getName());
                                    position.setName(positionInfo.getName());
                                    position.setSalary(positionInfo.getSalary());
                                    position.setDescription(positionInfo.getDescription());
                                    position.setCountry(enterprise1.getCountry());
                                    position.setProvince(enterprise1.getProvince());
                                    position.setCity(enterprise1.getCity());
                                    position.setUrl(positionInfo.getUrl());
                                    
                                    // 保存职位信息
                                    positionService.insertPosition(position);
                                    positions.add(position);
                                }
                                
                                // 关联职位到企业
                                enterprise1.setPosition(positions);
                            }
                        }
                    } catch (Exception e) {
                        // 爬取职位失败不影响企业信息的保存
                        System.out.println("获取职位信息失败: " + e.getMessage());
                    }
                }
            }
        }
        catch (Exception e){
            return Result.error("插入数据失败", enterprise);
        }
        return Result.success(enterprise);
    }
    /**
     * 导出企业信息列表
     */
    @ApiOperation(value = "导出企业信息")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "企业信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Enterprise enterprise)
    {
        List<Enterprise> list = enterpriseService.selectEnterpriseList(enterprise);
        ExcelUtil<Enterprise> util = new ExcelUtil<Enterprise>(Enterprise.class);
        try {
            util.exportExcel(response, list, "企业信息数据");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取企业信息详细信息
     */
    @ApiOperation(value = "获取企业信息详情", tags = "根据企业id值，基本不用")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping(value = "/id/{enterpriseId}")
    public AjaxResult getInfo(@PathVariable("enterpriseId") Long enterpriseId)
    {
        return success(enterpriseService.selectEnterpriseByEnterpriseId(enterpriseId));
    }

    /**
     * 新增企业信息
     */
    @ApiOperation(value = "新增企业信息", tags = "新增企业信息")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "企业信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/insert")
    public AjaxResult insert(@RequestBody Enterprise enterprise)
    {
        return toAjax(enterpriseService.insertEnterprise(enterprise));
    }

    /**
     * 修改企业信息
     */
    @ApiOperation(value = "修改企业信息", tags = "修改企业信息")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "企业信息", businessType = BusinessType.UPDATE)
    @PutMapping(value = "/update")
    public AjaxResult update(@RequestBody Enterprise enterprise)
    {
        return toAjax(enterpriseService.updateEnterprise(enterprise));
    }

    /**
     * 删除企业信息
     */
    @ApiOperation(value = "删除企业信息", tags = "删除企业信息")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "企业信息", businessType = BusinessType.DELETE)
	@DeleteMapping(value = "/id/{enterpriseIds}")
    public AjaxResult delete(@PathVariable Long[] enterpriseIds)
    {
        return toAjax(enterpriseService.deleteEnterpriseByEnterpriseIds(enterpriseIds));
    }



/*
* 利用loadBalancerClient完成，但是注入有误，换成@LoadBalancer
* */
//    @GetMapping("/user/searchEnterpriseByName")
//    @Log(title = "寻找", businessType = BusinessType.OTHER)
//    @ApiOperation("企业寻找服务")
//    public Result searchEnterpriseWithLoadBalanceByLoadBalancerClient(@RequestParam String enterpriseName){
//        // 用http服务获取
//        // TODO: 记得换一下地址，不要写死， 还有url注意改，还有注意锁要加上后面 已完成，看下面
//        // ResponseEntity<Result> response = restTemplate.getForEntity(url, Result.class);
//        // 新修改后代码
//        List<ServiceInstance> instance = loadBalancerClient.getInstances("search");
////        ServiceInstance instance = instances.get(0);
//        String url = "http://" + instance.getHost() + ":" + instance.getPort();
//        ResponseEntity<List<Enterprise>> response = restTemplate.exchange(
//                url + "?enterpriseName=" + enterpriseName,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {}
//        );
//        if (response.getStatusCode().is2xxSuccessful()) {
//            List<Enterprise> enterprise =  response.getBody();
//            try{
//                for (Enterprise enterprise1 : enterprise){
//                    // 上面已经检查过了，直接返回
//                    insert(enterprise1);
//                }
//            }
//            catch (Exception e){
//                return Result.error("插入数据失败");
//            }
//            return Result.success(enterprise);
//        }
//        return Result.error("搜索似乎为空");
//    }
    /**
     * 没用，不用管
     */
//    @GetMapping("/user/searchEnterprise")
//    @ApiOperation("企业搜索服务,外接API")
//    public Result getEnterprise(@RequestParam String keyword){
//
////
////        return Result.success(enterpriseService.getEnterprise(keyword));
////
//        try {
//            String code = APIConstant.API_CODE;
//            // 构建外部API URL
//            String externalApiUrl = "https://www.xujian.tech/atlapi/data/c/query/like?code=" + code + "&keyword=" + keyword;
//
//            // 调用外部API
//            ResponseEntity<String> response = restTemplate.getForEntity(externalApiUrl, String.class);
//            String externalData = response.getBody();
//
//            // 根据外部API返回的数据进行处理
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(externalData);
//
//            int resultCode = rootNode.get("code").asInt();
//            if (resultCode != 200) {
//                return Result.error("外部API调用失败: " + rootNode.get("msg").asText());
//            }
//            // 提取data数组
//            JsonNode dataArray = rootNode.get("data");
//            // 处理外部API返回的数据
//            return Result.success(enterpriseService.getEnterpriseByKeyword(dataArray));
//
//
//        } catch (Exception e) {
//            return Result.error("调用外部API失败: " + e.getMessage());
//        }
//    }

    /**
     * 获取企业标签
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping(value = "/id/{enterpriseId}/tags")
    public AjaxResult getEnterpriseTags(@PathVariable("enterpriseId") Long enterpriseId) {
        return tagClient.getTagsByEntity("enterprise", enterpriseId);
    }

    /**
     * 添加企业标签
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "企业信息", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/id/{enterpriseId}/tags")
    public AjaxResult addEnterpriseTags(@PathVariable("enterpriseId") Long enterpriseId, @RequestBody List<Long> tagIds) {
        List<TagClient.EntityTagDTO> entityTags = tagIds.stream()
                .map(tagId -> new TagClient.EntityTagDTO("enterprise", enterpriseId, tagId))
                .collect(Collectors.toList());
        return tagClient.batchAddEntityTags(entityTags);
    }

    /**
     * 删除企业标签
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "企业信息", businessType = BusinessType.UPDATE)
    @DeleteMapping(value = "/id/{enterpriseId}/tags")
    public AjaxResult removeEnterpriseTags(@PathVariable("enterpriseId") Long enterpriseId) {
        return tagClient.removeTagsByEntity("enterprise", enterpriseId);
    }

    /**
     * 推荐企业列表
     */
    @ApiOperation(value = "推荐企业列表", notes = "根据用户标签推荐企业")
    @PostMapping(value = "/recommend/list")
    public AjaxResult recommendEnterprises(@RequestBody EnterpriseRecommendationRequestDTO request) {
        try {
            EnterpriseRecommendationResponseDTO response = enterpriseRecommendationClient.recommendEnterprise(request);
            return AjaxResult.success(response.getData());
        } catch (Exception e) {
            return AjaxResult.error("企业推荐失败: " + e.getMessage());
        }
    }

    /**
     * 更新企业名录
     */
    @ApiOperation(value = "更新企业名录", notes = "从文件读取企业信息并使用SearchClient更新企业名录")
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping(value = "/updateDirectory")
    public AjaxResult updateEnterpriseDirectory() {
        try {
            // 读取文件
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    resourceLoader.getResource("classpath:enterprise_littleperson.txt").getInputStream()));
            
            String line;
            int totalCount = 0;
            int successCount = 0;
            List<String> failedEnterprises = new ArrayList<>();
            
            // 逐行解析文件内容
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                // 解析省份和企业名称
                String[] parts = line.split("\t");
                if (parts.length < 2) {
                    failedEnterprises.add(line + " (格式错误)");
                    continue;
                }
                
                String province = parts[0];
                String enterpriseName = parts[1];
                totalCount++;
                
                try {
                    // 使用SearchClient更新企业信息
                    List<Enterprise> enterprises = searchClient.list(enterpriseName);
                    if (enterprises != null && !enterprises.isEmpty()) {
                        // 将企业信息保存到数据库
                        for (Enterprise enterprise : enterprises) {
                            enterpriseService.insertEnterprise(enterprise);
                            successCount++;
                        }
                    } else {
                        failedEnterprises.add(enterpriseName + " (未找到企业信息)");
                    }
                } catch (Exception e) {
                    failedEnterprises.add(enterpriseName + " (更新失败: " + e.getMessage() + ")");
                }
            }
            
            reader.close();
            
            // 构造返回结果
            AjaxResult result = AjaxResult.success();
            result.put("totalCount", totalCount);
            result.put("successCount", successCount);
            result.put("failedCount", failedEnterprises.size());
            result.put("failedEnterprises", failedEnterprises);
            
            return result;
        } catch (IOException e) {
            return AjaxResult.error("读取企业名录文件失败: " + e.getMessage());
        }
    }

}
