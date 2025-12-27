package com.example.event.mapper;

import com.example.event.domain.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {

    /**
     * 新增事件
     * @param event 事件信息
     * @return 结果
     */
    int insertEvent(Event event);

    /**
     * 根据事件ID查询事件信息
     * @param eventId 事件ID
     * @return 事件信息
     */
    Event selectEventById(@Param("eventId") Long eventId);

    /**
     * 查询事件列表
     * @param event 事件信息
     * @return 事件列表
     */
    List<Event> selectEventList(Event event);

    /**
     * 更新事件信息
     * @param event 事件信息
     * @return 结果
     */
    int updateEvent(Event event);

    /**
     * 删除事件
     * @param eventId 事件ID
     * @return 结果
     */
    int deleteEventById(@Param("eventId") Long eventId);

    /**
     * 批量删除事件
     * @param eventIds 需要删除的事件ID列表
     * @return 结果
     */
    int deleteEventByIds(@Param("eventIds") Long[] eventIds);

    /**
     * 根据事件类型查询事件列表
     * @param eventType 事件类型
     * @return 事件列表
     */
    List<Event> selectEventListByType(@Param("eventType") String eventType);

    /**
     * 根据展示位置查询事件列表
     * @param displayPosition 展示位置
     * @return 事件列表
     */
    List<Event> selectEventListByPosition(@Param("displayPosition") String displayPosition);

    /**
     * 更新事件点击次数
     * @param eventId 事件ID
     * @return 结果
     */
    int incrementClickCount(@Param("eventId") Long eventId);

    /**
     * 更新事件浏览次数
     * @param eventId 事件ID
     * @return 结果
     */
    int incrementViewCount(@Param("eventId") Long eventId);
}