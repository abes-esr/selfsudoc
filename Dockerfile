###
# Image pour la compilation
FROM maven:3-eclipse-temurin-8-noble AS build-image
WORKDIR /build/

# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY ./pom.xml                 /build/pom.xml
COPY ./iText-src-5.0.2/pom.xml /build/iText-src-5.0.2/pom.xml
COPY ./iTextRenderer/pom.xml   /build/iTextRenderer/pom.xml
COPY ./Renderer/pom.xml        /build/Renderer/pom.xml
COPY ./Technic/pom.xml         /build/Technic/pom.xml
COPY ./Utils/pom.xml           /build/Utils/pom.xml

RUN mvn verify --fail-never
# et la compilation du code Java
COPY ./iText-src-5.0.2/   /build/iText-src-5.0.2/
COPY ./iTextRenderer/     /build/iTextRenderer/
COPY ./Renderer/          /build/Renderer/
COPY ./Technic/           /build/Technic/
COPY ./Utils/             /build/Utils/

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package -Passembly



## Batch Renderer
FROM eclipse-temurin:8-jre-noble AS batch-image

RUN mkdir -p /lib/ext
COPY --from=build-image /build/iText-src-5.0.2/target/*.jar /lib/iText-src-5.0.2-0.0.1.jar
COPY --from=build-image /build/iTextRenderer/target/*.jar /lib/iTextRenderer.jar
COPY --from=build-image /build/Renderer/target/*.jar /lib/Renderer.jar
COPY --from=build-image /build/Technic/target/*.jar /lib/Technic.jar
COPY --from=build-image /build/Utils/target/*.jar /lib/Utils.jar
COPY ./conf/log4j.xml /conf/log4j.xml

ENV JAVA_OPTIONS="-Dlog4j.configuration=file:/conf/log4j.xml"
ENV CLASS_MAIN=fr.abes.derives.cli.Chain
ENV ARG_MAIN="extracted cleaned grouped filtered sorted xhtml rtf pdf slk"

CMD ["sh", "-c", "exec java -cp /lib/*:/lib/ext/* $JAVA_OPTIONS $CLASS_MAIN $ARG_MAIN"]
