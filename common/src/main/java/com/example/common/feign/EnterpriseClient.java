package com.example.common.feign;

import com.example.common.domain.entity.AjaxResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "enterprise")
public interface EnterpriseClient {

    @GetMapping("/system/enterprise/id/{enterpriseId}")
    AjaxResult getEnterprise(@PathVariable("enterpriseId") Long enterpriseId);
}
