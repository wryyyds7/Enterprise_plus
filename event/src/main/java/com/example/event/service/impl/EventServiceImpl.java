package com.example.event.service.impl;

import com.example.event.domain.entity.Event;
import com.example.event.mapper.EventMapper;
import com.example.event.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventMapper eventMapper;

    @Override
    public int insertEvent(Event event) {
        return eventMapper.insertEvent(event);
    }

    @Override
    public Event selectEventById(Long eventId) {
        return eventMapper.selectEventById(eventId);
    }

    @Override
    public List<Event> selectEventList(Event event) {
        return eventMapper.selectEventList(event);
    }

    @Override
    public int updateEvent(Event event) {
        return eventMapper.updateEvent(event);
    }

    @Override
    public int deleteEventById(Long eventId) {
        return eventMapper.deleteEventById(eventId);
    }

    @Override
    public int deleteEventByIds(Long[] eventIds) {
        return eventMapper.deleteEventByIds(eventIds);
    }

    @Override
    public List<Event> selectEventListByType(String eventType) {
        return eventMapper.selectEventListByType(eventType);
    }

    @Override
    public List<Event> selectEventListByPosition(String displayPosition) {
        return eventMapper.selectEventListByPosition(displayPosition);
    }

    @Override
    public int incrementClickCount(Long eventId) {
        return eventMapper.incrementClickCount(eventId);
    }

    @Override
    public int incrementViewCount(Long eventId) {
        return eventMapper.incrementViewCount(eventId);
    }
}