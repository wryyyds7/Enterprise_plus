//package com.example.enterprise.controller;
//
//import com.example.enterprise.service.EnterpriseService;
//import com.example.common.constant.APIConstant;
//import com.example.enterprise.domain.dto.EnterpriseDTO;
//import com.example.enterprise.domain.entity.Enterprise;
//import com.example.common.domain.entity.Result;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//@Controller
//public class EnterpriseUserController {
//
//    @Autowired
//    private EnterpriseService enterpriseService;
//
//
//
//
//    @GetMapping("/user/searchEnterpriseByPage")
//    @ApiOperation("企业搜索服务(分页)")
//    public Result searchEnterpriseBypage(@RequestBody EnterpriseDTO enterpriseDTO){
//        return Result.success(enterpriseService.searchEnterpriseByPage(enterpriseDTO));
//    }
//
//
//
//    @GetMapping("/user/searchEnterpriseByName")
//    @ApiOperation("企业寻找服务")
//    public Result searchEnterprise(@RequestParam String name){
//
//        return Result.success(searchEngineService.searchByName(name));
//    }
//
//    @PostMapping("/user/addEnterprise")
//    @ApiOperation("企业添加服务")
//    public Result addEnterprise(@RequestBody List<Enterprise> enterprise){
//        return Result.success(enterpriseService.addEnterprise(enterprise));
//    }
//
//
//
//}
