package com.example.tag.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.AjaxResult;
import com.example.common.domain.entity.TableDataInfo;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.security.access.prepost.PreAuthorize;

import com.example.tag.domain.Tag;
import com.example.tag.service.ITagService;
import com.example.common.feign.EnterpriseRecommendationClient;
import com.example.common.domain.dto.TagRecommendationRequestDTO;
import com.example.common.domain.dto.TagRecommendationResponseDTO;

/**
 * 标签Controller
 * 
 * @author example
 */
@RestController
@RequestMapping("/tag")
public class TagController extends BaseController {
    @Autowired
    private ITagService tagService;

    @Autowired
    private EnterpriseRecommendationClient enterpriseRecommendationClient;

    /**
     * 查询标签列表
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/list")
    public TableDataInfo list(Tag tag) {
        startPage();
        List<Tag> list = tagService.selectTagList(tag);
        return getDataTable(list);
    }

    /**
     * 获取标签详细信息
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping(value = "{tagId}")
    public AjaxResult getInfo(@PathVariable("tagId") Long tagId) {
        return success(tagService.selectTagByTagId(tagId));
    }

    /**
     * 新增标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Tag tag) {
        return toAjax(tagService.insertTag(tag));
    }

    /**
     * 修改标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Tag tag) {
        return toAjax(tagService.updateTag(tag));
    }

    /**
     * 删除标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "标签", businessType = BusinessType.DELETE)
    @DeleteMapping("{tagIds}")
    public AjaxResult remove(@PathVariable Long[] tagIds) {
        return toAjax(tagService.deleteTagByTagIds(tagIds));
    }

    /**
     * 推荐相关标签
     * 根据输入的标签列表，推荐相关的标签
     * 集成Python标签推荐服务
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @PostMapping("/recommend-related-tags")
    public AjaxResult recommendRelatedTags(@RequestBody TagRecommendationRequestDTO request) {
        try {
            TagRecommendationResponseDTO response = enterpriseRecommendationClient.recommendTags(request);
            if (response != null && response.getCode() == 200) {
                return success(response.getData());
            } else {
                return error("标签推荐失败: " + (response != null ? response.getMsg() : "未知错误"));
            }
        } catch (Exception e) {
            return error("调用标签推荐服务失败: " + e.getMessage());
        }
    }
}