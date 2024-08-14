package exam.master.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class AwsS3Service {
  private final S3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  public String uploadFile(MultipartFile multipartFile) {

    try {
      // 중복 되는 파일을 덮어쓰지 않도록 고유한 파일 이름 생성
      String fileName = genernateUniqueFileName(multipartFile.getOriginalFilename());

      //  MultiPartFile 임시 파일 생성
      File tempFile = File.createTempFile("temp", null);
      multipartFile.transferTo(tempFile);

      // PutObjectRequest  생성
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .contentType(multipartFile.getContentType())
          .contentLength(multipartFile.getSize())
          .key(fileName)
          .build();
      // 파일을 S3에 업로드
      s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));

      // 업로드한 이미지 URL 가져오기
      GetUrlRequest getUrlRequest = GetUrlRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .build();

      //임시 파일 삭제
      tempFile.delete();

      log.info( "S3 이미지 경로" + s3Client.utilities().getUrl(getUrlRequest).toString());

//      return s3Client.utilities().getUrl(getUrlRequest).toString();
      return fileName;

    } catch (IOException e) {
      log.error("cannot upload image", e);
      throw new RuntimeException(e);
    }


  }

  public void deleteFile(String fileName) {
    try {
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(fileName)
          .build();
      s3Client.deleteObject(deleteObjectRequest);
      log.info("Deleted file with key: {}", fileName);
    } catch (S3Exception e) {
      log.error("Error deleting file from S3: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private String genernateUniqueFileName(String originalFileName){
    // 파일 확장자 추출 (jpg, png ...)
    String extension = "";
    int dotindex = originalFileName.lastIndexOf(".");
    if (dotindex > 0){
      extension = originalFileName.substring(dotindex);
    }

    // UUID 를 활용해서 이미지 파일명을 고유하게 생성
    String uniqueFileName = UUID.randomUUID().toString() + extension;
    return uniqueFileName;
  }
}