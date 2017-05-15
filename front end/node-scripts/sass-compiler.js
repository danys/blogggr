#!/usr/bin/env node
var sass = require('node-sass');
var fs = require('fs');
var cssDir = './src/styling/dist/css/';
var cssOutputFile = cssDir+'style.css';
var scssMapOutputFile = cssDir+'style.css.map';
sass.render({
  file: './src/styling/scss/style.scss',
  outFile: cssOutputFile,
  sourceMap: true,
  outputStyle: 'expanded'
}, function(error, result) {
if (error) {
    console.log(error.status); // used to be "code" in v2x and below 
    console.log(error.column);
    console.log(error.message);
    console.log(error.line);
  }
  else {

    console.log('Compilation success');
    fs.writeFileSync(cssOutputFile, result.css);
    fs.writeFileSync(scssMapOutputFile, result.map);
  }
});