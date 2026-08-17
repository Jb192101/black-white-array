#!/bin/bash

echo "=== Running BlackWhiteArray Tests ==="

mvn clean test

if [ $? -eq 0 ]; then
    echo "All tests passed successfully!"
    exit 0
else
    echo "Some tests failed!"
    exit 1
fi