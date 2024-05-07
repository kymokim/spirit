FROM openjdk:21
WORKDIR /home/kymokim/spirit
COPY /build/libs/spirit-0.0.1-SNAPSHOT.jar /home/kymokim/spirit/spirit.jar
ENTRYPOINT ["java","-jar","spirit.jar"]