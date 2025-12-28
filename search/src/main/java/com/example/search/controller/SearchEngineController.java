package com.example.search.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.common.domain.entity.Enterprise;
import com.example.search.service.SearchEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.common.aop.Log;
import com.example.common.controller.BaseController;
import com.example.common.domain.enums.BusinessType;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/system/search/engine")
public class SearchEngineController extends BaseController {


    @Autowired
    private SearchEngineService searchEngineService;

    /**
     * 查询企业信息列表
     * 期间正好更新岗位
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "利用engine寻找企业", businessType = BusinessType.OTHER)
    @GetMapping("/enterprise")
    public List<Enterprise> list(@RequestParam String enterpriseName) {
        System.out.println("已进入search模块方法list");
        List<Enterprise> a = new ArrayList<>();
        a.add(searchEngineService.searchEnterpriseByName(enterpriseName));
        return a;
    }


}
