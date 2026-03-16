#!/usr/bin/env bash

# change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar --quiet

cd text-ui-test

java  -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Filter out HashID lines before comparing
grep -v "HashID:" ACTUAL.TXT > ACTUAL.filtered.TXT
grep -v "HashID:" EXPECTED.TXT > EXPECTED.filtered.TXT

# Use the FILTERED files for comparison
cp EXPECTED.filtered.TXT EXPECTED-UNIX.TXT
dos2unix EXPECTED-UNIX.TXT ACTUAL.filtered.TXT
diff EXPECTED-UNIX.TXT ACTUAL.filtered.TXT
if [ $? -eq 0 ]
then
    echo "Test passed!"
    exit 0
else
    echo "Test failed!"
    exit 1
fi