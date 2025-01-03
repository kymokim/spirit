package com.kymokim.spirit.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:/secret/application-s3.properties")
public class S3Service {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert failed"));
        return upload(uploadFile, dirName);
    }

    // 다중 파일 업로드
    public List<String> uploadMultiple(List<MultipartFile> multipartFiles, String dirName) throws IOException {
        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            File uploadFile = convert(multipartFile)
                    .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert failed"));
            String uploadUrl = upload(uploadFile, dirName);
            uploadedUrls.add(uploadUrl);
        }
        return uploadedUrls;
    }

    private String upload(File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID().toString() + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    private String putS3(File uploadFile, String fileName) {
        // PutObjectRequest를 생성하고 S3에 파일 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)// 파일 공개 설정
                .build();

        s3Client.putObject(putObjectRequest, Paths.get(uploadFile.getAbsolutePath()));
        return s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(fileName)).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            System.out.println("파일이 삭제되었습니다.");
        } else {
            System.out.println("파일이 삭제되지 않았습니다.");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
                fileOutputStream.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public void deleteFile(String url) {
        // 파일 URL에서 파일 이름 추출
        String[] fileNameParts = url.split("/");
        String fileName = fileNameParts[fileNameParts.length - 2] + "/" + fileNameParts[fileNameParts.length - 1];

        // DeleteObjectRequest를 생성하여 파일 삭제
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
