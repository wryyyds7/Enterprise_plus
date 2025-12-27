import com.example.common.utils.EnterpriseJudgeUtils;

public class EnterpriseUrlValidationTest {
    public static void main(String[] args) {
        // Test Case 1: Problematic download.cc URL
        String problematicUrl = "http://www.downcc.com/company/341.htmlTranslate";
        boolean result1 = EnterpriseJudgeUtils.isValidEnterpriseUrl(problematicUrl);
        System.out.println("Test Case 1 - download.cc URL:");
        System.out.println("URL: " + problematicUrl);
        System.out.println("Result: " + result1 + " (should be false)");
        System.out.println();

        // Test Case 2: Valid enterprise website URL
        String validUrl = "https://www.alibabagroup.com/cn/";
        boolean result2 = EnterpriseJudgeUtils.isValidEnterpriseUrl(validUrl);
        System.out.println("Test Case 2 - Valid Enterprise Website URL:");
        System.out.println("URL: " + validUrl);
        System.out.println("Result: " + result2 + " (should be true)");
        System.out.println();

        // Test Case 3: URL containing translate
        String translateUrl = "https://translate.google.com/abc";
        boolean result3 = EnterpriseJudgeUtils.isValidEnterpriseUrl(translateUrl);
        System.out.println("Test Case 3 - URL containing translate:");
        System.out.println("URL: " + translateUrl);
        System.out.println("Result: " + result3 + " (should be false)");
        System.out.println();

        // Test Case 4: Search result URL
        String searchUrl = "https://www.baidu.com/s?wd=alibaba";
        boolean result4 = EnterpriseJudgeUtils.isValidEnterpriseUrl(searchUrl);
        System.out.println("Test Case 4 - Search Result URL:");
        System.out.println("URL: " + searchUrl);
        System.out.println("Result: " + result4 + " (should be false)");
        System.out.println();

        // Test Case 5: Deep path URL
        String deepPathUrl = "https://www.example.com/products/detail/123.html";
        boolean result5 = EnterpriseJudgeUtils.isValidEnterpriseUrl(deepPathUrl);
        System.out.println("Test Case 5 - Deep Path URL:");
        System.out.println("URL: " + deepPathUrl);
        System.out.println("Result: " + result5 + " (should be false)");
        System.out.println();

        // Test Case 6: One level path enterprise website URL
        String oneLevelPathUrl = "https://www.example.com/about";
        boolean result6 = EnterpriseJudgeUtils.isValidEnterpriseUrl(oneLevelPathUrl);
        System.out.println("Test Case 6 - One Level Path Enterprise URL:");
        System.out.println("URL: " + oneLevelPathUrl);
        System.out.println("Result: " + result6 + " (should be true)");
        System.out.println();
    }
}