# **CI/CD 자동화 배포 & Load Balancing** 

## ➡️ 홈페이지 들어가기
#### 오픈시간 (오전 10시 ~ 오후 10시)
| 테스트 계정 | 아이디 | 비밀번호 | 페이지 링크 |
|------|--------|--------|--------|
|`일반회원 계정`| test@test.com | 1234 | http://hikok.store |
|`기업회원 계정`| company@test.com | 1234 | http://hikok.store |
|`관리자 계정`| admin@test.com | 1234 | http://hikok.store/admin/login |
```
재접속 중 오류 시 관리자모드(F12) Application 내에 URL의 Cookies를 지워주시면 감사하겠습니다.
또한, SNS 로그인은 계정 보호를 위해 삼가해주시고 테스트용 계정으로 확인해주시면 감사하겠습니다.
```
## Load-Balancing 😺
<a name="readme-top"></a> 

### Load-Balancing(로드 밸런싱)

      서버나 시스템에 가해지는 네트워크 트래픽 과부화를 막기 위 여러 대의 서버에 분산시키는 기술이다.

### Load-Balancing의 주요 기능 및 장점

   - 부하 분산: 클라이언트의 요청을 여러 대의 서버에 고르게 분배하여 한 서버에 집중되는 것을 방지합니다.
   - 고가용성: 특정 서버에 장애가 발생하더라도 서비스가 중단되지 않고, 다른 정상적인 서버로 요청을 보내 안정적인 서비스를 제공합니다.
   - 성능 확장: 서비스에 트래픽이 몰릴 때 서버를 추가하거나 제거하는 방식으로, 서비스의 중단 없이 유연하게 확장할 수 있습니다.
   - 장애 예방: 다운된 서버가 있을 경우 자동으로 감지하고 해당 서버로 요청을 보내지 않도록 관리하여 서비스의 안정성을 높입니다.

### Load-Balancing의 주요 알고리즘

   - 라운드 로빈(Round Robin): 들어오는 요청을 여러 서버에 순차적으로 분배합니다.
   - 최소 연결(Least Connections): 현재 가장 적은 연결 수를 가진 서버로 요청을 보냅니다.
   - IP 해시(IP Hash): 클라이언트의 IP 주소를 기반으로 특정 서버에 요청을 고정시킵니다.

### Nginx 설치
   ```
   # 설치 명령어
   ~$ sudo apt install -y nginx

   # 상태 확인
   ~$ sudo systemctl status nginx

   # 파일 만들기
   ~$ sudo vim /etc/nginx/sites-available/(프로젝트 명)

      # 설정 작성
      upstream (프로젝트 명) {
         least_conn; 	
         server ipaddress1:80;  # 첫 번째 EC2 	
         server ipaddress2:80;  # 두 번째 EC2
      }
      server {
         listen 80;

         location / {
            proxy_pass http://(프로젝트 명);
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
         }
      }

   # 기존 링크 삭제하기
   ~$ sudo rm /etc/nginx/sites-enabled/default

   # 만든 파일 링크 걸어주기
   ~$ sudo ln -s /etc/nginx/sites-available/(프로젝트 명) /etc/nginx/sites-enabled/

   # 확인하기
   ~$ ls -l /etc/nginx/sites-enabled/

   # 설정한 파일 테스트
   ~$ sudo nginx -t
   ```

### Workflow 파일 및 Dockerfile 파일 생성하기

| 파일 | 설명 | 링크 |
|------|--------|--------|
|`load_balancing_deploy.yml`| GitHub Actions 배포 설정| 🔗 [보기](https://github.com/hjjung990927/actions-load-balancing-kok/blob/master/.github/workflows/load_balancing_deploy.yml)|
|`Dockerfile`| Docker 빌드 설정| 📦 [보기](https://github.com/hjjung990927/actions-load-balancing-kok/blob/master/Dockerfile)|

### CI/CD → Load Balancing 연동 흐름
<img width="1920" height="1080" alt="Load-Balancing" src="https://github.com/user-attachments/assets/de134f76-03f3-47a4-aaeb-e4994bf87710" />

### 🧩 Trouble Shooting
#### 🔍 문제 원인
| Issue                  | Cause               |
| ---------------------- | ------------------- |
| 504 Gateway Time-out | Nginx가 프록시하는 포트와 Spring 실행 포트가 다름 |
<br>
<img width="377" height="116" alt="nginx" src="https://github.com/user-attachments/assets/416c4baf-13de-43cf-9c9f-b785c8dff619" />

#### ✅ 해결 방안

      // Nginx의 설정 파일을 수정하기 위해 여는 명령어
      ~$ sudo vim /etc/nginx/sites-available/(프로젝트 명)

            // 업스트림(백엔드 서버 그룹) 설정
            upstream (프로젝트 명) {
        	      least_conn; 	
        	      server ipaddress1:80;  # 첫 번째 EC2 	
        	      server ipaddress2:80;  # 두 번째 EC2
	      }

	      server {
        	      listen 80;

        	      location / {
                	      proxy_pass http://(프로젝트 명);
                	      proxy_set_header Host $host;
                	      proxy_set_header X-Real-IP $remote_addr;
    			      }
	      }


      // nginx 설정 테스트
      ~$ sudo nginx -t

      // nginx 설정 재시작
      ~$ sudo systemctl reload nginx

#### 🔍 문제 원인
| Issue                  | Cause               |
| ---------------------- | ------------------- |
| Docker 빌드 단계 실패 | ${{ secrets.EMAIL_PASSWORD }} 값에 공백 포함 |
<br>
<img width="923" height="224" alt="build" src="https://github.com/user-attachments/assets/cea3005f-4244-43ee-8861-e013cd0e9e57" />

#### ✅ 해결 방안
<img width="402" height="409" alt="build-fix" src="https://github.com/user-attachments/assets/10c9a4ae-0dbf-4fcf-acfb-9f45b47b0a12" />

	GitHub Actions workflow에서 ${{ secrets.EMAIL_PASSWORD }}를 큰따옴표("")로 감싸 문자열로 인식하게 했다.

## 🗣️ AI 욕설 확인 기능

#### 📋 기능 개요
사용자가 게시글 작성 시 내용 내 비속어 및 욕설을 걸러내기 위한 기능이다. <br>
FastAPI 서버에서 Naive Bayes(나이브 베이즈) 모델을 사용해 게시글 작성 내용의 욕설 여부 결과를 반환한다.
	
#### 📊 나이브 베이즈 분류 (Naive Bayes Classifier)
- 텍스트 분류를 위해 전통적으로 사용되는 분류기로서, 분류에 있어서 준수한 성능을 보인다.
- 베이즈 정리에 기반한 통계적 분류 기법으로서, 정확성도 높고 대용량 데이터에 대한 속도도 빠르다.
- 반드시 모든 feature가 서로 독립적이여야 한다. 즉, 서로 영향을 미치지 않는 feature들로 구성되어야 한다.
- 감정 분석, 스팸 메일 필터링, 텍스트 분류, 추천 시스템 등 여러 서비스에서 활용되는 분류 기법이다.
- 빠르고 정확하고 간단한 분류 방법이지만, 실제 데이터에서  
  모든 feature가 독립적인 경우는 드물기 때문에 실생활에 적용하기 어려운 점이 있다.

#### 📚 사용 데이터셋

| 데이터셋명 | 설명 | 출처 |
|-----------|------|------|
| **한국어 악성댓글 데이터셋** | 욕설감지데이터셋 에서 욕설문장만 데이터셋으로 활용 | 🔗 https://github.com/ZIZUN/korean-malicious-comments-dataset |

## 🔧 모델 설명

### ✔️ 알고리즘  
- **Multinomial Naive Bayes**  
  - 텍스트 분류에 특화  
  - 단어 등장 빈도 기반으로 욕설 여부 예측  

### ✔️ 전처리 & 학습 과정
1. 특수문자 제거 / 형태소 단위 정규화(kiwipiepy 사용)
2. `CountVectorizer` 로 BoW(단어 출현 빈도) 벡터 변환
3. MultinomialNB 학습
4. `word_model.pkl` 로 모델 저장 후 FastAPI에 로드

#### 🔗 [모델 훈련 코드](https://github.com/hjjung990927/study-Machine-Learning/blob/master/machine-learning/task/word_task.ipynb)
#### ⚠️ 불편함을 느낄 수 있는 문장과 단어가 있습니다.

### ⚙️ FastAPI 욕설 여부 분석 엔드포인트
```python
@app.post("/api/community/word-check", response_model=WordCheckResponse)
async def check_word(request: WordCheckRequest):
    model = joblib.load(f"word_model.pkl")
    prediction = model.predict([request.message])
    return {"isBadWord": bool(prediction)}
```
<img width="314" height="77" alt="machine-learning" src="https://github.com/user-attachments/assets/b9a35420-e882-4cca-a322-5471cc9c5ee9" />


















<p align="right">(<a href="#readme-top">back to top</a>)</p>
