# **코리아 IT 아카데미 국비과정** 
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









<p align="right">(<a href="#readme-top">back to top</a>)</p>
