package com.example.event.controller;

import com.example.common.domain.entity.Result;
import com.example.event.domain.entity.Event;
import com.example.event.service.EventService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event")
public class EventController {

    @Autowired
    private EventService eventService;

    /**
     * 新增事件
     * @param event 事件信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "事件管理", businessType = BusinessType.INSERT)
    public Result insertEvent(@RequestBody Event event) {
        int result = eventService.insertEvent(event);
        if (result > 0) {
            return Result.success(event);
        }
        return Result.error("新增事件失败");
    }

    /**
     * 根据事件ID查询事件信息
     * @param eventId 事件ID
     * @return 事件信息
     */
    @GetMapping("/{eventId}")
    public Result selectEventById(@PathVariable Long eventId) {
        Event event = eventService.selectEventById(eventId);
        return Result.success(event);
    }

    /**
     * 查询事件列表
     * @param event 事件信息
     * @return 事件列表
     */
    @GetMapping
    public Result selectEventList(Event event) {
        List<Event> list = eventService.selectEventList(event);
        return Result.success(list);
    }

    /**
     * 更新事件信息
     * @param event 事件信息
     * @return 结果
     */
    @PutMapping
    @Log(title = "事件管理", businessType = BusinessType.UPDATE)
    public Result updateEvent(@RequestBody Event event) {
        int result = eventService.updateEvent(event);
        if (result > 0) {
            return Result.success("更新事件成功");
        }
        return Result.error("更新事件失败");
    }

    /**
     * 删除事件
     * @param eventId 事件ID
     * @return 结果
     */
    @DeleteMapping("/{eventId}")
    @Log(title = "事件管理", businessType = BusinessType.DELETE)
    public Result deleteEventById(@PathVariable Long eventId) {
        int result = eventService.deleteEventById(eventId);
        if (result > 0) {
            return Result.success("删除事件成功");
        }
        return Result.error("删除事件失败");
    }

    /**
     * 批量删除事件
     * @param eventIds 需要删除的事件ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "事件管理", businessType = BusinessType.DELETE)
    public Result deleteEventByIds(@RequestBody Long[] eventIds) {
        int result = eventService.deleteEventByIds(eventIds);
        if (result > 0) {
            return Result.success("批量删除事件成功");
        }
        return Result.error("批量删除事件失败");
    }

    /**
     * 根据事件类型查询事件列表
     * @param eventType 事件类型
     * @return 事件列表
     */
    @GetMapping("/type/{eventType}")
    public Result selectEventListByType(@PathVariable String eventType) {
        List<Event> list = eventService.selectEventListByType(eventType);
        return Result.success(list);
    }

    /**
     * 根据展示位置查询事件列表
     * @param displayPosition 展示位置
     * @return 事件列表
     */
    @GetMapping("/position/{displayPosition}")
    public Result selectEventListByPosition(@PathVariable String displayPosition) {
        List<Event> list = eventService.selectEventListByPosition(displayPosition);
        return Result.success(list);
    }

    /**
     * 更新事件点击次数
     * @param eventId 事件ID
     * @return 结果
     */
    @PutMapping("/click/{eventId}")
    @Log(title = "事件点击", businessType = BusinessType.UPDATE)
    public Result incrementClickCount(@PathVariable Long eventId) {
        int result = eventService.incrementClickCount(eventId);
        if (result > 0) {
            return Result.success("更新点击次数成功");
        }
        return Result.error("更新点击次数失败");
    }

    /**
     * 更新事件浏览次数
     * @param eventId 事件ID
     * @return 结果
     */
    @PutMapping("/view/{eventId}")
    @Log(title = "事件浏览", businessType = BusinessType.UPDATE)
    public Result incrementViewCount(@PathVariable Long eventId) {
        int result = eventService.incrementViewCount(eventId);
        if (result > 0) {
            return Result.success("更新浏览次数成功");
        }
        return Result.error("更新浏览次数失败");
    }
}
