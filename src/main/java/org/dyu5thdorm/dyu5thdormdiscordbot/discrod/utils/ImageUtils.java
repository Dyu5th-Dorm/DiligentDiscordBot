package org.dyu5thdorm.dyu5thdormdiscordbot.discrod.utils;


import com.gargoylesoftware.htmlunit.WebResponse;
import net.dv8tion.jda.api.utils.FileUpload;
import org.dyu5thdorm.dyu5thdormdiscordbot.DormWebClient;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.Base64;

/**
 * This class for fetches the student's personal image from iCloud.
 * It will return the base64 encoded string,
 * and this class will convert to byte stream and get as Discord file class FileUpload.
 *
 *
 */
@Component
public class ImageUtils {
    @Value("${image.student-api}")
    String imageApi;

    final DormWebClient webClient;

    public ImageUtils(DormWebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Convert the base64 encoded string to byte stream
     * @param studentId The student that wants to fetch
     * @return The discord file upload class
     * @throws IOException if from this.getImageData() can't find the student id which specified that will be thrown this exception
     */
    public FileUpload getStudentImage(@NotNull String studentId) throws IOException {
        String imageData = this.getImageData(studentId);
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        return FileUpload.fromData(
                imageBytes,
                String.format(
                        "%s.png", studentId.toUpperCase()
                )
        );
    }

    public Optional<FileUpload> tryGetStudentImage(@NotNull String studentId) {
        try {
            return Optional.of(getStudentImage(studentId));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    /**
     * To fetch the data from iCloud personal image API
     * @param studentId The student that wants to fetch
     * @return The image that base64 encoded
     * @throws IOException if from this.getImageData() can't find the student id which specified that will be thrown this exception
     */
    private String getImageData(@NotNull String studentId) throws IOException {
        // Should be an upper case in this API
        String target = studentId.toUpperCase();
        WebResponse response = this.webClient.getPage(
                String.format(imageApi, target)
        ).getWebResponse();

        String imageData = response.getContentAsString();

        /*
          data be like this
          {"result":1,"msg":"","data":{"":{"image_type":0,"image":""}}}
         */

        // convert to json data first
        JSONObject source = new JSONObject(imageData);
        // find data json object
        JSONObject data = source.getJSONObject("data");
        // get this image_type if exists or not
        int hasImage = data.getJSONObject(target).getInt("image_type");

        if (hasImage == 0) {
            throw new IOException("No image found");
        }

        String image = data.getJSONObject(target).getString("image");
        image = image.split(",")[1];

        return image;
    }

}
