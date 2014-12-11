#
# Shell script to execute AML OpenAPI project using command line.
#
MAGICDRAW_INSTALL="/Applications/Cameo Enterprise Architecture"

PLUGINS="$MAGICDRAW_INSTALL/plugins"
MAIN_CLASS="edu.mayo.aml.tooling.batch.AMLBatchMain"
VMOPTIONS="-Xmx1000M -XX:PermSize=4M -XX:MaxPermSize=135M"

AMLLIBJAR="target/AMDLib-1.0-SNAPSHOT.jar"

# IFS is the path separator - set to ":"
# Include all files in MagicDraw lib directory
MDLIBS="$MAGICDRAW_INSTALL/lib"
CLASSPATH1=$(JARS=("$MDLIBS"/*.jar); IFS=:; echo "${JARS[*]}")
GRPHLIBS="$MAGICDRAW_INSTALL/lib/graphics"
CLASSPATH2=$(JARS=("$GRPHLIBS"/*.jar); IFS=:; echo "${JARS[*]}")
WSLIBS="$MAGICDRAW_INSTALL/lib/webservice"
CLASSPATH3=$(JARS=("$WSLIBS"/*.jar); IFS=:; echo "${JARS[*]}")

# Log file is in src/main/resources foler
LOG4J=-Dlog4j.configuration=log4j.properties

CLASSPATH=$CLASSPATH1\:$CLASSPATH2\:$CLASSPATH3\:$AMLLIBJAR\:./target

java $VMOPTIONS -cp "$CLASSPATH" $MAIN_CLASS  