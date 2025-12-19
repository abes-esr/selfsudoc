###
# Image pour la compilation
FROM maven:3-eclipse-temurin-8-noble AS build-image
WORKDIR /build/

# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY ./pom.xml                     /build/pom.xml
COPY ./Connection/pom.xml          /build/Connection/pom.xml
COPY ./ExportsLibreService/pom.xml /build/ExportsLibreService/pom.xml
COPY ./Extract/pom.xml             /build/Extract/pom.xml
COPY ./iText-src-5.0.2/pom.xml     /build/iText-src-5.0.2/pom.xml
COPY ./iTextRenderer/pom.xml       /build/iTextRenderer/pom.xml
COPY ./Renderer/pom.xml            /build/Renderer/pom.xml
COPY ./Technic/pom.xml             /build/Technic/pom.xml
COPY ./Utils/pom.xml               /build/Utils/pom.xml

RUN mvn verify --fail-never
# et la compilation du code Java
COPY ./Connection/          /build/Connection/
COPY ./ExportsLibreService/ /build/ExportsLibreService/
COPY ./Extract/             /build/Extract/
COPY ./iText-src-5.0.2/     /build/iText-src-5.0.2/
COPY ./iTextRenderer/       /build/iTextRenderer/
COPY ./Renderer/            /build/Renderer/
COPY ./Technic/             /build/Technic/
COPY ./Utils/               /build/Utils/

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package -Passembly

RUN mv /build/ExportsLibreService/target/SelfSudoc.war ./SelfSudoc.war

RUN rm -f /build/ExportsLibreService/pom.xml && \
    rm -Rf /build/ExportsLibreService/target && \
    mv /build/ExportsLibreService/pom-filedownload.xml /build/ExportsLibreService/pom.xml

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package -Passembly



FROM eclipse-temurin:8-jre-noble AS batch

RUN mkdir -p /lib/ext
COPY --from=build-image /build/iText-src-5.0.2/target/*.jar /lib/iText-src-5.0.2-0.0.1.jar
COPY --from=build-image /build/iTextRenderer/target/*.jar /lib/iTextRenderer.jar
COPY --from=build-image /build/Renderer/target/*.jar /lib/Renderer.jar
COPY --from=build-image /build/Technic/target/*.jar /lib/Technic.jar
COPY --from=build-image /build/Utils/target/*.jar /lib/Utils.jar
COPY --from=build-image /build/Extract/target/*.jar /lib/Extract.jar
COPY --from=build-image /build/Connection/target/*.jar /lib/Connection.jar
COPY ./conf/log4j.xml /conf/log4j.xml

ENV JAVA_OPTIONS="-Dlog4j.configuration=file:/conf/log4j.xml"
ENV CLASS_MAIN=fr.abes.derives.cli.Chain
ENV ARG_MAIN="extracted cleaned grouped filtered sorted xhtml rtf pdf slk"
ENV EOD_HOME=/data

COPY ./Technic/DonnesCodesUnm.txt $EOD_HOME/
COPY ./Technic/datagrouploc.xsl $EOD_HOME/
COPY ./Technic/filterRCR.xsl $EOD_HOME/
COPY ./Technic/sort.xsl $EOD_HOME/
COPY ./Technic/xslt/ $EOD_HOME/xslt/

CMD ["sh", "-c", "exec java -cp /lib/*:/lib/ext/* $JAVA_OPTIONS $CLASS_MAIN $ARG_MAIN"]



FROM tomcat:8-jre8 AS front
# Supprimer l'application web par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT
# Définir le répertoire de travail dans l'étape de déploiement
WORKDIR /usr/local/tomcat/webapps
# Copier l'artefact WAR construit depuis l'étape 'build-image'
COPY --from=build-image /build/SelfSudoc.war ./SelfSudoc.war
COPY --from=build-image /build/ExportsLibreService/target/exportsdemandes.war ./exportsdemandes.war
EXPOSE 8080
ENV CATALINA_OUT=/dev/stdout
CMD ["catalina.sh", "run"]
