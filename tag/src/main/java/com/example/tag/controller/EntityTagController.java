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
import com.example.common.service.PermittionService;

import com.example.tag.domain.EntityTag;
import com.example.tag.service.IEntityTagService;

/**
 * 实体标签关联Controller
 * 
 * @author example
 */
@RestController
@RequestMapping("/entity/tag")
public class EntityTagController extends BaseController {
    @Autowired
    private IEntityTagService entityTagService;
    
    @Autowired
    private PermittionService permittionService;

    /**
     * 查询实体标签关联列表
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/list")
    public TableDataInfo list(EntityTag entityTag) {
        startPage();
        List<EntityTag> list = entityTagService.selectEntityTagList(entityTag);
        return getDataTable(list);
    }

    /**
     * 获取实体标签关联详细信息
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping(value = "{entityTagId}")
    public AjaxResult getInfo(@PathVariable("entityTagId") Long entityTagId) {
        return success(entityTagService.selectEntityTagByEntityTagId(entityTagId));
    }

    /**
     * 根据实体类型和ID查询标签
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @GetMapping("/byEntity/{entityType}/{entityId}")
    public AjaxResult getByEntity(@PathVariable("entityType") String entityType, @PathVariable("entityId") Long entityId) {
        return success(entityTagService.selectEntityTagsByEntity(entityType, entityId));
    }

    /**
     * 新增实体标签关联
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "实体标签关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EntityTag entityTag) {
        return toAjax(entityTagService.insertEntityTag(entityTag));
    }

    /**
     * 批量新增实体标签关联
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "实体标签关联", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchAdd(@RequestBody List<EntityTag> entityTags) {
        return toAjax(entityTagService.batchInsertEntityTags(entityTags));
    }

    /**
     * 删除实体标签关联
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "实体标签关联", businessType = BusinessType.DELETE)
    @DeleteMapping("{entityTagIds}")
    public AjaxResult remove(@PathVariable Long[] entityTagIds) {
        return toAjax(entityTagService.deleteEntityTagByEntityTagIds(entityTagIds));
    }

    /**
     * 根据实体类型和ID删除标签关联
     */
    @PreAuthorize("@permittionService.hasRole('ADMIN')")
    @Log(title = "实体标签关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/byEntity/{entityType}/{entityId}")
    public AjaxResult removeByEntity(@PathVariable("entityType") String entityType, @PathVariable("entityId") Long entityId) {
        return toAjax(entityTagService.deleteEntityTagsByEntity(entityType, entityId));
    }
}