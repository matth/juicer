resolvers += Classpaths.typesafeResolver

resolvers += Resolver.url("artifactory", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.2")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.7")

