package com.example.study.Utility;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Getter
@Setter
@Service
public class Utility {
    private String encryptKey = "jK9#mN2$pL5@Qx8&";

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
            return encryptedText;
        }
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3) //3시간마다 키 순환
    public void lotateEncryptKey() {
        this.encryptKey = CreateEncryptKey();
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

}
