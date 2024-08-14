package exam.master.config;

import org.json.JSONObject;
import org.json.JSONPointer;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.nio.charset.StandardCharsets;

public class InvokeModel {

  public static String invokeModel() {

    // Create a Bedrock Runtime client in the AWS Region you want to use.
    // Replace the DefaultCredentialsProvider with your preferred credentials provider.
    var client = BedrockRuntimeClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.create())
        .region(Region.US_EAST_1)
        .build();

    // model ID, e.g., claude-3-5-sonnet 클로우드 소넷
    var modelId = "anthropic.claude-3-5-sonnet-20240620-v1:0";

    // The InvokeModel API uses the model's native payload.
    // Learn more about the available inference parameters and response fields at:
    // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-anthropic-claude-messages.html
    var nativeRequestTemplate = """
                {
                    "anthropic_version": "bedrock-2023-05-31",
                    "max_tokens": 512,
                    "temperature": 0.5,
                    "messages": [{
                        "role": "user",
                        "content": "{{prompt}}"
                    }]
                }""";

    // Define the prompt for the model.
    var prompt = "3.5 sonet에 대해서 한글로 설명해줘";

    // Embed the prompt in the model's native request payload.
    String nativeRequest = nativeRequestTemplate.replace("{{prompt}}", prompt);

    try {
      // Encode and send the request to the Bedrock Runtime.
      var response = client.invokeModel(request -> request
          .body(SdkBytes.fromUtf8String(nativeRequest))
          .modelId(modelId)
      );

      System.out.println("response: " + response);

      // 응답 바이트 배열로 수신 후 UTF-8로 디코딩
      byte[] responseBytes = response.body().asByteArray();
      String responseBody = new String(responseBytes, StandardCharsets.UTF_8);

      System.out.println("Raw Response: " + responseBody);

      // JSON 파싱
      var jsonResponse = new JSONObject(responseBody);

      // jsonResponse 정보 중에 응답 text만 추출
      var text = new JSONPointer("/content/0/text").queryFrom(jsonResponse).toString();
      System.out.println("Extracted Text: " + text);

      return text;

    } catch (SdkClientException e) {
      System.err.printf("ERROR: Can't invoke '%s'. Reason: %s", modelId, e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    invokeModel();
  }
}