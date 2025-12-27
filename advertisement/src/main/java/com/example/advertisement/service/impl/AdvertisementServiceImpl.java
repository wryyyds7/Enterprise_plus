package com.example.advertisement.service.impl;

import com.example.advertisement.domain.entity.Advertisement;
import com.example.advertisement.mapper.AdvertisementMapper;
import com.example.advertisement.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Override
    public int insertAdvertisement(Advertisement advertisement) {
        return advertisementMapper.insertAdvertisement(advertisement);
    }

    @Override
    public Advertisement selectAdvertisementById(Long adId) {
        return advertisementMapper.selectAdvertisementById(adId);
    }

    @Override
    public List<Advertisement> selectAdvertisementList(Advertisement advertisement) {
        return advertisementMapper.selectAdvertisementList(advertisement);
    }

    @Override
    public int updateAdvertisement(Advertisement advertisement) {
        return advertisementMapper.updateAdvertisement(advertisement);
    }

    @Override
    public int deleteAdvertisementById(Long adId) {
        return advertisementMapper.deleteAdvertisementById(adId);
    }

    @Override
    public int deleteAdvertisementByIds(Long[] adIds) {
        return advertisementMapper.deleteAdvertisementByIds(adIds);
    }

    @Override
    public List<Advertisement> selectAdvertisementByPosition(String displayPosition) {
        return advertisementMapper.selectAdvertisementByPosition(displayPosition);
    }

    @Override
    public int incrementClickCount(Long adId) {
        return advertisementMapper.incrementClickCount(adId);
    }

    @Override
    public int incrementViewCount(Long adId) {
        return advertisementMapper.incrementViewCount(adId);
    }
}