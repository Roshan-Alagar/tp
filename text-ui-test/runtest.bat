@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean shadowJar --quiet

cd build\libs
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

java -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

REM Filter out HashID lines before comparing
findstr /v "HashID:" ACTUAL.TXT > ACTUAL.filtered.TXT
findstr /v "HashID:" EXPECTED.TXT > EXPECTED.filtered.TXT

REM Compare the filtered files
FC ACTUAL.filtered.TXT EXPECTED.filtered.TXT >NUL && ECHO Test passed! || Echo Test failed!