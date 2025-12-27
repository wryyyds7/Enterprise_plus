import com.example.common.utils.PositionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TestPositionUtils {
    public static void main(String[] args) {
        try {
            // 测试阿里巴巴官网
            String alibabaUrl = "https://www.alibabagroup.com/cn/zh";
            System.out.println("测试阿里巴巴官网：" + alibabaUrl);
            
            Document doc = Jsoup.connect(alibabaUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // 查找招聘页面
            String recruitmentUrl = PositionUtils.findRecruitmentPageUrl(doc);
            System.out.println("找到的招聘页面：" + recruitmentUrl);
            
            if (recruitmentUrl != null) {
                // 访问招聘页面并提取职位信息
                Document jobPageDoc = Jsoup.connect(recruitmentUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(10000)
                        .get();
                
                System.out.println("\n提取职位信息：");
                PositionUtils.extractPositionInfo(jobPageDoc).forEach(position -> {
                    System.out.println("职位名称：" + position.getName());
                    System.out.println("职位URL：" + position.getUrl());
                    System.out.println("薪资：" + position.getSalary());
                    System.out.println("描述：" + position.getDescription());
                    System.out.println("------------------------");
                });
            } else {
                System.out.println("未找到招聘页面");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}