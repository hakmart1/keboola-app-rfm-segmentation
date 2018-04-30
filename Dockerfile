FROM openjdk:8

COPY . /code/
WORKDIR /data/
CMD ["java", "-XX:MaxRAM=248M", "-jar", "/code/KeboolaRFMSegmentation.jar", "config.json"]

