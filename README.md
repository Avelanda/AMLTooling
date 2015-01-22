AMLTooling
==========

AML Tooling is collection of Archetype Modeling Language (AML) related tools.
It has three sub-projects

1. ADL2AMLConverter - Converts ADL 1.5 files into in memory AML Objects using Cameo/Magic Draw Open APIs.

2. AML-MDLibrary - Set of useful classes with methods to create AML Object. It is currently used by ADL2AML Converter, but can be included in any other application which wants to use MD OpenAPIs to create AML Objects.

3. AMLMDPlugin - A small prototype showing how to create create a Magic Draw plugin.

To compile run following maven commands in AMLTooling Project:

mvn clean install

To run it in an IDE:

1. Setup the heap size.
2. include Cameo/MD installation directory and its plugins subdirectory.
3. include the supplied log4j or use your own log4j logger.

-Xmx1000M
-XX:PermSize=4M
-XX:MaxPermSize=135M
-Dinstall.root="/Applications/Cameo Enterprise Architecture"
-Dmd.plugins.dir="/Applications/Cameo Enterprise Architecture/plugins"
-Dlog4j.configuration=log4j.properties
