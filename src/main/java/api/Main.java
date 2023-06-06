import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;


public class Main {
    public static void main(String[] args) {
        // [내 애플리케이션] > [앱 키] 에서 확인한 REST API 키 값 입력
        String REST_API_KEY = "b7892c9eb3fbbdbc7355583994f96b59";

        // 프롬프트에 사용할 제시어
        String text = "an airplane";

        // 이미지 생성하기 REST API 호출
        Map<String, Object> jsonMap = new HashMap<>();
        Map<String, Object> promptMap = new HashMap<>();
        promptMap.put("text", text);
        promptMap.put("batch_size", 1);
        jsonMap.put("prompt", promptMap);
        String json = new JSONObject(jsonMap).toString();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost("https://api.kakaobrain.com/v1/inference/karlo/t2i");
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + REST_API_KEY);
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setEntity(new StringEntity(json, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String responseBody = EntityUtils.toString(entity);
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    String base64Image = jsonResponse.getJSONArray("images")
                            .getJSONObject(0)
                            .getString("image");

                    // Check if base64Image is not null or empty
                    if (base64Image != null && !base64Image.isEmpty()) {
                        System.out.println("Base64 image: " + base64Image);
                    } else {
                        System.out.println("No image data found in the API response.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
