#!/bin/bash
rm -rf dist/
rm -rf vendor/
mkdir -p dist/css
mkdir -p dist/js
mkdir vendor
npm install
npm run scss
npm run mini-css
npm run copy-js
npm run mini-js
npm run copy-vendor