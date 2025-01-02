package com.example.study.Utility;
import com.example.study.Entity.MessageEntity;
import com.example.study.Repository.ChatRoomRepository;
import com.example.study.Repository.MessageRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Getter
@Setter
@Service
public class Utility {
    private String encryptKey = "jK9#mN2$pL5@Qx8&";

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private MessageEntity messageEntity = new MessageEntity();

    public String encrypt(String text, String key) { //문자열 암호화
        try {
            key = String.format("%-16s", key).substring(0, 16); //암호 문자열을 16자리로 맞춤

            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("암호화 오류 발생", e);
        }
    }


    public String decrypt(String encryptedText, String key) { //문자열 복호화
        try {
            // 16자리 키로 맞추기
            key = String.format("%-16s", key).substring(0, 16);

            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            return this.SimpleEncrypt(encryptedText.substring(0, 10));
        }
    }

    public String SimpleEncrypt(String text) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : text.toCharArray()) {
            // 각 문자를 숫자로 변환 (a=01, b=02, ... z=26)
            int num = Character.toLowerCase(c) - 'a' + 1;
            // 두 자리 숫자로 만들기 (한 자리 숫자 앞에 0 추가)
            encrypted.append(String.format("%02d", num));
        }
        return encrypted.toString();
    }

    public String SimpleDecrypt(String encrypted) { //간단한 문자열 난독화, 키 필요 없음
        StringBuilder decrypted = new StringBuilder();
        // 두 글자씩 읽어서 원래 문자로 변환
        for (int i = 0; i < encrypted.length(); i += 2) {
            int num = Integer.parseInt(encrypted.substring(i, i + 2));
            char c = (char) ('a' + num - 1);
            decrypted.append(c);
        }
        return decrypted.toString();
    }


    @Scheduled(fixedRate = 86400000) //3(10800000)시간마다 키 순환
    public void lotateEncryptKey() {
        this.encryptKey = CreateEncryptKey();
        this.reChatEncrypt(); //매일 메세지 재 암호화 후 저장
        this.deleteAllImages(); //매일 이미지 삭제
    }

    @Scheduled(fixedRate = 259200000)
    public void lotateDBclear(){
        messageRepository.deleteAll(); //3일마다 db내용 초기화
        chatRoomRepository.deleteAll(); //3일마다 db내용 초기화
    }

    public void reChatEncrypt() {
        List<MessageEntity> messageEntityList = messageRepository.findAll();
        messageEntityList.forEach(messageEntity -> {
            messageEntity.setMessage(encrypt(getOnlyChar() + messageEntity.getMessage() +
                    getOnlyChar(), encryptKey).substring(0, 30)); //암호화된 문자열을 30개 잘라서 다시 재 암호화
            try{ //파일이 존재할 경우 재 암호화
                messageEntity.setFilePath(encrypt(getOnlyChar() + messageEntity.getFilePath() +
                        getOnlyChar(), encryptKey).substring(0, 30)); //암호화된 문자열을 30개 잘라서 다시 재 암호화
            } catch (Exception e){ }
            messageRepository.save(messageEntity);
        });
    }

    public String CreateEncryptKey() {
        SecureRandom random = new SecureRandom();

        // 문자 집합 정의
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "#$@%&*";

        // 각 문자 종류별로 최소 하나씩 선택
        String key = "";
        key += upperChars.charAt(random.nextInt(upperChars.length()));  // 대문자
        key += lowerChars.charAt(random.nextInt(lowerChars.length()));  // 소문자
        key += numbers.charAt(random.nextInt(numbers.length()));        // 숫자
        key += specialChars.charAt(random.nextInt(specialChars.length())); // 특수문자

        // 모든 문자를 합친 집합
        String allChars = upperChars + lowerChars + numbers + specialChars;

        // 나머지 12자리 랜덤 생성
        for (int i = 0; i < 12; i++) {
            key += allChars.charAt(random.nextInt(allChars.length()));
        }

        // 문자열을 배열로 변환하여 섞기
        char[] keyChars = key.toCharArray();
        for (int i = keyChars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // swap
            char temp = keyChars[i];
            keyChars[i] = keyChars[j];
            keyChars[j] = temp;
        }

        return new String(keyChars);
    }

    public void deleteAllImages() {
        try {
            Path directory = Paths.get("build/resources/main/static/images");

            if (!Files.exists(directory)) {
                System.out.println("지정된 디렉토리가 존재하지 않습니다: " + directory);
                return;
            }

            // 파일 목록 먼저 수집
            List<File> filesToDelete = Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        String fileName = path.toString().toLowerCase();
                        return fileName.endsWith(".jpg") ||
                                fileName.endsWith(".jpeg") ||
                                fileName.endsWith(".png") ||
                                fileName.endsWith(".gif");
                    })
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            if (filesToDelete.isEmpty()) {
                System.out.println("삭제할 이미지 파일이 없습니다.");
                return;
            }

            // 파일이 존재할 경우에만 삭제 진행
            filesToDelete.forEach(File::delete);
            System.out.println("이미지 파일들이 성공적으로 삭제되었습니다.");

        } catch (IOException e) {
            System.out.println("이미지 삭제 중 오류 발생: " + e.getMessage());
        }
    }

    public static char getOnlyChar() {
        // 포함할 문자 목록 정의 (a-z, A-Z, 0-9, 특수기호)
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_=+[]{}|;:',.<>?/~`";
        Random random = new Random();
        // 랜덤하게 하나의 문자 선택
        return characters.charAt(random.nextInt(characters.length()));
    }
}
