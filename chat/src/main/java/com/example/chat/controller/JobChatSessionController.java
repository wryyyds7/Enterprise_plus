package com.example.chat.controller;

import java.io.IOException;
import java.util.List;

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
import com.example.chat.domain.JobChatSession;
import com.example.chat.service.IJobChatSessionService;
import com.example.common.utils.ExcelUtil;
import com.example.common.domain.entity.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 聊天会话Controller
 * 
 * @author wry
 * @date 2025-10-19添加
 */
@RestController
@RequestMapping("/system/session")
public class JobChatSessionController extends BaseController
{
    @Autowired
    private IJobChatSessionService jobChatSessionService;

    /**
     * 查询聊天会话列表
     */
//    @PreAuthorize("@permittionService.hasRole('USER')")
//    @GetMapping("/list")
//    public TableDataInfo list(JobChatSession jobChatSession)
//    {
//        startPage();
//        List<JobChatSession> list = jobChatSessionService.selectJobChatSessionList(jobChatSession);
//        return getDataTable(list);
//    }

    @PreAuthorize("@permittionService.hasRole('USER')")
    @GetMapping("/list")
    public AjaxResult list(JobChatSession jobChatSession)
    {
        startPage();
        List<JobChatSession> list = jobChatSessionService.selectJobChatSessionList(jobChatSession);
        return success(list);
    }


    /**
     * 导出聊天会话列表
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "聊天会话 export", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, JobChatSession jobChatSession) throws IOException
    {
        List<JobChatSession> list = jobChatSessionService.selectJobChatSessionList(jobChatSession);
        ExcelUtil<JobChatSession> util = new ExcelUtil<JobChatSession>(JobChatSession.class);
        util.exportExcel(response, list, "聊天会话数据");
    }

    /**
     * 获取聊天会话详细信息
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "聊天会话getInfo", businessType = BusinessType.SELECT)
    @GetMapping(value = "/{sessionId}")
    public AjaxResult select(@PathVariable("sessionId") Long sessionId)
    {
        return success(jobChatSessionService.selectJobChatSessionBySessionId(sessionId));
    }

    /**
     * 新增聊天会话
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "聊天会话insert", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult insert(@RequestBody JobChatSession jobChatSession)
    {
        return toAjax(jobChatSessionService.insertJobChatSession(jobChatSession));
    }

    /**
     * 修改聊天会话
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "聊天会话update", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult update(@RequestBody JobChatSession jobChatSession)
    {
        return toAjax(jobChatSessionService.updateJobChatSession(jobChatSession));
    }

    /**
     * 删除聊天会话
     */
    @PreAuthorize("@permittionService.hasRole('USER')")
    @Log(title = "聊天会话delete", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sessionIds}")
    public AjaxResult delete(@PathVariable Long[] sessionIds)
    {
        return toAjax(jobChatSessionService.deleteJobChatSessionBySessionIds(sessionIds));
    }
}
