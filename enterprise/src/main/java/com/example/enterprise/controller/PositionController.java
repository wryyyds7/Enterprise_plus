package com.example.enterprise.controller;

import com.example.common.domain.entity.Position;
import com.example.enterprise.service.IPositionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.common.aop.Log;
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.AjaxResult;
import com.example.common.domain.enums.BusinessType;
import com.example.common.utils.ExcelUtil;
import com.example.common.domain.entity.TableDataInfo;
import com.example.common.feign.TagClient;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import com.example.common.service.PermittionService;

/**
 * 招聘职位Controller
 * 
 * @author wry thanks for ruoyi
 * @date 2025-10-1
 */
@RestController
@RequestMapping("/system/position")
public class PositionController extends BaseController
{


    @Autowired
    private IPositionService positionService;

    @Autowired
    private TagClient tagClient;
    
    @Autowired
    private PermittionService permittionService;

    /**
     * 查询招聘职位列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/list")
    public TableDataInfo list(Position position)
    {
        log.info("开始查询职位列表，输入参数：{}", position);
        startPage();
        List<Position> list = positionService.selectPositionList(position);
        log.info("职位列表查询结束，共找到{}条记录", list != null ? list.size() : 0);
        return getDataTable(list);
    }

    /**
     * 导出招聘职位列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "招聘职位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Position position)
    {
        List<Position> list = positionService.selectPositionList(position);
        ExcelUtil<Position> util = new ExcelUtil<Position>(Position.class);
        try {
            util.exportExcel(response, list, "招聘职位数据");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取招聘职位详细信息
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping(value = "/{positionId}")
    public AjaxResult getInfo(@PathVariable("positionId") Long positionId)
    {
        return success(positionService.selectPositionByPositionId(positionId));
    }

    /**
     * 新增招聘职位
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "招聘职位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Position position)
    {
        return toAjax(positionService.insertPosition(position));
    }

    /**
     * 修改招聘职位
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "招聘职位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody Position position)
    {
        return toAjax(positionService.updatePosition(position));
    }

    /**
     * 删除招聘职位
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "招聘职位", businessType = BusinessType.DELETE)
	@DeleteMapping("/{positionIds}")
    public AjaxResult delete(@PathVariable Long[] positionIds)
    {
        return toAjax(positionService.deletePositionByPositionIds(positionIds));
    }

    /**
     * 获取职位标签
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping(value = "/{positionId}/tags")
    public AjaxResult getPositionTags(@PathVariable("positionId") Long positionId) {
        return tagClient.getTagsByEntity("position", positionId);
    }

    /**
     * 添加职位标签
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "招聘职位", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/{positionId}/tags")
    public AjaxResult addPositionTags(@PathVariable("positionId") Long positionId, @RequestBody List<Long> tagIds) {
        List<TagClient.EntityTagDTO> entityTags = tagIds.stream()
                .map(tagId -> new TagClient.EntityTagDTO("position", positionId, tagId))
                .collect(Collectors.toList());
        return tagClient.batchAddEntityTags(entityTags);
    }

    /**
     * 删除职位标签
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "招聘职位", businessType = BusinessType.UPDATE)
    @DeleteMapping(value = "/{positionId}/tags")
    public AjaxResult removePositionTags(@PathVariable("positionId") Long positionId) {
        return tagClient.removeTagsByEntity("position", positionId);
    }

}
