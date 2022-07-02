package org.nuzhd.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class ImageByFileIdParser {

    @Value("${bot.token}")
    private String botToken;
    HttpClient client = HttpClient.newHttpClient();

    private String getFilePathByFileId(String fileId) throws InterruptedException, IOException {

        HttpRequest request = makeRequest("https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        String filePath = null;
        if (json.getBoolean("ok")) {
            filePath = json.getJSONObject("result").getString("file_path");
        }

        return filePath;
    }

    public byte[] parseImageFromTgServers(String fileId) throws InterruptedException, IOException {

        String filePath = getFilePathByFileId(fileId);

        HttpRequest request = makeRequest("https://api.telegram.org/file/bot" + botToken + "/" + filePath);

        HttpResponse<byte[]> resp = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        return resp.body();
    }

    private HttpRequest makeRequest(String uri) {
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(uri))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        return request;
    }
}
