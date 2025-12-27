package com.example.advertisement.controller;

import com.example.common.domain.entity.Result;
import com.example.advertisement.domain.entity.Advertisement;
import com.example.advertisement.service.AdvertisementService;
import com.example.common.aop.Log;
import com.example.common.domain.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisement")
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    /**
     * 新增广告
     * @param advertisement 广告信息
     * @return 结果
     */
    @PostMapping
    @Log(title = "广告管理", businessType = BusinessType.INSERT)
    public Result insertAdvertisement(@RequestBody Advertisement advertisement) {
        int result = advertisementService.insertAdvertisement(advertisement);
        if (result > 0) {
            return Result.success(advertisement);
        }
        return Result.error("新增广告失败");
    }

    /**
     * 根据广告ID查询广告信息
     * @param adId 广告ID
     * @return 广告信息
     */
    @GetMapping("/{adId}")
    public Result selectAdvertisementById(@PathVariable Long adId) {
        Advertisement advertisement = advertisementService.selectAdvertisementById(adId);
        return Result.success(advertisement);
    }

    /**
     * 查询广告列表
     * @param advertisement 广告信息
     * @return 广告列表
     */
    @GetMapping
    public Result selectAdvertisementList(Advertisement advertisement) {
        List<Advertisement> list = advertisementService.selectAdvertisementList(advertisement);
        return Result.success(list);
    }

    /**
     * 更新广告信息
     * @param advertisement 广告信息
     * @return 结果
     */
    @PutMapping
    @Log(title = "广告管理", businessType = BusinessType.UPDATE)
    public Result updateAdvertisement(@RequestBody Advertisement advertisement) {
        int result = advertisementService.updateAdvertisement(advertisement);
        if (result > 0) {
            return Result.success("更新广告成功");
        }
        return Result.error("更新广告失败");
    }

    /**
     * 删除广告
     * @param adId 广告ID
     * @return 结果
     */
    @DeleteMapping("/{adId}")
    @Log(title = "广告管理", businessType = BusinessType.DELETE)
    public Result deleteAdvertisementById(@PathVariable Long adId) {
        int result = advertisementService.deleteAdvertisementById(adId);
        if (result > 0) {
            return Result.success("删除广告成功");
        }
        return Result.error("删除广告失败");
    }

    /**
     * 批量删除广告
     * @param adIds 需要删除的广告ID列表
     * @return 结果
     */
    @DeleteMapping("/batch")
    @Log(title = "广告管理", businessType = BusinessType.DELETE)
    public Result deleteAdvertisementByIds(@RequestBody Long[] adIds) {
        int result = advertisementService.deleteAdvertisementByIds(adIds);
        if (result > 0) {
            return Result.success("批量删除广告成功");
        }
        return Result.error("批量删除广告失败");
    }

    /**
     * 根据展示位置查询广告列表
     * @param displayPosition 展示位置
     * @return 广告列表
     */
    @GetMapping("/position/{displayPosition}")
    public Result selectAdvertisementByPosition(@PathVariable String displayPosition) {
        List<Advertisement> list = advertisementService.selectAdvertisementByPosition(displayPosition);
        return Result.success(list);
    }

    /**
     * 更新广告点击次数
     * @param adId 广告ID
     * @return 结果
     */
    @PutMapping("/click/{adId}")
    @Log(title = "广告点击", businessType = BusinessType.UPDATE)
    public Result incrementClickCount(@PathVariable Long adId) {
        int result = advertisementService.incrementClickCount(adId);
        if (result > 0) {
            return Result.success("更新点击次数成功");
        }
        return Result.error("更新点击次数失败");
    }

    /**
     * 更新广告浏览次数
     * @param adId 广告ID
     * @return 结果
     */
    @PutMapping("/view/{adId}")
    @Log(title = "广告浏览", businessType = BusinessType.UPDATE)
    public Result incrementViewCount(@PathVariable Long adId) {
        int result = advertisementService.incrementViewCount(adId);
        if (result > 0) {
            return Result.success("更新浏览次数成功");
        }
        return Result.error("更新浏览次数失败");
    }
}