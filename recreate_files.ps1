<#
This script recreates the project files for the Code Judge sample into D:\shodai.
Run in an elevated PowerShell (if necessary):

    powershell -ExecutionPolicy Bypass -File D:\shodai\recreate_files.ps1

It will create a minimal set of files and folders so they appear in Explorer.
#>

$root = 'D:\shodai'
New-Item -Path $root -ItemType Directory -Force | Out-Null

function Write-TextFile($path, $content) {
    $dir = Split-Path $path -Parent
    if (-not (Test-Path $dir)) { New-Item -Path $dir -ItemType Directory -Force | Out-Null }
    $content | Out-File -FilePath $path -Encoding UTF8 -Force
}

# pom.xml
Write-TextFile "$root\pom.xml" @'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>code-judge</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.6</version>
        <relativePath/>
    </parent>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
'@

# Dockerfile (judge image)
Write-TextFile "$root\Dockerfile" @'
FROM openjdk:17-alpine
WORKDIR /app
CMD ["sh", "-c", "javac Main.java 2> compile.err || true; java Main"]
'@

# Application main
Write-TextFile "$root\src\main\java\com\example\judge\CodeJudgeApplication.java" @'
package com.example.judge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CodeJudgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(CodeJudgeApplication.class, args);
    }
}
'@

# A tiny README
Write-TextFile "$root\README.md" @'
# Code Judge (local recreate)

Run `recreate_files.ps1` to recreate project structure. After running:

- Build judge image: `docker build -t judge-java:latest D:\shodai`
- Build backend: `mvn package` (requires Maven installed)
- Run frontend: `cd D:\shodai\frontend` then `npm install` and `npm run dev`
'@

# Marker file so you can see something immediately
Write-TextFile "$root\CREATED_BY_ASSISTANT.txt" "Files created by recreate_files.ps1 on $(Get-Date). Run the script to populate further files."

Write-Host "Wrote recreate_files.ps1 and helper files to D:\shodai. Run the script to populate the project files." -ForegroundColor Green
