#!/bin/bash

set -euo pipefail

currpath=$(dirname "$0")
basepath=$(cd "$currpath/../" || exit; pwd)

cmake -S "$basepath" -B "$basepath/cmake_build" \
    -DCMAKE_BUILD_TYPE=Debug -DENABLE_UNIT_TESTS=ON -DENABLE_COVERAGE=ON
cmake --build "$basepath/cmake_build" --target darabonba_xml_coverage
