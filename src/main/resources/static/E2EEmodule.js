/**
 * E2EEmodule.js - 종단간 암호화 모듈
 * 암호화 및 복호화 기능을 제공하는 JavaScript 모듈
 */

// 문자열을 그대로 Uint8Array로 변환하는 함수 (각 문자를 1바이트로 취급)
function stringToUint8Array(str) {
  const arr = new Uint8Array(str.length);
  for (let i = 0; i < str.length; i++) {
    arr[i] = str.charCodeAt(i);
  }
  return arr;
}

/**
 * 문자열을 암호화하는 함수
 * @param {string} plaintext - 암호화할 평문
 * @param {string} key - 암호화에 사용할 키 (16, 24, 또는 32바이트 문자열)
 * @returns {string} - Base64로 인코딩된 암호문 (iv + 암호화된 데이터)
 */
export async function encrypt(plaintext, key) {
  try {
    // 키를 Uint8Array로 변환 (문자열의 각 문자를 그대로 사용)
    const keyData = stringToUint8Array(key);

    // 암호화 키 생성
    const cryptoKey = await crypto.subtle.importKey(
      'raw',
      keyData,
      { name: 'AES-GCM' },
      false,
      ['encrypt']
    );

    // 초기화 벡터(IV) 생성 (12바이트)
    const iv = crypto.getRandomValues(new Uint8Array(12));

    // 평문을 Uint8Array로 변환 (UTF-8 인코딩)
    const data = new TextEncoder().encode(plaintext);

    // 암호화 수행
    const encryptedBuffer = await crypto.subtle.encrypt(
      {
        name: 'AES-GCM',
        iv
      },
      cryptoKey,
      data
    );

    // iv + 암호화된 데이터를 하나의 ArrayBuffer로 합치기
    const result = new Uint8Array(iv.length + encryptedBuffer.byteLength);
    result.set(iv, 0);
    result.set(new Uint8Array(encryptedBuffer), iv.length);

    // Base64로 인코딩하여 반환
    return btoa(String.fromCharCode.apply(null, result));
  } catch (error) {
    console.error('암호화 중 오류 발생:', error);
    throw new Error('암호화에 실패했습니다.');
  }
}

/**
 * 암호문을 복호화하는 함수
 * @param {string} ciphertext - Base64로 인코딩된 암호문
 * @param {string} key - 복호화에 사용할 키 (암호화에 사용한 키와 동일해야 함)
 * @returns {string} - 복호화된 평문
 */
export async function decrypt(ciphertext, key) {
  try {
    // Base64 디코딩 후 Uint8Array 생성
    const data = new Uint8Array(
      atob(ciphertext).split('').map(char => char.charCodeAt(0))
    );

    // iv와 암호화된 데이터 분리 (iv: 12바이트)
    const iv = data.slice(0, 12);
    const encryptedData = data.slice(12);

    // 키를 Uint8Array로 변환 (문자열의 각 문자를 그대로 사용)
    const keyData = stringToUint8Array(key);

    // 암호화 키 생성
    const cryptoKey = await crypto.subtle.importKey(
      'raw',
      keyData,
      { name: 'AES-GCM' },
      false,
      ['decrypt']
    );

    // 복호화 수행
    const decryptedBuffer = await crypto.subtle.decrypt(
      {
        name: 'AES-GCM',
        iv
      },
      cryptoKey,
      encryptedData
    );

    // Uint8Array를 문자열로 변환하여 반환
    return new TextDecoder().decode(decryptedBuffer);
  } catch (error) {
    console.error('복호화 중 오류 발생:', error);
    throw new Error('복호화에 실패했습니다. 키가 올바른지 확인해주세요.');
  }
}

/**
 * 모듈 테스트를 위한 예제 함수
 */
export async function testEncryption(plaintext, key) {
  try {
    const encrypted = await encrypt(plaintext, key);
    console.log('암호화된 텍스트:', encrypted);
    const decrypted = await decrypt(encrypted, key);
    console.log('복호화된 텍스트:', decrypted);
    return {
      success: true,
      originalText: plaintext,
      encryptedText: encrypted,
      decryptedText: decrypted
    };
  } catch (error) {
    console.error('테스트 중 오류 발생:', error);
    return {
      success: false,
      error: error.message
    };
  }
}

/**
 * 안전한 랜덤 키를 생성하는 유틸리티 함수
 * @param {number} keySize - 키의 크기 (128, 192, 또는 256 비트, 기본값: 256)
 * @returns {Object} - 생성된 키 정보 {key: 원본 키 문자열, base64Key: Base64 인코딩된 키}
 */
export function generateKey(keySize = 128) {
  let bytes;
  if (keySize === 128) {
    bytes = 16;
  } else if (keySize === 192) {
    bytes = 24;
  } else {
    bytes = 32;
  }
  const randomBytes = crypto.getRandomValues(new Uint8Array(bytes));
  let key = '';
  for (let i = 0; i < randomBytes.length; i++) {
    key += String.fromCharCode(randomBytes[i]);
  }
  const base64Key = btoa(key);
  return {
    key,
    base64Key,
    keySize: keySize,
    keyLength: bytes
  };
}
