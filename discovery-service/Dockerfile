FROM openjdk:11-jdk
ADD build/libs/discovery-service*.jar discovery-service.jar
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.8.0/wait /wait
RUN printf "#!/bin/bash\n/wait && java -jar /discovery-service.jar" > /entrypoint.sh
RUN chmod +x /wait /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]