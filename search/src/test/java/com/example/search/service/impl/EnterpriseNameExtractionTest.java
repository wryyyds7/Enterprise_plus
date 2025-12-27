package com.example.search.service.impl;

import com.example.common.domain.entity.BoChaResult.WebPageValue;
import com.example.common.domain.entity.EnterpriseInfo;
import com.example.common.utils.EnterpriseJudgeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 企业名称提取功能测试类
 * 测试场景：
 * 1. 完整名称输入测试
 * 2. 部分名称输入测试
 * 3. 名称前缀输入测试
 * 4. 边缘案例测试
 * 
 * 测试环境要求：
 * 1. Java版本: JDK 17 或以上
 * 2. Maven版本: 3.6.0 或以上
 * 3. 依赖: spring-boot-starter-test, junit-jupiter-api, mockito-junit-jupiter, jsoup
 * 
 * 运行步骤：
 * 1. 使用Maven命令行运行:
 *    - 进入项目根目录: cd d:\bianchenglianxi\java\project\enterprise_plus
 *    - 运行所有测试: mvn test -pl search
 *    - 仅运行企业名称提取功能测试: mvn test -pl search -Dtest=EnterpriseNameExtractionTest
 * 
 * 2. 使用IDE运行:
 *    - 在IntelliJ IDEA中，右键点击该文件，选择"Run 'EnterpriseNameExtractionTest'"
 *    - 在Eclipse中，右键点击该文件，选择"Run As" -> "JUnit Test"
 * 
 * 测试覆盖范围：
 * - 完整名称输入测试：测试输入完整企业名称时的提取效果
 * - 部分名称输入测试：测试输入企业名称一部分时的提取效果
 * - 名称前缀输入测试：测试输入企业名称前缀时的提取效果
 * - 版权信息提取逻辑测试：测试从不同格式的版权信息中提取企业名称
 * - 名称相关性检查测试：测试判断两个企业名称是否相关的逻辑
 * 
 * 如何分析测试结果：
 * 1. 测试通过情况：
 *    - 绿色对勾表示测试通过
 *    - 控制台输出"Tests passed: X, failed: 0"表示所有测试通过
 * 
 * 2. 测试失败分析：
 *    - 红色叉号表示测试失败
 *    - 控制台输出详细的错误信息，包括：
 *      - 失败的测试方法名称
 *      - 断言失败的具体原因
 *      - 期望的值和实际的值
 * 
 * 3. 常见失败原因及解决方法：
 *    - 网络连接问题：确保测试环境有网络连接，能够访问企业官网
 *    - 企业名称不匹配：检查输入的企业名称是否正确，是否与网页中的企业名称一致
 *    - URL无效：确保测试中使用的URL是有效的企业官网
 *    - 版权信息格式变化：如果企业官网的版权信息格式发生变化，可能需要更新正则表达式
 * 
 * 4. 调试技巧：
 *    - 使用IDE的调试功能，设置断点查看执行过程
 *    - 在测试方法中添加System.out.println()语句输出中间结果
 *    - 检查企业官网的实际HTML结构，确认版权信息的位置和格式
 *    - 分析日志文件，查看是否有异常信息
 */
@ExtendWith(MockitoExtension.class)
public class EnterpriseNameExtractionTest {

    /**
     * 测试场景1：完整名称输入
     * 输入：完整的企业名称
     * 预期结果：成功提取到完整的企业名称
     */
    @Test
    public void testFullNameExtraction() {
        // 模拟WebPageValue对象
        WebPageValue mockWebPage = new WebPageValue();
        mockWebPage.setName("阿里巴巴集团控股有限公司官网");
        mockWebPage.setUrl("https://www.alibaba.com");
        mockWebPage.setSnippet("阿里巴巴集团控股有限公司是中国领先的电子商务公司");
        
        // 输入：完整的企业名称
        String inputName = "阿里巴巴集团控股有限公司";
        
        // 执行测试
        EnterpriseInfo result = EnterpriseJudgeUtils.isRightEnterprise(mockWebPage, inputName);
        
        // 验证结果
        assertNotNull(result, "应该成功提取到企业信息");
        assertTrue(result.getEnterpriseName().contains("阿里巴巴集团控股有限公司"), "提取的企业名称应该与输入的完整名称一致");
    }

    /**
     * 测试场景2：部分名称输入
     * 输入：企业名称的一部分
     * 预期结果：成功提取到完整的企业名称
     */
    @Test
    public void testPartialNameExtraction() {
        // 模拟WebPageValue对象
        WebPageValue mockWebPage = new WebPageValue();
        mockWebPage.setName("腾讯官网");
        mockWebPage.setUrl("https://www.tencent.com");
        mockWebPage.setSnippet("腾讯是中国领先的互联网公司");
        
        // 输入：企业名称的一部分
        String inputName = "腾讯";
        
        // 执行测试
        EnterpriseInfo result = EnterpriseJudgeUtils.isRightEnterprise(mockWebPage, inputName);
        
        // 验证结果
        assertNotNull(result, "应该成功提取到企业信息");
        assertTrue(result.getEnterpriseName().contains(inputName), "提取的企业名称应该包含输入的部分名称");
    }

    /**
     * 测试场景3：名称前缀输入
     * 输入：企业名称的前缀
     * 预期结果：成功提取到完整的企业名称
     */
    @Test
    public void testPrefixNameExtraction() {
        // 模拟WebPageValue对象
        WebPageValue mockWebPage = new WebPageValue();
        mockWebPage.setName("百度在线网络技术有限公司官网");
        mockWebPage.setUrl("https://www.baidu.com");
        mockWebPage.setSnippet("百度在线网络技术有限公司是全球最大的中文搜索引擎");
        
        // 输入：企业名称的前缀
        String inputName = "百度在线";
        
        // 执行测试
        EnterpriseInfo result = EnterpriseJudgeUtils.isRightEnterprise(mockWebPage, inputName);
        
        // 验证结果
        assertNotNull(result, "应该成功提取到企业信息");
        assertTrue(result.getEnterpriseName().startsWith(inputName) || result.getEnterpriseName().contains(inputName), "提取的企业名称应该包含输入的前缀");
    }

    /**
     * 测试场景4：测试版权信息提取逻辑
     * 模拟不同格式的版权信息并验证提取结果
     */
    @Test
    public void testCopyrightExtractionLogic() throws Exception {
        // 模拟包含版权信息的网页
        String htmlContent = "<html><body><footer>© 2025 阿里巴巴集团控股有限公司 版权所有</footer></body></html>";
        Document mockDoc = Jsoup.parse(htmlContent);
        Element mockFooter = mockDoc.select("footer").first();
        
        // 使用反射调用私有方法来测试版权信息提取
        java.lang.reflect.Method method = EnterpriseJudgeUtils.class.getDeclaredMethod("extractCompanyNameFromCopyright", Element.class, String.class);
        method.setAccessible(true);
        
        // 执行测试
        String result = (String) method.invoke(null, mockFooter, "阿里巴巴");
        
        // 验证结果
        assertNotNull(result, "应该从版权信息中提取到企业名称");
        assertTrue(result.contains("阿里巴巴集团控股有限公司"), "提取的企业名称应该包含正确的公司名称");
    }

    /**
     * 测试场景5：测试名称相关性检查
     * 验证isNameRelated方法能够正确判断名称相关性
     */
    @Test
    public void testNameRelatedCheck() throws Exception {
        // 使用反射调用私有方法来测试名称相关性检查
        java.lang.reflect.Method method = EnterpriseJudgeUtils.class.getDeclaredMethod("isNameRelated", String.class, String.class);
        method.setAccessible(true);
        
        // 测试1：提取名称包含搜索名称
        boolean result1 = (boolean) method.invoke(null, "阿里巴巴集团控股有限公司", "阿里巴巴");
        assertTrue(result1, "'阿里巴巴集团控股有限公司' 应该与 '阿里巴巴' 相关");
        
        // 测试2：搜索名称包含提取名称
        boolean result2 = (boolean) method.invoke(null, "阿里巴巴", "阿里巴巴集团");
        assertTrue(result2, "'阿里巴巴' 应该与 '阿里巴巴集团' 相关");
        
        // 测试3：不相关的名称
        boolean result3 = (boolean) method.invoke(null, "腾讯科技有限公司", "阿里巴巴");
        assertFalse(result3, "'腾讯科技有限公司' 不应该与 '阿里巴巴' 相关");
    }
}