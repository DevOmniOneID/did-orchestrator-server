import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

  public static void main(String[] args) {
    try {
      // 실행할 여러 쉘 명령을 세미콜론으로 구분하여 하나의 명령어로 합침
      String command = "cd /Users/shlee1223/workspace/did-release/shells/Postgre; sh status.sh";

      // ProcessBuilder를 사용하여 여러 명령을 실행
      ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
      processBuilder.redirectErrorStream(true); // 에러 출력도 표준 출력에 합침(Optional)

      // 프로세스를 실행하고 결과를 받아오기
      Process process = processBuilder.start();
      String output = getProcessOutput(process);

      process.waitFor(); // 프로세스 종료 대기

      System.out.println("output : " + output);

      // output 기반의 핸들링 영역
      if (!output.equals("0")) {
        // 프로세스 상태 정상
        System.out.println("프로세스 상태 정상");
      } else {
        // 프로세스 상태 비정상
        System.out.println("프로세스 상태 비정상");
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static String getProcessOutput(Process process) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    StringBuilder output = new StringBuilder();

    String line;
    while ((line = reader.readLine()) != null) {
      output.append(line).append("\n");
    }

    return output.toString().trim();
  }
}
