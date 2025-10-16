package com.Ayan.Mondal.VOTEONN.SERVICE;


import com.Ayan.Mondal.VOTEONN.DTO.FaceVerificationResponse;
import org.bytedeco.opencv.presets.opencv_core;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;



@Service
public class FaceVerificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public FaceVerificationResponse verifyFace(Long userId, byte[] fileBytes, String originalFilename) {

        String url = "http://localhost:8000/verify_face/" + userId;
        System.out.println("Here is the url::"+url);
        // Wrap byte[] as ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(fileBytes) {
            @Override
            public String getFilename() {
                return (originalFilename != null) ? originalFilename : "upload.jpg";
            }

            @Override
            public long contentLength() {
                return fileBytes.length;
            }
        };

        // Prepare multipart form data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send POST request
        ResponseEntity<FaceVerificationResponse> response = restTemplate.postForEntity(
                url, requestEntity, FaceVerificationResponse.class
        );

        return response.getBody();
    }
}
