# oikoumene
Tinkering with Scala while reverse engineering Clausewitz (Europa Universalis 4).

Reconstructing oikoumene. 
> The ecumene (US) or oecumene (UK; Greek: οἰκουμένη, oikouménē, lit. "inhabited") 
was an ancient Greek term for the known, the inhabited, or the habitable world. 
Under the Roman Empire, it came to refer to civilization 
as well as the secular and religious imperial administration.

# TIL
### Scala
- Things they don't tell you about in books - [optics](https://scalac.io/scala-optics-lenses-with-monocle/). See [quicklens](https://github.com/softwaremill/quicklens).
### Scala-Jackson
- Having run into problems with deserializing SortedSets and TreeSets,
it appears to be a [known issue](https://github.com/FasterXML/jackson-module-scala/wiki/FAQ#deserializing-optionint-and-other-primitive-challenges).
TLDR: Use ```@JsonDeserialize(contentAs = classOf[java.lang.Integer])``` on the offending member.
### SBT
- Configure multimodule cross-dependencies in root sbt file. In particular 
use:
    - dependsOn (to tell compiler to look for sources in those modules),
    - aggregate (to tell compiler to use those project for compilation and building)
    - settings (to share common settings).
- Encountered with Play, but it is a general sbt issue.
Compilation in Idea may fail with TypeChecker StackOverflowError.
This is caused by insufficient resources being allocated to sbt by default.
To address it up JVM values in your project and run configurations.
Additionally run configurations might also require environment variable
such as _SBT_OPTS=-J-Xmx2G_
### Play
- Play doesn't care about your project layout. It has its own structure
and it will reset your projects configs to its own forcibly if you
do not comply.
- sbt and play have conflicting project layouts, so you have to either
create a separate module for play or disable play layout tracking as
[described in the official docs](https://www.playframework.com/documentation/2.6.x/Anatomy#Default-SBT-layout):
```$xslt
disablePlugins(PlayLayoutPlugin)
PlayKeys.playMonitoredFiles ++= (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value
```
- Don't forget config files under 'project' to set up play.
- Reopening the project in Idea or even wiping '.idea' folder might be
required to force IDE to register above updates.
- To setup Play debugging, create a new run configuration 
from 'Play 2 App' template, don't forget to pick a scala module
that contains the play app (clausewitz-rest in this case).
