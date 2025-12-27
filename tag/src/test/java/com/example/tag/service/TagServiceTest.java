package com.example.tag.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.tag.TagApplication;
import com.example.tag.domain.Tag;
import com.example.tag.domain.EntityTag;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 标签服务测试类
 */
@SpringBootTest(classes = TagApplication.class)
public class TagServiceTest {

    @Autowired
    private ITagService tagService;

    @Autowired
    private IEntityTagService entityTagService;

    private Tag testTag;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        testTag = new Tag();
        testTag.setTagName("测试标签");
        testTag.setDescription("这是一个测试标签");
        testTag.setStatus("0");
    }

    @Test
    public void testTagCRUD() {
        // 测试新增标签
        int insertResult = tagService.insertTag(testTag);
        assertEquals(1, insertResult, "标签新增失败");
        assertNotNull(testTag.getTagId(), "标签ID未生成");

        // 测试查询标签
        Tag queriedTag = tagService.selectTagByTagId(testTag.getTagId());
        assertNotNull(queriedTag, "标签查询失败");
        assertEquals(testTag.getTagName(), queriedTag.getTagName(), "标签名称不匹配");

        // 测试更新标签
        testTag.setDescription("更新后的测试标签");
        int updateResult = tagService.updateTag(testTag);
        assertEquals(1, updateResult, "标签更新失败");
        Tag updatedTag = tagService.selectTagByTagId(testTag.getTagId());
        assertEquals("更新后的测试标签", updatedTag.getDescription(), "标签描述更新失败");

        // 测试标签列表查询
        Tag tagQuery = new Tag();
        tagQuery.setTagName("测试");
        List<Tag> tagList = tagService.selectTagList(tagQuery);
        assertTrue(tagList.size() > 0, "标签列表查询失败");

        // 测试实体标签关联
        EntityTag entityTag = new EntityTag();
        entityTag.setEntityType("enterprise");
        entityTag.setEntityId(1L);
        entityTag.setTagId(testTag.getTagId());
        int insertEntityTagResult = entityTagService.insertEntityTag(entityTag);
        assertEquals(1, insertEntityTagResult, "实体标签关联新增失败");

        // 测试根据实体查询标签
        List<EntityTag> entityTags = entityTagService.selectEntityTagsByEntity("enterprise", 1L);
        assertTrue(entityTags.size() > 0, "根据实体查询标签失败");

        // 测试删除实体标签关联
        int deleteEntityTagResult = entityTagService.deleteEntityTagByEntityTagId(entityTag.getEntityTagId());
        assertEquals(1, deleteEntityTagResult, "实体标签关联删除失败");

        // 测试删除标签
        int deleteResult = tagService.deleteTagByTagId(testTag.getTagId());
        assertEquals(1, deleteResult, "标签删除失败");
    }
}