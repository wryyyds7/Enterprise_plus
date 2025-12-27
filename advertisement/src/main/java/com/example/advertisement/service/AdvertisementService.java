package com.example.advertisement.service;

import com.example.advertisement.domain.entity.Advertisement;
import java.util.List;

public interface AdvertisementService {

    /**
     * 新增广告
     * @param advertisement 广告信息
     * @return 结果
     */
    int insertAdvertisement(Advertisement advertisement);

    /**
     * 根据广告ID查询广告信息
     * @param adId 广告ID
     * @return 广告信息
     */
    Advertisement selectAdvertisementById(Long adId);

    /**
     * 查询广告列表
     * @param advertisement 广告信息
     * @return 广告列表
     */
    List<Advertisement> selectAdvertisementList(Advertisement advertisement);

    /**
     * 更新广告信息
     * @param advertisement 广告信息
     * @return 结果
     */
    int updateAdvertisement(Advertisement advertisement);

    /**
     * 删除广告
     * @param adId 广告ID
     * @return 结果
     */
    int deleteAdvertisementById(Long adId);

    /**
     * 批量删除广告
     * @param adIds 需要删除的广告ID列表
     * @return 结果
     */
    int deleteAdvertisementByIds(Long[] adIds);

    /**
     * 根据展示位置查询广告列表
     * @param displayPosition 展示位置
     * @return 广告列表
     */
    List<Advertisement> selectAdvertisementByPosition(String displayPosition);

    /**
     * 更新广告点击次数
     * @param adId 广告ID
     * @return 结果
     */
    int incrementClickCount(Long adId);

    /**
     * 更新广告浏览次数
     * @param adId 广告ID
     * @return 结果
     */
    int incrementViewCount(Long adId);
}