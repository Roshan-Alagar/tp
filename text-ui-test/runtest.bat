@echo off
setlocal enableextensions
pushd %~dp0

echo Current directory: %CD%
echo Building JAR...

cd ..
call gradlew clean shadowJar --quiet

echo Build complete. Looking for JAR...

cd build\libs
dir *.jar
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
    echo Found JAR: %%a
)

echo Running JAR with input.txt...
java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

echo Current directory: %CD%
echo ACTUAL.TXT contents:
type ACTUAL.TXT
echo.
echo EXPECTED.TXT contents:
type EXPECTED.TXT
echo.

REM Filter out HashID lines before comparing
echo Filtering HashID lines...
findstr /v "HashID:" ACTUAL.TXT > ACTUAL.filtered.TXT
findstr /v "HashID:" EXPECTED.TXT > EXPECTED.filtered.TXT

echo Filtered ACTUAL content:
type ACTUAL.filtered.TXT
echo.
echo Filtered EXPECTED content:
type EXPECTED.filtered.TXT
echo.

REM Compare the filtered files
echo Comparing filtered files...
FC ACTUAL.filtered.TXT EXPECTED.filtered.TXT > comparison.txt
type comparison.txt

if %errorlevel% equ 0 (
    echo Test passed!
    exit /b 0
) else (
    echo Test failed! Error level: %errorlevel%
    exit /b 1
)