package com.kymokim.spirit.common.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:/secret/application-s3.properties")
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    // S3 업로드
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        System.out.println(multipartFile.getName());
        System.out.println(multipartFile.getOriginalFilename());
        //S3에 Multipartfile 타입은 전송이 안되므로 file로 타입 전환
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File convert failed"));
        return upload(uploadFile, dirName);
    }
    private String upload(File uploadFile, String dirName){
        String fileName = dirName + "/"+ UUID.randomUUID().toString()+ uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);
        System.out.println(uploadImageUrl);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }
    private String putS3(File uploadFile, String fileName){
        //외부에서 정적 파일을 읽을 수 있도록 public 읽기 권한으로 put
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        //업로드 된 파일의 S3 URL 주소 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
    private void removeNewFile(File targetFile){
        // Multipartfile -> file로 전환되면서 로컬에 파일 생성된 것을 삭제
        if(targetFile.delete()){
            System.out.println("파일이 삭제되었습니다.");
        }else
        {
            System.out.println("파일이 삭제되지 않았습니다.");
        }
    }
    private Optional<File> convert(MultipartFile file) throws IOException{
        File convertFile = new File(file.getOriginalFilename());
        if(convertFile.createNewFile()){
            try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)){
                fileOutputStream.write(file.getBytes());
            }

            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    public void deleteFile(String url){
        String[] fileName = url.split("/");
        System.out.println(fileName[3]+fileName[4]);
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName[3]+"/"+fileName[4]);
        amazonS3Client.deleteObject(request);
    }

}
