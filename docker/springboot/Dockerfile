FROM registry.gitlab.com/open-source-devex/containers/java:latest
ARG jarName
ADD $jarName /home/bender/root.jar
ENV JAVA_OPTS="-Xmx512m"
ENV APP_WORK_DIR="/home/bender"
EXPOSE 8080
CMD [ "/home/bender/root.jar" ]