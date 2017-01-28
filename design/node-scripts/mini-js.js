#!/usr/bin/env node
var fs = require('fs');
var uglifyJS = require('uglify-js');
var result = uglifyJS.minify("./js/main.js");
destinationFile = './dist/js/main.min.js';
fs.writeFileSync(destinationFile, result.code);