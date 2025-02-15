package druid.elf.tool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class FileDownloader {
    public static void main(String[] args) {
        String fileUrl = "https://times1736938454.subxiandan.top:9604/v2b/shandian/api/v1/client/subscribe?token=a1fdcb7b977fad51b14e77d3885ff3f6";
        String localFilePath = "downloaded_file.base64"; // 保存下载的 Base64 编码文件
        String decodedFilePath = "decoded_file.yaml"; // 解码后的 YAML 文件路径

        try {
            // 下载文件
            downloadFile(fileUrl, localFilePath);
            System.out.println("File downloaded and saved as: " + localFilePath);

            // 读取 Base64 编码的文件并解码
            String base64EncodedContent = readBase64File(localFilePath);

            // 解码并保存为正常的 YAML 文件
            decodeBase64AndSaveToFile(base64EncodedContent, decodedFilePath);
            System.out.println("Decoded file saved as: " + decodedFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 下载文件并保存为本地 Base64 编码文件
    private static void downloadFile(String fileUrl, String localFilePath) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        // 如果请求成功
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(localFilePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("File downloaded successfully!");
            }
        } else {
            throw new IOException("Failed to download file. HTTP Code: " + connection.getResponseCode());
        }
    }

    // 读取 Base64 编码的文件内容
    private static String readBase64File(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        }
        return contentBuilder.toString();
    }

    // 解码 Base64 内容并保存为实际的文件
    private static void decodeBase64AndSaveToFile(String base64Content, String decodedFilePath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);

        try (FileOutputStream fileOutputStream = new FileOutputStream(decodedFilePath)) {
            fileOutputStream.write(decodedBytes);
        }
    }
}
