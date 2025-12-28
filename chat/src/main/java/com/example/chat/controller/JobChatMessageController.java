package com.example.chat.controller;

import java.io.IOException;
import java.util.List;

import com.example.common.utils.ExcelUtil;
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
import com.example.common.controller.BaseController;
import com.example.common.domain.entity.AjaxResult;
import com.example.common.domain.entity.TableDataInfo;
import com.example.chat.domain.JobChatMessage;
import com.example.chat.service.IJobChatMessageService;
import org.springframework.security.access.prepost.PreAuthorize;

import static com.example.common.domain.entity.AjaxResult.success;
import static com.github.pagehelper.page.PageMethod.startPage;

/**
 * 聊天消息Controller
 * 
 * @author wry
 * @date 2025-10-19添加
 */
@RestController
@RequestMapping("/system/message")
public class JobChatMessageController extends BaseController
{
    @Autowired
    private IJobChatMessageService jobChatMessageService;

    /**
     * 查询聊天消息列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/list")
    public TableDataInfo list(JobChatMessage jobChatMessage)
    {
        startPage();
        List<JobChatMessage> list = jobChatMessageService.selectJobChatMessageList(jobChatMessage);
        return getDataTable(list);
    }

    /**
     * 导出聊天消息列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, JobChatMessage jobChatMessage)
    {
        List<JobChatMessage> list = jobChatMessageService.selectJobChatMessageList(jobChatMessage);
        ExcelUtil<JobChatMessage> util = new ExcelUtil<JobChatMessage>(JobChatMessage.class);
        try {
            util.exportExcel(response, list, "聊天消息数据");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取聊天消息详细信息
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping(value = "/{msgId}")
    public AjaxResult getInfo(@PathVariable("msgId") Long msgId)
    {
        return success(jobChatMessageService.selectJobChatMessageByMsgId(msgId));
    }

    /**
     * 新增聊天消息
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PostMapping
    public AjaxResult insert(@RequestBody JobChatMessage jobChatMessage)
    {
        return toAjax(jobChatMessageService.insertJobChatMessage(jobChatMessage));
    }

    /**
     * 修改聊天消息
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @PutMapping
    public AjaxResult update(@RequestBody JobChatMessage jobChatMessage)
    {
        return toAjax(jobChatMessageService.updateJobChatMessage(jobChatMessage));
    }

    /**
     * 删除聊天消息
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @DeleteMapping("/{msgIds}")
    public AjaxResult delete(@PathVariable Long[] msgIds)
    {
        return toAjax(jobChatMessageService.deleteJobChatMessageByMsgIds(msgIds));
    }
}
