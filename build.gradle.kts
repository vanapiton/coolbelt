import java.net.URI

plugins {
	id("maven-publish")
	id("fabric-loom") version "1.16.3"
	id("babric-loom-extension") version "1.15.3"
}

// Helper function to get gradle properties
fun Project.prop(name: String): String =
	providers.gradleProperty(name).get()

base {
	archivesName = prop("archives_base_name")
}

version = prop("mod_version")
group = prop("maven_group")

loom {
//	accessWidenerPath = file("src/main/resources/coolbelt.accesswidener")

	runs {
		// If you want to make a test mod for your mod, right click on src, and create a new folder with the same name as source() below.
		// IntelliJ should give suggestions for test mod folders.
		register("testClient") {
			source("test")
			client()
			configurations.transitiveImplementation
		}
		register("testServer") {
			source("test")
			server()
			configurations.transitiveImplementation
		}
	}
}

repositories {
	maven("https://maven.glass-launcher.net/snapshots/")
	maven("https://maven.glass-launcher.net/releases/")
	maven("https://maven.glass-launcher.net/babric")
	maven("https://maven.minecraftforge.net/")
	maven("https://jitpack.io/")
	maven("https://matthewperiut.github.io/repository")
	maven("https://maven.ornithemc.net/")
	mavenCentral()
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:b1.7.3")
	mappings("net.glasslauncher:biny:${prop("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")

	implementation("org.apache.logging.log4j:log4j-core:2.17.2")

	implementation("org.slf4j:slf4j-api:1.8.0-beta4")
	implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.17.1")

	// Convenience stuff
	// adds some useful annotations for data classes. does not add any dependencies
	compileOnly("org.projectlombok:lombok:1.18.42")
	annotationProcessor("org.projectlombok:lombok:1.18.42")

	// adds some useful annotations for miscellaneous uses. does not add any dependencies, though people without the lib will be missing some useful context hints.
	implementation("org.jetbrains:annotations:23.0.0")
	implementation("com.google.guava:guava:33.2.1-jre")

	// Dependencies
	// https://github.com/matthewperiut/accessory-api
	modImplementation("maven.modrinth:accessory-api:${prop("accessoryapi_version")}")
	// https://github.com/calmilamsy/glass-config-api
	modImplementation("net.glasslauncher.mods:GlassConfigAPI:${prop("gcapi_version")}")

	// Extra mods
	// https://github.com/ModificationStation/StationAPI
	modImplementation("net.modificationstation:StationAPI:${prop("stationapi_version")}")
	// https://github.com/calmilamsy/modmenu
	modImplementation("net.danygames2014:modmenu:${prop("modmenu_version")}")
	// https://github.com/Glass-Series/Always-More-Items
	modImplementation("net.glasslauncher.mods:AlwaysMoreItems:${prop("alwaysmoreitems_version")}")
}

configurations.all {
	exclude(group = "babric")
}

tasks.withType<ProcessResources>().configureEach {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

// ensure that the encoding is set to UTF-8, no matter what the system default is
// this fixes some edge cases with special characters not displaying correctly
// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks.withType<Jar>().configureEach {
	from("LICENSE") {
		rename { "${it}_${base.archivesName}" }
	}
}

// Tells Gradle to not generate module files for maven.
// They aren't standard and the documentation is abysmal. Stop it.
tasks.withType<GenerateModuleMetadata>().configureEach {
	enabled = false
}

publishing {
	repositories {
		mavenLocal()
		if (project.hasProperty("my_maven_username")) {
			maven {
				url = URI("https://maven.example.com")
				credentials {
					username = prop("my_maven_username")
					password = prop("my_maven_password")
				}
			}
		}
	}

	publications {
		register<MavenPublication>("mavenJava") {
			artifactId = prop("archives_base_name")
			from(components["java"])
		}
	}
}

