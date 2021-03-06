#!/usr/bin/env node
var sass = require('node-sass');
var fs = require('fs');
var cssDir = './dist/css/';
var cssOutputFile = cssDir+'style.css';
var scssMapOutputFile = cssDir+'style.css.map';

var bs = require("browser-sync").create();
bs.init({
  server: "."
});
bs.watch("**/**.scss", function (event, file) {
  if (event === "change") {
    sass.render({
      file: './scss/style.scss',
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
    bs.reload();
  }
});
