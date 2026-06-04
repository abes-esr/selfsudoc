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


FROM eclipse-temurin:8-jre-noble AS batch

# Installation et configuration de la locale FR
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install locales \
    && sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && locale-gen \
    && rm -rf /var/lib/apt/lists/*
ENV LANG=fr_FR.UTF-8
ENV LANGUAGE=fr_FR:fr
ENV LC_ALL=fr_FR.UTF-8

RUN mkdir -p /lib/ext
COPY --from=build-image /build/iText-src-5.0.2/target/*.jar /lib/iText-src-5.0.2-0.0.1.jar
COPY --from=build-image /build/iTextRenderer/target/*.jar /lib/iTextRenderer.jar
COPY --from=build-image /build/Renderer/target/*.jar /lib/Renderer.jar
COPY --from=build-image /build/Technic/target/*.jar /lib/Technic.jar
COPY --from=build-image /build/Utils/target/*.jar /lib/Utils.jar
COPY --from=build-image /build/Extract/target/*.jar /lib/Extract.jar
COPY --from=build-image /build/Connection/target/*.jar /lib/Connection.jar
COPY ./conf/log4j2.xml /conf/log4j2.xml

ENV JAVA_OPTIONS="-Dlog4j.configurationFile=file:/conf/log4j2.xml"
ENV CLASS_MAIN=fr.abes.derives.cli.Chain
ENV ARG_MAIN="extracted cleaned grouped filtered sorted xhtml rtf pdf slk"
ENV EOD_HOME=/data

COPY ./Technic/DonnesCodesUnm.txt $EOD_HOME/
COPY ./Technic/datagrouploc.xsl $EOD_HOME/
COPY ./Technic/filterRCR.xsl $EOD_HOME/
COPY ./Technic/sort.xsl $EOD_HOME/
COPY ./Technic/script.js $EOD_HOME/
COPY ./Technic/Bloclogoadresse1.gif $EOD_HOME/
COPY ./Technic/xslt/ $EOD_HOME/xslt/

HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=30s \
  CMD test $(find /tmp/selfsudoc_heartbeat -mmin -1) || exit 1

CMD ["sh", "-c", "exec java -cp /lib/*:/lib/ext/* $JAVA_OPTIONS $CLASS_MAIN $ARG_MAIN"]



FROM tomcat:8-jre8 AS front
# Installation et configuration de la locale FR
RUN apt-get update && DEBIAN_FRONTEND=noninteractive apt-get -y install locales \
    && sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && locale-gen \
    && rm -rf /var/lib/apt/lists/*
ENV LANG=fr_FR.UTF-8
ENV LANGUAGE=fr_FR:fr
ENV LC_ALL=fr_FR.UTF-8
# Supprimer l'application web par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT
# Définir le répertoire de travail dans l'étape de déploiement
WORKDIR /usr/local/tomcat/webapps
# Copier l'artefact WAR construit depuis l'étape 'build-image'
COPY --from=build-image /build/ExportsLibreService/target/SelfSudoc.war ./ROOT.war
COPY ./conf/log4j2.xml /conf/log4j2.xml
ENV CATALINA_OPTS="-Dlog4j.configurationFile=/conf/log4j2.xml"
ENV CATALINA_OUT=/dev/stdout
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=40s \
  CMD curl -s http://localhost:8080/selfsudoc/ | grep -q "<html" || exit 1
CMD ["catalina.sh", "run"]
