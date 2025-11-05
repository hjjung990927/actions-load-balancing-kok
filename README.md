# **코리아 IT 아카데미 국비과정** 
## Load-Balancing 😺
<a name="readme-top"></a> 

#### Load-Balancing(로드 밸런싱)

      서버나 시스템에 가해지는 네트워크 트래픽 과부화를 막기 위 여러 대의 서버에 분산시키는 기술이다.

#### Load-Balancing의 주요 기능 및 장점

   - 부하 분산: 클라이언트의 요청을 여러 대의 서버에 고르게 분배하여 한 서버에 집중되는 것을 방지합니다.
   - 고가용성: 특정 서버에 장애가 발생하더라도 서비스가 중단되지 않고, 다른 정상적인 서버로 요청을 보내 안정적인 서비스를 제공합니다.
   - 성능 확장: 서비스에 트래픽이 몰릴 때 서버를 추가하거나 제거하는 방식으로, 서비스의 중단 없이 유연하게 확장할 수 있습니다.
   - 장애 예방: 다운된 서버가 있을 경우 자동으로 감지하고 해당 서버로 요청을 보내지 않도록 관리하여 서비스의 안정성을 높입니다.

#### Load-Balancing의 주요 알고리즘

   - 라운드 로빈(Round Robin): 들어오는 요청을 여러 서버에 순차적으로 분배합니다.
   - 최소 연결(Least Connections): 현재 가장 적은 연결 수를 가진 서버로 요청을 보냅니다.
   - IP 해시(IP Hash): 클라이언트의 IP 주소를 기반으로 특정 서버에 요청을 고정시킵니다.

#### Nginx 설치
   ```
   ~$ sudo apt install -y nginx
   ~$ sudo systemctl status nginx
   ~$ sudo vim /etc/nginx/sites-available/(프로젝트 명)

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
         
   ~$ sudo rm /etc/nginx/sites-enabled/default
   ~$ sudo ln -s /etc/nginx/sites-available/(프로젝트 명) /etc/nginx/sites-enabled/
   ~$ ls -l /etc/nginx/sites-enabled/
   ~$ sudo nginx -t
   ~$ sudo systemctl reload nginx~$ sudo systemctl status nginx
   ```

#### CI/CD → Load Balancing 연동 흐름
<img width="1920" height="1080" alt="Load-Balancing" src="https://github.com/user-attachments/assets/de134f76-03f3-47a4-aaeb-e4994bf87710" />








<p align="right">(<a href="#readme-top">back to top</a>)</p>
