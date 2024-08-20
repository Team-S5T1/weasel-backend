package exam.master.config;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler.Visitor;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.ExecutorService;


@Slf4j
public class InvokeModelWithResponseStream {
    public static void invokeModel(String prompt, byte[] imageBytes, SseEmitter emitter, ExecutorService executorService) {

        executorService.submit(() -> {
            try {
                // 이미지 파일을 Base64로 인코딩
                String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                // 모델 ID 및 요청 설정
                var modelId = "anthropic.claude-3-5-sonnet-20240620-v1:0";
                var nativeRequestTemplate = """
                  {
                      "anthropic_version": "bedrock-2023-05-31",
                      "max_tokens": 1024,
                      "temperature": 0.5,
                      "messages": [{
                          "role": "user",
                          "content": [
                              {
                                  "type": "image",
                                  "source": {
                                      "type": "base64",
                                      "media_type": "image/png",
                                      "data": "{{image}}"
                                  }
                              },
                              {
                                  "type": "text",
                                  "text": "{{prompt}}"
                              }
                          ]
                      }]
                  }""";

                // 프롬프트를 모델의 네이티브 요청 페이로드에 삽입
                String nativeRequest = nativeRequestTemplate
                  .replace("{{image}}", encodedImage)
                  .replace("{{prompt}}", prompt);

                // BedrockRuntimeAsyncClient 생성
                var client = BedrockRuntimeAsyncClient.builder()
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .region(Region.US_EAST_1)
                        .build();

                // 요청 생성
                var request = InvokeModelWithResponseStreamRequest.builder()
                        .body(SdkBytes.fromUtf8String(nativeRequest))
                        .modelId(modelId)
                        .build();

                // 응답 스트림 핸들러 준비
                var responseStreamHandler = InvokeModelWithResponseStreamResponseHandler.builder()
                        .subscriber(Visitor.builder().onChunk(chunk -> {
                            var response = new JSONObject(chunk.bytes().asUtf8String());

                            // 텍스트 블록에서 응답 추출 및 전송
                            if (Objects.equals(response.getString("type"), "content_block_delta")) {
                                var text = new JSONPointer("/delta/text").queryFrom(response);
                                try {
                                    emitter.send(SseEmitter.event()
                                            .data("{\"type\": \"content\", \"data\": \"" + text + "\"}")
                                            .build());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }).build()).build();

                // 모델 호출
                client.invokeModelWithResponseStream(request, responseStreamHandler).get();

                // 모델 호출 완료 후 Emitter 닫기
                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
    }
}

//            ------------------------------------------------------------
//
//            // push test
//
//            // 이미지 파일을 Base64로 인코딩
//            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
//
//            // claude 3.5 sonet 모델은 버지니아 리전에서 밖에 지원 안함
//            var client = BedrockRuntimeClient.builder()
//                    .credentialsProvider(DefaultCredentialsProvider.create())
//                    .region(Region.US_EAST_1)
//                    .build();
//
//            // model ID, e.g., claude-3-5-sonnet 클로우드 소넷 3.5
//            var modelId = "anthropic.claude-3-5-sonnet-20240620-v1:0";
//
//            // bedrock 요청시 Message 예제
//            // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html
//            // img 타입은 png, jpeg 이미지만 가능 3.75 MB 이하의 저용량 파일만 요청할수 있음
//
//            var nativeRequestTemplate = """
//          {
//              "anthropic_version": "bedrock-2023-05-31",
//              "max_tokens": 1024,
//              "temperature": 0.5,
//              "messages": [{
//                  "role": "user",
//                  "content": [
//                      {
//                          "type": "image",
//                          "source": {
//                              "type": "base64",
//                              "media_type": "image/png",
//                              "data": "{{image}}"
//                          }
//                      },
//                      {
//                          "type": "text",
//                          "text": "{{prompt}}"
//                      }
//                  ]
//              }]
//          }""";
//
//            // Prompt Customizing
//            String customizePrompt =
//                    "Strictly adhere to the following rules.\\n" +
//                            "1. Always respond in Korean.\\n" +
//                            "2. Provide the closest answer to the correct one.\\n" +
//                            "3. Consider each step carefully and respond thoughtfully.\\n" +
//                            "4. Think from an expert's perspective.\\n" +
//                            "5. Keep answers short and concise.\\n" +
//                            "6. If an option is not the correct answer, explain why it is not correct.\\n" +
//                            "7. Explain why the correct answer is correct.\\n" +
//                            "User Prompt:\\n\\n";
//            String combinedPrompt = customizePrompt + prompt;
//
//            log.debug("Prompt Customizing >>> " + combinedPrompt);
//
//            // 템플릿에 prompt 삽입
//            String nativeRequest = nativeRequestTemplate
//                    .replace("{{image}}", encodedImage)
//                    .replace("{{prompt}}", combinedPrompt);
//
//            // 보낼 내용 Message 바이트로 인코딩 후 베드락 런타임에 요청.
//            var response = client.InvokeModelWithResponseStreamRequest(request -> request
//                    .body(SdkBytes.fromUtf8String(nativeRequest))
//                    .modelId(modelId)
//            );
//
//            System.out.println("response: " + response);
//
//            // 응답 바이트 배열로 수신 후 UTF-8로 디코딩
//            byte[] responseBytes = response.body().asByteArray();
//            String responseBody = new String(responseBytes, StandardCharsets.UTF_8);
//
//            System.out.println("Raw Response: " + responseBody);
//
//            // JSON 파싱
//            var jsonResponse = new JSONObject(responseBody);
//
//            // jsonResponse 정보 중에 응답 text만 추출
//            var text = new JSONPointer("/content/0/text").queryFrom(jsonResponse).toString();
//            System.out.println("Extracted Text: " + text);
//
//            return text;
//
//        } catch (SdkClientException e) {
//            System.err.printf("ERROR: Can't invoke '%s'. Reason: %s", e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
//}
