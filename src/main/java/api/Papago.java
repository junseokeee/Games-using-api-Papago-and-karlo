import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Papago {

    public static void main(String[] args) {
        // Your code here
    }

    public static String[] getRandomWords() {
        String[] names = {"사과", "바나나", "오렌지", "포도", "수박", "참외", "딸기", "체리", "자두", "배", "파인애플", "복숭아", "망고", "레몬", "라임", "블루베리", "복분자", "아보카도", "키위", "자몽", "라즈베리", "블랙베리", "살구", "감", "석류", "크랜베리", "토마토", "석류", "오디", "파파야"};
        Random random = new Random();
        int[] selectedIndices = random.ints(0, names.length).distinct().limit(3).toArray();
        String[] selectedWords = new String[3];
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedWords[i] = names[selectedIndices[i]];
        }
        return selectedWords;
    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text) {
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=ko&target=en&text=" + text;
        try {
            con.setRequestMethod("POST");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readBody(con.getInputStream());
            } else {
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static String extractTranslatedText(String responseBody) {
        int startIndex = responseBody.indexOf("translatedText\":\"") + 17;
        int endIndex = responseBody.indexOf("\",\"engineType");
        return responseBody.substring(startIndex, endIndex);
    }
}
