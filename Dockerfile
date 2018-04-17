FROM openjdk:8

COPY . /code/
WORKDIR /data/
CMD ["java", "-jar", "/code/KeboolaRFMSegmentation.jar", "config.json"]

